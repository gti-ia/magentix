package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface ShutdownStateMethod {
	
	public String run(CProcessor myProcessor, ACLMessage msg);
	
}
