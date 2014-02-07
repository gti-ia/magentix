package es.upv.dsic.gti_ia.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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

import es.upv.dsic.gti_ia.trace.TraceInteract;
import es.upv.dsic.gti_ia.trace.TraceMask;
import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;
import es.upv.dsic.gti_ia.trace.exception.TraceSystemUnreachableException;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.secure.SecurityTools;

/**
 * Base class to inherit when implementing a basic Magentix agent. This class
 * provides mechanisms to enable communication between agents. A good
 * alternative is to inherit from SingleAgent, which provides methods
 * (callbacks) when a message arrives, etc. If this class is too basic, consider
 * to inherit from one of the two possibilities given by the QueueAgent and the
 * CAgent.
 * 
 * @see SingleAgent
 * @see QueueAgent
 * @see CAgent
 * 
 * @author Ricard Lopez Fogues
 * @author Sergio Pajares Ferrando
 * @author Joan Bellver Faus
 * @author Luis Burdalo
 * @author Jose Vicente Ruiz Cepeda
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * @author Javier Jorge Cano
 * @author José Manuel Mejías Rodríguez
 */
public class BaseAgent implements Runnable {
	
	/**
	 * Constant with the name of the system.
	 */
	public static final String SYSTEM_NAME = "system";
	
	/**
	 * Constant with the AID of the system.
	 */
	public static final AgentID SYSTEM_AID = new AgentID(SYSTEM_NAME);
	
	/**
	 * The logger variable considers to print any event that occurs by the agent
	 */
	protected Logger logger = Logger.getLogger(BaseAgent.class);
	
	/**
	 * Configuration object to read the configuration from the Settings.xml file
	 * for this agent.
	 */
	private es.upv.dsic.gti_ia.organization.Configuration c = es.upv.dsic.gti_ia.organization.Configuration
			.getConfiguration();
	
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
	 * QPid session used for communication.
	 */
	protected Session session;
	/**
	 * Qpid session used for tracing.
	 * 
	 * @see TracingService
	 */
	protected Session traceSession;
	/**
	 * @uml.property name="myThread"
	 */
	private Thread myThread;
	
	private FileInputStream propFile;
	
	private Properties propSecurity;
	
	private SecurityTools st;
	
	/**
	 * Bit mask used to manage the trace interactions.
	 */
	private TraceMask traceMask;
	
	/**
	 * Check if mask is updated.
	 */
	private ArrayList<String> traceMaskUpdated;
	
	/**
	 * Class representing the communication listener.
	 * 
	 */
	private class Listener implements SessionListener {
		public void opened(Session ssn) {
		}
		
		public void resumed(Session ssn) {
		}
		
		/**
		 * Called to treat the message. At the end, the method
		 * {@link BaseAgent#onMessage(ACLMessage)} is called.
		 */
		public void message(Session ssn, MessageTransfer xfr) {
			ACLMessage msg = null;
			try {
				msg = MessageTransfertoACLMessage(xfr);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/*
			 * Send trace event with the message if the corresponding service is
			 * active in the mask (checked in sendTraceEvent(...) method).
			 */
			TracingService tsMsgRecv = TracingService.DI_TracingServices[TracingService.MESSAGE_RECEIVED];
			try {
				sendTraceEvent(new TraceEvent(tsMsgRecv.getName(), aid, msg
						.getSender().toString()));
			} catch (TraceServiceNotAllowedException e1) {
			}
			
			/*
			 * Send trace event with the detailed message if the corresponding
			 * service is active in the mask (checked in sendTraceEvent(...)
			 * method).
			 */
			TracingService tsMsgRecvDtl = TracingService.DI_TracingServices[TracingService.MESSAGE_RECEIVED_DETAIL];
			try {
				sendTraceEvent(new TraceEvent(tsMsgRecvDtl.getName(), aid,
						msg.toString()));
			} catch (TraceServiceNotAllowedException e1) {
			}
			
			try {
				preOnMessage(msg);
			} catch (Exception e) {
				logger.error(aid.getLocalName()
						+ " ended execution incorrectly: " + e);
			}
		}
		
		public void exception(Session ssn, SessionException exc) {
			exc.printStackTrace();
		}
		
		public void closed(Session ssn) {
		}
		
	}
	
	/**
	 * Class representing the trace event listener.
	 * 
	 */
	private class TraceListener implements SessionListener {
		public void opened(Session ssn) {
		}
		
		public void resumed(Session ssn) {
		}
		
