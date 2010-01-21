package es.upv.dsic.gti_ia.cAgents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public class CProcessor implements Runnable, Cloneable{	
	private String conversationID;
	protected CAgent myAgent;
	private String currentState = "";
	private String firstName = null;
	private String backState = null;
	protected Map<String, State> states = new HashMap<String, State>();
	private TransitionTable transitiontable = new TransitionTable();
	private Queue<ACLMessage> messageQueue = new LinkedList<ACLMessage>();
	public ACLMessage currentMessage;
	//protected String parentConversationId;
	protected CProcessor parent;
	private boolean terminated;
	private boolean idle;
	protected int factoryArrayIndex;
	public Map<String, Object> internalData = new HashMap<String, Object>();
	
	protected CProcessor(){
		terminated = false;
	}
	
	protected void setConversationID(String id){
		conversationID = id;
	}
	
	protected Object clone(){
		Object obj = null;
		try {
			obj = super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();		
		}
		//manually clone all the elements that super.clone() function does not clone
		CProcessor aux = (CProcessor) obj;
		aux.states = new HashMap<String, State>();
		aux.transitiontable = new TransitionTable();
		aux.messageQueue = new LinkedList<ACLMessage>();
		aux.currentMessage = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		Iterator<String> it = states.keySet().iterator();
		//clone states
		while(it.hasNext()) { 
			String key = it.next();
			State val = (State) states.get(key).clone();
			aux.registerState(val);
			//clone transitions
			Iterator<String> itTrans = this.transitiontable.getTransitions(key).iterator();
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
	
	public String getConversationID(){
		return conversationID;
	}
	
	public void registerState(State s){
		states.put(s.getName(), s);
		transitiontable.addState(s.getName());
	}
	
	public void registerFirstState(State s){
		registerState(s);
		firstName = s.getName();
	}
	
	public void deregisterState(State s){
		states.remove(s.getName());
		transitiontable.removeState(s.getName());
	}
	
	public TransitionTable getTransitionTable(){
		return this.transitiontable;
	}
	
	public void addTransition(String from, String destination){
		this.transitiontable.addTransition(from, destination);
	}
	
	public void removeTransition(String from, String destination){
		this.transitiontable.removeTransition(from, destination);
	}
	
	protected void addMessage(ACLMessage msg){
		messageQueue.add(msg);
	}
	
	protected void setIdle(boolean idle){
		this.idle = idle;
	}
	
	protected boolean isIdle(){
		return idle;
	}
	
	protected boolean isTerminated(){
		return terminated;
	}
	
	public CAgent getMyAgent(){
		return myAgent;
	}
	
	protected void setFactoryArrayIndex(int index){
		this.factoryArrayIndex = index;
	}
	
	public State getState(String name){
		return this.states.get(name);		
	}
	
	public CProcessor getParent(){
		return parent;
	}
	
	protected void setParent(CProcessor parent){
		this.parent = parent;
	}
	
	public void run(){
		String next;
		
		//System.out.println(this.myAgent.getName()+"currentMessage "+this.currentMessage.hashCode());
		//check if current state is set
		//if it's null then we are starting
		if(currentState.equals(""))
			currentState = firstName;
		
		int currentStateType = states.get(currentState).getType();
		
		//check if the conversation must stop due to the lack of available conversations in the factory
		if(currentStateType == State.BEGIN){
			try {
				this.getMyAgent().factories.get(factoryArrayIndex).availableConversations.acquire();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		
		//check if current state is Wait or Begin tpye, if not rise exception
		if(currentStateType != State.BEGIN && currentStateType != State.WAIT){
			//error
			System.out.println(this.myAgent.getName()+": Error: starting conversation and currentState different from Wait or Begin");
		}
		else{
			while(true){
				System.out.println("Agente: "+this.myAgent.getName()+" currentState: "+currentState);
				switch(currentStateType){
					case State.BEGIN:
						//TODO :a consultar, de moment, si un missatge es de tipo start, messagequeue.remove, si no peek
						ACLMessage peekMessage = messageQueue.peek();						
						BeginState beginState = (BeginState) states.get(currentState);
						if(peekMessage.getHeaderValue("start") != ""){
							currentState = beginState.run(this, messageQueue.remove());
						}
						else{
							currentState = beginState.run(this, messageQueue.peek());
						}
						break;
					case State.ACTION:
						ActionState actionState = (ActionState) states.get(currentState);
						currentState = actionState.run(this);
						break;
					case State.SEND:
						ACLMessage messageToSend;
						try {
							this.myAgent.availableSends.acquire();
							SendState sendState = (SendState) states.get(currentState);
							messageToSend = sendState.run(this, currentMessage);
							this.myAgent.send(messageToSend);
							currentState = sendState.getNext(this, currentMessage);
							this.myAgent.availableSends.release();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
					case State.WAIT:
						WaitState waitState = (WaitState) states.get(currentState);
						if(messageQueue.size() > 0){
							ACLMessage retrievedMessage = messageQueue.remove();
							//check if message queue contains an exception message
							if(retrievedMessage.getHeaderValue("ERROR").equals("SENDING_ERRORS")
									|| retrievedMessage.getHeaderValue("ERROR").equals("CANCEL")
									|| retrievedMessage.getHeaderValue("ERROR").equals("TERMINATED_FATHER")){
								if(retrievedMessage.getHeaderValue("ERROR").equals("SENDING_ERRORS")){ 
									backState = currentState;
									currentState = "SENDING_ERRORS_STATE";
								}
								else if(retrievedMessage.getHeaderValue("ERROR").equals("CANCEL")){ //CANCEL
									backState = currentState;
									currentState = "CANCEL_STATE";
								}
								else{//FATHER TERMINATED
									backState = currentState;
									currentState = "TERMINATED_FATHER_STATE";
								}
							}
							else{ //there is no exception message in the queue
								Set<String> receiveStates;
								receiveStates = transitiontable.getTransitions(currentState);
								boolean accepted = false;
								//check if any receiving state can handle the message
								Iterator<String> it = receiveStates.iterator();
								while (it.hasNext() && !accepted) {
									String stateName = it.next();
									if(states.get(stateName).getType() == State.RECEIVE){
										ReceiveState receiveState = (ReceiveState) states.get(stateName);
										if(retrievedMessage.getPerformativeInt() == receiveState.getAcceptFilter().getPerformativeInt()
												&& retrievedMessage.headersAreEqual(receiveState.getAcceptFilter())){
											currentState = stateName;
											currentMessage = retrievedMessage;
											accepted = true;
										}
									}
								}
								
								if(!accepted){
									backState = currentState;
									System.out.println("Performativa "+retrievedMessage.getPerformativeInt());
									Iterator<String> itr = retrievedMessage.getHeaders().keySet().iterator();
									String key1;
									while(itr.hasNext()){
										key1 = itr.next();
										System.out.println("Header: "+key1+" Value: "+retrievedMessage.getHeaderValue(key1));
									}
									currentState = "NOT_ACCEPTED_MESSAGES_STATE";
								}
							}
						}
						else{ //queueMessage is empty
							System.out.println("Cola vacia");
							idle = true;
							myAgent.addTimer(conversationID, waitState.getTimeOut());
							return;
						}						
						break;
					case State.RECEIVE:
						ReceiveState receiveState = (ReceiveState) states.get(currentState);
						currentState = receiveState.run(this, currentMessage);
						break;
					case State.FINAL:
						FinalState finalState = (FinalState) states.get(currentState);
						messageToSend = finalState.run(this);
						if(messageToSend != null){
							try {						
								this.myAgent.availableSends.acquire();
								this.myAgent.send(messageToSend);
								this.myAgent.availableSends.release();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						terminated = true;
						//decrease the conversations counter in the processor's factory
						myAgent.endConversation(factoryArrayIndex);
						myAgent.removeProcessor(this.conversationID);
						return;
					case State.SENDING_ERRORS:
						next = backState;
						SendingErrorsState sendingErrors = (SendingErrorsState) states.get(currentState);
						ACLMessage sendingErrorsMessage = new ACLMessage(ACLMessage.FAILURE);
						sendingErrorsMessage.setContent("Exception! Reason : SENDING_ERRORS");
						sendingErrorsMessage.setHeader("ERROR", "SENDING_ERRORS");
						next = sendingErrors.run(sendingErrorsMessage, next);
						currentState = next;
						break;
					case State.CANCEL:
						next = backState;
						CancelState cancelState = (CancelState) states.get(currentState);
						ACLMessage cancelMessage = new ACLMessage(ACLMessage.FAILURE);
						cancelMessage.setContent("Exception! Reason : CANCEL");
						cancelMessage.setHeader("ERROR", "CANCEL");
						next = cancelState.run(cancelMessage, next);
						currentState = next;
						break;
					case State.TERMINATED_FATHER:
						next = backState;
						TerminatedFatherState terminatedFatherState = (TerminatedFatherState) states.get(currentState);
						ACLMessage terminatedFatherMessage = new ACLMessage(ACLMessage.FAILURE);
						terminatedFatherMessage.setContent("Exception! Reason : TERMINATED_FATHER");
						terminatedFatherMessage.setHeader("ERROR", "TERMINATED_FATHER");
						next = terminatedFatherState.run(terminatedFatherMessage, next);
						currentState = next;
						break;
					case State.NOT_ACCEPTED_MESSAGES:
						next = backState;
						NotAcceptedMessagesState notAcceptedMessagesState = (NotAcceptedMessagesState) states.get(currentState);
						switch(notAcceptedMessagesState.run(currentMessage, next)){
						case NotAcceptedMessagesState.IGNORE:
							break;
						case NotAcceptedMessagesState.REPLY_NOT_UNDERSTOOD:
							ACLMessage cloneCurrentMessage = (ACLMessage) currentMessage.clone();
							cloneCurrentMessage.setPerformative(ACLMessage.FAILURE);
							cloneCurrentMessage.clearAllReceiver();
							cloneCurrentMessage.addReceiver(currentMessage.getSender());
							myAgent.send(cloneCurrentMessage);
							break;
						case NotAcceptedMessagesState.KEEP:
							myAgent.addMessage(currentMessage);
							break;						
						}
						next = notAcceptedMessagesState.getNext(next);
						currentState = next;
						break;
				}
				currentStateType = states.get(currentState).getType();
			}
		}
	}	
}