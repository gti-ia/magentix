package es.upv.dsic.gti_ia.cAgents;

/**
 * This class represents a cancel state during an interaction protocol.
 * When a conversation reaches this state it executes the state's method.
 * @author Ricard Lopez Fogues
 * 
 */

class CancelState extends State {

	private CancelStateMethod Method;

	/**
	 * Create a new cancel state
	 * @param name of the state
	 */
	protected CancelState() {
		super("CANCEL_STATE");
		type = State.CANCEL;
	}

	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param method of this state
	 */
	public void setMethod(CancelStateMethod method) {
		Method = method;
	}

	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public CancelStateMethod getMethod() {
		return Method;
	}

}