		/**
		 * Called to treat the trace event. At the end, the method
		 * {@link BaseAgent#preOnTraceEvent(TraceEvent tEvent)} is called.
		 */
		public void message(Session ssn, MessageTransfer xfr) {
			TraceEvent tEvent = MessageTransfertoTraceEvent(xfr);
			preOnTraceEvent(tEvent);
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
	 * @uml.property name="traceListener"
	 */
	private TraceListener traceListener;
	
	/**
	 * Creates a new agent in an open broker connection
	 * 
	 * @param aid
	 *            Agent identification for the new agent, it has to be unique on
	 *            the platform
	 * 
	 * @throws Exception
	 *             If Agent ID already exists on the platform
	 * 
	 */
	public BaseAgent(AgentID aid) throws Exception {
		
		// Si no estamos en modo seguro funcionara como siempre, es por tanto
		// transparente al programador.
		if (c.isSecureMode()) {
			
			propFile = new FileInputStream(
					"./configuration/securityUser.properties");
			propSecurity = new Properties();
			try {
				// Nuevo fichero para la configuración de datos para la
				// seguridad.
				propSecurity.load(propFile);
				
			} catch (FileNotFoundException e) {
				logger.error(e);
			} catch (IOException e) {
				logger.error(e);
				e.printStackTrace();
			}
			
			st = SecurityTools.GetInstance();
			
			// Vemos si el usuario ya posee algún certificado para ese agente.
			// Se comprueba también
			// la validez.
			// Este método es el encargado de crear todo el proceso de solicitud
			// y creación de
			// certificados para los
			// agentes del usuario. Podemos encontrarlo en la clase
			// SecurityTools del paquete
			// secure.
			if (st.generateAllProcessCertificate(aid.name, propSecurity)) {
				
				connection = null;
				// El alias sera el mismo que el nombre del agente
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
				
				// Accedemos al fichero de configuración de seguridad del
				// usuario.
				connectSettings.setKeyStorePassword(propSecurity
						.getProperty("KeyStorePassword"));
				
				connectSettings.setKeyStorePath(propSecurity
						.getProperty("KeyStorePath"));
				// Lo convertimos a minusculas para que no haya problemas
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
			propFile.close();
			
		} else {
			if (AgentsConnection.connection == null) {
				logger.error("Before create a agent, the qpid broker connection is necesary");
				throw new Exception("Error doesn't work the broken connection");
			} else {
				this.connection = AgentsConnection.getConnection();
			}
		}
		// Esta parte es la misma que cuando no es modo seguro.
		this.session = createSession();
		
		// Create a session for trace event transmission
		this.traceSession = createTraceSession();
		
		if (this.existAgent(aid)) {
			session.close();
			traceSession.close();
			throw new Exception("Agent ID " + aid.name
					+ " already exists on the platform");
		} else {
			this.aid = aid;
			this.listener = new Listener();
			myThread = new Thread(this);
			createQueue();
			createBind();
			createSubscription();
			
			// Install the listener for trace events
			this.traceListener = new TraceListener();
			createEventQueue();
			createTraceBind();
			createTraceSubscription();
			
		}
		//Initialize the map of update requests
		this.traceMaskUpdated = new ArrayList<String>();
		
		// Initialize a trace mask with all the services not available.
		this.traceMask = new TraceMask(false);
		
		// Send trace event NEW_AGENT
		sendSystemTraceEvent(new TraceEvent(
				TracingService.DI_TracingServices[TracingService.NEW_AGENT]
						.getName(),
				new AgentID("system", aid.protocol, aid.host, aid.port), aid
						.toString()));
	}
	
	/**
	 * Changes the name of the agent to acquire a new identity.
	 * 
	 * @param _aid
	 *            the new agent id
	 * @throws Exception
	 */
	public void changeIdentity(AgentID _aid) throws Exception {
		
		// Si el MMS nos da un certificado para el agente es que podemos crear
		// la conexión para ese agente.
		// o si ya tenemos un certificado válido para ese agente.
		if (st.generateAllProcessCertificate(_aid.name, propSecurity)) {
			
			ConnectionSettings connectSettings = new ConnectionSettings();
			
			// Se abre por que se ha cerrado anteriormente.
			propFile = new FileInputStream(
					"./configuration/securityUser.properties");
			propSecurity.load(propFile);
			
			/***************************************************************************
			 * Conexión como agente anterior
			 **************************************************************************/
			
			unbindExchange();
			unbindTraceExchange();
			
			session.queueDelete(aid.name);
			traceSession.queueDelete(aid.name + ".trace");
			
			session.close();
			traceSession.close();
			
			/***************************************************************************
			 * Conexión como nuevo agente
			 **************************************************************************/
			aid = _aid;
			connection = null;
			
			String certAlias = aid.name;
			
			connectSettings.setHost(c.getqpidHost());
			connectSettings.setPort(c.getqpidPort());
			connectSettings.setVhost(c.getqpidVhost());
			connectSettings.setUsername(c.getqpidUser());
			connectSettings.setPassword(c.getqpidPassword());
			connectSettings.setUseSSL(c.getqpidSSL());
			connectSettings.setSaslMechs(c.getqpidsaslMechs());
			connectSettings.setCertAlias(certAlias.toLowerCase());
			
			connectSettings.setKeyStorePassword(propSecurity
					.getProperty("KeyStorePassword"));
			connectSettings.setKeyStorePath(propSecurity
					.getProperty("KeyStorePath"));
			connectSettings.setTrustStorePassword(propSecurity
					.getProperty("TrustStorePassword"));
			connectSettings.setTrustStorePath(propSecurity
					.getProperty("TrustStorePath"));
			
			connection = new Connection();
			connection.connect(connectSettings);
			
			// Create new sessions.
			session = createSession();
			traceSession = createTraceSession();
			
			createQueue();
			createBind();
			createSubscription();
			
			// Install the listener for trace events
			createEventQueue();
			createTraceBind();
			createTraceSubscription();
			
			propFile.close();
			
			System.out.println("Identity " + this.aid
					+ " changed successfully!!");
			
		} else {
			logger.error("Agent ID " + _aid.name
					+ " already exists on the platform");
		}
	}
	
	// Cuando el agente infringe alguna regla de seguridad, la política del
	// broker es destruir la
	// sesión
	// del usuario, que no la conexión. Por tanto este método recarga la session
	// creando una nueva.
	
	/**
	 * The method reloads the session. The qpid broker policy by default is to
	 * destroy the session (but not the connection) if the agent violates any
	 * security rule. By using this method the sesion can be reloaded (or
	 * <i>restored</i>).
	 */
	private void reloadSession() {
		this.session = this.createSession();
		this.createQueue();
		this.createBind();
		this.createSubscription();
		
		this.traceSession = createTraceSession();
		this.traceListener = new TraceListener();
		createEventQueue();
		createTraceBind();
		createTraceSubscription();
		// this.sessionCommandsIn = this.session.getCommandsIn();
		
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
	 * Unbind the exchange and the agent queue
	 */
	private void unbindExchange() {
		
		this.session.exchangeUnbind(aid.name, "amq.direct", aid.name);
	}
	
	/**
	 * Sends a ACLMessage to all specified recipients agents. If a message
	 * destination having another platform, this will be forwarded to
	 * BridgeAgentInOut agent.
	 * 
	 * @param msg
	 */
	public void send(ACLMessage msg) {
		if (msg.getExchangeHeaders().isEmpty()) {
			/**
			 * Permite incluir un arroba en el nombre del agente destinatario.
			 * Condici�n Obligatoria para JADE. @ ser� reemplazado por ~
			 */
			msg.getReceiver().name = msg.getReceiver().name.replace('@', '~');
			
			MessageTransfer xfr = new MessageTransfer();
			
			xfr.destination("amq.direct");
			xfr.acceptMode(MessageAcceptMode.EXPLICIT);
			xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
			
			DeliveryProperties deliveryProps = new DeliveryProperties();
			MessageProperties messageProperties = new MessageProperties();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(bos);
				oos.writeObject(msg);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			xfr.setBody(bos.toByteArray());
			
			// Esto forma parte de la implementación para el soporte del no
			// repudio por parte de los
			// agentes.
			// Obligamos a que en el mensaje se envie la identidad verdadera del
			// agente emisor.
			if (c.isSecureMode()) {
				try {
					messageProperties.setUserId(msg.getSender().name.toString()
							.getBytes("UTF-8"));
				} catch (java.io.UnsupportedEncodingException e) {
					logger.error("Caught exception " + e.toString());
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
				session.messageTransfer(xfr.getDestination(), xfr
						.getAcceptMode(), xfr.getAcquireMode(), new Header(
						deliveryProps, messageProperties), xfr.getBodyBytes());
			}
		} else {
			
			MessageTransfer xfr = new MessageTransfer();
			
			xfr.destination("amq.match");
			xfr.acceptMode(MessageAcceptMode.EXPLICIT);
			xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
			
			DeliveryProperties deliveryProps = new DeliveryProperties();
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos;
			try {
				oos = new ObjectOutputStream(bos);
				oos.writeObject(msg);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			xfr.setBody(bos.toByteArray());
			
			// set message headers
			MessageProperties messageProperties = new MessageProperties();
			Map<String, Object> messageHeaders = new HashMap<String, Object>();
			
			for (String key : msg.getExchangeHeaders().keySet()) {
				messageHeaders.put(key, msg.getExchangeHeader(key));
			}
			
			messageProperties.setApplicationHeaders(messageHeaders);
			
			Header header = new Header(deliveryProps, messageProperties);
			
			this.session
					.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT,
							MessageAcquireMode.PRE_ACQUIRED, header,
							xfr.getBodyBytes());
		}
		
		/*
		 * Send trace event with the message if the corresponding service is
		 * active in the mask (checked in sendTraceEvent(...) method).
		 */
		TracingService tsMsgSent = TracingService.DI_TracingServices[TracingService.MESSAGE_SENT];
		try {
			sendTraceEvent(new TraceEvent(tsMsgSent.getName(), aid, msg
					.getReceiver().toString()));
		} catch (TraceServiceNotAllowedException e) {
		}
		
		/*
		 * Send trace event with the detailed message if the corresponding
		 * service is active in the mask (checked in sendTraceEvent(...)
		 * method).
		 */
		TracingService tsMsgSentDtl = TracingService.DI_TracingServices[TracingService.MESSAGE_SENT_DETAIL];
		try {
			sendTraceEvent(new TraceEvent(tsMsgSentDtl.getName(), aid,
					msg.toString()));
		} catch (TraceServiceNotAllowedException e) {
		}
		
		/**
		 * Permite incluir un arroba en el nombre del agente destinatario.
		 * Condición Obligatoria para JADE. @ será reemplazado por ~
		 */
		/*
		 * msg.getReceiver().name = msg.getReceiver().name.replace('@', '~');
		 * MessageTransfer xfr = new MessageTransfer();
		 * xfr.destination("amq.direct");
		 * xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		 * xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED); DeliveryProperties
		 * deliveryProps = new DeliveryProperties(); MessageProperties
		 * messageProperties = new MessageProperties(); // Serialize message
		 * content String body; // Performative body = msg.getPerformativeInt()
		 * + "#"; // Sender body = body + msg.getSender().toString().length() +
		 * "#" + msg.getSender().toString(); // receiver body = body +
		 * msg.getReceiver().toString().length() + "#" +
		 * msg.getReceiver().toString(); // reply to body = body +
		 * msg.getReplyTo().toString().length() + "#" +
		 * msg.getReplyTo().toString(); // language body = body +
		 * msg.getLanguage().length() + "#" + msg.getLanguage(); // encoding
		 * body = body + msg.getEncoding().length() + "#" + msg.getEncoding();
		 * // ontology body = body + msg.getOntology().length() + "#" +
		 * msg.getOntology(); // protocol body = body +
		 * msg.getProtocol().length() + "#" + msg.getProtocol(); // conversation
		 * id body = body + msg.getConversationId().length() + "#" +
		 * msg.getConversationId(); // reply with body = body +
		 * msg.getReplyWith().length() + "#" + msg.getReplyWith(); // in reply
		 * to body = body + msg.getInReplyTo().length() + "#" +
		 * msg.getInReplyTo(); // reply by body = body +
		 * msg.getReplyBy().length() + "#" + msg.getReplyBy(); // content body =
		 * body + msg.getContent().length() + "#" + msg.getContent(); //
		 * serialize message headers, it looks like: number of //
		 * headers#key.length#key|value.length#value // number of headers body =
		 * body + String.valueOf(msg.getHeaders().size()) + "#"; Map<String,
		 * String> headers = new HashMap<String, String>(msg.getHeaders());
		 * Iterator<String> itr = headers.keySet().iterator(); String key; //
		 * iterate through HashMap values iterator while (itr.hasNext()) { key =
		 * itr.next(); body = body + key.length() + "#" + key; body = body +
		 * headers.get(key).length() + "#" + headers.get(key); }
		 * xfr.setBody(body); // Esto forma parte de la implementación para el
		 * soporte del no repudio por parte de los // agentes. // Obligamos a
		 * que en el mensaje se envie la identidad verdadera del agente emisor.
		 * if (c.isSecureMode()) { try {
		 * messageProperties.setUserId(msg.getSender().name.toString()
		 * .getBytes("UTF-8")); } catch (java.io.UnsupportedEncodingException e)
		 * { logger.error("Caught exception " + e.toString()); } } for (int i =
		 * 0; i < msg.getTotalReceivers(); i++) { // If protocol is not qpid
		 * then the message goes outside the // platform if
		 * (!msg.getReceiver(i).protocol.equals("qpid")) {
		 * deliveryProps.setRoutingKey("BridgeAgentInOut"); } else {
		 * deliveryProps.setRoutingKey(msg.getReceiver(i).name); }
		 * xfr.header(new Header(deliveryProps)); try { // Si el broker destruye
		 * la session por una accion no permitida // realizada anteriormente. /*
		 * if (session.getCommandsIn() == this.sessionCommandsIn + 1) {
		 * this.reloadSession(); }
		 */
		/*
		 * session.messageTransfer(xfr.getDestination(), xfr .getAcceptMode(),
		 * xfr.getAcquireMode(), new Header( deliveryProps, messageProperties),
		 * xfr.getBodyString()); } catch (SessionException e) {
		 * this.reloadSession(); logger.error(e.getMessage()); } catch
		 * (Exception e) { logger.error("Caught exception " + e.toString()); } }
		 */
		
	}
	
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
	 * Unbind the exchange and the agent queue
	 */
	private void unbindTraceExchange() {
		this.session.exchangeUnbind(aid.name + ".trace", "amq.match", aid.name
				+ ".system.all");
		this.session.exchangeUnbind(aid.name + ".trace", "amq.match", aid.name
				+ ".system.direct");
	}
	
	/**
	 * Creates queue where the agent will receive trace events. The queue name
	 * is the name of the agent (aid.name) followed by the suffix ".trace"
	 */
	private void createEventQueue() {
		this.traceSession.queueDeclare(aid.name + ".trace", null, null,
				Option.AUTO_DELETE);
		
	}
	
	/**
	 * Creates the bindings needed by the event trace system. Two different
	 * bindings are made to the trace queue (agent_name.trace):
	 * agent_name.system.all => Receive all trace events sent from the system to
	 * ALL agents agent_name.system.direct => Receive all trace events sent from
	 * the system to this agent
	 */
	private void createTraceBind() {
		Map<String, Object> arguments = new HashMap<String, Object>();
		
		arguments.put("x-match", "all");
		arguments.put("origin_entity", "system");
		arguments.put("receiver", "all");
		// To let an agent receive all those system trace events from the
		// tracing system to ALL
		// agents
		this.traceSession.exchangeBind(aid.name + ".trace", "amq.match",
				aid.name + ".system.all", arguments);
		// this.session.exchangeBind(aid.name+".trace", "mgx.trace", aid.name +
		// ".system.all",
		// arguments);
		
		arguments.clear();
		
		arguments.put("x-match", "all");
		arguments.put("origin_entity", "system");
		arguments.put("receiver", aid.name);
		// To let an agent receive all those system trace events from the
		// tracing system to THAT
		// agent
		this.traceSession.exchangeBind(aid.name + ".trace", "amq.match",
				aid.name + ".system.direct", arguments);
		// this.session.exchangeBind(aid.name+".trace", "mgx.trace", aid.name +
		// ".system.direct",
		// arguments);
		
		// confirm completion
		this.traceSession.sync();
		
	}
	
	/**
	 * Creates the subscription through the agent listener will get trace events
	 * from the event queue
	 */
	private void createTraceSubscription() {
		this.traceSession.setSessionListener(this.traceListener);
		
		this.traceSession.messageSubscribe(aid.name + ".trace",
				"listener_destination", MessageAcceptMode.NONE,
				MessageAcquireMode.PRE_ACQUIRED, null, 0, null);
		
		this.traceSession.messageFlow("listener_destination",
				MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
		this.traceSession.messageFlow("listener_destination",
				MessageCreditUnit.MESSAGE, Session.UNLIMITED_CREDIT);
		this.traceSession.sync();
	}
	
	/**
	 * Sends a trace event to the mgx.trace exchange
	 * 
	 * @param tEvent
	 *            Trace event which is to be sent
	 * @throws TraceServiceNotAllowedException
	 *             if the custom tracing services are not allowed
	 */
	public void sendTraceEvent(TraceEvent tEvent)
			throws TraceServiceNotAllowedException {
		Integer bitIndex = -1;
		
		/*
		 * Check if the trace event corresponds to a domain independent (DI)
		 * tracing service.
		 */
		TracingService ts = TracingService.getDITracingServiceByName(tEvent
				.getTracingService());
		
		/*
		 * If it is null, the trace event is not related with a DI tracing
		 * service, so the CUSTOM bit must be checked in the mask.
		 * 
		 * Else, the trace event corresponds to a DI tracing service, so its
		 * corresponding bit in the mask must be checked.
		 */
		if (ts == null) {
			bitIndex = TraceMask.CUSTOM;
		} else {
			bitIndex = ts.getMaskBitIndex();
		}
		
		/*
		 * If the bit is null (because the tracing service has not associated
		 * bit in the mask) or it is active, proceed with the send.
		 * 
		 * Else, throw an exception.
		 */
		if (bitIndex == null || this.traceMask.get(bitIndex) == true) {
			sendFilteredTraceEvent(tEvent);
		} else {
			throw new TraceServiceNotAllowedException();
		}
	}
	
	/**
	 * Sends a trace event to the mgx.trace exchange
	 * 
	 * @param tEvent
	 *            Trace event which is to be sent
	 */
	private void sendFilteredTraceEvent(TraceEvent tEvent) {
		MessageTransfer xfr = new MessageTransfer();
		
		xfr.destination("amq.match");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();
		
		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getTracingService().length() + "#"
				+ tEvent.getTracingService();
		// OriginEntiy
		body = body + tEvent.getOriginEntity().getType() + "#";
		if (tEvent.getOriginEntity().getType() == TracingEntity.AGENT) {
			body = body + tEvent.getOriginEntity().getAid().toString().length()
					+ "#" + tEvent.getOriginEntity().getAid().toString();
		}
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();

		xfr.setBody(body);
		
		// set message headers
		MessageProperties messageProperties = new MessageProperties();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		// set the message property
		messageHeaders.put("tracing_service", tEvent.getTracingService());
		if (tEvent.getOriginEntity().getType() == TracingEntity.AGENT) {
			messageHeaders.put("origin_entity", tEvent.getOriginEntity()
					.getAid().toString());
		}
		
		messageProperties.setApplicationHeaders(messageHeaders);
		
		Header header = new Header(deliveryProps, messageProperties);
		
		this.traceSession.messageTransfer("amq.match",
				MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
				header, xfr.getBodyString());
		
		/*
		 * PRE-OPTIMIZATION OF EVENT TRANSMISSION MessageTransfer xfr = new
		 * MessageTransfer(); xfr.destination("amq.match");
		 * xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		 * xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED); DeliveryProperties
		 * deliveryProps = new DeliveryProperties(); MessageProperties
		 * messageProperties = new MessageProperties(); Map<String, Object>
		 * messageHeaders = new HashMap<String, Object>(); ByteArrayOutputStream
		 * bos = new ByteArrayOutputStream(); ObjectOutputStream oos; try { oos
		 * = new ObjectOutputStream(bos); oos.writeObject(tEvent); oos.flush();
		 * } catch (IOException e) { e.printStackTrace(); }
		 * xfr.setBody(bos.toByteArray()); // set the message property
		 * messageHeaders.put("tracing_service", tEvent.getTracingService()); if
		 * (tEvent.getOriginEntity().getType() == TracingEntity.AGENT) {
		 * messageHeaders.put("origin_entity",
		 * tEvent.getOriginEntity().getAid().name); }
		 * messageProperties.setApplicationHeaders(messageHeaders);
		 * traceSession.messageTransfer(xfr.getDestination(),
		 * xfr.getAcceptMode(), xfr.getAcquireMode(), new Header(deliveryProps,
		 * messageProperties), xfr.getBodyBytes());
		 */
	}
	
	/**
	 * Sends a trace event with "system" as origin entity to the amq.match
	 * exchange
	 * 
	 * @param tEvent
	 * @param destination
	 *            Tracing entity to which the trace event is directed to. If set
	 *            to null, the system trace event is understood to be directed
	 *            to all tracing entities.
	 */
	private void sendSystemTraceEvent(TraceEvent tEvent) {
		MessageTransfer xfr = new MessageTransfer();
		
		xfr.destination("amq.match");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();
		
		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getTracingService().length() + "#"
				+ tEvent.getTracingService();
		// OriginEntiy
		body = body + tEvent.getOriginEntity().getType() + "#";
		body = body + this.getAid().toString().length() + "#"
				+ this.getAid().toString();
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();

		xfr.setBody(body);
		
		// set message headers
		MessageProperties messageProperties = new MessageProperties();
		Map<String, Object> messageHeaders = new HashMap<String, Object>();
		// set the message property
		messageHeaders.put("tracing_service", tEvent.getTracingService());
		messageHeaders.put("origin_entity", "system");
		
		messageProperties.setApplicationHeaders(messageHeaders);
		
		Header header = new Header(deliveryProps, messageProperties);
		
		this.traceSession.messageTransfer("amq.match",
				MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
				header, xfr.getBodyString());
		/*
		 * PRE-OPTIMIZATION OF EVENT TRANSMISSION MessageTransfer xfr = new
		 * MessageTransfer(); xfr.destination("amq.match");
		 * xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		 * xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED); DeliveryProperties
		 * deliveryProps = new DeliveryProperties(); MessageProperties
		 * messageProperties = new MessageProperties(); Map<String, Object>
		 * messageHeaders = new HashMap<String, Object>(); ByteArrayOutputStream
		 * bos = new ByteArrayOutputStream(); ObjectOutputStream oos; try { oos
		 * = new ObjectOutputStream(bos); oos.writeObject(tEvent); oos.flush();
		 * } catch (IOException e) { e.printStackTrace(); }
		 * xfr.setBody(bos.toByteArray()); // set the message property
		 * messageHeaders.put("tracing_service", tEvent.getTracingService());
		 * messageHeaders.put("origin_entity", "system");
		 * messageProperties.setApplicationHeaders(messageHeaders);
		 * traceSession.messageTransfer(xfr.getDestination(),
		 * xfr.getAcceptMode(), xfr.getAcquireMode(), new Header(deliveryProps,
		 * messageProperties), xfr.getBodyBytes());
		 */
	}
	
	/**
	 * Request an update trace mask to Trace Manager. Blocking action with timeout.
	 * 
	 * @return the traceMask
	 */
	public TraceMask updateTraceMask() throws TraceSystemUnreachableException {
		
		// Building an ACL message.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.getAid());
		msg.setReceiver(this.getTraceManager());
		msg.setContent("UpdateMask#any");
		send(msg);
		
		int accumulatedTime = 0;
		int defaultTimeOut = 200;
		String messageId = msg.getConversationId();
		
		traceMaskUpdated.add(messageId);
				
		while(traceMaskUpdated.contains(messageId) && accumulatedTime < defaultTimeOut) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.getMessage();
			}
			accumulatedTime += 10;
		}
				
		if (traceMaskUpdated.contains(messageId))
			throw new TraceSystemUnreachableException();
		
		return traceMask.clone();
	}
	
	/**
	 * Request an update trace mask to Trace Manager. Blocking action with timeout.
	 * 
	 * @param timeOut 
	 * 				Time in milliseconds that the agent will be blocked.
	 * 
	 * @return the traceMask
	 */
	public TraceMask updateTraceMask(int timeOut) throws TraceSystemUnreachableException {

		// Building an ACL message.
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setSender(this.getAid());
		msg.setReceiver(this.getTraceManager());
		msg.setContent("UpdateMask#any");
		send(msg);
		
		int accumulatedTime = 0;
		String messageId = msg.getConversationId();
		
		traceMaskUpdated.add(messageId);
				
		while(traceMaskUpdated.contains(messageId) && accumulatedTime < timeOut) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.getMessage();
			}
			accumulatedTime += 10;
		}
				
		if (traceMaskUpdated.contains(messageId))
			throw new TraceSystemUnreachableException();
		
		return traceMask.clone();
	}
	
	/**
	 * Define activities such as initialization resources, and every task
	 * necessary before execution of execute procedure. It will be executed when
	 * the agent will be launched and may be defined by the user.
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
	 * The trace system is notified when the agent is about to disappear, in
	 * this method.
	 */
	public void finalize() {
		sendSystemTraceEvent(new TraceEvent(
				TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED]
						.getName(), new AgentID("system", aid.protocol,
						aid.host, aid.port), aid.toString()));
	}
	
	
	/**
	 * UNTIL NOT COMMENT
	 * 
	 * @param msg
	 * @throws Exception
	 */
	protected void preOnMessage(ACLMessage msg) throws Exception {
		
		boolean preprocessed = false;
		
		if (msg.getContent() != null){
			
			String[] content = msg.getContent().split("#");
			if (msg.getPerformativeInt() == ACLMessage.AGREE && content[0].equals("UpdateMask")) {
				this.traceMask = new TraceMask(content[1]);
				this.traceMaskUpdated.remove(msg.getConversationId());
				preprocessed = true;
			}
		}
		
		/*
		 * If the ACLMessage has not been preprocessed, the default method,
		 * which is supposed to be overriden by the user, is called.
		 */
		if (!preprocessed) {
			onMessage(msg);
		}
	}
	
