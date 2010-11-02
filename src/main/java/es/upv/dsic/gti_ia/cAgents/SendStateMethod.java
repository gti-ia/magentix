package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface SendStateMethod {
	
	/**
	 * The method to be executed by the action state
	 * @param myProcessor The CProcessor of the conversation
	 * @return The name of the next state of the conversation
	 */
	public String run(CProcessor myProcessor, ACLMessage messageToSend);
	
}
