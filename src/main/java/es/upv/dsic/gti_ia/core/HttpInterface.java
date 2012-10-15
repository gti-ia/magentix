package es.upv.dsic.gti_ia.core;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.SingleAgent;
import es.upv.dsic.gti_ia.organization.Configuration;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.log4j.xml.DOMConfigurator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * 
 * @author ricard
 */
public class HttpInterface {

	static int http_port;
	private long petitions = 0;
	Configuration configuration = Configuration.getConfiguration();
	
	public static int getHttp_port() {
		return http_port;
	}

	private class ServerAgent extends SingleAgent {
		
		private class JSONMessage {

			public String agent_name;
			public String conversation_id;
			public String content;
		}
		
	

		Socket socket;

		public ServerAgent(AgentID aid, Socket socket) throws Exception {
			super(aid);
			this.socket = socket;
		}

		public void execute() {
			InputStream is = null;
			try {
				is = socket.getInputStream();
				String mensaje = this.pop(is);
				logger.info("InterfaceAgent: HTTP request received "+mensaje);
				BufferedReader reader = new BufferedReader(new StringReader(mensaje));
				boolean stop = false;
				String content;
				while (!stop) {
					if (reader.readLine().indexOf("Content-Length:") != -1) {
						stop = true;
					}
				}
				reader.readLine();
				content = reader.readLine();
				String jsonString = "{\"jsonObject\":" + content + "}";
				XStream xstream = new XStream(new JettisonMappedXmlDriver());
				xstream.alias("jsonObject", JSONMessage.class);
				JSONMessage jsonMessage = (JSONMessage)xstream.fromXML(jsonString);				
				logger.info("InterfaceAgent: Message to send: Agent name: "+jsonMessage.agent_name+" conversation id: "+jsonMessage.conversation_id);

				// enviem missatge al agent destí
				ACLMessage pregunta = new ACLMessage(ACLMessage.REQUEST);
				pregunta.setProtocol("web");
				pregunta.setReceiver(new AgentID(jsonMessage.agent_name));
				pregunta.setSender(this.getAid());
				pregunta.setConversationId(jsonMessage.conversation_id);
				pregunta.setContent(jsonString);
				this.send(pregunta);

				// esperem a la resposta
				ACLMessage resposta = null;
				try {
					resposta = this.receiveACLMessage();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info("InterfaceAgent: HTTP Response to send: "+resposta.getContent());
				OutputStream os = socket.getOutputStream();
				
				// Prova, acabar amb \r\n. Si no va, llevar \r
				String OK = "HTTP/1.1 200 OK \r\n "
						+ "Server:	Apache/2.2.14 (Ubuntu)\r\n"
						+ "X-Powered-By:	PHP/5.3.2-1ubuntu4.9\r\n"
						+ "Vary:	Accept-Encoding\r\n"
						+ "Content-Encoding:	gzip\r\n" + "Content-Length:	"
						+ resposta.getContent().length()*2 + "\r\n"
						+ "Connection:	close\r\n" + "Content-Type:	text/html\n\n"
						+ resposta.getContent();

				byte a[] = OK.getBytes();
				os.write(a);

				// Cerrar
				os.close();
				//is.close(); no tanquem el inputstream o no funciona be
				socket.close();
			}
			catch (IOException ex) {
				Logger.getLogger(HttpInterface.class.getName()).log(Level.SEVERE, null, ex);
			} 
			finally {
				try {
					is.close();
				} 
				catch (IOException ex) {
					Logger.getLogger(HttpInterface.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

		/**
		 * Creates a HTTP header to be used in the message.
		 * 
		 * @param hostDestiny
		 *            The destination of the receiver.
		 * @param portDestiny
		 *            The port destiny of the receiver.
		 * @return The String containing the header
		 */
		private String Generate_Header(InetAddress hostDestiny,
				int portDestiny, String content) {
			String httpheader = "POST ";
			httpheader += this.socket.getInetAddress();
			httpheader += " HTTP/1.1\r\n";
			httpheader += "Cache-Control: no-cache\r\n";
			httpheader += "Mime-Version: 1.0\r\n";
			httpheader += "Host: ";
			httpheader += hostDestiny.getHostAddress() + ":" + portDestiny
					+ "\r\n";
			httpheader += "Content-Type: multipart/mixed\r\n";
			httpheader += "Content-Length: ";
			httpheader += content.getBytes().length + "\r\n";
			httpheader += "\r\n";

			return httpheader;
		}

		private String pop(InputStream is) throws IOException {
			StringBuffer stb = new StringBuffer();
			int i;
			byte[] buffer = new byte[16384];

			/*
			 * Parche: HTTP 1.1 no cierra la conexi�n, por lo que no podemos
			 * leer hasta eof. La soluci�n adoptada es que al final de la
			 * lectura, si los bytes leidos son menores que el tama�o del buffer
			 * y adem�s al final se encuentra la cadena "\r\n\r\n" se asume el
			 * final del mensaje. No funcionar�a este parche, si y solo si: - El
			 * tama�o del mensaje total coincide exactamente con el tama�o del
			 * buffer, pues no cortariamos el mensaje. - El tama�o leido es
			 * menor al del buffer y el final del paquete coincide con la cadena
			 * \r\n\r\n, pero no es el final del mensaje sino un trozo del
			 * mensaje total.
			 */
			i = is.read(buffer);
			stb.append(new String(buffer, 0, i));
			new String(stb);

			boolean condicion = false;
			/*
			 * try { while ((i = is.read(buffer)) != -1 && !condicion) {
			 * stb.append(new String(buffer, 0, i)); char[] temp = new char[4];
			 * // Array temporal, en busca // de final de mensaje.
			 * stb.getChars(stb.length() - 4, stb.length(), temp, 0);
			 * 
			 * if (temp[0] == '\r' && temp[1] == '\n' && temp[2] == '\r' &&
			 * temp[3] == '\n') { condicion = true; } if(condicion){ // ja tenim
			 * les capçaleres, ara necessitem el cos BufferedReader reader = new
			 * BufferedReader(new StringReader(new String(stb))); boolean stop =
			 * false; String line = ""; while (!stop) { line =
			 * reader.readLine(); if (line.indexOf("Content-Length:") != -1) {
			 * stop = true; } } String str_size =
			 * line.substring(line.indexOf(':')); int size =
			 * Integer.parseInt(str_size); int j; condicion = false; while ((j =
			 * is.read(buffer)) != -1 && !condicion) { stb.append(new
			 * String(buffer, 0, j)); if(stb.length() >= size) condicion = true;
			 * if (temp[0] == '\r' && temp[1] == '\n' && temp[2] == '\r' &&
			 * temp[3] == '\n') { condicion = true; } } } } } catch (Exception
			 * e) { System.out.println(e.getMessage() + " - " +
			 * e.getStackTrace()); }
			 */
			return new String(stb);
		}
	}

	public HttpInterface()
	{
		http_port = Integer.parseInt(configuration.getHttpInterfacepPort());
	}
	
	public HttpInterface(int http_port)
	{
		HttpInterface.http_port = http_port;
	}
	public void execute() {
		try {

			ServerSocket skServidor = new ServerSocket(http_port);
			System.out.println("HTTPInterface service started. Listening port " + http_port);
			DOMConfigurator.configure("configuration/loggin.xml");
			AgentsConnection.connect();

			while (true) {
				Socket skCliente = skServidor.accept(); // Crea objeto
				this.petitions++;
				ServerAgent serverAgent = new ServerAgent(new AgentID(
						"interfaceAgent" + this.petitions), skCliente);
				serverAgent.start();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}