	/**
	 * Function that will be executed when the agent gets a message The user has
	 * to write his/her code here
	 * 
	 * @param msg
	 *            Message received
	 */
	protected void onMessage(ACLMessage msg) throws Exception {
	}
	
	/**
	 * Function that will be executed when the agent gets a trace event. The
	 * system will apply a preprocess of the TraceEvent and, if required, will
	 * make a call to the method
	 * {@link BaseAgent#onTraceEvent(TraceEvent tEvent)}, which can be
	 * overridden by the user.
	 */
	private void preOnTraceEvent(TraceEvent tEvent) {
		boolean preprocessed = false;
		AgentID aidSender = tEvent.getOriginEntity().getAid();
		
		// If the origin entity is the system.
		if (aidSender.toString().equals(SYSTEM_AID.toString())) {
			String tServiceName = tEvent.getTracingService();
			
			// New trace manager in the system.
			if (tServiceName
					.equals(TracingService.DI_TracingServices[TracingService.WELCOME_TM]
							.getName())) {
				// New agent trace event is created and sent.
				sendSystemTraceEvent(new TraceEvent(
						TracingService.DI_TracingServices[TracingService.NEW_AGENT]
								.getName(),
						new AgentID("system", this.aid.protocol, this.aid.host,
								this.aid.port), this.aid.toString()));
				preprocessed = true;
			}
			// New trace mask in the system.
			else if (tServiceName
					.equals(TracingService.DI_TracingServices[TracingService.NEW_MASK]
							.getName())) {
				logger.debug('[' + this.aid.toString() + ']' + " new mask is "
						+ tEvent.getContent());
				
				try {
					this.traceMask = new TraceMask(tEvent.getContent());
				} catch (Exception e) {
					logger.error("The received trace mask was not well encoded.");
					e.printStackTrace();
				} finally {
					preprocessed = true;
				}
			}
		}
		/*
		 * If the trace event has not been preprocessed, the default method,
		 * which is supposed to be overriden by the user, is called.
		 */
		if (!preprocessed) {
			onTraceEvent(tEvent);
		}
	}
	
