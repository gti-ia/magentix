// CAMBIOS PRINCIPALES EN EL PAQUETE

// Inicialización y finalización del cAgent
//  >> OK
// Usar el estado Send sólo para enviar mensajes fuera sin poder cambiar el ID
//  >> OK
// Nuevos métodos para lanzar conversaciones síncronas y asíncronas
//  >> OK pero falta ejemplo asíncrona
// Gestión totalmente automatizada de IDs
//  >> Falta hacer que las subconversaciones compartan IDs con la padre
// Revisar visibilidad de datos internos de conversación a conversaciones hijas
//  >> OK
// Eliminar starting factories
//  >> OK
// Métodos sustitiubles en los estados de los autómatas
//  >> OK
// Fábricas participantes anidadas
// Revisar accesos en exclusión mutua
// Creo que necesario un lock global. Interno seguro y externo creo que también.
// Supongo que deberá ser el mismo.

// En core debe implementarse cómo comparar un mensaje con un mensaje que actua como template. Lo que 
//   se hace en algún lugar de los Cagents sólo compara la performativa y las cabeceras de usuario

// Usar log4java
// La construcción de autómatas es muy dada a cometer errores dado que se usan etiquetas
//   por lo que habrá que esmerar el uso de excepciones
// Revisar estados de excepción
// Metodo para evaluar mensajes aceptados en estado receive, como
//   complemento al filtro
// Método para evaluar mensajes aceptados por fábricas, como complemento al filtro

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
		this.logger.info(this.getName() + " sends the message "
				+ msg.getPerformative() + " " + msg.getContent());
		this.lock();
		super.send(msg);
		this.unlock();

	}

	private void createDefaultFactory(final CAgent me) {

		// PENDIENTE
		// Probar y definir defaultfactory

		defaultFactory = new CProcessorFactory("DefaultFactory",
				new ACLMessage(ACLMessage.UNKNOWN), 1,this);

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
				new ACLMessage(ACLMessage.UNKNOWN), 1, this);

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
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("PURPOSE", "AGENT_END");
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
					waitMessage.setContent("LÃ­mite temporal alcanzado");
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
		// PENDIENTE: Lanzar excepción si no hay fabricas asociadas
	}
}