package myfirstcagent;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

// In order to create a CAgent it is necessary to extend
// the class CAgent and implement Initialize and Finalize methods.

class HelloWorldAgentClass extends CAgent {

	public HelloWorldAgentClass(AgentID aid) throws Exception {
		super(aid);
	}

	// The platform starts a conversation with each agent that has been just created
	// by sending her a welcome message. This sending creates the first CProcessor
	// of the agent. In order to manage this message the user must implement 
	// the Ininitialize method defined by the class CAgent, this method will
	// be executed by the first CProcessor.

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());
		System.out.println(myProcessor.getMyAgent().getName()
				+ ":  inevitably I have to say hello world");
		
		
		// ShutdownAgent method initialize the process which will finalize the
		// active conversations of the agent. When this process ends, the platform
		// sends a finalize message to the agent.
		myProcessor.ShutdownAgent();
	}

	// In order to manage the finalization message, the user has to
	// implement the Finalize method defined by the CAgent class.
	
	protected void Finalize(CProcessor myProcessor, ACLMessage finalizeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the finalize message is " + finalizeMessage.getContent());
	}
}