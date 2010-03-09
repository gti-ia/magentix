package conversaciones;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

class HelloWorldAgentClass extends CAgent {

	public HelloWorldAgentClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(ACLMessage welcomeMessage) {

		System.out.println(welcomeMessage.getContent());
	}

	protected void Finalize(ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}