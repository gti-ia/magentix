package es.upv.dsic.gti_ia.cAgents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.UUID;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * CProcessor class
 * @author Ricard Lopez Fogues
 * 
 */

public class CProcessor implements Runnable, Cloneable {
	class SHUTDOWN_Method implements ShutdownStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {
			return null;
		}
	}

	private String conversationID;
	private CAgent myAgent;
	private String currentState = "";
	private String firstName = null;
	private String backState = null;
	private Map<String, State> states = new HashMap<String, State>();
	private TransitionTable transitiontable = new TransitionTable();
	private Queue<ACLMessage> messageQueue = new LinkedList<ACLMessage>();
	private ACLMessage currentMessage;
	private CProcessor parent;
	private boolean terminated;
	private boolean idle;
	private Map<String, Object> internalData = new HashMap<String, Object>();
	private BeginState BEGIN;
	private CancelState CANCEL_STATE;
	private ShutdownState SHUTDOWN;
	private SendingErrorsState ses;
	Condition syncConversationFinished;
	ACLMessage syncConversationResponse;
	private Boolean isSynchronized;
	//private long nextSubID = 0;
	private String previousState;
	private ACLMessage lastSentMessage;
	private CFactory myFactory;
	private boolean initiator;

	Logger logger = Logger.getLogger(CProcessor.class);

	/**
	 * Creates a new CProcessor associated to an agent
	 * @param myAgent The associated agent
	 */
	protected CProcessor(CAgent myAgent) {
		this.myAgent = myAgent;
		terminated = false;
		BEGIN = new BeginState("BEGIN");
		CANCEL_STATE = new CancelState();
		CANCEL_STATE.setName("CANCEL");
		ses = new SendingErrorsState();
		ses.setName("SENDING_ERRORS");
		SHUTDOWN = new ShutdownState();
		SHUTDOWN.setMethod(new SHUTDOWN_Method());

		this.registerFirstState(BEGIN);
		this.registerState(SHUTDOWN);

	}

	/**
	 * Adds a transition between two states
	 * @param from
	 * @param destination
	 */
	public void addTransition(String from, String destination) {
		this.lockMyAgent();
		this.transitiontable.addTransition(from, destination);
		this.unlockMyAgent();
	}

	/**
	 * Adds a transition between two states
	 * @param from
	 * @param destination
	 */
	public void addTransition(State from, State destination) {
		this.lockMyAgent();
		this.transitiontable.addTransition(from.getName(), destination
				.getName());
		this.unlockMyAgent();
	}

	
	/**
	 * Creates a asynchronous CProcessor
	 * @param initalMessage
	 */
	public void createAsyncConversation(ACLMessage initalMessage) {

		this.lockMyAgent();
		initalMessage.setConversationId(myAgent.newConversationID());
		myAgent.startConversation(initalMessage, this, false);
		this.unlockMyAgent();
	}

	/**
	 * Creates a synchronous conversation where this agent is the initiator
	 * @param initalMessage The message tha will be sent as the first message of the conversation
	 * @return Final message
	 */
	public ACLMessage createSyncConversation(ACLMessage initalMessage) {

		this.lockMyAgent();

		//nextSubID = nextSubID + 1;

		//initalMessage.setConversationId(this.conversationID + "." + nextSubID);
		initalMessage.setConversationId(myAgent.newConversationID());

		myAgent.startConversation(initalMessage, this, true);

		try {
			syncConversationFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.unlockMyAgent();
		return this.syncConversationResponse;
	}
	
	/**
	 * Creates a synchrnous conversation where this agent is the initiator
	 * @param factory Fatory that will create the conversation
	 * @param id Conversation id
	 * @return
	 */
	public ACLMessage createSyncConversation(CFactory factory, String id) {

		this.lockMyAgent();

		//nextSubID = nextSubID + 1;

		//initalMessage.setConversationId(this.conversationID + "." + nextSubID);
		//initalMessage.setConversationId(myAgent.newConversationID());

		//myAgent.startConversation(initalMessage, this, true);
		factory.startConversationWithID(id, this, true);

		try {
			syncConversationFinished.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.unlockMyAgent();
		return this.syncConversationResponse;
	}

	/**
	 * Removes a state from the graph
	 * @param s State to remove
	 */
	public void deregisterState(State s) {
		this.lockMyAgent();
		states.remove(s.getName());
		transitiontable.removeState(s.getName());
		this.unlockMyAgent();
	}

	/**
	 * Gets conversation identifier
	 * @return Conversation identifier
	 */
	public String getConversationID() {
		return conversationID;
	}

	/**
	 * Gets conversation's internal data
	 * @return
	 */
	public Map<String, Object> getInternalData() {
		return internalData;
	}

	/**
	 * Gets the last received message during this conversation 
	 * @return
	 */
	public ACLMessage getLastReceivedMessage() {
		return currentMessage;
	}

	/**
	 * Gets the last sent message during this conversation
	 * @return
	 */
	public ACLMessage getLastSentMessage() {
		return lastSentMessage;
	}

	/**
	 * Returns the agent owner of this CProcessor
	 * @return
	 */
	public CAgent getMyAgent() {
		return myAgent;
	}

	/**
	 * Returns the CProcessor which is parent of this one
	 * @return
	 */
	public CProcessor getParent() {
		return parent;
	}

	/**
	 * Returns parent internal data
	 * @return
	 */
	public Map<String, Object> getParentInternalData() {
		return parent.internalData;
	}

	/**
	 * Returns the state identified by its name
	 * @param name
	 * @return
	 */
	public State getState(String name) {
		return this.states.get(name);
	}

	/**
	 * Returns the table of transitions between the states of the graph
	 * @return
	 */
	public TransitionTable getTransitionTable() {
		// PENDIENTE reemplazar por funciones de consulta
		return this.transitiontable;
	}

	/**
	 * Locks the agent's mutex
	 */
	private void lockMyAgent() {
		this.myAgent.mutex.lock();
	}

	/**
	 * Registers a new state in the CProcessor
	 * @param s The new state
	 */
	public void registerState(State s) {
		this.lockMyAgent();
		states.put(s.getName(), s);
		transitiontable.addState(s.getName());
		this.unlockMyAgent();
	}

	/**
	 * Removes a transition between from state and destination state
	 * @param from
	 * @param destination
	 */
	public void removeTransition(String from, String destination) {
		this.lockMyAgent();
		this.transitiontable.removeTransition(from, destination);
		this.unlockMyAgent();
	}

	/**
	 * This method executes the CProcessor thread. This method manages the
	 * conversation through the different graph states
	 */
	public void run() {
		String next;

		this.lockMyAgent();
		// check if current state is set
		// if it's null then we are starting

		if (currentState.equals("")) {
			currentState = firstName;
			previousState = currentState;
		}
		int currentStateType = states.get(currentState).getType();

		// check if the conversation must stop due to the lack of available
		// conversations in the factory
		if (currentStateType == State.BEGIN && this.myFactory.getLimit() != 0) {
			try {
				this.unlockMyAgent();
				this.myFactory.availableConversations.acquire();
				this.lockMyAgent();
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
				this.logger.info("[" + this.myAgent.getName() + " "
						+ this.conversationID + " " + currentState + "]");
				switch (currentStateType) {
				case State.BEGIN:
					// ACLMessage aux = messageQueue.remove(); //!!!!!Cuidado,
					// he cambiado por peek -> Ricard!!!!
					ACLMessage aux = null;
					if (this.isInitiator())
						aux = messageQueue.peek();
						if(aux != null)
							aux = messageQueue.remove();
					else
						aux = messageQueue.peek();
					this.unlockMyAgent();
					currentState = this.beginState().getMethod().run(this, aux);
					this.lockMyAgent();
					currentMessage = aux;
					break;
				case State.ACTION:
					ActionState actionState = (ActionState) states
							.get(currentState);
					this.unlockMyAgent();
					currentState = actionState.getMethod().run(this);
					this.lockMyAgent();
					break;
				case State.SEND:
					ACLMessage messageToSend;
					SendState sendState = (SendState) states.get(currentState);
					messageToSend = new ACLMessage();
					logger.info("Template "+sendState.messageTemplate.getContent());
					if (sendState.messageTemplate != null) {
						messageToSend
								.copyFromAsTemplate(sendState.messageTemplate);
					}
					this.unlockMyAgent();
					currentState = sendState.getMethod().run(this,
							messageToSend);
					this.lockMyAgent();
					messageToSend.setConversationId(this.conversationID);
					
					this.myAgent.send(messageToSend);
					this.lastSentMessage = messageToSend;

					break;

				case State.WAIT:
					WaitState waitState = (WaitState) states.get(currentState);
					if (messageQueue.size() > 0) {
						ACLMessage retrievedMessage = messageQueue.remove();
						// check if message queue contains an exception message

						if (retrievedMessage.getHeaderValue("PURPOSE").equals(
								"SHUTDOWN")) {
							backState = currentState;
							currentState = "SHUTDOWN";
							currentMessage = retrievedMessage;

						} else if (retrievedMessage.getHeaderValue("ERROR")
								.equals("SENDING_ERRORS")) {
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
									// PENDIENTE
									// Hacer una comparaci�n de mensaje con
									// template completa.
									// Probablemente mejor en ACLMessage

									MessageFilter filter = receiveState
											.getAcceptFilter();
									if (filter == null
											|| filter
													.compareHeaders(retrievedMessage)) {
										currentState = stateName;
										currentMessage = retrievedMessage;
										accepted = true;
									}
								}
							}

							if (!accepted) {
								backState = currentState;
								logger.info("Performativa "
										+ retrievedMessage.getPerformativeInt()
										+ "Contenido "
										+ retrievedMessage.getContent());
								Iterator<String> itr = retrievedMessage
										.getHeaders().keySet().iterator();
								String key1;
								while (itr.hasNext()) {
									key1 = itr.next();
									logger.info("Header: "
											+ key1
											+ " Value: "
											+ retrievedMessage
													.getHeaderValue(key1));
								}
								currentState = "NOT_ACCEPTED_MESSAGES_STATE";
							}
						}
					} else { // queueMessage is empty
						this.logger
								.info(this.myAgent.getName() + " Empty queue");
						idle = true;
						if (waitState.getPeriod() != 0) {
							myAgent.addTimer(conversationID, waitState.getName(), waitState.getPeriod(), waitState.getWaitType());
						}
						this.unlockMyAgent();
						return;
					}
					break;
				case State.RECEIVE:
					ReceiveState receiveState = (ReceiveState) states
							.get(currentState);
					this.lockMyAgent();
					currentState = receiveState.getMethod().run(this,
							currentMessage);
					this.unlockMyAgent();
					break;
				case State.FINAL:
					FinalState finalState = (FinalState) states
							.get(currentState);
					messageToSend = new ACLMessage(ACLMessage.INFORM);
					this.unlockMyAgent();
					finalState.getMethod().run(this, messageToSend);
					this.lockMyAgent();
					if (this.isSynchronized) {
						this.parent
								.notifySyncConversationFinished(messageToSend);
					} else {
						// PENDIENTE qu� hacer cuando es as�ncrona
					}

					terminated = true;
					// decrease the conversations counter in the processor's
					// factory
					myAgent.endConversation(this.myFactory);
					myAgent.removeProcessor(this.conversationID);
					this.unlockMyAgent();
					return;
				case State.SENDING_ERRORS:
					next = backState;
					this.unlockMyAgent();
					next = this.sendingErrorsState().getMethod().run(this,
							currentMessage);
					this.lockMyAgent();
					if (next == null) {
						next = backState;
					}
					currentState = next;
					break;
				case State.SHUTDOWN:
					next = backState;
					this.unlockMyAgent();
					next = this.SHUTDOWN.getMethod().run(this, currentMessage);
					this.lockMyAgent();
					if (next == null) {
						if (this.isSynchronized) {
							this.parent
									.notifySyncConversationFinished(currentMessage);
						} else {
							// PENDIENTE qu� hacer cuando es as�ncrona
						}

						terminated = true;
						// decrease the conversations counter in the processor's
						// factory
						myAgent.endConversation(this.myFactory);
						myAgent.removeProcessor(this.conversationID);
						this.unlockMyAgent();
						return;
					}

					currentState = next;
					break;
				case State.CANCEL:
					next = backState;
					this.unlockMyAgent();
					next = this.cancelState().getMethod().run(this,
							currentMessage);
					this.lockMyAgent();
					if (next == null) {
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
					this.unlockMyAgent();
					next = terminatedFatherState.run(terminatedFatherMessage,
							next);
					this.lockMyAgent();
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
					logger.info(currentState + " state "
							+ " doesn' exist");
				}
				if (!this.transitiontable.existsTransation(previousState,
						currentState)) {
					this.logger.error(this.myAgent.getName() + " "
							+ this.conversationID
							+ " No transition defined between " + previousState
							+ " and " + currentState);
					return;
				}
				currentStateType = states.get(currentState).getType();
				previousState = currentState;
			} // end while (true)
		}
	}

	/**
	 * Tries to end agent execution
	 */
	public void ShutdownAgent() {
		this.lockMyAgent();
		this.myAgent.Shutdown();
		this.unlockMyAgent();
	}

	/**
	 * Unlocks the mutex of the agent owner of this CProcessor
	 */
	public void unlockMyAgent() {
		this.myAgent.mutex.unlock();
	}

	/**
	 * Clones this object
	 */
	public Object clone() {
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
		aux.syncConversationFinished = this.myAgent.mutex.newCondition();
		return aux;
	}

	/**
	 * Assigns a message to this CProcessor
	 * @param msg
	 */
	protected void addMessage(ACLMessage msg) {
		messageQueue.add(msg);
	}

	/**
	 * Returns the name of the first state
	 * @return The name of the begin state
	 */
	protected BeginState beginState() {
		return BEGIN;
	}

	/**
	 * Returns the name of the exception cancel state
	 * @return The name of the cancel state
	 */
	protected CancelState cancelState() {
		return CANCEL_STATE;
	}

	/**
	 * Returns the factory that has associated this CProcessor
	 * @return
	 */
	public CFactory getMyFactory() {
		return myFactory;
	}

	/**
	 * Returns true if the CProcessor is idle, false otherwise
	 * @return
	 */
	public boolean isIdle() {
		return idle;
	}

	/**
	 * Returns true if the CProcessor is terminated, false otherwise
	 * @return
	 */
	public boolean isTerminated() {
		return terminated;
	}

	/**
	 * Returns a new conversation identifier
	 * @return
	 */
	protected String newConversationID() {
		return this.myAgent.getName() + UUID.randomUUID().toString();
	}

	/**
	 * When a synchrnous conversation finished this method is executed
	 * @param response
	 */
	private void notifySyncConversationFinished(ACLMessage response) {
		this.lockMyAgent();
		this.syncConversationResponse = response;
		syncConversationFinished.signal();
		this.unlockMyAgent();
	}

	/**
	 * Registers the first state of the graph
	 * @param s
	 */
	public void registerFirstState(State s) {
		registerState(s);
		firstName = s.getName();
	}

	/**
	 * Returns the exception state that manages sending errors
	 * @return
	 */
	protected SendingErrorsState sendingErrorsState() {
		return ses;
	}

	/**
	 * Sets the conversation identifier
	 * @param id
	 */
	protected void setConversationID(String id) {
		conversationID = id;
	}

	/**
	 * Sets the factory to which this CProcessor is associated
	 * @param factory
	 */
	protected void setFactory(CFactory factory) {
		this.myFactory = factory;
	}

	/**
	 * Sets the CProcessor as idle
	 * @param idle
	 */
	protected void setIdle(boolean idle) {
		this.idle = idle;
	}

	/**
	 * Sets the CProcessor as synchronous or asynchronous
	 * @param value
	 */
	protected void setIsSynchronized(boolean value) {
		isSynchronized = value;
	}

	/**
	 * Sets the parent CProcessor of this one
	 * @param parent
	 */
	protected void setParent(CProcessor parent) {
		this.parent = parent;
	}

	/**
	 * Sets the CProcessor as initiator or participant
	 * @param initiator
	 */
	protected void setInitiator(boolean initiator) {
		this.initiator = initiator;
	}

	/**
	 * Returns true if this CProcessor is initiator, false otherwise
	 * @return
	 */
	protected boolean isInitiator() {
		return this.initiator;
	}
}