	/**
	 * Function that will be executed after a previous preprocessing of a
	 * received trace event. The user might override this method if he wants to
	 * apply a custom postprocessing.
	 */
	protected void onTraceEvent(TraceEvent tEvent) {
	}
	
	/**
	 * Function that will be executed when the agent terminates
	 */
	protected void terminate() {
		
		try {
			this.unbindExchange();
			this.unbindTraceExchange();
			
			session.queueDelete(aid.name);
			session.close();
			
			traceSession.queueDelete(aid.name + ".trace");
			traceSession.close();
		} catch (Exception e) {
			logger.error(this.aid.getLocalName()
					+ " ended execution incorrectly: " + e);
		}
		
	}
	
	/**
	 * Runs Agent's thread
	 */
	public void run() {
		try {
			
			init();
			execute();
			finalize();
			terminate();
		} catch (Exception e) {
			try {
				finalize();
			} catch (Exception ex) {
				terminate();
				logger.error(this.aid.getLocalName()
						+ " ended execution incorrectly: " + ex);
				ex.printStackTrace();
			}
			terminate();
			logger.error(this.aid.getLocalName()
					+ " ended execution incorrectly: " + e);
			e.printStackTrace();
		}
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
	 * Returns the agent name
	 * 
	 * @return Agent name
	 */
	public String getName() {
		return aid.name;
	}
	
	/**
	 * Returns a structure as the Agent Identificator formed by the name,
	 * protocol, host and port Agent.
	 * 
	 * @return agent ID
	 * @uml.property name="aid"
	 */
	public AgentID getAid() {
		return this.aid;
	}
	
	/**
	 * Returns the trace mask that is being used by this agent.
	 * 
	 * @return the traceMask
	 */
	public TraceMask getTraceMask() {		
		return traceMask.clone();
	}
	
	/**
	 * This method should find and return the system TraceManager. 
	 * (For now just returns the default TraceManager. It will be expandable in the future.)
	 * 
	 * @return System TraceManager
	 */
	public AgentID getTraceManager() {		
		return TraceInteract.DEFAULT_TM_AID;
	}
	
	/**
	 * Returns true if an agent exists on the platform, false otherwise
	 * 
	 * @param aid
	 *            Agent ID to look for
	 * @return True if agent exists, false otherwise
	 */
	private boolean existAgent(AgentID aid) {
		return session.queueQuery(aid.name).get().getQueue() != null;
	}
	
	/**
	 * Transforms the message to ACLMessage
	 * 
	 * @param xfr
	 *            MessageTransfer
	 * @return ACLMessage
	 */
	private final ACLMessage MessageTransfertoACLMessage(MessageTransfer xfr)
			throws Exception {
		byte[] binaryContent = xfr.getBodyBytes();
		
		ACLMessage msg = null;
		ObjectInputStream oin;
		try {
			oin = new ObjectInputStream(new ByteArrayInputStream(binaryContent));
			msg = (ACLMessage) oin.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
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
		
		/*
		 * // des-serializamos el mensaje // inicializaciones int indice1 = 0;
		 * int indice2 = 0; int aidindice1 = 0; int aidindice2 = 0; int tam = 0;
		 * String aidString; String body = xfr.getBodyString(); //
		 * System.out.println("BODY: " + body); indice2 = body.indexOf('#',
		 * indice1); ACLMessage msg = new ACLMessage(Integer.parseInt(body
		 * .substring(indice1, indice2))); // deserializamos los diferentes
		 * AgentesID (Sender, Receiver, ReplyTo) for (int i = 0; i < 3; i++) {
		 * AgentID aid = new AgentID(); aidindice1 = 0; aidindice2 = 0; indice1
		 * = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1); tam =
		 * Integer.parseInt(body.substring(indice1, indice2)); aidString =
		 * body.substring(indice2 + 1, indice2 + 1 + tam); aidindice2 =
		 * aidString.indexOf(':'); if (aidindice2 - aidindice1 <= 0)
		 * aid.protocol = ""; else aid.protocol =
		 * aidString.substring(aidindice1, aidindice2); aidindice1 = aidindice2
		 * + 3; aidindice2 = aidString.indexOf('@', aidindice1); if (aidindice2
		 * - aidindice1 <= 0) aid.name = ""; else aid.name =
		 * aidString.substring(aidindice1, aidindice2); aidindice1 = aidindice2
		 * + 1; aidindice2 = aidString.indexOf(':', aidindice1); if (aidindice2
		 * - aidindice1 <= 0) aid.host = ""; else aid.host =
		 * aidString.substring(aidindice1, aidindice2); aid.port =
		 * aidString.substring(aidindice2 + 1); if (i == 0) msg.setSender(aid);
		 * if (i == 1) msg.setReceiver(aid); if (i == 2) msg.setReplyTo(aid); }
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); // language
		 * msg.setLanguage(body.substring(indice2 + 1, indice2 + 1 + tam));
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); // encoding
		 * msg.setEncoding(body.substring(indice2 + 1, indice2 + 1 + tam));
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); //
		 * ontologyencodingACLMessage template
		 * msg.setOntology(body.substring(indice2 + 1, indice2 + 1 + tam));
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); // Protocol
		 * msg.setProtocol(body.substring(indice2 + 1, indice2 + 1 + tam));
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); //
		 * Conversation id msg.setConversationId(body.substring(indice2 + 1,
		 * indice2 + 1 + tam)); indice1 = indice2 + 1 + tam; indice2 =
		 * body.indexOf('#', indice1); tam =
		 * Integer.parseInt(body.substring(indice1, indice2)); // Reply with
		 * msg.setReplyWith(body.substring(indice2 + 1, indice2 + 1 + tam));
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf("#", indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); // In reply
		 * to msg.setInReplyTo(body.substring(indice2 + 1, indice2 + 1 + tam));
		 * indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#', indice1);
		 * tam = Integer.parseInt(body.substring(indice1, indice2)); // reply by
		 * if (tam != 0) msg.setReplyByDate(new
		 * Date(Integer.parseInt(body.substring( indice2 + 10, indice2 +
		 * tam)))); indice1 = indice2 + 1 + tam; indice2 = body.indexOf('#',
		 * indice1); tam = Integer.parseInt(body.substring(indice1, indice2));
		 * // Content msg.setContent(body.substring(indice2 + 1, indice2 + 1 +
		 * tam)); MessageProperties mp =
		 * xfr.getHeader().get(MessageProperties.class); if (c.isSecureMode()) {
		 * if (mp == null) throw new Exception(
		 * "In Magentix Secure mode, the UserID is required in message."); else
		 * try { if (!msg.getSender().name.equals(new java.lang.String(mp
		 * .getUserId(), "UTF-8"))) throw new Exception( "Sender field (" +
		 * msg.getSender().name +
		 * ") doesn't match with the name of the sender agent (" + new
		 * java.lang.String(mp.getUserId(), "UTF-8") + ")"); } catch
		 * (java.io.UnsupportedEncodingException e) { } } return msg;
		 */
	}
	
	/**
	 * Transforms the message to TraceEvent
	 * 
	 * @param xfr
	 *            MessageTransfer
	 * @return TraceEvent
	 */
	private final TraceEvent MessageTransfertoTraceEvent(MessageTransfer xfr) {
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

		tEvent.setTimestamp(Long.parseLong(body.substring(indice2, indice1 + tam)));

		// Event Type
		indice1 = indice1 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		tEvent.setTracingService(body.substring(indice2 + 1, indice2 + 1 + tam));

		// Origin Entity
		AgentID aid = new AgentID();
		int type;
		aidindice1 = 0;
		aidindice2 = 0;
		indice1 = indice2 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		type = Integer.parseInt(body.substring(indice1, indice2));
		indice1 = indice2 + 1;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		if (type == TracingEntity.AGENT) {
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

			tEvent.setOriginEntity(new TracingEntity(type, aid));
		}
		// Content
		indice1 = indice1 + 1 + tam;
		indice2 = body.indexOf('#', indice1);
		tam = Integer.parseInt(body.substring(indice1, indice2));
		tEvent.setContent(body.substring(indice2 + 1));
		
		/*
		TraceEvent tEvent = new TraceEvent(parts[2], new TracingEntity(Integer.parseInt(parts[3]), new AgentID(parts[5])), parts[6]);
		tEvent.setTimestamp(Long.parseLong(parts[0]));
		if (Integer.parseInt(parts[2])!=TracingEntity.AGENT)
			tEvent.setOriginEntity(null);
		*/
		return tEvent;
	}
}
