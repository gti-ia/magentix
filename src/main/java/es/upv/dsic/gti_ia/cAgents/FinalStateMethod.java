package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface FinalStateMethod {
	
	 public void run(CProcessor myProcessor, ACLMessage messageToSend);
	
}
