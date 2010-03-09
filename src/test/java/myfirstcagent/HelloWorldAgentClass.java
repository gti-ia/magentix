package myfirstcagent;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

class HelloWorldAgentClass extends CAgent {

	public HelloWorldAgentClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		System.out.println(welcomeMessage.getContent());
	}

	protected void Finalize(CProcessor firstProcessor, ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}