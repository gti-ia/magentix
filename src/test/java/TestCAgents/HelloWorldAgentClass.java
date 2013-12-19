package TestCAgents;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * Example class HelloWOrldAgentClass modificated for a basic CAgent Test 
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Paolo Rosso - prosso@dsic.upv.es
 */

class HelloWorldAgentClass extends CAgent {

	//Public variables for the tests
	public String welcomeMsg;
	public String finalizeMsg;
	private CProcessor processor;

	public HelloWorldAgentClass(AgentID aid) throws Exception {
		super(aid);

		welcomeMsg = "";
		finalizeMsg = "";
		processor = null;
	}

	// The platform starts a conversation with each agent that has been just created
	// by sending her a welcome message. This sending creates the first CProcessor
	// of the agent. In order to manage this message the user must implement 
	// the Ininitialize method defined by the class CAgent, this method will
	// be executed by the first CProcessor.

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		processor = myProcessor;
		welcomeMsg = myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent();

		System.out.println(welcomeMsg);
		System.out.println(myProcessor.getMyAgent().getName()
				+ ":  inevitably I have to say hello world");
		
		
		
		processor.ShutdownAgent();
	}


	// ShutdownAgent method initialize the process which will finalize the
	// active conversations of the agent. When this process ends, the platform
	// sends a finalize message to the agent.
	public void shutDownAgent(){
		processor.ShutdownAgent();
	}

	// In order to manage the finalization message, the user has to
	// implement the Finalize method defined by the CAgent class.
	
	protected void finalize(CProcessor myProcessor, ACLMessage finalizeMessage) {

		finalizeMsg = myProcessor.getMyAgent().getName()+ ": the finalize message is " + finalizeMessage.getContent();

		System.out.println(finalizeMsg);
	}
}