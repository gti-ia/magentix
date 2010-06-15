package es.upv.dsic.gti_ia.core;

/**
 * This agent routes messages from inside the platform to outside the platform.
 * *
 * 
 * @author Sergio Pajares
 * 
 */
public class BridgeAgentInOut extends BaseAgent {
	// public BaseAgent bd;
	public ACLMessage ACLsms;
	String envelope;
	String message;
	private boolean finalized = false;
	// private DatagramSocket socket;

	private Socket socket;
	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public BridgeAgentInOut(AgentID aid) throws Exception {
		super(aid);
	}

	private String Generate_All(ACLMessage ACLsms) throws UnknownHostException {

		String port = ACLsms.getReceiver().port;

		// Comprovem si el port es d'un agent Jade, tipus 7778/acc, ens quedem
		// només en la part numèrica
		if (ACLsms.getReceiver().port.indexOf('/') > -1)
			port = ACLsms.getReceiver().port.substring(0,
					ACLsms.getReceiver().port.indexOf('/'));

		ACLsms.getReceiver().port = port;

		this.ACLsms = ACLsms;

		InetAddress hostDestiny = InetAddress
				.getByName(ACLsms.getReceiver().host);
		int portDestiny = Integer.valueOf(port);

		String content = Generate_Content();
		String header = Generate_Header(hostDestiny, portDestiny);
		return header + content;

	}

	private String Generate_Header(InetAddress hostDestiny, int portDestiny) {
		String boundary = "a36869921a26b9d812878a42b8fc2cd";
		String httpheader = "POST ";
		// tosend += "http://serpafer.dsic.upv.es:7778/acc";
		httpheader += this.ACLsms.getReceiver().addresses_all();
		httpheader += " HTTP/1.1\r\n";
		httpheader += "Cache-Control: no-cache\r\n";
		httpheader += "Mime-Version: 1.0\r\n";
		// tosend += "Host: \r\n";
		httpheader += "Host: ";
		httpheader += hostDestiny.getHostAddress() + ":" + portDestiny + "\r\n";

		httpheader += "Content-Type: multipart/mixed ; boundary=\"" + boundary
				+ "\"\r\n";
		// httpheader += "Content-Length: 1139\r\n";
		httpheader += "Content-Length: ";
		httpheader += 800 + "\r\n";// to
		// doString.valueOf(envelope.toCharArray().length
		// + message.toCharArray().length) + "\r\n";
		// httpheader += boost::lexical_cast<string > (content.size()) + "\r\n";

		httpheader += "Connection: Keep-Alive\r\n";
		httpheader += "\r\n";

		return httpheader;
	}

	private String Generate_Content() {
		String message = BuildACLMessage();
		int length = message.getBytes().length;

		String boundary = "a36869921a26b9d812878a42b8fc2cd";

		String content = "This is not part of the MIME multipart encoded message.\r\n";
		content += "--" + boundary + "\r\n";
		content += "Content-Type: application/xml\r\n";
		content += "\r\n";

		content += Generate_Envelope(length);

		content += "\r\n" + "--" + boundary + "\r\n";
		content += "Content-Type: application/text\r\n";
		content += "\r\n";

		content += message;

		content += "\r\n--" + boundary + "--\r\n";
		content += "\r\n\r\n";

		return content;

	}

	private String Generate_Envelope(int messageLenght) {
		String aux = "<?xml version=\"1.0\"?>\n" + "<envelope>"
				+ "<params index=\"1\">" + "<to>" + "<agent-identifier>"
				+ "<name>";
		aux += ACLsms.getReceiver().name;
		aux += "</name>" + "<addresses>" + "<url>";
		// agent destination

		aux += ACLsms.getReceiver().addresses_all();

		aux += "</url>" + "</addresses>" + "</agent-identifier>" + "</to>";

		aux += "<from>" + "<agent-identifier>" + "<name>";
		aux += ACLsms.getSender().name_all();
		aux += "</name>" + "<addresses>" + "<url>";

		String s = "";
		try {
			s = "http://" + InetAddress.getLocalHost().getCanonicalHostName()
					+ ":" + BridgeAgentOutIn.http_port;
		} catch (UnknownHostException e1) {
			logger.debug("Error BridgeAgentInOut InetAddress.getLocalHost");
			e1.printStackTrace();
		}
		aux += s;

		aux += "</url>" + "</addresses>" + "</agent-identifier>" + "</from>";

		aux += "<acl-representation>" + "fipa.acl.rep.string.std"
				+ "</acl-representation>" + "<payload-length>";
		// message length
		aux += messageLenght;
		aux += "</payload-length>" + "<date>";
		// timestamp
		Calendar c1 = Calendar.getInstance();
		String dia = String.format("%02d", (c1.get(Calendar.DATE)));
		String mes = String.format("%02d", (c1.get(Calendar.MONTH)));
		String annio = Integer.toString(c1.get(Calendar.YEAR));
		String hora = String.format("%02d", c1.get(Calendar.HOUR_OF_DAY));
		String minutos = String.format("%02d", c1.get(Calendar.MINUTE));
		String segundos = String.format("%02d", c1.get(Calendar.SECOND));
		String msegundos = String.format("%03d", c1.get(Calendar.MILLISECOND));

		// timestamp += "20090223Z194230825";

		aux += annio + "" + mes + "" + dia + "Z" + hora + "" + minutos + ""
				+ segundos + msegundos;

		aux += "</date>" + "<intended-receiver>" + "<agent-identifier>"
				+ "<name>";
		aux += ACLsms.getReceiver().name;
		aux += "</name>" + "<addresses>" + "<url>";
		aux += ACLsms.getReceiver().addresses_all();
		aux += "</url>" + "</addresses>" + "</agent-identifier>"
				+ "</intended-receiver>" + "</params>" + "</envelope>";

		// this.envelope = aux;
		return aux;

	}

