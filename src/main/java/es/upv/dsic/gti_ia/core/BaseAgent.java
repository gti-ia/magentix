package es.upv.dsic.gti_ia.core;

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
 * @author  Ricard Lopez Fogues
 */
public class BaseAgent implements Runnable{
	
	/**
	 * To enable log4j in Qpid agents
	 */
	static Logger logger = Logger.getLogger(BaseAgent.class);

	/*Atributos*/
	/**
	 * @uml.property  name="aid"
	 * @uml.associationEnd  
	 */
	private AgentID aid;
	/**
	 * @uml.property  name="connection"
	 */
	private Connection connection;
	/**
	 * @uml.property  name="session"
	 */
	protected Session session;
	/**
	 * @uml.property  name="myThread"
	 */
	private Thread myThread;
	
	private class Listener implements SessionListener{
		public void opened(Session ssn) {}
	
	    public void resumed(Session ssn) {}
	
	    public void message(Session ssn, MessageTransfer xfr)
	    {
	    	//ejecutamos codigo creado por el usuario
	    	onMessage(ssn, xfr);
	    }
	
	    public void exception(Session ssn, SessionException exc)
	    {
	        exc.printStackTrace();
	    }
	
	    public void closed(Session ssn) {}	
	}	
	/**
	 * @uml.property  name="listener"
	 * @uml.associationEnd  
	 */
	private Listener listener;
	
	/**
	 * Creates a new agent
	 * @param aid Agent identification for the new agent, it has to be unique on the platform
	 * @param connection Connection that the agent will use
	 * @throws Exception If Agent ID already exists on the platform
	 */
	public BaseAgent(AgentID aid, Connection connection) throws Exception{
		this.connection = connection;
		this.session = createSession();
		if(this.existAgent(aid)){
			session.close();
			throw new Exception("Agent ID already exists on the platform");
		}
		else{
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
	 * @return The new Session
	 */
	private Session createSession(){
		 Session session = this.connection.createSession(0);
		 return session;
	}
		
	/**
	 * Creates de queue the agent will listen to for messages	 * 
	 */
	private void createQueue(){
		this.session.queueDeclare(aid.name, null, null, Option.AUTO_DELETE);
	}
	
	/**
	 * Binds the exchange and the agent queue
	 */
	private void createBind(){
		//this.session.exchangeBind(aid.name, aid.name, null, null);
		this.session.exchangeBind(aid.name, "amq.direct", aid.name,null);
	}
	
	/**
	 * Creates the subscription through the agent listener will get the message from the queue
	 */
	private void createSubscription(){
		this.session.setSessionListener(this.listener);

       this.session.messageSubscribe(aid.name,
                                 "listener_destination",
                                 MessageAcceptMode.NONE,
                                 MessageAcquireMode.PRE_ACQUIRED,
                                 null, 0, null);
		
		this.session.messageFlow("listener_destination", MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
        this.session.messageFlow("listener_destination", MessageCreditUnit.MESSAGE, Session.UNLIMITED_CREDIT);
	}
	
	/**
	 * Sends an ACLMessage to the message's receivers
	 * @param msg Message to be sent
	 */
	public void send(ACLMessage msg){
		MessageTransfer xfr = new MessageTransfer();
			
		xfr.destination("amq.direct");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
        
        DeliveryProperties deliveryProps = new DeliveryProperties();
	            
        //Serialize message content
        String body;
        //Performative
        body = msg.getPerformativeInt() + "#";
        //Sender
        body = body + msg.getSender().toString().length() + "#" + msg.getSender().toString();
        //receiver
        body = body + msg.getReceiver().toString().length() + "#" + msg.getReceiver().toString();
        //reply to
        body = body + msg.getReplyTo().toString().length() + "#" + msg.getReplyTo().toString();
        //language
        body = body + msg.getLanguage().length() + "#" + msg.getLanguage();
        //encoding
        body = body + msg.getEncoding().length() + "#" + msg.getEncoding();
        //ontology
        body = body + msg.getOntology().length() + "#" + msg.getOntology();
        //protocol
        body = body + msg.getProtocol().length() + "#" + msg.getProtocol();
        //conversation id
        body = body + msg.getConversationId().length() + "#" + msg.getConversationId();
        //reply with
        body = body + msg.getReplyWith().length() + "#" + msg.getReplyWith();
        //in reply to
        body = body + msg.getInReplyTo().length() + "#" + msg.getInReplyTo();
        //reply by
        body = body + msg.getReplyBy().length() + "#" + msg.getReplyBy();
        //content
        body = body + msg.getContent().length() + "#" + msg.getContent();
        
        xfr.setBody(body);
		for(int i=0; i< msg.getTotalReceivers(); i++){
			//If protocol is not qpid then the message goes outside the platform
			if(!msg.getReceiver(i).protocol.equals("qpid")){			
	        	deliveryProps.setRoutingKey("BridgeAgentInOut");			
			}
			else{
				deliveryProps.setRoutingKey(msg.getReceiver(i).name);			
			}
			xfr.header(new Header(deliveryProps));	     
			session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(), xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
		}
	}
	
	/**
	 * Gets agent name
	 * @return Agent name
	 */
	public String getName(){
		return aid.name;
	}
	
	/**
	 * Function that will be executed by the agent when it starts
	 * The user has to write his/her code here
	 */
	protected void execute(){
		
	}
	
	/**
	 * Function that will be executed when the agent gets a message
	 * The user has to write his/her code here
	 * @param ssn
	 * @param xfr
	 */
	protected void onMessage(Session ssn, MessageTransfer xfr){
			
	}
	
	/**
	 * Function that will be executed when the agent terminates
	 */
	protected void terminate(){
		session.queueDelete(aid.name);
		session.close();
	
	}
	
	/**
	 * Runs Agent's thread
	 */
	public void run(){
		execute();
		terminate();
	}
	
	/**
	 * Starts the agent
	 */
	public void start(){
		myThread.start();
	}
	

	/**
	 * @return agent ID
	 * @uml.property  name="aid"
	 */
	public AgentID getAid()
	   {
	       return this.aid;
	   }

	/**
	 * @return agent connection
	 * @uml.property  name="connection"
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Set a diferent connection for the agent
	 * @param connection
	 * @uml.property  name="connection"
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * @return Agent's session
	 * @uml.property  name="session"
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @param session
	 * @uml.property  name="session"
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @return Agent's Thread
	 * @uml.property  name="myThread"
	 */
	public Thread getMyThread() {
		return myThread;
	}

	/**
	 * @param myThread
	 * @uml.property  name="myThread"
	 */
	public void setMyThread(Thread myThread) {
		this.myThread = myThread;
	}

	/**
	 * @return Agent's listener
	 * @uml.property  name="listener"
	 */
	public Listener getListener() {
		return listener;
	}

	/**
	 * @param listener
	 * @uml.property  name="listener"
	 */
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	/**
	 * @param aid
	 * @uml.property //hilo del agente name="aid"
	 */
	public void setAid(AgentID aid) {
		this.aid = aid;
	}
	
	/**
	 * Returns true if an agent exists on the platform, false otherwise
	 * @param aid Agent ID to look for
	 * @return True if agent exists, false otherwise
	 */
	public boolean existAgent(AgentID aid){
		return session.queueQuery(aid.name).get().getQueue() != null;
	}
	
}
