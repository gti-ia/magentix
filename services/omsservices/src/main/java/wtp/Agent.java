package wtp;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

class Agent extends CAgent {
	
	public int propose = -1;

	public Agent(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

	
	}

	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
	}
}