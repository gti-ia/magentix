package es.upv.dsic.gti_ia.cAgents;

/**
 * This interface represents a method of a begin state
 * @author Ricard Lopez Fogues
 *
 */

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface BeginStateMethod {
	/**
	 * The method to be executed by the begin state
	 * @param myProcessor The CProcessor of the conversation
	 * @param inputMessage The first message received by the CProcessor 
	 * @return The name of the next state of the conversation
	 */
	public String run(CProcessor myProcessor, ACLMessage inputMessage);
	
}
