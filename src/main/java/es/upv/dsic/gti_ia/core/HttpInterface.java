package es.upv.dsic.gti_ia.core;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.SingleAgent;
import es.upv.dsic.gti_ia.organization.Configuration;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.log4j.xml.DOMConfigurator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * 
 * @author ricard
 * @author Pedro Perez Sanchez
 */
public class HttpInterface {

	static int http_port;
	private static ServerAgent interfaceAgent;
	Configuration configuration = Configuration.getConfiguration();
	
	/**
	 * Gets the HTTP port where agent is waiting
	 * @return http port
	 */
	public static int getHttp_port() {
		return http_port;
	}

	/**
	 * Gets the ServerAgent that acts as interface agent for the HTTP interface
	 * @return interface agent
	 */
	public static ServerAgent getInterfaceAgent() {
		return interfaceAgent;
	}

	private static class ServerAgent extends BaseAgent {
		
		private static class JSONMessage {
			public String agent_name;
			public String conversation_id;
			public String content;

			public static JSONMessage fromString(String jsonString) {
				XStream xstream = new XStream(new JettisonMappedXmlDriver());
				xstream.alias("jsonObject", JSONMessage.class);
				JSONMessage jsonMessage = (JSONMessage)xstream.fromXML(jsonString);
				jsonMessage.content = jsonString.substring(14, jsonString.length()-1); // extracting X from {"jsonObject":X}
				return jsonMessage;
			}
			
			public String toString() {
				return "{\"jsonObject\":" + content + "}";
			}
		}

		private BlockingQueue<Socket> sockets; // queue of sockets to process
		private Map<String, Socket> socketOfConversation; // stores the socket to respond, given the conversation ID

		/**
		 * Creates a new ServerAgent
		 * 
		 * @param agent ID
		 */
		public ServerAgent(AgentID aid) throws Exception {
			super(aid);
			sockets = new LinkedBlockingQueue<Socket>();
			socketOfConversation = new HashMap<String, Socket>();
		}

		/**
		 * Adds a socket to the list of sockets to process by the agent
		 * 
		 * @param socket to add
		 */
		public void addSocket(Socket s) throws Exception {
			sockets.add(s);
		}

