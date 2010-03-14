package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public interface BeginStateMethod {
	
	public String run(CProcessor myProcessor, ACLMessage inputMessage);
	
}
