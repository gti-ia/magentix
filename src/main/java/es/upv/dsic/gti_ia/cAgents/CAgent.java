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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public abstract class CAgent extends BaseAgent {

	private Map<String, CProcessor> processors = new HashMap<String, CProcessor>();
	private Map<String, Timer> timers = new HashMap<String, Timer>();
	ReentrantLock mutex = new ReentrantLock();
	private CProcessorFactory welcomeFactory;
	private CProcessor welcomeProcessor;
	private CProcessorFactory defaultFactory;
	ArrayList<CProcessorFactory> initiatorFactories = new ArrayList<CProcessorFactory>();
	ArrayList<CProcessorFactory> participantFactories = new ArrayList<CProcessorFactory>();

	ExecutorService exec;
	// Semaphore availableSends = new Semaphore(1, true);
	final Condition iAmFinished = mutex.newCondition();
	final Condition cProcessorRemoved = mutex.newCondition();
	boolean inShutdown = false;

	public CAgent(AgentID aid) throws Exception {
		super(aid);
		exec = Executors.newCachedThreadPool();
	}

	public void lock() {
		this.mutex.lock();
	}

	public void unlock() {
		this.mutex.unlock();
	}

	public void addFactoryAsInitiator(CProcessorFactory factory) {
		this.lock();
		initiatorFactories.add(factory);
		this.unlock();
	}

	public void addFactoryAsParticipant(CProcessorFactory factory) {
		this.lock();
		participantFactories.add(factory);
		this.unlock();
	}

	public void onMessage(ACLMessage msg) {

		this.logger.info(this.getName() + " receives the message "
				+ msg.getPerformative() + " " + msg.getContent());
		this.processMessage(msg);
	}

	public void removeFactory(String name) {
		this.lock();
		for (int i = 0; i < initiatorFactories.size(); i++) {
			if (initiatorFactories.get(i).name.equals(name)) {
				initiatorFactories.remove(i);
				return;
			}
		}
		for (int i = 0; i < participantFactories.size(); i++) {
			if (participantFactories.get(i).name.equals(name)) {
				participantFactories.remove(i);
				return;
			}
		}
		this.unlock();
	}

	public void Shutdown() {
		this.lock();
		this.inShutdown = true;
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("PURPOSE", "SHUTDOWN");
		if (processors.size() > 1) {
			for (CProcessor c : processors.values()) {
				if (!c.getMyFactory().equals(welcomeFactory)) {
					c.addMessage(msg);
				}
			}
		} else {
			this.notifyLastProcessorRemoved();
		}
		this.unlock();
	}

	public void send(ACLMessage msg) {
		this.logger.info(this.getName() + " sends " + msg.getReceiver().name + " the message "
				+ msg.getPerformative() + " " + msg.getContent());
		this.lock();
		super.send(msg);
		this.unlock();

	}

	private void createDefaultFactory(final CAgent me) {

		// PENDIENTE
		// Probar y definir defaultfactory

		defaultFactory = new CProcessorFactory("DefaultFactory",
				new MessageFilter("performative = UNKNWON"), 1,this);

		// BEGIN STATE

		BeginState BEGIN = (BeginState) defaultFactory.cProcessorTemplate()
				.getState("BEGIN");
		class BEGIN_Method implements BeginStateMethod {
			public String run(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Default factory tratando mensaje "
						+ msg.getContent() + " origen: " + msg.getSender()
						+ " ConversationID: " + msg.getConversationId());
				return "FINAL";
			}
		}
		BEGIN.setMethod(new BEGIN_Method());

		// FINAL STATE

		FinalState FINAL = new FinalState("FINAL");

		class F_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Default factory tratando mensaje "
						+ msg.getContent() + " origen: " + msg.getSender()
						+ " ConversationID: " + msg.getConversationId());
			}
		}
		FINAL.setMethod(new F_Method());
		defaultFactory.cProcessorTemplate().registerState(FINAL);
		defaultFactory.cProcessorTemplate().addTransition("BEGIN", "FINAL");


	}

	private void createWelcomeFactory(final CAgent me) {
		welcomeFactory = new CProcessorFactory("WelcomeFactory",
				new MessageFilter("performative = UNKNOWN"), 1, this);

		// BEGIN STATE

		BeginState BEGIN = (BeginState) welcomeFactory.cProcessorTemplate()
				.getState("BEGIN");
		class BEGIN_Method implements BeginStateMethod {

			public String run(CProcessor myProcessor, ACLMessage msg) {
				me.Initialize(myProcessor, msg);
				return "WAIT";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		// WAIT STATE

		WaitState WAIT = new WaitState("WAIT", 0);
		welcomeFactory.cProcessorTemplate().registerState(WAIT);
		welcomeFactory.cProcessorTemplate().addTransition("BEGIN", "WAIT");

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
		MessageFilter msg = new MessageFilter("performative = UNKNOWN AND PURPOSE = AGENT_END");
		RECEIVE.setAcceptFilter(msg);
		welcomeFactory.cProcessorTemplate().registerState(RECEIVE);
		welcomeFactory.cProcessorTemplate().addTransition("WAIT", "RECEIVE");

		// FINAL STATE

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_METHOD implements FinalStateMethod {

			public void run(CProcessor myProcessor, ACLMessage msg) {
				msg.copyFromAsTemplate((ACLMessage) myProcessor
						.getInternalData().get("AGENT_END_MSG"));
				me.Finalize(myProcessor, msg);
				myProcessor.getMyAgent().notifyAgentEnd();
			}
		}

		FINAL.setMethod(new FINAL_METHOD());

		welcomeFactory.cProcessorTemplate().registerState(FINAL);

	}

	void notifyAgentEnd() {
		this.lock();
		iAmFinished.signal();
		this.unlock();
	}

	private void processMessage(ACLMessage msg) {

		this.lock();
		CProcessor auxProcessor = processors.get(msg.getConversationId());
		boolean accepted = false;
		if (auxProcessor != null) {
			auxProcessor.addMessage(msg);
			if (auxProcessor.isIdle()) {
				auxProcessor.setIdle(false);
				if (!msg.getHeaderValue("Purpose").equals("WaitMessage"))
					if (removeTimer(msg.getConversationId()))
						System.out.println("Timer cancelado");
				exec.execute(auxProcessor);
			}
		} else if (!inShutdown) {
			for (int i = 0; i < participantFactories.size(); i++) {
				CProcessorFactory factory = participantFactories.get(i);
				if (factory.templateIsEqual(msg)) {
					factory.startConversation(msg, null, false);
					accepted = true;
					break;
				}
			}
			if (!accepted) {
				this.logger.info("Agent: " + this.getName()
						+ " Message delivered to the DefaultFactory");
				defaultFactory.startConversation(msg, null, false);
			}
		}
		this.unlock();
	}

	protected final void execute() {
		this.lock();

		createDefaultFactory(this);
		createWelcomeFactory(this);
		ACLMessage welcomeMessage = new ACLMessage(ACLMessage.INFORM);
		welcomeMessage.setContent("Welcome to this platform");
		welcomeMessage.setConversationId(this.newConversationID());
		welcomeProcessor = welcomeFactory.startConversation(welcomeMessage,
				null, false);
		try {
			iAmFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.logger.info("Agent " + this.getName() + "ENDED");

		this.unlock();

	}

	protected abstract void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage);

	protected abstract void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage);

	void addProcessor(String conversationID, CProcessor processor) {
		this.lock();
		processors.put(conversationID, processor);
		this.unlock();
	}

	boolean addTimer(final String conversationId, long milliseconds) {
		this.lock();
		if (this.timers.get(conversationId) == null) {
			Date timeToRun = new Date(System.currentTimeMillis() + milliseconds);
			Timer timer = new Timer();

			timer.schedule(new TimerTask() {
				public void run() {
					ACLMessage waitMessage = new ACLMessage(ACLMessage.INFORM);
					waitMessage.setHeader("purpose", "waitMessage");
					waitMessage.setContent("Límite temporal alcanzado");
					waitMessage.setConversationId(conversationId);
					processMessage(waitMessage);
				}
			}, timeToRun);
			this.timers.put(conversationId, timer);
			this.unlock();
			return true;
		} else
			this.unlock();
		return false;
	}

	void endConversation(CProcessorFactory theFactory) {
		this.lock();
		if (theFactory.getLimit() != 0) {
			theFactory.availableConversations.release();
		}
		this.unlock();
	}

	String newConversationID() {
		return this.getName() + "." + UUID.randomUUID().toString();
	}

	void notifyLastProcessorRemoved() {
		this.lock();
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("PURPOSE", "AGENT_END");
		msg.setContent("See you");
		welcomeProcessor.addMessage(msg);
		this.unlock();
	}

	void removeProcessor(String conversationID) {
		this.lock();
		processors.remove(conversationID);
		if (inShutdown) {
			if (processors.size() == 1) {
				this.notifyLastProcessorRemoved();
			}
		}
		this.unlock();
	}

	boolean removeTimer(String conversationId) {
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

	void startConversation(ACLMessage msg, CProcessor parent, Boolean sync) {
		this.lock();
		for (int i = 0; i < initiatorFactories.size(); i++) {
			if (initiatorFactories.get(i).templateIsEqual(msg)) {
				initiatorFactories.get(i).startConversation(msg, parent, sync);
				this.unlock();
				return;
			}
		}
		System.out.println("No hay factorias");
		this.unlock();
		// PENDIENTE: Lanzar excepci�n si no hay fabricas asociadas
	}
}