package es.upv.dsic.gti_ia.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;

/**
 * This agent routes messages from inside the platform to outside the platform.
 * It converts ACLMessages into ACLJadeMessages
 * @author Sergio Pajares
 *
 */
public class BridgeAgentInOut extends SingleAgent {
	//public BaseAgent bd;
	public ACLMessage ACLsms;
	String envelope;
	String message;
	private DatagramSocket socket;
	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public BridgeAgentInOut(AgentID aid) throws Exception

	{
		super(aid);
		// New DatagramSocket object to send and receive package
		try {

			socket = new DatagramSocket();
		}

		// Capturing possible problem
		catch (SocketException excepcionSocket) {
			excepcionSocket.printStackTrace();
			logger
					.error("Error creating DatagramSocket instance, see more in BridgeAgentInOut constructor");
			System.exit(1);
		}

	}

	public String Generate_All(ACLMessage ACLsms) throws UnknownHostException {
		this.ACLsms = ACLsms;

		InetAddress hostDestiny = InetAddress
				.getByName(ACLsms.getReceiver().host);
		int portDestiny = Integer.valueOf(ACLsms.getReceiver().port);

		String content = Generate_Content();
		String header = Generate_Header(hostDestiny, portDestiny);

		return header + content;
	}

	public String Generate_Header(InetAddress hostDestiny, int portDestiny) {
		String boundary = "a36869921a26b9d812878a42b8fc2cd";
		String httpheader = "POST ";
		//tosend += "http://serpafer.dsic.upv.es:7778/acc";
		httpheader += this.ACLsms.getReceiver().addresses_all();
		httpheader += " HTTP/1.1\r\n";
		httpheader += "Cache-Control: no-cache\r\n";
		httpheader += "Mime-Version: 1.0\r\n";
		//tosend += "Host: \r\n";
		httpheader += "Host: ";
		httpheader += hostDestiny.getHostAddress() + ":" + portDestiny + "\r\n";

		httpheader += "Content-Type: multipart/mixed ; boundary=\"" + boundary
				+ "\"\r\n";
		//httpheader += "Content-Length: 1139\r\n";
		httpheader += "Content-Length: ";
		httpheader += 800;//to doString.valueOf(envelope.toCharArray().length + message.toCharArray().length) + "\r\n";
		//httpheader += boost::lexical_cast<string > (content.size()) + "\r\n";

		httpheader += "Connection: Keep-Alive\r\n";
		httpheader += "\r\n";

		return httpheader;
	}

	public String Generate_Content() {
		String message = BuildACLMessage();

		String boundary = "a36869921a26b9d812878a42b8fc2cd";

		String content = "This is not part of the MIME multipart encoded message.\r\n";
		content += "--" + boundary + "\r\n";
		content += "Content-Type: application/xml\r\n";
		content += "\r\n";

		content += Generate_Envelope();

		content += "\r\n" + "--" + boundary + "\r\n";
		content += "Content-Type: application/text\r\n";
		content += "\r\n";

		content += message;

		content += "\r\n--" + boundary + "--\r\n";
		content += "\r\n\r\n";

		return content;

	}

	public String Generate_Envelope() {
		String aux = "<?xml version=\"1.0\"?>\n" + "<envelope>"
				+ "<params index=\"1\">" + "<to>" + "<agent-identifier>"
				+ "<name>";
		aux += ACLsms.getReceiver().name_all();
		aux += "</name>" + "<addresses>" + "<url>";
		// agent destination
		aux += ACLsms.getReceiver().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>" + "</to>";

		aux += "<from>" + "<agent-identifier>" + "<name>";
		aux += ACLsms.getSender().name_all();
		aux += "</name>" + "<addresses>" + "<url>";
		aux += ACLsms.getSender().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>" + "</from>";

		aux += "<acl-representation>" + "fipa.acl.rep.string.std"
				+ "</acl-representation>" + "<payload-length>";
		// message length
		aux += ACLsms.getContent().length(); 
		aux += "</payload-length>" + "<date>";
		// timestamp
		Calendar c1 = Calendar.getInstance();
		String dia = Integer.toString(c1.get(Calendar.DATE));
		String mes = Integer.toString(c1.get(Calendar.MONTH));
		String annio = Integer.toString(c1.get(Calendar.YEAR));
		int hora = c1.get(Calendar.HOUR_OF_DAY);
		int minutos = c1.get(Calendar.MINUTE);
		int segundos = c1.get(Calendar.SECOND);

		//timestamp += "20090223Z194230825";

		aux += annio + "" + mes + "" + dia + "Z" + hora + "" + minutos + ""
				+ segundos;

		aux += "</date>" + "<intended-receiver>" + "<agent-identifier>"
				+ "<name>";
		aux += ACLsms.getReceiver().name_all();
		aux += "</name>" + "<addresses>" + "<url>";
		aux += ACLsms.getReceiver().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>"
				+ "</intended-receiver>" + "</params>" + "</envelope>";

		//this.envelope = aux;
		return aux;

	}

