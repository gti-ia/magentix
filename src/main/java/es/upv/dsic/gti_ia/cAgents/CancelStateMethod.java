package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class CancelStateMethod {
	
	protected abstract String run(CProcessor myProcessor, ACLMessage cancellationState);
	
}
