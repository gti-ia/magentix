package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class FinalStateMethod {
	
	protected abstract void run(CProcessor myProcessor, ACLMessage messageToSend);
	
}
