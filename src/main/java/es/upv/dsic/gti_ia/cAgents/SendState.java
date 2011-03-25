package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class represents a send state during an interaction protocol.
 * When a conversation reaches this state it executes the state's method.
 * @author Ricard Lopez Fogues
 * 
 */

public class SendState extends State {

	ACLMessage messageTemplate;
	private SendStateMethod methodToRun;

	/**
	 * Creates a new send state
	 * @param name of the state
	 */
	public SendState(String n) {
		super(n);
		type = State.SEND;
	}

	/**
	 * Sets the message that will be sent by this state
	 * @param mt messageToSend
	 */
	public void setMessageTemplate(ACLMessage mt){
		messageTemplate = mt;
	}
	
	/**
	 * Returns the message that will be sent by this state
	 * @return the message that will be sent by this state
	 */
	public ACLMessage getMessageTemplate(){
		return messageTemplate;
	}
	

	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param The method of this state
	 */
	public void setMethod(SendStateMethod method) {
		methodToRun = method;
	}

	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public SendStateMethod getMethod() {
		return methodToRun;
	}

}
