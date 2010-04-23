package es.upv.dsic.gti_ia.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import es.upv.dsic.gti_ia.architecture.Monitor;

/**
 * This agent routes messages from inside the platform to outside the platform.
 * 
 * @author Ricard Lopez Fogues
 */

public class BridgeAgentOutIn extends BaseAgent {

	//private DatagramSocket socket;
	private ServerSocket socket;
	private Socket s;
	/**
	 * BridgeAgentOutIn runs on 8081 port
	 */
	public static int http_port = 8081;
	
	private boolean finalized = false;

	/**
	 * Creates a new BrideAgentOutIn
	 * 
	 * @param aid
	 * @param connection
	 * @throws Exception
	 */
	public BridgeAgentOutIn(AgentID aid) throws Exception {
		super(aid);

		// crear objeto DatagramSocket para enviar y recibir paquetes
		try {
			//socket = new DatagramSocket(5000);
			socket = new ServerSocket(http_port);
		}

		// procesar los problemas que pueden ocurrir al crear el objeto
		// DatagramSocket
		catch (SocketException excepcionSocket) {
			excepcionSocket.printStackTrace();
			System.exit(1);
		}

	}

	public void execute() {
		while (!finalized) {
			// Escuchar por protocolo http y enviar a quien corresponda
			// recibir paquete, mostrar su contenido
			try {

							
				
				InputStream is;
				

			

				logger.info("BridgeAgentOutIn waiting receive external FIPA-Messages");
				 s = socket.accept(); //Socket Cliente

					//	Monitor m = new Monitor();
					//	m.waiting(10);
	
							
				is = s.getInputStream();
				OutputStream os = s.getOutputStream();
				
		/*		 try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		*/
			

				StringBuffer stb = new StringBuffer();
				int i;
				byte[] buffer = new byte[16384];

			/*	while ((is.available() > 0)
						&& (i = is.read(buffer)) != -1) {
					stb.append(new String(buffer, 0, i));
				}
			*/	
			
			boolean condicion = false;
			
			
			/*
			 * Parche: HTTP 1.1 no cierra la conexión, por lo que no podemos leer hasta eof.
			 * La solución adoptada es que al final de la lectura, si los bytes leidos son menores
			 * que el tamaño del buffer y además al final se encuentra la cadena "\r\n\r\n" se asume
			 * el final del mensaje.
			 * 
			 * 
			 * 
			 * No funcionaría este parche, si y solo si: 
			 * 
			 * - El tamaño del mensaje total coincide exactamente con el tamaño del buffer, pues no cortariamos
			 * el mensaje.
			 * 
			 * - El tamaño leido es menor al del buffer y el final del paquete coincide con la cadena \r\n\r\n, pero
			 * no es el final del mensaje sino un trozo del mensaje total.
			 * 
			 */	
			
			try{
				while ((i = is.read(buffer)) != -1) 
				{
					stb.append(new String(buffer, 0, i));
					
					char[] temp = new char[4]; // Array temporal, en busca de final de mensaje.
					stb.getChars(stb.length()-4, stb.length(), temp, 0);
										
					
					if (temp[0] == '\r' && temp[1] == '\n' && temp[2] == '\r' && temp[3] == '\n')
					{
						condicion = true;
					}
					
					if(i < buffer.length && condicion)
					{
						break;
					}
				
				}
			}catch(Exception e)
			{
				logger.error(e.getMessage() +" - "+e.getStackTrace());
			}
		
				
				String texto = new String(stb);

				

				httpToACL hilo = new httpToACL(stringToInputStream(texto));
				
				
				String OK = "HTTP/1.0 200 OK\r\n\r\n";
				byte a [] = OK.getBytes();
				
				os.write(a);
				
				//Cerrar
				os.close();
				is.close();
				
				hilo.run();
				
				s.close();
				
			
				

				/* Esto era con UDP */
				
				//System.out.println("Received on BridgeAgentOutIn: "
				// + new String(recibirPaquete.getData()));
				//
				// // mostrar la informaciÃ³n del paquete recibido
				// System.out.println("\nPackage received:"
				// + "\nfrom host: "
				// + recibirPaquete.getAddress()
				// + "\nPort of the host: "
				// + recibirPaquete.getPort()
				// + "\nLenght: "
				// + recibirPaquete.getLength()
				// + "\nContent:\n\t"
				// + new String(recibirPaquete.getData(), 0,
				// recibirPaquete.getLength()));
				//
				// // creamos nuevo hilo encargado de enviar el mensaje
				// // httpToACL hilo = new httpToACL(stringToInputStream(new
				// String(
				// // recibirPaquete.getData())));
				// // hilo.run();
				 }

			

			// procesar los problemas que pueden ocurrir al manipular el paquete
			catch (IOException excepcionES) {
		

					if (excepcionES.getClass().toString().equals("class java.net.SocketException"))
						System.out.println("BridgeAgentOutIn Socket Closed");
					else
					{
					System.err.println("Error on BridgeAgentOutIn, "
							+ excepcionES.toString() + "\n");
					excepcionES.printStackTrace();
					}
			
			}
			
		}
	}

