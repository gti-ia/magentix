package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public class ReceiveState extends State {

	private MessageFilter acceptFilter;
	private ReceiveStateMethod methodToRun;

	/**
	 * Creates a new receive state
	 * @param name of the state
	 */
	public ReceiveState(String n) {
		super(n);
		type = State.RECEIVE;
	}

	/**
	 * Set the message filter
	 * @param filter
	 */
	public void setAcceptFilter(MessageFilter filter) {
		acceptFilter = filter;
	}

	/**
	 * Returns the message filter
	 * @return the message filter
	 */
	public MessageFilter getAcceptFilter() {
		return acceptFilter;
	}

	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param The method of this state
	 */
	public void setMethod(ReceiveStateMethod method) {
		methodToRun = method;
	}

	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public ReceiveStateMethod getMethod() {
		return methodToRun;
	}

}
