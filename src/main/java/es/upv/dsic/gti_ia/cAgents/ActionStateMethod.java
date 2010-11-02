package es.upv.dsic.gti_ia.cAgents;

/**
 * This interface represents a method of an action state
 * @author Ricard Lopez Fogues
 *
 */

public interface ActionStateMethod {
	/**
	 * The method to be executed by the action state
	 * @param myProcessor The CProcessor of the conversation
	 * @return The name of the next state of the conversation
	 */
	public String run(CProcessor myProcessor);
	
}
