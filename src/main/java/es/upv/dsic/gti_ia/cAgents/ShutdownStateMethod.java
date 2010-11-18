package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This interface represents a method of a shutdown state
 * @author Agustín Espinosa
 *
 */

public interface ShutdownStateMethod {
	
	/**
	 * The method to be executed by the action state
	 * @param myProcessor The CProcessor of the conversation
	 * @return The name of the next state of the conversation
	 */
	public String run(CProcessor myProcessor, ACLMessage msg);
	
}
