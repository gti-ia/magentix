package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

class ShutdownState extends State {

	private ShutdownStateMethod Method;

	/**
	 * Creates a new shutdown state
	 * @param name of the state
	 */
	protected ShutdownState() {
		super("SHUTDOWN");
		type = State.SHUTDOWN;
	}

	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param The method of this state
	 */
	public void setMethod(ShutdownStateMethod method) {
		Method = method;
	}

	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public ShutdownStateMethod getMethod() {
		return Method;
	}

}