	/**
	 * You must first invoke this method, 
	 * it is responsible for issuing the redirect message, 
	 * the gateway address. 

	 * @return
	 */
	public String BuildACLMessage() {
		String acl;

		/*************************************************/
		/*********   performative   **********************/
		/*************************************************/

		String performative = "INFORM";

		acl = "(" + performative + "\r\n";

		/*************************************************/
		/***************   sender   **********************/
		/*************************************************/
		acl += " :sender ( agent-identifier :name \""
				+ ACLsms.getSender().name_all() + "\" :addresses (sequence ";

		/*		acl+= Constants.SERVERNAME +":"+Constants.HTTPLISTENINGPORT;	
		 ACLsms.getSender().host = Constants.SERVERNAME;
		 ACLsms.getSender().port = Constants.HTTPLISTENINGPORT;
		 ACLsms.getSender().protocol = "http";
		 */

		acl += ACLsms.getSender().addresses_all() + " ))\r\n";

		/*************************************************/
		/***************   receiver   ********************/
		/*************************************************/

		acl += " :receiver (set ( agent-identifier :name \""
				+ ACLsms.getReceiver().name_all() + "\" :addresses (sequence ";

		acl += ACLsms.getReceiver().addresses_all() + " )) )\r\n";

		/*************************************************/
		/***************   content   *********************/
		/*************************************************/
		/*
			// substitute every " for \" in content
			Utils::replacein(content, "\"", "\\\"");
		 */
		acl += " :content \"" + ACLsms.getContent() + "\"\r\n";

		/*************************************************/
		/***************   reply-with  *********************/
		/*************************************************/

		if (!ACLsms.getReplyWith().equals("")) {
			acl += " :reply-with " + ACLsms.getReplyWith() + "\r\n";
		} else {
			acl += " :reply-with " + "\"Warning: reply-with not found.\"\r\n";
		}
		/*************************************************/
		/***************   in-reply-to   *********************/
		/*************************************************/

		if (!ACLsms.getInReplyTo().equals("")) {
			acl += " :in-reply-to " + ACLsms.getInReplyTo() + "\r\n";
		} else {
			acl += " :in-reply-to " + "\"Warning: in-reply-to not found.\"\r\n";
		}

		/*************************************************/
		/***************   language  *********************/
		/*************************************************/

		if (!ACLsms.getLanguage().equals("")) {
			acl += " :language " + ACLsms.getLanguage() + "\r\n";
		} else {
			acl += " :language " + "\"Warning: language not found.\"\r\n";
		}

		/*************************************************/
		/***************   ontology  *********************/
		/*************************************************/

		if (!ACLsms.getOntology().equals("")) {
			acl += " :ontology \"" + ACLsms.getOntology() + "\"\r\n";
		} else {
			acl += " :ontology " + "\"Warning: ontology not found.\"\r\n";
		}

		/*************************************************/
		/***************   protocol  *********************/
		/*************************************************/

		if (!ACLsms.getProtocol().equals("")) {
			acl += " :protocol \"" + ACLsms.getProtocol() + "\"\r\n";
		} else {
			acl += " :protocol " + "\"Warning: protocol not found.\"\r\n";
		}

		/*************************************************/
		/*************   conversation ID  ****************/
		/*************************************************/

		if (!ACLsms.getConversationId().equals("")) {
			acl += " :conversation-id " + ACLsms.getConversationId() + "\r\n";
		} else {
			acl += " :conversation-id "
					+ "\"Warning: ConversationId not found.\"";
		}

		acl += " )";

		return acl;
	}

	/**
	 * Send out a DatagramPacket
	 * @param p
	 * @throws IOException
	 */
	private void SendOut(DatagramPacket p) throws IOException {
		PrintMessage("\nEnviando datos al agente exterior...");

		// crear paquete a enviar
		DatagramPacket enviarPaquete = new DatagramPacket(p.getData(), p
				.getLength(), p.getAddress(), p.getPort());

		logger.debug("Length in enviarPaqueteAExterior" + p.getData().length);

		logger.debug("Sending on enviarPaqueteAExterior: ");

		socket.send(enviarPaquete); // enviar el paquete
		PrintMessage("Paquete enviado\n");
	}

	/**
	 * Transform a ACLMessage message to a DatagramPacket and send out
	 */
	public boolean ACLMessageToDatagramPacket(ACLMessage ACLsms) {
		try {
			String message = Generate_All(ACLsms);
			byte datos[] = message.getBytes();

			// crear enviarPaquete
			DatagramPacket enviarPaquete = new DatagramPacket(datos,
					datos.length, InetAddress
							.getByName(ACLsms.getReceiver().host), Integer
							.valueOf(ACLsms.getReceiver().port));

			logger.debug("LONGITUD!!!" + datos.length);
			SendOut(enviarPaquete);

			return true;
		} catch (IOException e) {
			return false;
		}

	}

