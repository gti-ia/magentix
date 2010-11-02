package es.upv.dsic.gti_ia.cAgents;

/**
 * This class represents an action state during an interaction protocol.
 * When a conversation reaches this state it executes the state's method.
 * @author Ricard Lopez Fogues
 * 
 */
public class ActionState extends State {

	private ActionStateMethod Method;

	/**
	 * Create a new action state
	 * @param name of the state
	 */
	public ActionState(String n) {
		super(n);
		type = State.ACTION;
	}

	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param The method of this state
	 */
	public void setMethod(ActionStateMethod method) {
		Method = method;
	}

	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public ActionStateMethod getMethod() {
		return Method;
	}

}