	public class httpToACL extends Thread {
		InputStream httpmessage;

		public httpToACL(InputStream message) {
			httpmessage = message;
		}

		public void run() {
			ACLMessage msg = new ACLMessage(-1);
			AgentID sender = new AgentID();
			AgentID receiver = new AgentID();
			// parseamos las cabeceras html, y obtenemos las diferentes partes
			// del mensaje
			BufferedInputStream bis = null;
			BufferedReader dis = null;

			try {
				// Here BufferedInputStream is added for fast reading.
				bis = new BufferedInputStream(httpmessage);
				dis = new BufferedReader(new InputStreamReader(bis));

				// leemos las cuatro primeras lineas que no nos interesan y nos
				// quedamos con la quinta
				String cadena = dis.readLine();

				int cont = 0;
				while (cadena != null && cont < 4) {
					// this statement reads the line from the file and print it
					// to
					// the console.
					//System.out.println(cadena);
					cadena = dis.readLine();
					cont++;
				}
				
				if (cadena==null)
				{
					System.err.println("Error receiving JADE message");
					
				}
				else
				{
				// buscamos el boundary
				int indexboundary = cadena.indexOf("boundary=\"");
				String boundary = cadena.substring(indexboundary + 10, cadena
						.indexOf("\"", indexboundary + 10));
				// leemos hasta encontrar el boundary, donde empieza el envelope
				do
					cadena = dis.readLine();
				while (cadena != null && cadena.indexOf(boundary) == -1);
				// la primera linea nos indica el content type
				cadena = dis.readLine();
				// linea en blanco
				cadena = dis.readLine();
				// version xml
				cadena = dis.readLine();
				// envelope
				cadena = dis.readLine();
				//logger.debug(cadena);
				// parseamos contenido XML
				Xml envelope = new Xml(stringToInputStream(cadena), "envelope");

				Xml params = envelope.child("params");
				logger.debug("index: " + params.integer("index"));

				int index = 0;
				// Agente destino
				logger.debug("Agent Details destination");
				Xml to = params.child("to");
				Xml agent_indentifier = to.child("agent-identifier");

				int index2 = agent_indentifier.child("name").content().indexOf(
						'@');
				

				receiver.name = agent_indentifier.child("name").content()
						.substring(0, index2);
				logger.debug("Agent Name: " + receiver.name);
				
				Xml adresses = agent_indentifier.child("addresses");
				for (Xml url : adresses.children("url")) {
					index = url.content().indexOf(':');
					receiver.protocol = url.content().substring(0, index);
					receiver.host = url.content().substring(index + 3,
							url.content().indexOf(":", index + 1));
					index = url.content().indexOf(":", index + 1);
					receiver.port = url.content().substring(index + 1);
					logger.debug("Adress: " + receiver.toString());
				}

				msg.setReceiver(receiver);

				// Agente remitente
				logger.debug("Details sender agent");
				Xml from = params.child("from");
				agent_indentifier = from.child("agent-identifier");

				//index2 = agent_indentifier.child("name").content().indexOf('@');
				index2 = agent_indentifier.child("name").content().length();
				
				sender.name = agent_indentifier.child("name").content()
						.substring(0, index2);
				
				//String nombreJade = 
				//sender.name = agent_indentifier.child("name").content().substring(0);
				logger.debug("Agent name: " + sender.name);
				adresses = agent_indentifier.child("addresses");

				for (Xml url : adresses.children("url")) {
					index = url.content().indexOf(':');
					sender.protocol = url.content().substring(0, index);
					
					sender.host = url.content().substring(index + 3,
							url.content().indexOf(":", index + 1));
					index = url.content().indexOf(":", index + 1);
					
					sender.port = url.content().substring(index + 1);
					logger.debug("Sender: " + sender.toString());
				}
				msg.setSender(sender);

				
	
				// volvemos a buscar el boundary
				do
					cadena = dis.readLine();
				while (cadena != null && cadena.indexOf(boundary) == -1);
				// tipo contenido
				cadena = dis.readLine();
				// linea en blanco
				cadena = dis.readLine();
				// performativa
				cadena = dis.readLine();
				
				String performative = cadena.substring(1).trim();
				msg.setPerformative(performative);
				logger.debug("Performative: " + msg.getPerformative());
				// agente remitente, ya tenemos sus datos
				cadena = dis.readLine();
				// agente receptor, ya tenemos sus datos
				cadena = dis.readLine();
				// content
				cadena = dis.readLine();
	
				int indexcontent = cadena.indexOf('"');
				String content = "";
				boolean seguir = true;
				int pos;
				content = content + cadena.substring(indexcontent + 1);
				pos = cadena.length();
				while (cadena.charAt(pos - 1) == ' ')
					pos--;
				if (cadena.charAt(pos - 1) == '"'
						&& cadena.charAt(pos - 2) != '\\')
					seguir = false;
				while (seguir) {
					cadena = dis.readLine();
					content = content + cadena;
					pos = cadena.length();
					while (cadena.charAt(pos - 1) == ' ')
						pos--;
					if (cadena.charAt(pos - 1) == '"'
							&& cadena.charAt(pos - 2) != '\\')
						seguir = false;
				}
				content = content.substring(0, content.length() - 2);
		
				msg.setContent(content);
				logger.debug("content " + content);

				// language
		/*		String lang = "";
				cadena = dis.readLine();
				while (cadena.indexOf(":language") + 9 < 0 && cadena != null)
					cadena = dis.readLine();

				cadena = cadena.substring(cadena.indexOf(":language") + 9);

				int k = 0;
				while (cadena.charAt(k) == ' ')
					k++;

				while (cadena.charAt(k) != ' ') {
					lang = lang + cadena.charAt(k);
					k++;
				}
				msg.setLanguage(lang);

				// ontology
				String ontology = "";
				while (cadena.indexOf(":ontology") + 9 < 0 && cadena != null)
					cadena = dis.readLine();
				cadena = cadena.substring(cadena.indexOf(":ontology") + 9);

				k = 0;
				while (cadena.charAt(k) == ' ')
					k++;

				while (cadena.charAt(k) != ' ') {
					ontology = ontology + cadena.charAt(k);
					k++;
				}
				msg.setOntology(ontology);
				*/
			//protocol
				
				String protocol ="";
				int k = 0;
				boolean j = true;
				cadena = dis.readLine();
				if (cadena.indexOf(":protocol") != -1)
				{
				while(cadena.indexOf(":protocol") + 9 <0 && cadena !=null)
					cadena = dis.readLine();
				cadena = cadena.substring(cadena.indexOf(":protocol")+9);
				
				
				
				while (cadena.charAt(k) == ' ')
					k++;
				
				while (cadena.charAt(k) != ' ' && j) {
					protocol = protocol + cadena.charAt(k);
					k++;
					if (k == cadena.length())
					{
						k--;
						j = false;
					}
				}
				msg.setProtocol(protocol);
				}
				cadena = dis.readLine();
				//conversation_Id
				if (cadena.indexOf(":conversation-id") != -1)
				{
				String conversation_id ="";
				k = 0;
				while(cadena.indexOf(":conversation-id") + 16 <0 && cadena !=null)
					cadena = dis.readLine();
				cadena = cadena.substring(cadena.indexOf(":conversation-id")+16);
				
				
				
				while (cadena.charAt(k) == ' ')
					k++;

				j = true;
				while (cadena.charAt(k) != ' ' && j) {
					conversation_id = conversation_id + cadena.charAt(k);
					k++;
					if (k == cadena.length())
					{
						k--;
						j = false;
					}
				}
				msg.setConversationId(conversation_id);
				}
				
				msg.getReceiver().protocol = "qpid";
				
				send(msg);
				
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static InputStream stringToInputStream(String cadena) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(cadena.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return is;
	}
	
	public void exit()
	{
		try {
			
			if (s != null)
				s.close();
			
			
			socket.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.finalized = true;
		System.out.println("Bridge Agent Out In leave the system");
	}
}