	/**
	 * @param message
	 */
	private void PrintMessage(final String message) {
		logger.info(message);
	}


/**
 * Waiting to new package
 * Redirecting packets to another platform 
 */
	public void execute() {
		while (true) {

			logger.info("I'm BridgeAgentInOut, waiting request....");
			ACLMessage mensaje;
			try {

				mensaje = receiveACLMessage();
				logger.info("Message was received in BridgeAgentInOut: "
						+ mensaje.getContent());

				/*
				 * In the next version, here for each request will create 
				 * a new execution thread
				 */
				ACLMessageToDatagramPacket(mensaje); // Send packacge to out

			} catch (Exception e) {
			}

			/* 
			 // mostrar la informacion del paquete recibido 
			 mostrarMensaje( "\nPaquete recibido:" + 
			 "\nDel host: " + recibirPaquete.getAddress() + 
			 "\nPuerto del host: " + recibirPaquete.getPort() + 
			 "\nLongitud: " + recibirPaquete.getLength() + 
			 "\nContenido:\n\t" + new String( recibirPaquete.getData(), 
			 0, recibirPaquete.getLength() ) );
			 */

		}

	}
/**
 * This is a test, to prove everything
 * @param args
 * @throws UnknownHostException
 */
	public static void main(String args[]) throws UnknownHostException {
		//Connection con = new Connection();
		//  con.connect("rilpefo.dsic.upv.es", 5672, "test", "guest", "guest",false);
		//	AgentePasarela agente2 = new AgentePasarela(new AgentID("agentepasarela", "http", "localhost","8080"),con);

		AgentID receiver = new AgentID();
		receiver.protocol = "http";
		receiver.name = "agentereceiver";
		receiver.host = "localhost";
		receiver.port = "8080";

		AgentID sender = new AgentID();
		sender.name = "agentesender";
		sender.protocol = "http";
		sender.host = "localhost";
		sender.port = "8000";

		//		AgentID pasarela = new AgentID();
		receiver.protocol = "http";
		receiver.name = "agentepasarela";
		receiver.host = "localhost";
		sender.port = "8080";

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);
		msg.setSender(sender);
		msg.setLanguage("ACL");
		msg.setContent("Hola, Holaaa");
		//	String todo = new AgentePasarela(pasarela,con).generate_all(msg);

		//		InetAddress hostDestiny = InetAddress.getByName( msg.getReceiver().host );
		//		int portDestiny = Integer.valueOf(msg.getReceiver().port);
		//	logger.debug(hostDestiny.getHostAddress()+"---"+portDestiny);

		String boundary = "a36869921a26b9d812878a42b8fc2cd";
		String httpheader = "POST ";
		//tosend += "http://serpafer.dsic.upv.es:7778/acc";
		httpheader += msg.getReceiver().addresses_all();
		httpheader += " HTTP/1.1\r\n";
		httpheader += "Cache-Control: no-cache\r\n";
		httpheader += "Mime-Version: 1.0\r\n";
		//tosend += "Host: serpafer.dsic.upv.es:7778\r\n";
		httpheader += "Host: ";
		httpheader += "localhost" + ":" + 8080 + "\r\n";

		httpheader += "Content-Type: multipart/mixed ; boundary=\"" + boundary
				+ "\"\r\n";
		//httpheader += "Content-Length: 1139\r\n";
		httpheader += "Content-Length: ";
		httpheader += "3333" + "\r\n";
		//httpheader += boost::lexical_cast<string > (content.size()) + "\r\n";

		httpheader += "Connection: Keep-Alive\r\n";
		httpheader += "\r\n";

		//	logger.debug(httpheader);

		String content = "This is not part of the MIME multipart encoded message.\r\n";
		content += "--" + boundary + "\r\n";
		content += "Content-Type: application/xml\r\n";
		content += "\r\n";

		String aux = "<?xml version=\"1.0\"?>\n" + "<envelope>"
				+ "<params index=\"1\">" + "<to>" + "<agent-identifier>"
				+ "<name>";
		aux += msg.getReceiver().name_all();
		aux += "</name>" + "<addresses>" + "<url>";
		// direcciÃ³n del agente
		aux += msg.getReceiver().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>" + "</to>";

		aux += "<from>" + "<agent-identifier>" + "<name>";
		aux += msg.getSender().name_all();
		aux += "</name>" + "<addresses>" + "<url>";
		aux += msg.getSender().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>" + "</from>";

