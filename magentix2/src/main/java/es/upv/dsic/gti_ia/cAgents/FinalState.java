package es.upv.dsic.gti_ia.cAgents;

/**
 * This class represents a final state during an interaction protocol.
 * When a conversation reaches this state it executes the state's method.
 * @author Ricard Lopez Fogues
 *
 */

public class FinalState extends State{

	/**
	 * Create a new final state
	 * @param name of the state
	 */
	public FinalState(String n) {
		super(n);
		type = State.FINAL;
	}

	private FinalStateMethod methodToRun;
	
	/**
	 * Set the method that will be executed when a conversation reaches this state
	 * @param The method of this state
	 */
	public void setMethod(FinalStateMethod method) {
		methodToRun = method;
	}
	
	/**
	 * Returns the method assigned to this state by the setMethod() function
	 * @return This state's method
	 */
	public FinalStateMethod getMethod() {
		return methodToRun;
	}
	
}
