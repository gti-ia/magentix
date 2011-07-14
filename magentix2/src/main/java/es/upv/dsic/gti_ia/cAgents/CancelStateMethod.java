package es.upv.dsic.gti_ia.cAgents;

/**
 * This interface class represents a method of a cancel state
 * @author Ricard Lopez Fogues
 *
 */

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface CancelStateMethod {
	
	/**
	 * The method to be executed by the cancel state
	 * @param myProcessor The CProcessor of the conversation
	 * @param cancellationState Message that produced this cancel exception
	 * @return The name of the next state of the conversation
	 */
	public String run(CProcessor myProcessor, ACLMessage cancellationState);
	
}
