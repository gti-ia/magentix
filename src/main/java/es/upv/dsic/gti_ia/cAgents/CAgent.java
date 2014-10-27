// CAMBIOS PRINCIPALES EN EL PAQUETE

// Inicializaci�n y finalizaci�n del cAgent. Ahora existe un CProcesor que 
// ejecuta la conversaci�n de bienvenida. Cuando se llama a CAgent.Shutdown 
// las conversaciones activas pasan a un nuevo estado de excepci�n llamado "SHUTDOWN".
// Cuando todas las conversaciones acaban, finaliza la conversaci�n de bienvenida.

// El estado tipo SEND ahora s�lo sirve para enviar mensajes a otros agentes.

// Las conversaciones hijas se crean ahora llamando a m�todos.

// La gesti�n de conversationIDs es ahora totalmente autom�tica.

// Al poder crear subconversaciones mediante el API ya no son necesarias
// las autostart factories.

// Los m�todos de los estados son ahora reemplazables. Esto permite, por ejemplo,
// cambiar el m�todo del estado BEGIN sin tener que cambiar el estado entero. De
// esta forma, cuando se crea un procesador los estados especiales se crean
// de forma autom�tica y el usuario s�lo tiene que cambiar el m�todo asociado.

// Todas las acciones internas de un Cagent, sus fabricas y procesadores incluidos,
// se realizan ahora en exclusi�n mutua. Hay un �nico mutex para el agente que
// comparten sus procesadores y sus f�bricas.

// En core debe implementarse c�mo comparar un mensaje con un mensaje que actua como template. Lo que 
//   se hace en alg�n lugar de los Cagents s�lo compara la performativa y las cabeceras de usuario

// Los protocolos est�n ahora en el subpaquete protocols.

// He modificado en core ACLMessage y BaseAgent !!!!!!!

// DIRECTRICES

// Usar log4java para los mensajes internos. Si queda alg�n println sustituir.

// Formatear el c�digo desde Eclipse con "Source/Format"

// Todos los campos de la clase privados, ofreciendo una funci�n get asociada.
// La visibilidad de los m�todos al m�nimo:
//   private si solo lo usa la propia clase
//   "nada" si solo se usa desde dentro del paquete
//   protected si puede usarse desde otros paquetes pero s�lo desde una subclase
//   public en otro caso
// de todas formas, Java impone sus reglas, concretamente, nunca puede reducirse
// la visibilidad de un m�todo cuando se reimplementa. Ante esto no hay nada que
// hacer.

// Todos los m�todos tienen que estar protegidos por el mutex general del agente.

// ASUNTOS PENDIENTES

// Templates con AND, OR, NOT y () para estados Receive u f�bricas
// Completar protocolo FIPA REQUEST
// Implementar protocolo CONTRACT_NET
// En el c�digo hay comentarios //PENDIENTE y //??? para revisar.
// Estados de excepci�n. Todav�a no los he modificado. Tenemos que hablar sobre
// ellos primero.
// La construcci�n de aut�matas es muy dada a cometer errores dado que se usan etiquetas
//   por lo que habr� que esmerar el uso de excepciones.

package es.upv.dsic.gti_ia.cAgents;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Queue;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * An agent has to extend this class in order to use the conversation support.
 * 
 * @author Ricard Lopez Fogues
 * @author David Fernández - dfernandez@dsic.upv.es - Fixed CAgent porcessing
 *         messages with default factory when a proper factory should process
 *         them
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * @author Javier Jorge Cano - jjorge@dsic.upv.es
 */

public abstract class CAgent extends BaseAgent {

	private Map<String, CProcessor> processors = new HashMap<String, CProcessor>();
	private Map<String, Timer> timers = new HashMap<String, Timer>();
	private HashMap<String, HashMap<String, Long>> deadlines = new HashMap<String, HashMap<String, Long>>();
	ReentrantLock mutex = new ReentrantLock();
	private CFactory welcomeFactory;
	protected CProcessor welcomeProcessor;
	protected CFactory defaultFactory;
	ArrayList<CFactory> initiatorFactories = new ArrayList<CFactory>();
	ArrayList<CFactory> participantFactories = new ArrayList<CFactory>();

