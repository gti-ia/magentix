package TestCAgents.Agents;

import java.util.concurrent.CountDownLatch;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 * Example class HelloWOrldAgentClass modificated for a basic CAgent Test
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 */

public class HelloWorldAgentClass extends CAgent {

	// Public variables for the tests
	public String welcomeMsg;
	public String finalizeMsg;
	private CProcessor processor;
	private CountDownLatch finished;

	public HelloWorldAgentClass(AgentID aid, CountDownLatch finished)
			throws Exception {
		super(aid);
		this.finished = finished;
		welcomeMsg = "";
		finalizeMsg = "";
		processor = null;
	}

	// The platform starts a conversation with each agent that has been just
	// created
	// by sending her a welcome message. This sending creates the first
	// CProcessor
	// of the agent. In order to manage this message the user must implement
	// the Ininitialize method defined by the class CAgent, this method will
	// be executed by the first CProcessor.

	protected void execution(CProcessor myProcessor, ACLMessage welcomeMessage) {

		processor = myProcessor;
		welcomeMsg = myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent();

		logger.info(welcomeMsg);
		logger.info(myProcessor.getMyAgent().getName()
				+ ":  inevitably I have to say hello world");

		processor.ShutdownAgent();
	}

	protected void finalize(CProcessor myProcessor, ACLMessage finalizeMessage) {

		finalizeMsg = myProcessor.getMyAgent().getName()
				+ ": the finalize message is " + finalizeMessage.getContent();
		finished.countDown();
		logger.info(finalizeMsg);
	}
}