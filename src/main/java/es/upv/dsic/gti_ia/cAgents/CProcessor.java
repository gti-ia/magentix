// CAMBIOS

// creaci�n v�a API de conversaciones s�ncronas y as�ncronas
// los estados fijos se crean ahora solos
// las subclases de state han cambiado bastante, as� que esto ha afectado muchas partes de esta clase
// a�ado m�todos para recuperar el estado previo y �ltimo mensaje recibido

package es.upv.dsic.gti_ia.cAgents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.UUID;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public class CProcessor implements Runnable, Cloneable {
	private String conversationID;
	protected CAgent myAgent;
	private String currentState = "";
	private String firstName = null;
	private String backState = null;
	protected Map<String, State> states = new HashMap<String, State>();
	private TransitionTable transitiontable = new TransitionTable();
	private Queue<ACLMessage> messageQueue = new LinkedList<ACLMessage>();
	public ACLMessage currentMessage;
	// protected String parentConversationId;
	protected CProcessor parent;
	private boolean terminated;
	private boolean idle;
	protected int factoryArrayIndex;
	public Map<String, Object> internalData = new HashMap<String, Object>();
	private BeginState bs;
	private CancelState cs;
	private SendingErrorsState ses;
	private ReentrantLock mutex = new ReentrantLock();
	final Condition syncConversationFinished = mutex.newCondition();
	ACLMessage syncConversationResponse;
	private Boolean isSynchronized;
	private long nextSubID = 0;
	private String previousState;
	private ACLMessage lastReceivedMessage;
	private ACLMessage lastSendedMessage;

	protected CProcessor() {
		terminated = false;
		bs = new BeginState("BEGIN");
		cs = new CancelState();
		cs.setName("CANCEL");
		ses = new SendingErrorsState();
		ses.setName("SENDING_ERRORS");

		this.registerFirstState(bs);
	}

	public String getPreviousState() {
		return previousState;
	}

	public ACLMessage getLastReceivedMessage() {
		return lastReceivedMessage;
	}

	public ACLMessage getLastSendedMessage() {
		return lastSendedMessage;
	}

	public Map<String, Object> getParentInternalData() {
		return parent.internalData;
	}

	void setIsSynchronized(Boolean value) {

		isSynchronized = value;
	}

	BeginState beginState() {
		return bs;
	}

	CancelState cancelState() {
		return cs;
	}

	SendingErrorsState sendingErrorsState() {
		return ses;
	}

	protected void setConversationID(String id) {
		conversationID = id;
	}

	String newConversationID() {
		return this.myAgent.getName() + UUID.randomUUID().toString();
	}

	public ACLMessage createSyncConversation(ACLMessage initalMessage) {

		mutex.lock();

		nextSubID = nextSubID + 1;

		initalMessage.setConversationId(this.conversationID + "." + nextSubID);

		myAgent.startConversation(initalMessage, this, true);

		try {
			syncConversationFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mutex.unlock();
		return this.syncConversationResponse;
	}

	void notifySyncConversationFinished(ACLMessage response) {
		mutex.lock();
		this.syncConversationResponse = response;
		syncConversationFinished.signal();
		mutex.unlock();
	}

	public void createAsyncConversation(ACLMessage initalMessage) {

		// Clonar antes el mensaje ???
		initalMessage.setConversationId(myAgent.newConversationID());
		myAgent.startConversation(initalMessage, this, false);
	}

	protected Object clone() {
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// manually clone all the elements that super.clone() function does not
		// clone
		CProcessor aux = (CProcessor) obj;
		aux.states = new HashMap<String, State>();
		aux.transitiontable = new TransitionTable();
		aux.messageQueue = new LinkedList<ACLMessage>();
		aux.currentMessage = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		Iterator<String> it = states.keySet().iterator();
		// clone states
		while (it.hasNext()) {
			String key = it.next();
			State val = (State) states.get(key).clone();
			aux.registerState(val);
			// clone transitions
			Iterator<String> itTrans = this.transitiontable.getTransitions(key)
					.iterator();
			while (itTrans.hasNext()) {
				try {
					aux.transitiontable.addTransition(key, itTrans.next());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return aux;
	}

	public String getConversationID() {
		return conversationID;
	}

	public void registerState(State s) {
		states.put(s.getName(), s);
		transitiontable.addState(s.getName());
	}

	public void registerFirstState(State s) {
		registerState(s);
		firstName = s.getName();
	}

	public void deregisterState(State s) {
		states.remove(s.getName());
		transitiontable.removeState(s.getName());
	}

	public TransitionTable getTransitionTable() {
		return this.transitiontable;
	}

	public void addTransition(String from, String destination) {
		this.transitiontable.addTransition(from, destination);
	}

	public void removeTransition(String from, String destination) {
		this.transitiontable.removeTransition(from, destination);
	}

	protected void addMessage(ACLMessage msg) {
		messageQueue.add(msg);
	}

	protected void setIdle(boolean idle) {
		this.idle = idle;
	}

	protected boolean isIdle() {
		return idle;
	}

	protected boolean isTerminated() {
		return terminated;
	}

	public CAgent getMyAgent() {
		return myAgent;
	}

	protected void setFactoryArrayIndex(int index) {
		this.factoryArrayIndex = index;
	}

	public State getState(String name) {
		return this.states.get(name);
	}

	public CProcessor getParent() {
		return parent;
	}

	protected void setParent(CProcessor parent) {
		this.parent = parent;
	}

	public void run() {
		String next;

		// check if current state is set
		// if it's null then we are starting

		if (currentState.equals("")) {
			currentState = firstName;
			previousState = currentState;
		}
		int currentStateType = states.get(currentState).getType();

		// check if the conversation must stop due to the lack of available
		// conversations in the factory
		if (currentStateType == State.BEGIN) {
			try {
				this.getMyAgent().factories.get(factoryArrayIndex).availableConversations
						.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		// check if current state is Wait or Begin tpye, if not rise exception
		if (currentStateType != State.BEGIN && currentStateType != State.WAIT) {
			// error
			System.out
					.println(this.myAgent.getName()
							+ ": Error: starting conversation and currentState different from Wait or Begin");
		} else {
			while (true) {
				System.out.println("[" + this.myAgent.getName()
						+ this.conversationID + " " + currentState + "]");
				switch (currentStateType) {
				case State.BEGIN:
					ACLMessage aux = messageQueue.remove();
					currentState = this.beginState().getMethod().run(this, aux);
					lastReceivedMessage = aux;
					break;
				case State.ACTION:
					ActionState actionState = (ActionState) states
							.get(currentState);
					currentState = actionState.getMethod().run(this);
					break;
				case State.SEND:
					ACLMessage messageToSend;
					try {
						this.myAgent.availableSends.acquire();
						SendState sendState = (SendState) states
								.get(currentState);
						messageToSend = new ACLMessage();
						messageToSend.copyFromAsTemplate(sendState.messageTemplate);
						currentState = sendState.getMethod().run(this,
								messageToSend);
						messageToSend.setConversationId(this.conversationID); // Foce
						// ID

						// PENDIENTE
						// Ver si es necesario que el env�o sea en exclusi�n
						// mutua. En todo caso deber�a ir a BaseAgent

						this.myAgent.send(messageToSend);
						this.lastSendedMessage = messageToSend;
						this.myAgent.availableSends.release();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;

				case State.WAIT:
					Integer i = 1;
					i = i + 2;
					WaitState waitState = (WaitState) states.get(currentState);
					if (messageQueue.size() > 0) {
						ACLMessage retrievedMessage = messageQueue.remove();
						// check if message queue contains an exception message

						if (retrievedMessage.getHeaderValue("ERROR").equals(
								"SENDING_ERRORS")) {
							backState = currentState;
							currentState = "SENDING_ERRORS_STATE";
							currentMessage = retrievedMessage;

						} else if (retrievedMessage.getPerformativeInt() == ACLMessage.CANCEL) { // CANCEL
							backState = currentState;
							currentState = "CANCEL_STATE";
							currentMessage = retrievedMessage;

						} else if (retrievedMessage.getHeaderValue("ERROR")
								.equals("TERMINATED_FATHER")) {
							backState = currentState;
							currentState = "TERMINATED_FATHER_STATE";

						} else { // there is no exception message in the queue
							Set<String> receiveStates;
							receiveStates = transitiontable
									.getTransitions(currentState);
							boolean accepted = false;
							// check if any receiving state can handle the
							// message
							Iterator<String> it = receiveStates.iterator();
							while (it.hasNext() && !accepted) {
								String stateName = it.next();
								if (states.get(stateName).getType() == State.RECEIVE) {
									ReceiveState receiveState = (ReceiveState) states
											.get(stateName);
									if (retrievedMessage.getPerformativeInt() == receiveState
											.getAcceptFilter()
											.getPerformativeInt()
											&& retrievedMessage
													.headersAreEqual(receiveState
															.getAcceptFilter())) {
										currentState = stateName;
										currentMessage = retrievedMessage;
										accepted = true;
									}
								}
							}

							if (!accepted) {
								backState = currentState;
								System.out
										.println("Performativa "
												+ retrievedMessage
														.getPerformativeInt());
								Iterator<String> itr = retrievedMessage
										.getHeaders().keySet().iterator();
								String key1;
								while (itr.hasNext()) {
									key1 = itr.next();
									System.out.println("Header: "
											+ key1
											+ " Value: "
											+ retrievedMessage
													.getHeaderValue(key1));
								}
								currentState = "NOT_ACCEPTED_MESSAGES_STATE";
							}
						}
					} else { // queueMessage is empty
						System.out.println("Cola vacia");
						idle = true;
						if (waitState.getTimeOut() > 0) {
							myAgent.addTimer(conversationID, waitState
									.getTimeOut());
						}
						return;
					}
					break;
				case State.RECEIVE:
					ReceiveState receiveState = (ReceiveState) states
							.get(currentState);
					currentState = receiveState.getMethod().run(this,
							currentMessage);
					lastReceivedMessage = currentMessage;
					break;
				case State.FINAL:
					FinalState finalState = (FinalState) states
							.get(currentState);
					messageToSend = new ACLMessage(ACLMessage.INFORM);
					finalState.getMethod().run(this, messageToSend);
					if (this.isSynchronized) {
						this.parent
								.notifySyncConversationFinished(messageToSend);
					} else {
						// PENDIENTE qu� hacer cuando es as�ncrona
					}

					terminated = true;
					// decrease the conversations counter in the processor's
					// factory
					myAgent.endConversation(factoryArrayIndex);
					myAgent.removeProcessor(this.conversationID);
					return;
				case State.SENDING_ERRORS:
					next = backState;
					next = this.sendingErrorsState().getMethod().run(this,
							currentMessage);
					if (next == "PREV") {
						next = backState;
					}
					currentState = next;
					break;
				case State.CANCEL:
					next = backState;
					next = this.cancelState().getMethod().run(this,
							currentMessage);
					if (next == "PREV") {
						next = backState;
					}
					currentState = next;
					break;
				case State.TERMINATED_FATHER:
					next = backState;
					TerminatedFatherState terminatedFatherState = (TerminatedFatherState) states
							.get(currentState);
					ACLMessage terminatedFatherMessage = new ACLMessage(
							ACLMessage.FAILURE);
					terminatedFatherMessage
							.setContent("Exception! Reason : TERMINATED_FATHER");
					terminatedFatherMessage.setHeader("ERROR",
							"TERMINATED_FATHER");
					next = terminatedFatherState.run(terminatedFatherMessage,
							next);
					currentState = next;
					break;
				case State.NOT_ACCEPTED_MESSAGES:
					next = backState;
					NotAcceptedMessagesState notAcceptedMessagesState = (NotAcceptedMessagesState) states
							.get(currentState);
					switch (notAcceptedMessagesState.run(currentMessage, next)) {
					case NotAcceptedMessagesState.IGNORE:
						break;
					case NotAcceptedMessagesState.REPLY_NOT_UNDERSTOOD:
						ACLMessage cloneCurrentMessage = (ACLMessage) currentMessage
								.clone();
						cloneCurrentMessage.setPerformative(ACLMessage.FAILURE);
						cloneCurrentMessage.clearAllReceiver();
						cloneCurrentMessage.addReceiver(currentMessage
								.getSender());
						myAgent.send(cloneCurrentMessage);
						break;
					case NotAcceptedMessagesState.KEEP:
						// PENDIENTE: myAgent.addMessage(currentMessage);
						break;
					}
					next = notAcceptedMessagesState.getNext(next);
					currentState = next;
					break;
				}
				// PENDIENTE Excepcion si no existe estado. Java no me permite
				// enviar una excepcion desde este metodo?

				if (!states.containsKey(currentState)) {
					System.out.println(currentState + " state " + " doesn' exist");
				}
				currentStateType = states.get(currentState).getType();
				previousState = currentState;
			} // end while (true)
		}
	}
}