	/**
	 * You must first invoke this method, it is responsible for issuing the
	 * redirect message, the gateway address.
	 * 
	 * @return
	 */
	private String BuildACLMessage() {
		String acl;

		/** ********************************************** */
		/** ******* performative ********************* */
		/** ********************************************** */

		String performative = ACLsms.getPerformative();

		acl = "(" + performative + "\r\n";

		/** ********************************************** */
		/** ************* sender ********************* */
		/** ********************************************** */

		String sender = "";
		try {
			sender = ACLsms.getSender().name + "@"
					+ InetAddress.getLocalHost().getHostName() + ":"
					+ BridgeAgentOutIn.http_port;
		} catch (UnknownHostException e1) {
			logger.debug("Error BridgeAgentInOut InetAddress.getLocalHost");
			e1.printStackTrace();
		}

		acl += " :sender ( agent-identifier :name \"" + sender
				+ "\" :addresses (sequence ";

		/*
		 * acl+= Constants.SERVERNAME +":"+Constants.HTTPLISTENINGPORT;
		 * ACLsms.getSender().host = Constants.SERVERNAME;
		 * ACLsms.getSender().port = Constants.HTTPLISTENINGPORT;
		 * ACLsms.getSender().protocol = "http";
		 */

		String host = "";
		try {
			host = "http://"
					+ InetAddress.getLocalHost().getCanonicalHostName() + ":"
					+ BridgeAgentOutIn.http_port;
		} catch (UnknownHostException e) {
			logger
					.debug("Error BridgeAgentInOut getting InetAddress.getLocalHost");
			e.printStackTrace();
		}

		acl += host + " ))\r\n";

		/** ********************************************** */
		/** ************* receiver ******************* */
		/** ********************************************** */

		acl += " :receiver (set ( agent-identifier :name \""
				+ ACLsms.getReceiver().name + "\" :addresses (sequence ";

		acl += ACLsms.getReceiver().protocol + "://"
				+ ACLsms.getReceiver().host + ":" + ACLsms.getReceiver().port
				+ " )) )\r\n";

		/** ********************************************** */
		/** ************* content ******************** */
		/** ********************************************** */
		/*
		 * // substitute every " for \" in content Utils::replacein(content,
		 * "\"", "\\\"");
		 */
		acl += " :content \"" + ACLsms.getContent() + "\"\r\n";

		/** ********************************************** */
		/** ************* reply-with ******************** */
		/** ********************************************** */

		if (!ACLsms.getReplyWith().equals("")) {
			acl += " :reply-with " + ACLsms.getReplyWith() + "\r\n";
		} else {
			// acl += " :reply-with " +
			// "\"Warning: reply-with not found.\"\r\n";
		}
		/** ********************************************** */
		/** ************* in-reply-to ******************** */
		/** ********************************************** */

		if (!ACLsms.getInReplyTo().equals("")) {
			acl += " :in-reply-to " + ACLsms.getInReplyTo() + "\r\n";
		} else {
			// acl += " :in-reply-to " +
			// "\"Warning: in-reply-to not found.\"\r\n";
		}

		/** ********************************************** */
		/** ************* language ******************** */
		/** ********************************************** */

		if (!ACLsms.getLanguage().equals("")) {
			acl += " :language " + ACLsms.getLanguage() + "\r\n";
		} else {
			// acl += " :language " + "\"Warning: language not found.\"\r\n";
		}

		/** ********************************************** */
		/** ************* ontology ******************** */
		/** ********************************************** */

		if (!ACLsms.getOntology().equals("")) {
			acl += " :ontology \"" + ACLsms.getOntology() + "\"\r\n";
		} else {
			// acl += " :ontology " + "\"Warning: ontology not found.\"\r\n";
		}

		/** ********************************************** */
		/** ************* protocol ******************** */
		/** ********************************************** */

		if (!ACLsms.getProtocol().equals("")) {
			acl += " :protocol \"" + ACLsms.getProtocol() + "\"\r\n";
		} else {
			// acl += " :protocol " + "\"Warning: protocol not found.\"\r\n";
		}

		/** ********************************************** */
		/** *********** conversation ID *************** */
		/** ********************************************** */

		if (!ACLsms.getConversationId().equals("")) {
			acl += " :conversation-id " + ACLsms.getConversationId() + "\r\n";
		} else {
			// acl += " :conversation-id "
			// + "\"Warning: ConversationId not found.\"";
		}

		acl += " )";

		return acl;
	}