		public void execute() {
			Socket socket; // socket of the HTTP request that is being processed
			InputStream is; // InputStream of socket
			OutputStream os; // OutputStream of socket
			String content; // body of the HTTP request received
			JSONMessage jsonMessage; // content converted to a json object
			ACLMessage request; // content converted to an ACL Message
			byte[] response; // HTTP response to be sent to the client
			while(true) {
				try {
					socket = sockets.take();
					is = socket.getInputStream();
					os = socket.getOutputStream();
					content = this.getHttpBody(is);
					logger.info("InterfaceAgent: HTTP request body received: "+content);
					jsonMessage = JSONMessage.fromString("{\"jsonObject\":"+content+"}");

					// Looking for possible errors
					if(jsonMessage.agent_name == null || jsonMessage.conversation_id == null || jsonMessage.agent_name == "" || jsonMessage.conversation_id == "") { // malformed query
						response = httpResponse(400, "Wrong request format. Read Magentix manual for more information.");
						os.write(response);
						socket.close();
						logger.error("InterfaceAgent: The received request was malformed. Response sent: 400 Bad Request.");
						continue;
					}
					if(socketOfConversation.containsKey(jsonMessage.conversation_id)) { // http interface already processing that request
						response = httpResponse(403, "Magentix is already processing a request with that conversation id.");
						os.write(response);
						socket.close();
						logger.error("InterfaceAgent: Receiving a request with a conversation id that is already being processed. Response sent: 403 Forbidden.");
						continue;
					}
					try { // checking if the target agent exists
						BaseAgent auxAgent = new BaseAgent(new AgentID(jsonMessage.agent_name)); // if this instruction does not throw an Exception, the agent did not exist
						auxAgent.start(); // it is an empty agent, it will die immediately
						response = httpResponse(404, "There is not any agent with that name.");
						os.write(response);
						socket.close();
						logger.error("InterfaceAgent: Receiving a request with an agent name that doesn't exist. Response sent: 404 Not Found.");
						continue;
					} catch(Exception e) {} // the target agent exists (correct situation)
					
					// Sending the message to the target agent
					logger.info("InterfaceAgent: Message to send: Agent name: "+jsonMessage.agent_name+" conversation id: "+jsonMessage.conversation_id);
					request = new ACLMessage(ACLMessage.REQUEST);
					request.setProtocol("web");
					request.setReceiver(new AgentID(jsonMessage.agent_name));
					request.setSender(this.getAid());
					request.setConversationId(jsonMessage.conversation_id);
					request.setContent(jsonMessage.toString());
					socketOfConversation.put(jsonMessage.conversation_id, socket);
					this.send(request);
				} catch (Exception ex) {
					Logger.getLogger(HttpInterface.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		
		public void onMessage(ACLMessage msg) {
			logger.info("InterfaceAgent: HTTP Response to send: "+msg.getContent());
			String convId = msg.getConversationId();
			Socket socket = socketOfConversation.get(convId);
			if(socket == null) {
				logger.error("InterfaceAgent: Receiving unknown conversation id from the target agent. Can't give a response!");
				return;
			}
			socketOfConversation.remove(convId);
			try {
				OutputStream os = socket.getOutputStream();
				byte[] response = httpResponse(200, msg.getContent());
				os.write(response);
				socket.close();
			} catch(IOException ex) {
				Logger.getLogger(HttpInterface.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		private String getHttpBody(InputStream is) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String header;
			while ((header = reader.readLine()).indexOf("Content-Length:") != 0); // header = Content-Length HTTP header
			Matcher intInHeader = Pattern.compile("\\d+").matcher(header);
			intInHeader.find(); // first int found in header
			int contentLength = Integer.parseInt(intInHeader.group()); // contentLength = value specified in the header
			char[] buffer = new char[contentLength];
			while (reader.readLine().length() > 0); // this reads until the empty line that indicates the beginning of the body
			reader.read(buffer, 0, contentLength); // reading contentLength bytes
			return new String(buffer); // returning the http request body
		}

		private byte[] httpResponse(int code, String content) {
			String statusMsg[] = new String[600];
			statusMsg[200] = "OK";
			statusMsg[400] = "Bad Request";
			statusMsg[403] = "Forbidden";
			statusMsg[404] = "Not Found";
			statusMsg[500] = "Internal Server Error";
			// Test, ending with \r\n. If it doesn't work, remove \r
			return ("HTTP/1.1 "+code+" "+statusMsg[code]+"\r\n"
			+ "Server:	Apache/2.2.14 (Ubuntu)\r\n"
			+ "X-Powered-By:	PHP/5.3.2-1ubuntu4.9\r\n"
			+ "Vary:	Accept-Encoding\r\n"
			+ "Content-Encoding:	gzip\r\n"
			+ "Content-Length:	"+content.getBytes().length+"\r\n"
			+ "Connection:	close\r\n"
			+ "Content-Type:	text/html\n\n"
			+ content).getBytes();
		}
	}

	/**
	 * Creates a new HttpInterface using the default port specified in the configuration file
	 */
	public HttpInterface() {
		http_port = configuration.getHttpInterfacepPort();
	}
	
	/**
	 * Creates a new HttpInterface
	 * 
	 * @param http port
	 */
	public HttpInterface(int http_port) {
		HttpInterface.http_port = http_port;
	}

	public void execute() {
		try {
			ServerSocket skServidor = new ServerSocket(http_port);
			System.out.println("HTTPInterface service started. Listening on port " + http_port);
			DOMConfigurator.configure("configuration/loggin.xml");
			AgentsConnection.connect();
			interfaceAgent = new ServerAgent(new AgentID("interfaceAgent"));
			interfaceAgent.start();

			while (true) {
				Socket skCliente = skServidor.accept();
				try {
					interfaceAgent.addSocket(skCliente);
				}
				catch(Exception ex) {
					Logger.getLogger(HttpInterface.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}