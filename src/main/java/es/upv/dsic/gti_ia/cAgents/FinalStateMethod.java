package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface FinalStateMethod {
	/**
	 * The method to be executed by the final state
	 * @param myProcessor The CProcessor of the conversation
	 * @return The name of the next state of the conversation
	 */
	 public void run(CProcessor myProcessor, ACLMessage messageToSend);
	
}
