package es.upv.dsic.gti_ia.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.qpid.transport.Connection;
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

/**
 * @author Ricard Lopez Fogues
 * @author Sergio Pajares Ferrando
 * @author Joan Bellver Faus
 */
/**
 * [TRACE]: Modified by Luis Burdalo to support event tracing
 *
 */
public class BaseAgent implements Runnable {

	/**
	 * The logger variable considers to print any event that occurs by the agent
	 */
	protected Logger logger = Logger.getLogger(BaseAgent.class);

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
	 * @uml.property name="traceSession"
	 */
	protected Session traceSession;
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
	
	private class TraceListener implements SessionListener {
		public void opened(Session ssn) {
		}

		public void resumed(Session ssn) {
		}

		public void message(Session ssn, MessageTransfer xfr) {
			TraceEvent tEvent = MessageTransfertoTraceEvent(xfr);
			onTraceEvent(tEvent);
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
	 * [TRACE]:
	 * Attributes necessary for event tracing
	 *
	 */
	/**
	 * @uml.property name="traceListener"
	 */
	private TraceListener traceListener;
	
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
	 *             
	 * [TRACE]:
	 * 		Create the event queue and the event listener
	 */
	public BaseAgent(AgentID aid) throws Exception {
				
		if (AgentsConnection.connection == null) {
			logger.error("Before create a agent, the qpid broker connection is necesary");
			throw new Exception("Error doesn't work the broken connection");
		} else {
			this.connection = AgentsConnection.connection;
		}

		this.session = createSession();
		
		this.traceSession = createTraceSession();
		
		if (this.existAgent(aid)) {
			session.close();
			traceSession.close();
			throw new Exception("Agent ID already exists on the platform");
		} else {
			this.aid = aid;
			this.listener = new Listener();
			myThread = new Thread(this);
			createQueue();
			createBind();
			createSubscription();
			
			this.traceListener = new TraceListener();			
			createEventQueue();
			createTraceBind();
			createTraceSubscription();

		}
		
		sendTraceEvent(new TraceEvent("NEW_AGENT", aid, ""));
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
	 * Sends a ACLMessage to all specified recipients agents. If a message destination having another platform, this will be forwarded to BridgeAgentInOut agent.
	 * @param msg
	 *         
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
			
			//sendTraceEvent(new TraceEvent("MESSAGE_SENT", this.aid, xfr.getBodyString()));
		}
		
	}

	/**
	 * [TRACE]: Methods necessary for event trace support
	 */
	/**
	 * Creates the exclusive session the agent will use for trace events
	 * 
	 * @return The new Session
	 */
	private Session createTraceSession() {
		Session session = this.connection.createSession(0);
		return session;
	}
	/**
	 * Creates queue where the agent will receive trace events
	 */
	private void createEventQueue() {
		this.traceSession.queueDeclare(aid.name+".trace", null, null, Option.AUTO_DELETE);
		
	}
	
	private void createTraceBind(){
		Map<String, Object> arguments = new HashMap<String, Object>();
		
		arguments.put("x-match", "all");
    	arguments.put("origin_entity", "system");
    	arguments.put("receiver", "all");
    	// To let an agent receive all those system trace events from the tracing system to ALL agents
    	this.traceSession.exchangeBind(aid.name+".trace", "amq.match", aid.name + ".system.all", arguments);
    	//this.session.exchangeBind(aid.name+".trace", "mgx.trace", aid.name + ".system.all", arguments);
		
    	arguments.clear();
    	
		arguments.put("x-match", "all");
    	arguments.put("origin_entity", "system");
    	arguments.put("receiver", aid.name);
    	// To let an agent receive all those system trace events from the tracing system to THAT agent  
    	this.traceSession.exchangeBind(aid.name+".trace", "amq.match", aid.name + ".system.direct", arguments);
    	//this.session.exchangeBind(aid.name+".trace", "mgx.trace", aid.name + ".system.direct", arguments);
    	
    	// confirm completion
    	this.traceSession.sync();
	}
	
