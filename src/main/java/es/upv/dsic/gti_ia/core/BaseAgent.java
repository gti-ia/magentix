package es.upv.dsic.gti_ia.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.ConnectionSettings;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageCreditUnit;
import org.apache.qpid.transport.MessageProperties;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Option;
import org.apache.qpid.transport.Session;
import org.apache.qpid.transport.SessionException;
import org.apache.qpid.transport.SessionListener;

import es.upv.dsic.gti_ia.secure.SecurityTools;

/**
 * @author Ricard Lopez Fogues
 * @author Sergio Pajares Ferrando
 * @author Joan Bellver Faus
 */
public class BaseAgent implements Runnable {

	/**
	 * The logger variable considers to print any event that occurs by the agent
	 */
	protected Logger logger = Logger.getLogger(BaseAgent.class);
	private es.upv.dsic.gti_ia.organization.Configuration c = es.upv.dsic.gti_ia.organization.Configuration
			.getConfiguration();
	// Variable para controlar cuando salta una excepción en el broker.
	private int sessionCommandsIn = 0;

	/**
	 * @uml.property name="aid"
	 * @uml.associationEnd
	 */
	private AgentID aid;
	/**
	 * @uml.property name="connection"
	 */
	private Connection connection;

	/**
	 * @uml.property name="session"
	 */
	protected Session session;
	/**
	 * @uml.property name="myThread"
	 */
	private Thread myThread;

	private class Listener implements SessionListener {
		public void opened(Session ssn) {
		}

		public void resumed(Session ssn) {
		}