	public ExecutorService exec; // Bexy: Added 'public'
	// Semaphore availableSends = new Semaphore(1, true);
	final Condition iAmFinished = mutex.newCondition();
	final CountDownLatch agentEnd = new CountDownLatch(1);
	final Condition cProcessorRemoved = mutex.newCondition();
	public boolean inShutdown = false;
	boolean ready = false; // Is CAgent ready to attend messages?
	final Condition iAmReady = mutex.newCondition(); // Condition to stop the
														// message processing
														// until agent is ready

	private long conversationCounter = 0;
	private long pendingQueueDeltaToExpire = 10 * 60 * 1000;
	private long pendingQueueIntervalToClean = 60 * 1000;
	private PendingQueueRepository pendingQueues = new PendingQueueRepository(pendingQueueDeltaToExpire, pendingQueueIntervalToClean);
	
	/**
	 * Creates a new CAgent
	 * 
	 * @param aid
	 * @throws Exception
	 */
	public CAgent(AgentID aid) throws Exception {
		super(aid);
		exec = Executors.newCachedThreadPool();
	}

	/**
	 * Locks the agent's mutex
	 */
	public void lock() {
		this.mutex.lock();
	}

	/**
	 * Unlocks the agent's mutex
	 */
	public void unlock() {
		this.mutex.unlock();
	}

	/**
	 * Add a new factory that will create conversations where this agent will
	 * play the initiator role
	 * 
	 * @param factory
	 *            to be added as a initiator one
	 */
	public void addFactoryAsInitiator(CFactory factory) {
		this.lock();
		factory.setInitiator(true);
		initiatorFactories.add(factory);
		this.unlock();
	}

	/**
	 * Add a new factory that will create conversations where this agent will
	 * play the participant role
	 * 
	 * @param factory
	 *            to be added as a participant one
	 */
	public void addFactoryAsParticipant(CFactory factory) {
		this.lock();
		// Check if there are any message in the PendingQueueRepository
		// that matches with the factory template.
		ArrayList<QueueWithTimestamp> goodQueues = pendingQueues.popQueues(factory.getFilter());
		if (goodQueues.size() > 0) {
			for (QueueWithTimestamp qwt : goodQueues) {
				Queue<ACLMessage> q = qwt.getQueue();
				factory.startConversation(q, null, false);
			}
		}
		factory.setInitiator(false);
		participantFactories.add(factory);
		this.unlock();
	}

	/**
	 * This method should not be modified.
	 * 
	 * @param msg
	 *            message received by the agent
	 */
	public void onMessage(ACLMessage msg) {

		// this.logger.info(this.getName() + " receives the message "
		// + msg.getPerformative() + " " + msg.getContent());
		this.processMessage(msg);
	}

	/**
	 * Removes the specified factory
	 * 
	 * @param name
	 *            of the factory to remove
	 */
	public void removeFactory(String name) {
		this.lock();
		for (int i = 0; i < initiatorFactories.size(); i++) {
			if (initiatorFactories.get(i).name.equals(name)) {
				initiatorFactories.remove(i);
				this.unlock();
				return;
			}
		}
		for (int i = 0; i < participantFactories.size(); i++) {
			if (participantFactories.get(i).name.equals(name)) {
				participantFactories.remove(i);
				this.unlock();
				return;
			}
		}
		this.unlock();
	}