	/**
	 * Creates the subscription through the agent listener will get trace events
	 * from the event queue
	 */
	private void createTraceSubscription() {
		this.traceSession.setSessionListener(this.traceListener);
		
		this.traceSession.messageSubscribe(aid.name + ".trace", "listener_destination",
				MessageAcceptMode.NONE, MessageAcquireMode.PRE_ACQUIRED, null,
				0, null);

		this.traceSession.messageFlow("listener_destination",
				MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
		this.traceSession.messageFlow("listener_destination",
				MessageCreditUnit.MESSAGE, Session.UNLIMITED_CREDIT);
		this.traceSession.sync();
	}
		
	/**
	 * 
	 * Sends a trace event to the mgx.trace exchange
	 * @param tEvent
	 *         
	 */
	public void sendTraceEvent(TraceEvent tEvent) {
		MessageTransfer xfr = new MessageTransfer();

		xfr.destination("amq.match");
		//xfr.destination("mgx.trace");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();
		
		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getEventType().length() + "#"
				+ tEvent.getEventType();
		//body = body + tEvent.getEventType() + "#";
		// OriginEntiy
		body = body + tEvent.getOriginEntity().toString().length() + "#" + tEvent.getOriginEntity().toString();
		//body = body + tEvent.getOriginEntity().toString() + "#";
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
		//body = body + tEvent.getContent();
		
		xfr.setBody(body);
//		xfr.setBody("Trace Event");
		
//		deliveryProps.setRoutingKey(msg.getReceiver(i).name);
		
		// set message headers
    	MessageProperties messageProperties = new MessageProperties();
    	Map<String, Object> messageHeaders = new HashMap<String, Object>();
    	// set the message property
    	messageHeaders.put("event_type", tEvent.getEventType());
    	messageHeaders.put("origin_entity", tEvent.getOriginEntity().name);
    	messageProperties.setApplicationHeaders(messageHeaders);
		
    	Header header = new Header(deliveryProps, messageProperties);
    	
    	//xfr.header(new Header(deliveryProps, messageProperties));
    	this.traceSession.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
                header, xfr.getBodyString());

	}
	
	/**
	 * Returns the agent name
	 * 
	 * @return Agent name
	 */
	public String getName() {
		return aid.name;
	}

	/**Define activities such as initialization resources, and every task necessary before execution of execute procedure.
	 * It will be executed when the agent will be launched and may be defined by the user.
	 * 
	 */

	protected void init() {

	}
	/**
	 * Method that defines all the logic and behavior of the agent.
	 * This method necessarily must be defined.
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

	/** [TRACE]
	 * Function that will be executed when the agent gets a trace event. The user has
	 * to write his/her code here
	 * 
	 */
	protected void onTraceEvent(TraceEvent tEvent) {
		
	}
	
	/**
	 * Function that will be executed when the agent terminates
	 */
	protected void terminate() {
		session.queueDelete(aid.name);
		session.close();
		
		traceSession.queueDelete(aid.name + ".trace");
		traceSession.close();

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
	 *Returns a structure as the Agent Identificator formed by the name, protocol, host and port Agent.
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

	/**
	 * [TRACE]: Transforms the message to TraceEvent
	 * 
	 * @param xfr
	 *            MessageTransfer
	 * @return TraceEvent
	 */
	public final TraceEvent MessageTransfertoTraceEvent(MessageTransfer xfr) {
		// des-serializamos el evento
		// inicializaciones
		int indice1 = 0;
		int indice2 = 0;
		int aidindice1 = 0;
		int aidindice2 = 0;
		int tam = 0;
		String aidString;
		String body = xfr.getBodyString();

		TraceEvent tEvent = new TraceEvent();
		
		// Timestamp
		tam = body.indexOf('#', indice1);
				
		tEvent.setTimestamp(Long.parseLong(body.substring(indice2, indice1+tam)));
		
		// Event Type
		indice1 = indice1 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		tEvent.setEventType(body.substring(indice2+1, indice2+1+tam));
		
		// Origin Entity
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
		
		tEvent.setOriginEntity(aid);
		
		// Content
		indice1 = indice1 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		tEvent.setContent(body.substring(indice2+1));

		
		return tEvent;
	}
	
}
