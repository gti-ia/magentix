package es.upv.dsic.gti_ia.cAgents;

/**
 * This class represents a begin state during an interaction protocol.
 * When a conversation reaches this state it executes the state's method.
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public class BeginState extends State {

	private BeginStateMethod Method;

	/**
	 * Create a new begin state
	 * @param name of the state
	 */
	public BeginState(String n) {
		super(n);
		type = State.BEGIN;
	}

	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param The method of this state
	 */
	public void setMethod(BeginStateMethod method) {
		Method = method;
	}

	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public BeginStateMethod getMethod() {
		return Method;
	}

}