	/**
	 * Terminates the agent's execution
	 */
	public void Shutdown() {
		this.lock();
		this.inShutdown = true;
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("PURPOSE", "SHUTDOWN");
		if (processors.size() > 1) {

			for (CProcessor c : processors.values()) {
				if (!c.getMyFactory().equals(welcomeFactory)) {

					c.addMessage(msg);
					if (c.isIdle()) {
						c.setIdle(false);
						exec.execute(c);
					}

				}
			}
		} else {

			this.notifyLastProcessorRemoved();
		}
		this.unlock();
	}

	public void send(ACLMessage msg) {

		// this.logger.info(this.getName() + " sends " + msg.getReceiverList()
		// + " the message " + msg.getPerformative() + " "
		// + msg.getContent());

		this.lock();
		super.send(msg);
		this.unlock();

	}

	/**
	 * This method creates the default factory that will manage the messages
	 * that no other factory can
	 * 
	 * @param me
	 *            The agent owner of this factory
	 */
	protected void createDefaultFactory(final CAgent me) {

		// PENDIENTE
		// Probar y definir defaultfactory

		defaultFactory = new CFactory("DefaultFactory", new MessageFilter(
				"performative = UNKNWON"), 1, this);

		// BEGIN STATE

		BeginState BEGIN = (BeginState) defaultFactory.cProcessorTemplate()
				.getState("BEGIN");
		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				logger.info("Default factory processing message "
						+ msg.getContent() + " source: " + msg.getSender()
						+ " ConversationID: " + msg.getConversationId());
				return "FINAL";
			}
		}
		BEGIN.setMethod(new BEGIN_Method());

		// FINAL STATE

		FinalState FINAL = new FinalState("FINAL");

		class F_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage msg) {
				logger.info("Default factory processing message "
						+ msg.getContent() + " sourse: " + msg.getSender()
						+ " ConversationID: " + msg.getConversationId());
			}
		}
		FINAL.setMethod(new F_Method());
		defaultFactory.cProcessorTemplate().registerState(FINAL);
		defaultFactory.cProcessorTemplate().addTransition("BEGIN", "FINAL");

	}

	/**
	 * This method creates the factory that will control the execution of the
	 * agent
	 * 
	 * @param me
	 *            The agent owner of this factory
	 */
	private void createWelcomeFactory(final CAgent me) { /*
														 * Cambio factoria
														 * welcome para que sea
														 * coherente con el
														 * hecho de que se hace
														 * peek de los mensajes
														 * en los estados begin
														 * y no remove
														 */
		welcomeFactory = new CFactory("WelcomeFactory", new MessageFilter(
				"performative = UNKNOWN"), 1, this);

		// BEGIN STATE

		BeginState BEGIN = (BeginState) welcomeFactory.cProcessorTemplate()
				.getState("BEGIN");
		class BEGIN_Method implements BeginStateMethod {

			public String run(CProcessor myProcessor, ACLMessage msg) {
				// me.Initialize(myProcessor, msg);
				return "WAIT";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		// WAIT STATE

		WaitState WAIT = new WaitState("WAIT", 0);
		welcomeFactory.cProcessorTemplate().registerState(WAIT);
		welcomeFactory.cProcessorTemplate().addTransition("BEGIN", "WAIT");

		// RECEIVE WELCOME STATE

		ReceiveState RECEIVE_WELCOME = new ReceiveState("RECEIVE_WELCOME");
		class RECEIVE_WELCOME_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage receivedMessage) {
				// myProcessor.getInternalData().put("AGENT_END_MSG",
				// receivedMessage);

				me.lock();

				if (!ready) {
					ready = true;
					iAmReady.signal();
					logger.info("Wake up after Welcome factory initialization, agent: "
							+ myProcessor.getMyAgent().getName());
				}

				me.unlock();
				me.execution(myProcessor, receivedMessage);
				return "WAIT2";
			}
		}
		RECEIVE_WELCOME.setMethod(new RECEIVE_WELCOME_Method());
		MessageFilter filter = new MessageFilter("performative = INFORM");
		RECEIVE_WELCOME.setAcceptFilter(filter);
		welcomeFactory.cProcessorTemplate().registerState(RECEIVE_WELCOME);
		welcomeFactory.cProcessorTemplate().addTransition("WAIT",
				"RECEIVE_WELCOME");

		// WAIT STATE 2

		WaitState WAIT2 = new WaitState("WAIT2", 0);
		welcomeFactory.cProcessorTemplate().registerState(WAIT2);
		welcomeFactory.cProcessorTemplate().addTransition("RECEIVE_WELCOME",
				"WAIT2");

		// RECEIVE STATE

		ReceiveState RECEIVE = new ReceiveState("RECEIVE");
		class RECEIVE_Method implements ReceiveStateMethod {
			public String run(CProcessor myProcessor, ACLMessage receivedMessage) {
				myProcessor.getInternalData().put("AGENT_END_MSG",
						receivedMessage);
				return "FINAL";
			}
		}
		RECEIVE.setMethod(new RECEIVE_Method());
		MessageFilter filter2 = new MessageFilter(
				"performative = UNKNOWN AND PURPOSE = AGENT_END");
		RECEIVE.setAcceptFilter(filter2);
		welcomeFactory.cProcessorTemplate().registerState(RECEIVE);
		welcomeFactory.cProcessorTemplate().addTransition("WAIT2", "RECEIVE");

		// FINAL STATE

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_METHOD implements FinalStateMethod {

			public void run(CProcessor myProcessor, ACLMessage msg) {
				msg.copyFromAsTemplate((ACLMessage) myProcessor
						.getInternalData().get("AGENT_END_MSG"));
				me.finalize(myProcessor, msg);
				myProcessor.getMyAgent().notifyAgentEnd();
			}
		}

		FINAL.setMethod(new FINAL_METHOD());

		welcomeFactory.cProcessorTemplate().registerState(FINAL);
		welcomeFactory.cProcessorTemplate().addTransition(RECEIVE, FINAL);

	}

	/**
	 * This method signals the end of the agent in order to unlock the main
	 * conversation thread and remove the barrier if the user wants expects the
	 * agent finalizes
	 */
	protected void notifyAgentEnd() {
		this.lock();
		iAmFinished.signal();
		agentEnd.countDown();
		this.unlock();
	}

	/**
	 * This method assigns a received message to a factory, an already running
	 * CProcessor or to the default Factory
	 * 
	 * @param msg
	 *            Message to be processed
	 */

	private void processMessage(ACLMessage msg) {
		this.lock();
		if (!ready) {
			try {
				iAmReady.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} // With this we stop the message process until agent is ready
		
		// Temporal code
		this.logger.info("Agent: " + this.getName() + " processing message");
		this.logger.info("Agent: " + this.getName() + " Number of processors: "+ processors.size());
		this.logger.info("Agent: " + this.getName() + " Number of Participant CFactories "+ participantFactories.size());
		// End of temporal code
		
		CProcessor auxProcessor = processors.get(msg.getConversationId());
		boolean accepted = false;
		if (auxProcessor != null) {
			auxProcessor.addMessage(msg);
			if (auxProcessor.isIdle()) {
				auxProcessor.setIdle(false);
				if (!msg.getHeaderValue("Purpose").equals("WaitMessage"))
					if (removeTimer(msg.getConversationId()))
						this.logger.info(this.getName() + " "
								+ msg.getConversationId() + " Timer canceled");
				exec.execute(auxProcessor);
			}
		} else if (!inShutdown) {
			for (int i = 0; i < participantFactories.size(); i++) {
				CFactory factory = participantFactories.get(i);
				if (factory.templateIsEqual(msg)) {
					factory.startConversation(createQueue(msg), null, false);
					accepted = true;
					break;
				}
			}
			if (!accepted && msg.getConversationId().compareTo("") != 0) {
				pendingQueues.addMessage(msg);
			} else if (!accepted) {
				this.logger.info("Agent: " + this.getName()
						+ " Message delivered to the DefaultFactory");
				defaultFactory.startConversation(createQueue(msg), null, false);
			}
		}
		this.unlock();
	}

	/**
	 * This is the main method of the agent. It creates the default and welcome
	 * factories and starts the welcome factory
	 */
	protected final void execute() {
		this.lock();

		createDefaultFactory(this);
		createWelcomeFactory(this);
		ACLMessage welcomeMessage = new ACLMessage(ACLMessage.INFORM);
		welcomeMessage.setContent("Welcome to this platform");
		welcomeMessage.setConversationId(this.newConversationID());
		welcomeProcessor = welcomeFactory.startConversation(createQueue(welcomeMessage),
				null, false);
		try {
			iAmFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.exec.shutdownNow();
		this.logger.info("Agent " + this.getName() + " ENDED");

		this.unlock();
	}

	/**
	 * This method is executed just before the agent ends its execution
	 * 
	 * @param firstProcessor
	 *            The CProcessor managing the welcome conversation
	 * @param finalizeMessage
	 *            The final message produced by this conversation
	 */
	protected abstract void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage);

	/**
	 * This is the main method of the agent
	 * 
	 * @param firstProcessor
	 *            The CProcessor managing the welcome conversation
	 * @param welcomeMessage
	 *            The message sent by the platform to the agent
	 */
	protected abstract void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage);

	/**
	 * Adds a CProcessor to the agent
	 * 
	 * @param conversationID
	 *            of the conversation that the CProcessor will manage
	 * @param processor
	 *            The CProcessor to add
	 */
	protected void addProcessor(String conversationID, CProcessor processor) {
		// Ricard
		this.lock();
		processors.put(conversationID, processor);
		this.unlock();
	}

	/**
	 * Adds a timer in a CProcessor, this is called from a wait state
	 * 
	 * @param conversationId
	 * @param stateName
	 * @param period
	 *            Time to wait
	 * @param waitType
	 *            Wait type (oneshot, absolut or periodic)
	 * @return true if a timer was added, false otherwise
	 */
	protected boolean addTimer(final String conversationId, String stateName,
			final long period, int waitType) {
		this.lock();
		final Date deadline;
		if (waitType == WaitState.ONESHOT) {
			deadline = new Date(System.currentTimeMillis() + period);
			Timer timer = new Timer();

			timer.schedule(new TimerTask() {
				public void run() {
					DateFormat df = DateFormat.getDateInstance();
					ACLMessage waitMessage = new ACLMessage(ACLMessage.INFORM);
					waitMessage.setHeader("purpose", "waitMessage");
					waitMessage.setContent(df.format(deadline));
					waitMessage.setConversationId(conversationId);
					processMessage(waitMessage);
				}
			}, deadline);
			this.timers.put(conversationId, timer);
			this.unlock();
			return true;
		} else if (waitType == WaitState.ABSOLUT) {
			long auxDeadline;
			if (this.deadlines.get(conversationId) == null)// the first timer
															// for this
															// conversation
				auxDeadline = System.currentTimeMillis() + period;
			else if (this.deadlines.get(conversationId).get(stateName) == null)// the
																				// first
																				// time
																				// the
																				// conversation
																				// passes
																				// through
																				// this
																				// state
				auxDeadline = System.currentTimeMillis() + period;
			else {
				auxDeadline = this.deadlines.get(conversationId).get(stateName);
				if (auxDeadline <= System.currentTimeMillis())
					auxDeadline = System.currentTimeMillis() + period;
			}
			deadline = new Date(auxDeadline);
			DateFormat df = DateFormat.getTimeInstance();
			this.logger.info("Deadline " + this.getName() + " "
					+ df.format(deadline));
			Timer timer = new Timer();

			timer.schedule(new TimerTask() {
				public void run() {
					DateFormat df = DateFormat.getDateInstance();
					ACLMessage waitMessage = new ACLMessage(ACLMessage.INFORM);
					waitMessage.setHeader("purpose", "waitMessage");
					waitMessage.setContent(df.format(deadline));
					waitMessage.setConversationId(conversationId);
					processMessage(waitMessage);
				}
			}, deadline);
			this.timers.put(conversationId, timer);
			if (this.deadlines.get(conversationId) == null)
				this.deadlines.put(conversationId, new HashMap<String, Long>());
			this.deadlines.get(conversationId).put(stateName,
					new Long(auxDeadline));
			this.unlock();
			return true;
		} else if (waitType == WaitState.PERIODIC) {
			this.unlock();
			long auxDeadline;
			if (this.deadlines.get(conversationId) == null)// the first timer
															// for this
															// conversation
				auxDeadline = System.currentTimeMillis() + period;
			else if (this.deadlines.get(conversationId).get(stateName) == null)// the
																				// first
																				// time
																				// the
																				// conversation
																				// passes
																				// through
																				// this
																				// state
				auxDeadline = System.currentTimeMillis() + period;
			else {
				auxDeadline = this.deadlines.get(conversationId).get(stateName);
				if (auxDeadline <= System.currentTimeMillis()) {
					ACLMessage waitMessage = new ACLMessage(ACLMessage.INFORM);
					waitMessage.setHeader("purpose", "waitMessage");
					waitMessage.setConversationId(conversationId);
					processMessage(waitMessage);
					while (auxDeadline < System.currentTimeMillis())
						auxDeadline += period;
				}
			}
			deadline = new Date(auxDeadline);
			DateFormat df = DateFormat.getTimeInstance();
			this.logger.info("Deadline " + df.format(deadline));
			Timer timer = new Timer();

			timer.schedule(new TimerTask() {
				public void run() {
					DateFormat df = DateFormat.getDateInstance();
					ACLMessage waitMessage = new ACLMessage(ACLMessage.INFORM);
					waitMessage.setHeader("purpose", "waitMessage");
					waitMessage.setContent(df.format(deadline));
					waitMessage.setConversationId(conversationId);
					processMessage(waitMessage);
				}
			}, deadline);
			this.timers.put(conversationId, timer);
			if (this.deadlines.get(conversationId) == null)
				this.deadlines.put(conversationId, new HashMap<String, Long>());
			auxDeadline = auxDeadline + period;// we store the next deadline
												// after the timer sends the
												// message
			this.deadlines.get(conversationId).put(stateName,
					new Long(auxDeadline));
			// this.unlock();
			return true;
		}
		// this.unlock();
		return false;
	}

	/**
	 * Ends a conversation
	 * 
	 * @param theFactory
	 *            managing the conversation that is going to be finished
	 */
	protected void endConversation(CFactory theFactory) {
		this.lock();
		if (theFactory.getLimit() != 0) {
			theFactory.availableConversations.release();
		}
		this.unlock();
	}

	/**
	 * Gets a new conversation identifier
	 * 
	 * @return new conversation identifier
	 */
	// protected synchronized String newConversationID() {
	public synchronized String newConversationID() {
		// return this.getName() + "." + UUID.randomUUID().toString();
		this.conversationCounter++;
		return this.getName() + "." + this.conversationCounter;
	}

	/**
	 * When the agent has ended all its conversations this method is called
	 */
	private void notifyLastProcessorRemoved() {
		this.lock();
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("PURPOSE", "AGENT_END");
		msg.setContent("See you");
		welcomeProcessor.addMessage(msg);
		if (welcomeProcessor.isIdle()) {
			welcomeProcessor.setIdle(false);
			exec.execute(welcomeProcessor);
		}
		this.unlock();
	}

	/**
	 * Removes a processor identified by its conversation id
	 * 
	 * @param conversationID
	 *            of the conversation that the CProcessor that is going to be
	 *            removed is managing
	 */
	protected void removeProcessor(String conversationID) {
		this.lock();

		processors.remove(conversationID);

		if (inShutdown) {
			if (processors.size() == 1) {
				this.notifyLastProcessorRemoved();
			}
		}
		this.unlock();
	}

	/**
	 * Removes a timer of a CProcessor
	 * 
	 * @param conversationId
	 *            of the conversation that the CProcessor is managing
	 * @return true if a timer was removed, false otherwise
	 */
	private boolean removeTimer(String conversationId) {
		this.lock();
		if (this.timers.get(conversationId) == null) {
			this.unlock();
			return false;
		} else {
			this.timers.get(conversationId).cancel();
			this.timers.remove(conversationId);
			this.unlock();
			return true;
		}
	}

	/**
	 * Starts a conversation
	 * 
	 * @param msg
	 *            Initial message
	 * @param parent
	 *            CProcessor
	 * @param sync
	 *            true if it is a synchronous conversation or false it is
	 *            asynchronous
	 */
	protected void startConversation(ACLMessage msg, CProcessor parent,
			Boolean sync) {
		this.lock();
		for (int i = 0; i < initiatorFactories.size(); i++) {
			if (initiatorFactories.get(i).templateIsEqual(msg)) {
				initiatorFactories.get(i).startConversation(createQueue(msg), parent, sync);
				this.unlock();
				return;
			}
		}
		logger.error("There aren't factories that match with the message's template");
		this.unlock();
		// PENDIENTE: Lanzar excepci�n si no hay fabricas asociadas
	}

	/**
	 * Starts a new conversation asynchronously
	 * 
	 * @param factoryName
	 *            Name of the initiator factory that will create the
	 *            conversation
	 */
	public void startSyncConversation(String factoryName) {
		this.lock();
		for (int i = 0; i < initiatorFactories.size(); i++) {
			if (initiatorFactories.get(i).name.equals(factoryName)) {
				this.welcomeProcessor.createSyncConversation(
						initiatorFactories.get(i), newConversationID());
				this.unlock();

				return;
			}
		}
		logger.error("There aren't factories that match with the message's template");
		this.unlock();
	}

	/**
	 * Gets the hold count of the agent's mutex
	 * 
	 * @return mutex hold count
	 */
	public int getMutexHoldCount() {
		return this.mutex.getHoldCount();
	}

	/**
	 * Wait until the agent actually finalize
	 */
	public void await() {
		try {
			agentEnd.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a queue with all messages received as function parameters
	 * 
	 * @param 	messages
	 * 			List of ACLMessages that will are added to the queue
	 * 			
	 * @return Queue with all messages
	 */
	private Queue<ACLMessage> createQueue(ACLMessage ... messages){
		Queue<ACLMessage> qMsg = new LinkedList<ACLMessage>();
		for (ACLMessage msg : messages) {
			qMsg.add(msg);
		}
		return qMsg;
	}
	
	/**
	 * Get the DeltaToExpire parameter value
	 * 
	 * @return DeltaToExpire
	 */
	public long getPendingQueueDeltaToExpire(){
		return pendingQueues.getDeltaToExpire();
	}
	
	/**
	 * Set the DeltaToExpire parameter value
	 * 
	 * @param 	delta
	 * 			Maximum time that one message is stored in pendingQueue
	 */
	public void setPendingQueueDeltaToExpire(long delta){
		pendingQueues.setDeltaToExpire(delta);
	}
	
	/**
	 * Get the IntervalToClean parameter value
	 * 
	 * @return IntervalToClean
	 */
	public long getPendingQueueIntervalToClean(){
		return pendingQueues.getIntervalToClean();
	}
	
	/**
	 * Set the IntervalToClean parameter value
	 * 
	 * @param 	interval
	 * 			Minimum interval of time that the expiry of a message is checked in pendingQueue
	 */
	public void setPendingQueueIntervalToClean(long interval){
		pendingQueues.setIntervalToClean(interval);
	}
}
