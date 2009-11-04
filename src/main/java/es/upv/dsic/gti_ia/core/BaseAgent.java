package es.upv.dsic.gti_ia.core;

import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageCreditUnit;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Option;
import org.apache.qpid.transport.Session;
import org.apache.qpid.transport.SessionException;
import org.apache.qpid.transport.SessionListener;

/**
 * @author Ricard Lopez Fogues
 * @author Sergio Pajares Ferrando
 * @author Joan Bellver Faus
 */
public class BaseAgent implements Runnable {

	/**
	 * To enable log4j in Qpid agents
	 */
	static Logger logger = Logger.getLogger(BaseAgent.class);

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
			ACLMessage msg = MessageTransfertoACLMessage(xfr);
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

		if (AgentsConecction.connection == null) {
			logger
					.error("Before create a agent, the qpid broker connection is necesary");
			throw new Exception("Error doesn't work the broken connection");
		} else {
			this.connection = AgentsConecction.connection;
		}

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
	 * Sends an ACLMessage to the message's receivers
	 * 
	 * @param msg
	 *            Message to be sent
	 */
	public void send(ACLMessage msg) {
		MessageTransfer xfr = new MessageTransfer();

		xfr.destination("amq.direct");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);

		DeliveryProperties deliveryProps = new DeliveryProperties();

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
		for (int i = 0; i < msg.getTotalReceivers(); i++) {
			// If protocol is not qpid then the message goes outside the
			// platform
			if (!msg.getReceiver(i).protocol.equals("qpid")) {
				deliveryProps.setRoutingKey("BridgeAgentInOut");
			} else {
				deliveryProps.setRoutingKey(msg.getReceiver(i).name);
			}
			xfr.header(new Header(deliveryProps));
			session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
					xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
		}
	}

	/**
	 * Gets agent name
	 * 
	 * @return Agent name
	 */
	public String getName() {
		return aid.name;
	}

	/**
	 * Function that will be executed by the agent when it starts The user has
	 * to write his/her code here
	 */

	protected void init() {

	}

	protected void execute() {

	}

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
		session.queueDelete(aid.name);
		session.close();

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
	public final ACLMessage MessageTransfertoACLMessage(MessageTransfer xfr) {

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

		return msg;
	}

}