		aux += "<acl-representation>" + "fipa.acl.rep.string.std"
				+ "</acl-representation>" + "<payload-length>";
		// tamaÃ±o del mensaje
		aux += msg.getContent().length(); //¿?
		aux += "</payload-length>" + "<date>";
		// timestamp
		Calendar c1 = Calendar.getInstance();
		String dia = Integer.toString(c1.get(Calendar.DATE));
		String mes = Integer.toString(c1.get(Calendar.MONTH));
		String annio = Integer.toString(c1.get(Calendar.YEAR));
		int hora = c1.get(Calendar.HOUR_OF_DAY);
		int minutos = c1.get(Calendar.MINUTE);
		int segundos = c1.get(Calendar.SECOND);

		//timestamp += "20090223Z194230825";

		aux += annio + "" + mes + "" + dia + "Z" + hora + "" + minutos + ""
				+ segundos;

		aux += "</date>" + "<intended-receiver>" + "<agent-identifier>"
				+ "<name>";
		aux += msg.getReceiver().name_all();
		aux += "</name>" + "<addresses>" + "<url>";
		aux += msg.getReceiver().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>"
				+ "</intended-receiver>" + "</params>" + "</envelope>";

		content += aux;

		content += "\r\n" + "--" + boundary + "\r\n";
		content += "Content-Type: application/text\r\n";
		content += "\r\n";

		//-----------------
		String acl;

		/*************************************************/
		/*********   performative   **********************/
		/*************************************************/

		String performative = "INFORM";

		acl = "(" + performative + "\r\n";

		/*************************************************/
		/***************   sender   **********************/
		/*************************************************/
		acl += " :sender ( agent-identifier :name \""
				+ msg.getSender().name_all() + "\" :addresses (sequence ";

		//		acl+= Constants.SERVERNAME +":"+Constants.HTTPLISTENINGPORT;
		//Si hacemos esto perdemos la dirección del agente origen no¿?¿?		
		//		ACLsms.getSender().host = Constants.SERVERNAME;
		//		ACLsms.getSender().port = Constants.HTTPLISTENINGPORT;
		//		ACLsms.getSender().protocol = "http";

		acl += msg.getSender().addresses_all() + " ))\r\n";

		/*************************************************/
		/***************   receiver   ********************/
		/*************************************************/

		acl += " :receiver (set ( agent-identifier :name \""
				+ msg.getReceiver().name_all() + "\" :addresses (sequence ";

		acl += msg.getReceiver().addresses_all() + " )) )\r\n";

		/*************************************************/
		/***************   content   *********************/
		/*************************************************/
		/*
			// substitute every " for \" in content
			Utils::replacein(content, "\"", "\\\"");
		 */
		acl += " :content \"" + msg.getContent() + "\"\r\n";

		/*************************************************/
		/***************   reply-with  *********************/
		/*************************************************/

		if (!msg.getReplyWith().equals("")) {
			acl += " :reply-with " + msg.getReplyWith() + "\r\n";
		} else {
			acl += " :reply-with " + "\"Warning: reply-with not found.\"\r\n";
		}
		/*************************************************/
		/***************   in-reply-to   *********************/
		/*************************************************/

		if (!msg.getInReplyTo().equals("")) {
			acl += " :in-reply-to " + msg.getInReplyTo() + "\r\n";
		} else {
			acl += " :in-reply-to " + "\"Warning: in-reply-to not found.\"\r\n";
		}

		/*************************************************/
		/***************   language  *********************/
		/*************************************************/

		if (!msg.getLanguage().equals("")) {
			acl += " :language " + msg.getLanguage() + "\r\n";
		} else {
			acl += " :language " + "\"Warning: language not found.\"\r\n";
		}

		/*************************************************/
		/***************   ontology  *********************/
		/*************************************************/

		if (!msg.getOntology().equals("")) {
			acl += " :ontology \"" + msg.getOntology() + "\"\r\n";
		} else {
			acl += " :ontology " + "\"Warning: ontology not found.\"\r\n";
		}

		/*************************************************/
		/***************   protocol  *********************/
		/*************************************************/

		if (!msg.getProtocol().equals("")) {
			acl += " :protocol \"" + msg.getProtocol() + "\"\r\n";
		} else {
			acl += " :protocol " + "\"Warning: protocol not found.\"\r\n";
		}

		/*************************************************/
		/*************   conversation ID  ****************/
		/*************************************************/

		if (!msg.getConversationId().equals("")) {
			acl += " :conversation-id " + msg.getConversationId() + "\r\n";
		} else {
			acl += " :conversation-id "
					+ "\"Warning: ConversationId not found.\"";
		}

		acl += " )";

		//-----------------

		content += acl;

		content += "\r\n--" + boundary + "--\r\n";
		content += "\r\n\r\n";

		logger.debug(httpheader + content);
	}
}
