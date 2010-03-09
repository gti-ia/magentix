// CAMBIOS PRINCIPALES EN EL PAQUETE

// Inicialización y finalización del cAgent
//  >> A medias. Falta el mecanismo de finalización ordenada de conversaciones.
// Usar el estado Send sólo para enviar mensajes fuera sin poder cambiar el ID
//  >> OK
// Nuevos métodos para lanzar conversaciones síncronas y asíncronas
//  >> OK
// Gestión totalmente automatizada de IDs
//  >> OK
// Una subconversación síncrona puede debe poder crearse con el mismo ID que su padre
//  >> OK
// Revisar visibilidad de datos internos de conversación a conversaciones hijas
// Eliminar starting factories
//  >> OK
// Métodos sustitiubles en los estados de los autómatas
//  >> OK
// Creo que necesario un lock global. Interno seguro y externo creo que también. Supongo que deberá ser el mismo.

// Visibilidad de miembros y métodos al mínimo
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
	protected ArrayList<CProcessorFactory> factories = new ArrayList<CProcessorFactory>();
	protected ExecutorService exec;
	protected final Semaphore availableSends = new Semaphore(1, true); // ???
	private Map<String, Timer> timers = new HashMap<String, Timer>();
	private ReentrantLock lock = new ReentrantLock();
	final Condition iAmFinished = lock.newCondition();

	public CAgent(AgentID aid) throws Exception {
		super(aid);
		exec = Executors.newCachedThreadPool();
	}

	private CProcessorFactory createDefaultFactory() {
		CProcessorFactory defaultFactory = new CProcessorFactory(
				"DefaultFactory", new ACLMessage(ACLMessage.UNKNOWN), 1000);

		// BEGIN STATE

		BeginState bs = (BeginState) defaultFactory.getCProcessor().getState(
				"BEGIN");

		class BeginMethod extends BeginStateMethod {

			protected String run(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Default factory tratando mensaje "
						+ msg.getContent() + " origen: " + msg.getSender()
						+ " ConversationID: " + msg.getConversationId());
				return "FINAL";
			}
		}

		bs.setMethod(new BeginMethod());

		// FINAL STATE

		FinalState fs = new FinalState("FINAL");

		class fsMethod extends FinalStateMethod {

			protected void run(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Default factory tratando mensaje "
						+ msg.getContent() + " origen: " + msg.getSender()
						+ " ConversationID: " + msg.getConversationId());
			}
		}

		fs.setMethod(new fsMethod());

		defaultFactory.getCProcessor().registerState(fs);

		defaultFactory.getCProcessor().addTransition("BEGIN", "FINAL");

		return defaultFactory;
	}

	public void send(ACLMessage msg) {
		super.send(msg);
	}

	public synchronized void addFactory(CProcessorFactory factory) {
		factory.setAgent(this);
		factories.add(factory);
	}

	public synchronized void removeFactory(String name) {
		for (int i = 0; i < factories.size(); i++) {
			if (factories.get(i).name.equals(name)) {
				factories.remove(i);
				break;
			}
		}
	}

	protected synchronized void addProcessor(String conversationID,
			CProcessor processor) {
		processors.put(conversationID, processor);
	}

	protected synchronized void removeProcessor(String conversationID) {
		processors.remove(conversationID);
	}

	synchronized void startConversation(ACLMessage msg, CProcessor parent,
			Boolean sync) {
		for (int i = 1; i < factories.size(); i++) {
			if (factories.get(i).templateIsEqual(msg)) {
				factories.get(i).startConversation(msg, i, parent, sync);
				return;
			}
		}
		// PENDIENTE: Lanzar excepción si no hay fabricas asociadas
	}

	private synchronized void processMessage(ACLMessage msg) {
		CProcessor auxProcessor = processors.get(msg.getConversationId());
		boolean accepted = false;
		if (auxProcessor != null) {
			processors.get(msg.getConversationId()).addMessage(msg);
			if (auxProcessor.isIdle()) {
				auxProcessor.setIdle(false);
				if (!msg.getHeaderValue("Purpose").equals("WaitMessage"))
					if (removeTimer(msg.getConversationId()))
						System.out.println("Timer cancelado");
				exec.execute(auxProcessor);
			}
		} else {
			for (int i = 1; i < factories.size(); i++) {
				if (factories.get(i).templateIsEqual(msg)) {
					factories.get(i).startConversation(msg, i, null, false);
					accepted = true;
					break;
				}
			}
			if (!accepted) {
				System.out.println("Agente: " + this.getName()
						+ " Mensaje a tratar por la DefaultFactory");
				factories.get(0).startConversation(msg, 0, null, false);
			}
		}
	}

	protected synchronized boolean addTimer(final String conversationId,
			long milliseconds) {
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

	protected synchronized boolean removeTimer(String conversationId) {
		if (this.timers.get(conversationId) == null)
			return false;
		else {
			this.timers.get(conversationId).cancel();
			this.timers.remove(conversationId);
			return true;
		}
	}

	protected void endConversation(int factoryArrayIndex) {
		factories.get(factoryArrayIndex).availableConversations.release();
	}

	public void onMessage(ACLMessage msg) {
		this.processMessage(msg);
	}

	protected abstract void Initialize(ACLMessage welcomeMessage);

	protected abstract void Finalize(ACLMessage finalizeMessage);

	void Finished() {

		// PENDIENTE. Implementar el mecanismo de terminación de conversaciones
		// en curso,
		// mecanismo que concluirá llamando a este método para liberar el hilo
		// principal
		// y se alcance el final de agente

		lock.lock();
		iAmFinished.signal();
		lock.unlock();
	}

	protected final void execute() {
		addFactory(createDefaultFactory());
		ACLMessage welcomeMessage = new ACLMessage(ACLMessage.INFORM);
		welcomeMessage.setContent("Welcome to this plarform");

		Initialize(welcomeMessage);

		System.out.println("Soy " + this.getName() + ". Arranco");

		lock.lock();
		try {
			iAmFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();

		Finalize(welcomeMessage);

	}
}