		public void message(Session ssn, MessageTransfer xfr) {
			ACLMessage msg = null;
			try {
				msg = MessageTransfertoACLMessage(xfr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			onMessage(msg);
		}

		public void exception(Session ssn, SessionException exc) {
			exc.printStackTrace();
		}

		public void closed(Session ssn) {
		}

	}

	/**
	 * @uml.property name="listener"
	 * @uml.associationEnd
	 */
	private Listener listener;

	/**
	 * Creates a new agent in an open broker connection
	 * 
	 * @param aid
	 *            Agent identification for the new agent, it has to be unique on
	 *            the platform
	 * @param connection
	 *            Connection that the agent will use
	 * @throws Exception
	 *             If Agent ID already exists on the platform
	 */
	public BaseAgent(AgentID aid) throws Exception {

		// Si no estamos en modo seguro funcionara como siempre, es por tanto
		// transparente al programador.
		if (c.isSecureMode()) {

			SecurityTools st = SecurityTools.GetInstance();
			Properties propSecurity = new Properties();
			try {
				//Nuevo fichero para la configuración de datos para la seguridad.
				propSecurity.load(new FileInputStream("./configuration/securityUser.properties"));
			} catch (FileNotFoundException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
				e.printStackTrace();
			}

			//Vemos si el usuario ya posee algún certificado para ese agente. Se comprueba también la validez.
			//Este método es el encargado de crear todo el proceso de solicitud y creación de certificados para los 
			//agentes del usuario. Podemos encontrarlo en la clase SecurityTools del paquete secure.
			if (st.generateAllProcessCertificate(aid.name)) {

				

				
				connection = null;
				//El alias sera el mismo que el nombre del agente
				String certAlias = aid.name;
				
				// deberemos crear una conexion por cada agente del usuario.
				connection = new Connection();
				ConnectionSettings connectSettings = new ConnectionSettings();

				connectSettings.setHost(c.getqpidHost());
				connectSettings.setPort(c.getqpidPort());
				connectSettings.setVhost(c.getqpidVhost());
				connectSettings.setUsername(c.getqpidUser());
				connectSettings.setPassword(c.getqpidPassword());
				connectSettings.setUseSSL(c.getqpidSSL());
				connectSettings.setSaslMechs(c.getqpidsaslMechs());
				
				//Accedemos al fichero de configuración de seguridad del usuario.
				connectSettings.setKeyStorePassword(propSecurity
						.getProperty("KeyStorePassword"));
				
				connectSettings.setKeyStorePath(propSecurity
						.getProperty("KeyStorePath"));
				//Lo convertimos a minisculas para que no haya problemas
				connectSettings.setCertAlias(certAlias.toLowerCase());
				
				connectSettings.setTrustStorePassword(propSecurity
						.getProperty("TrustStorePassword"));
				connectSettings.setTrustStorePath(propSecurity
						.getProperty("TrustStorePath"));

				try {
					connection.connect(connectSettings);
				} catch (Exception e) {
					System.out.println("Error in connect: " + e);
				}
			}

			
		} else {
			if (AgentsConnection.connection == null) {
				logger
						.error("Before create a agent, the qpid broker connection is necesary");
				throw new Exception("Error doesn't work the broken connection");
			} else {
				this.connection = AgentsConnection.connection;
			}

		}
		//Esta parte es la misma que cuando no es modo seguro.
		this.session = createSession();
		if (this.existAgent(aid)) {
			session.close();
			throw new Exception("Agent ID already exists on the platform");
		} else {
			this.aid = aid;
			this.listener = new Listener();
			myThread = new Thread(this);
			createQueue();
			createBind();
			createSubscription();
		}

	}

	//Cuando el agente infringe alguna regla de seguridad, la política del broker es destruir la sesión 
	//del usuario, que no la conexión. Por tanto este método recarga la session creando una nueva.
	
	private void reloadSession() {

		this.session = this.createSession();
		this.createQueue();
		this.createBind();
		this.createSubscription();
		this.sessionCommandsIn = this.session.getCommandsIn();

	}

	/**
	 * Creates the exclusive session the agent will use
	 * 
	 * @return The new Session
	 */
	private Session createSession() {

		Session session = this.connection.createSession(0);

		return session;
	}

	/**
	 * Creates queue the agent will listen to for messages *
	 */
	private void createQueue() {
		this.session.queueDeclare(aid.name, null, null, Option.AUTO_DELETE);

	}

	/**
	 * Binds the exchange and the agent queue
	 */
	private void createBind() {
		// this.session.exchangeBind(aid.name, aid.name, null, null);
		this.session.exchangeBind(aid.name, "amq.direct", aid.name, null);

	}

	/**
	 * Creates the subscription through the agent listener will get the message
	 * from the queue
	 */
	private void createSubscription() {
		this.session.setSessionListener(this.listener);

		this.session.messageSubscribe(aid.name, "listener_destination",
				MessageAcceptMode.NONE, MessageAcquireMode.PRE_ACQUIRED, null,
				0, null);

		this.session.messageFlow("listener_destination",
				MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
		this.session.messageFlow("listener_destination",
				MessageCreditUnit.MESSAGE, Session.UNLIMITED_CREDIT);

	}

	/**
	 * 
	 * Unbind the exchange and the agent queue
	 */
	private void unbindExchange() {

		this.session.exchangeUnbind(aid.name, "amq.direct", aid.name);
	}

	/**
	 * 
	 * Sends a ACLMessage to all specified recipients agents. If a message
	 * destination having another platform, this will be forwarded to
	 * BridgeAgentInOut agent.
	 * 
	 * @param msg
	 * 
	 */
	public void send(ACLMessage msg) {

		DeliveryProperties deliveryProps = new DeliveryProperties();
		deliveryProps.setRoutingKey("routing_key");

		MessageProperties messageProperties = new MessageProperties();

		MessageTransfer xfr = new MessageTransfer();

		xfr.destination("amq.direct");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);

		// deliveryProps.deliveryMode(MessageDeliveryMode.PERSISTENT);

		// Serialize message content
		String body;
		// Performative
		body = msg.getPerformativeInt() + "#";
		// Sender
		body = body + msg.getSender().toString().length() + "#"
				+ msg.getSender().toString();
		// receiver
		body = body + msg.getReceiver().toString().length() + "#"
				+ msg.getReceiver().toString();
		// reply to
		body = body + msg.getReplyTo().toString().length() + "#"
				+ msg.getReplyTo().toString();
		// language
		body = body + msg.getLanguage().length() + "#" + msg.getLanguage();
		// encoding
		body = body + msg.getEncoding().length() + "#" + msg.getEncoding();
		// ontology
		body = body + msg.getOntology().length() + "#" + msg.getOntology();
		// protocol
		body = body + msg.getProtocol().length() + "#" + msg.getProtocol();
		// conversation id
		body = body + msg.getConversationId().length() + "#"
				+ msg.getConversationId();
		// reply with
		body = body + msg.getReplyWith().length() + "#" + msg.getReplyWith();
		// in reply to
		body = body + msg.getInReplyTo().length() + "#" + msg.getInReplyTo();
		// reply by
		body = body + msg.getReplyBy().length() + "#" + msg.getReplyBy();
		// content
		body = body + msg.getContent().length() + "#" + msg.getContent();

		xfr.setBody(body);

		//Esto forma parte de la implementación para el soporte del no repudio por parte de los agentes.
		//Obligamos a que en el mensaje se envie la identidad verdadera del agente emisor.
		if (c.isSecureMode()) {
			try {
				messageProperties.setUserId(msg.getSender().name.toString()
						.getBytes("UTF-8"));
			} catch (java.io.UnsupportedEncodingException e) {
				System.err.println("Caught exception " + e.toString());
			}
		}

		for (int i = 0; i < msg.getTotalReceivers(); i++) {
			// If protocol is not qpid then the message goes outside the
			// platform
			if (!msg.getReceiver(i).protocol.equals("qpid")) {
				deliveryProps.setRoutingKey("BridgeAgentInOut");
			} else {
				deliveryProps.setRoutingKey(msg.getReceiver(i).name);
			}
			xfr.header(new Header(deliveryProps));
			// session.messageTransfer(xfr.getDestination(),
			// xfr.getAcceptMode(),
			// xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
			try {

				// Si el broker destruye la session por una accion no permitida
				// realizada anteriormente.
				if (session.getCommandsIn() == this.sessionCommandsIn + 1) {
					this.reloadSession();
				}

				session.messageTransfer(xfr.getDestination(), xfr
						.getAcceptMode(), xfr.getAcquireMode(), new Header(
						deliveryProps, messageProperties), xfr.getBodyString());

			} catch (SessionException e) {

				this.reloadSession();
				logger.error(e.getMessage());
			} catch (Exception e) {
				System.err.println("Caught exception " + e.toString());

			}

		}

	}

	/**
	 * Returns the agent name
	 * 
	 * @return Agent name
	 */
	public String getName() {
		return aid.name;
	}

	/**
	 * Define activities such as initialization resources, and every task
	 * necessary before execution of execute procedure. It will be executed when
	 * the agent will be launched and may be defined by the user.
	 * 
	 */

	protected void init() {

	}

	/**
	 * Method that defines all the logic and behavior of the agent. This method
	 * necessarily must be defined.
	 */
	protected void execute() {

	}

	/**
	 * 
	 */
	public void finalize() {

	}

	/**
	 * Function that will be executed when the agent gets a message The user has
	 * to write his/her code here
	 * 
	 * @param ssn
	 * @param xfr
	 */
	protected void onMessage(ACLMessage msg) {

	}

	/**
	 * Function that will be executed when the agent terminates
	 */
	protected void terminate() {

		this.unbindExchange();
		// if (AgentsConnection.isSecure())
		// this.deleteQueue();
		session.close();
		this.connection.close();

	}

	/**
	 * Runs Agent's thread
	 */
	public void run() {
		init();
		execute();
		finalize();
		terminate();
	}

	/**
	 * Starts the agent
	 */
	public void start() {
		myThread.start();
	}

	/***************************************************************************
	 * CONSULTATION METHODS
	 **************************************************************************/

	/**
	 *Returns a structure as the Agent Identificator formed by the name,
	 * protocol, host and port Agent.
	 * 
	 * @return agent ID
	 * @uml.property name="aid"
	 */
	public AgentID getAid() {
		return this.aid;
	}

	/**
	 * Returns true if an agent exists on the platform, false otherwise
	 * 
	 * @param aid
	 *            Agent ID to look for
	 * @return True if agent exists, false otherwise
	 */
	public boolean existAgent(AgentID aid) {
		return session.queueQuery(aid.name).get().getQueue() != null;
	}

	/**
	 * Transforms the message to ACLMessage
	 * 
	 * @param xfr
	 *            MessageTransfer
	 * @return ACLMessage
	 */
	public final ACLMessage MessageTransfertoACLMessage(MessageTransfer xfr)
			throws Exception {

		// des-serializamos el mensaje
		// inicializaciones
		int indice1 = 0;
		int indice2 = 0;
		int aidindice1 = 0;
		int aidindice2 = 0;
		int tam = 0;
		String aidString;
		String body = xfr.getBodyString();

		// System.out.println("BODY: " + body);

		indice2 = body.indexOf('#', indice1);
		ACLMessage msg = new ACLMessage(Integer.parseInt(body.substring(
				indice1, indice2)));

		// deserializamos los diferentes AgentesID (Sender, Receiver, ReplyTo)
		for (int i = 0; i < 3; i++) {
			AgentID aid = new AgentID();
			aidindice1 = 0;
			aidindice2 = 0;
			indice1 = indice2 + 1 + tam;
			indice2 = body.indexOf('#', indice1);
			tam = Integer.parseInt(body.substring(indice1, indice2));
			aidString = body.substring(indice2 + 1, indice2 + 1 + tam);
			aidindice2 = aidString.indexOf(':');
			if (aidindice2 - aidindice1 <= 0)
				aid.protocol = "";
			else
				aid.protocol = aidString.substring(aidindice1, aidindice2);
			aidindice1 = aidindice2 + 3;
			aidindice2 = aidString.indexOf('@', aidindice1);
			if (aidindice2 - aidindice1 <= 0)
				aid.name = "";
			else
				aid.name = aidString.substring(aidindice1, aidindice2);
			aidindice1 = aidindice2 + 1;
			aidindice2 = aidString.indexOf(':', aidindice1);
			if (aidindice2 - aidindice1 <= 0)
				aid.host = "";
			else
				aid.host = aidString.substring(aidindice1, aidindice2);
			aid.port = aidString.substring(aidindice2 + 1);

			if (i == 0)
				msg.setSender(aid);
			if (i == 1)
				msg.setReceiver(aid);
			if (i == 2)
				msg.setReplyTo(aid);
		}
		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// language
		msg.setLanguage(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// encoding
		msg.setEncoding(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// ontologyencodingACLMessage template
		msg.setOntology(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Protocol
		msg.setProtocol(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Conversation id
		msg.setConversationId(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Reply with
		msg.setReplyWith(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf("#", indice1);

		tam = Integer.parseInt(body.substring(indice1, indice2));
		// In reply to
		msg.setInReplyTo(body.substring(indice2 + 1, indice2 + 1 + tam));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// reply by

		if (tam != 0)
			msg.setReplyByDate(new Date(Integer.parseInt(body.substring(
					indice2 + 10, indice2 + tam))));

		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		// Content
		msg.setContent(body.substring(indice2 + 1, indice2 + 1 + tam));

		MessageProperties mp = xfr.getHeader().get(MessageProperties.class);

		
		if (c.isSecureMode()) {
			if (mp == null)
				throw new Exception(
						"In Magentix Secure mode, the UserID is required in message.");

			else
				try {
					if (!msg.getSender().name.equals(new java.lang.String(mp
							.getUserId(), "UTF-8")))
						throw new Exception(
								"Sender field ("
										+ msg.getSender().name
										+ ") doesn't match with the name of the sender agent ("
										+ new java.lang.String(mp.getUserId(),
												"UTF-8") + ")");
				} catch (java.io.UnsupportedEncodingException e) {
				}
		}

		return msg;
	}

}