	/**
	 * Send out a DatagramPacket
	 * 
	 * @param p
	 * @throws IOException
	 */
	private void SendOut(DatagramPacket p) throws IOException {
		PrintMessage("\n Redirecting package to BridgeAgentOutIn external agent");

		DatagramPacket enviarPaquete = new DatagramPacket(p.getData(), p
				.getLength(), p.getAddress(), p.getPort());

		// logger.debug("Length in enviarPaqueteAExterior" +
		// p.getData().length);

		// logger.debug("Sending on enviarPaqueteAExterior: ");

		// socket.send(enviarPaquete);
		PrintMessage("The package was sent\n");
	}

	/**
	 * Transform a ACLMessage message to a DatagramPacket and send out
	 */
	private boolean ACLMessageToDatagramPacket(ACLMessage ACLsms) {
		try {

			if (ACLsms.getReceiver().host.indexOf("@") != -1)
				ACLsms.getReceiver().host = ACLsms.getReceiver().host
						.substring(ACLsms.getReceiver().host.indexOf("@") + 1,
								ACLsms.getReceiver().host.length());
			String message = Generate_All(ACLsms);

			// System.out.println(message);

			// System.out.println(message);
			byte datos[] = message.getBytes();

			InetAddress a = InetAddress.getByName(ACLsms.getReceiver().host);

			socket = new Socket(a, 7778);

			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			os.write(datos);

			// DatagramPacket(datos,
			// datos.length, InetAddress
			// .getByName(ACLsms.getReceiver().host), Integer
			// .valueOf(ACLsms.getReceiver().port));
			//
			//          
			// logger.debug("LONGITUD!!!" + datos.length);
			// SendOut();

			return true;
		} catch (IOException e) {
			logger.error("Exception in ACLMessageToDatagramPacket");
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
	 * We consider a special char '~' instead of '@'
	 */
	private void ReplaceSpecialChar(ACLMessage msg) {
		msg.getReceiver().name = msg.getReceiver().name.replace('~', '@');
	}

	/**
	 * Waits new package, and redirects packets to another platform
	 */
	public void execute() {
		while (!finalized) {

			ACLMessage mensaje;
			try {

				Thread.sleep(Long.MAX_VALUE);
				// mensaje = receiveACLMessage();
				// logger.info("Message was received in BridgeAgentInOut: "
				// + mensaje.getContent());

				/*
				 * In the next version, here for each request will create a new
				 * execution thread
				 */
				// ACLMessageToDatagramPacket(mensaje); // Send packacge to out
			} catch (Exception e) {
			}

			/*
			 * 
			 * "\nPaquete recibido:" + "\nDel host: " +
			 * recibirPaquete.getAddress() + "\nPuerto del host: " +
			 * recibirPaquete.getPort() + "\nLongitud: " +
			 * recibirPaquete.getLength() + "\nContenido:\n\t" + new String(
			 * recibirPaquete.getData(), 0, recibirPaquete.getLength() ) );
			 */

		}

	}

	public void onMessage(ACLMessage msg) {
		/**
		 * When a message arrives, its shows on screen
		 */
		logger.info("Mensaje received in " + this.getName()
				+ " agent, by onMessage: " + msg.getContent());

		try {

			ReplaceSpecialChar(msg);

			ACLMessageToDatagramPacket(msg);
			// System.out.println(message);
		} catch (Exception e) {

		}

	}

	public void finalize() {
		this.finalized = true;
		System.out.println("Bridge Agent In Out leave the system");
	}
}
