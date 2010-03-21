// CAMBIOS PRINCIPALES EN EL PAQUETE

// Inicialización y finalización del cAgent
//  >> A medias. Falta el mecanismo de finalización ordenada de conversaciones.
// Usar el estado Send sólo para enviar mensajes fuera sin poder cambiar el ID
//  >> OK
// Nuevos métodos para lanzar conversaciones síncronas y asíncronas
//  >> OK
// Gestión totalmente automatizada de IDs
//  >> OK
// Revisar visibilidad de datos internos de conversación a conversaciones hijas
//  >> OK
// Eliminar starting factories
//  >> OK
// Métodos sustitiubles en los estados de los autómatas
//  >> OK

// Creo que necesario un lock global. Interno seguro y externo creo que también. Supongo que deberá ser el mismo.

// En core debe implementarse cómo comparar un mensaje con un mensaje que actua como template. Lo que 
//   se hace en algún lugar de los Cagents sólo compara la performativa y las cabeceras de usuario

// Considerar cambiar argumento myProcessor en métodos de estado por método del xxxStateMethod
// Usar log4java
// La construcción de autómatas es muy dada a cometer errores dado que se usan etiquetas
//   por lo que habrá que esmerar el uso de excepciones
// Revisar estados de excepción
// Metodo para evaluar mensajes aceptados en estado receive, como
//   complemento al filtro
// Método para evaluar mensajes aceptados por fábricas, como complemento al filtro
// Combinar estructuralmente autómatas

// CAMBIOS EN ESTA CLASE

// Eliminada la cola de mensajes. Ahora onMessage procesa directamente el mensaje.
// SetFactories reemplazado por Initialize (welcomeMessage)
// La construcción de la default factory es muy distinta debido a
//   los cambios en las clases State, cProcessor y cProcessorFactory
// Desaparece la autostart factory

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
import java.util.concurrent.Semaphore;
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
	private ReentrantLock lock = new ReentrantLock();
	private CProcessorFactory welcomeFactory;
	private CProcessor welcomeProcessor;
	private CProcessorFactory defaultFactory;
	ArrayList<CProcessorFactory> initiatorFactories = new ArrayList<CProcessorFactory>();
	ArrayList<CProcessorFactory> participantFactories = new ArrayList<CProcessorFactory>();

	ExecutorService exec;
	Semaphore availableSends = new Semaphore(1, true); // ???
	final Condition iAmFinished = lock.newCondition();
	final Condition cProcessorRemoved = lock.newCondition();
	boolean inShutdown = false;


	public CAgent(AgentID aid) throws Exception {
		super(aid);
		exec = Executors.newCachedThreadPool();
	}

	public synchronized void addFactory(CProcessorFactory factory) {
		factory.setAgent(this);
		if (factory.getRole() == CProcessorFactory.FactoryRole.Initiator) {
			initiatorFactories.add(factory);
		} else {
			participantFactories.add(factory);
		}
	}

	public void onMessage(ACLMessage msg) {

		this.logger.info(this.getName() + " receives the message "
				+ msg.getPerformative() + " " + msg.getContent());
		this.processMessage(msg);
	}

	public synchronized void removeFactory(String name) {
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
	}

	public void Shutdown() {
		lock.lock();
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
		lock.unlock();
	}

	public void send(ACLMessage msg) {
		this.logger.info(this.getName() + " sends the message "
				+ msg.getPerformative() + " " + msg.getContent());
		super.send(msg);
	}

	private void createDefaultFactory(final CAgent me) {

		// PENDIENTE
		// Probar y definir defaultfactory

		defaultFactory = new CParticipantFactory("DefaultFactory",
				new ACLMessage(ACLMessage.UNKNOWN), 1000);

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

		defaultFactory.setAgent(this);

	}

	private void createWelcomeFactory(final CAgent me) {
		welcomeFactory = new CParticipantFactory("WelcomeFactory",
				new ACLMessage(ACLMessage.UNKNOWN), 1000);

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
				myProcessor.getInternalData().put("AGENT_END_MSG", receivedMessage);
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
				msg.copyFromAsTemplate((ACLMessage) myProcessor.getInternalData().get("AGENT_END_MSG"));
				me.Finalize(myProcessor, msg);
				myProcessor.getMyAgent().notifyAgentEnd();
			}
		}

		FINAL.setMethod(new FINAL_METHOD());

		welcomeFactory.cProcessorTemplate().registerState(FINAL);

		welcomeFactory.setAgent(this);

	}

	void notifyAgentEnd() {
		lock.lock();
		iAmFinished.signal();
		lock.unlock();
	}

	private synchronized void processMessage(ACLMessage msg) {

		lock.lock();
		CProcessor auxProcessor = processors.get(msg.getConversationId());
		boolean accepted = false;
		if (auxProcessor != null) {
			auxProcessor.addMessage(msg);
			if (auxProcessor.isIdle()) {
				auxProcessor.setIdle(false);
				if (!msg.getHeaderValue("Purpose").equals("WaitMessage")) // ???
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
				System.out.println("Agente: " + this.getName()
						+ " Mensaje a tratar por la DefaultFactory");
				defaultFactory.startConversation(msg, null, false);
			}
		}
		lock.unlock();
	}

	protected final void execute() {

		createDefaultFactory(this);
		createWelcomeFactory(this);
		ACLMessage welcomeMessage = new ACLMessage(ACLMessage.INFORM);
		welcomeMessage.setContent("Welcome to this platform");
		welcomeMessage.setConversationId(this.newConversationID());
		welcomeProcessor = welcomeFactory.startConversation(welcomeMessage,
				null, false);

		lock.lock();
		try {
			iAmFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("SACABO");

		lock.unlock();

	}

	protected abstract void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage);

	protected abstract void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage);

	synchronized void addProcessor(String conversationID, CProcessor processor) {
		processors.put(conversationID, processor);
	}

	synchronized boolean addTimer(final String conversationId, long milliseconds) {
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
			return true;
		} else
			return false;
	}

	void endConversation(CProcessorFactory theFactory) {
		theFactory.availableConversations.release();
	}

	class ShutdownProcess implements Runnable {

		public void run() {
			while (true) {
				lock.lock();
				try {
					cProcessorRemoved.await();
					if (processors.isEmpty()) {
						iAmFinished.signal();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
			}
		}
	}

	String newConversationID() {
		return this.getName() + "." + UUID.randomUUID().toString();
	}

	void notifyLastProcessorRemoved() {
		ACLMessage msg = new ACLMessage(ACLMessage.UNKNOWN);
		msg.setHeader("PURPOSE", "AGENT_END");
		msg.setContent("See you");
		welcomeProcessor.addMessage(msg);
	}

	synchronized void removeProcessor(String conversationID) {
		lock.lock();
		processors.remove(conversationID);
		if (inShutdown) {
			if (processors.size() == 1) {
				this.notifyLastProcessorRemoved();
			}
		}
		lock.unlock();
	}

	synchronized boolean removeTimer(String conversationId) {
		if (this.timers.get(conversationId) == null)
			return false;
		else {
			this.timers.get(conversationId).cancel();
			this.timers.remove(conversationId);
			return true;
		}
	}

	synchronized void startConversation(ACLMessage msg, CProcessor parent,
			Boolean sync) {
		for (int i = 0; i < initiatorFactories.size(); i++) {
			if (initiatorFactories.get(i).templateIsEqual(msg)) {
				initiatorFactories.get(i).startConversation(msg, parent, sync);
				return;
			}
		}
		System.out.println("No hay factorias");
		// PENDIENTE: Lanzar excepción si no hay fabricas asociadas
	}
}