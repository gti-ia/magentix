package TestTrace;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.trace.TraceManager;

public class TestCoverageTraces {

	/* Constants */
	// Dependent of other classes (TraceInteract and TraceManager).
	private static final String PUBLISH_PREFIX = "publish#";
	private static final String UNPUBLISH_PREFIX = "unpublish#";
	private static final String LIST_SERVICES_PREFIX = "list#services";
	
	// Independent, proper of this class.
	private static final String DEFAULT_TM_NAME = "TM";
	private static final String ALTERNATIVE_TM_NAME = "ALT";
	private static final String AGENT1_NAME = "qpid://agent1@localhost:8080";
	private static final String AGENT2_NAME = "qpid://agent2@localhost:8080";
	
	private static final String AGENT1_SUBSCRIPTION_SERVICE_NAME = "AGENT1_MAGIC_SERVICE";
	private static final String AGENT2_SUBSCRIPTION_SERVICE_NAME = "AGENT2_USELESS_TRACING";
	
	private static final String AGENT1_DESCRIPTION = "And now for something completely different";
	private static final String AGENT2_DESCRIPTION = "Your advertisement goes here.";
	
	/* Attributes */
	private static TraceManager defaultTM = null;
	private static TraceManager alternativeTM = null;
	private static CommandedAgent agent1 = null;
	private static CommandedAgent agent2 = null;
	
	static Process qpid_broker;
	
	
	/* Set up class and tear down class */
	@BeforeClass
	public static void setUpClass() throws Exception {
		
		qpid_broker = Runtime.getRuntime().exec("./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(qpid_broker.getInputStream()));

		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}
		
		AgentsConnection.connect();		// Connecting to Qpid Broker.

		defaultTM = new TraceManager(new AgentID(DEFAULT_TM_NAME));
		alternativeTM = new TraceManager(new AgentID(ALTERNATIVE_TM_NAME));
		agent1 = new CommandedAgent(new AgentID(AGENT1_NAME));
		agent2 = new CommandedAgent(new AgentID(AGENT2_NAME));
		
		defaultTM.start();
		alternativeTM.start();
		agent1.start();
		agent2.start();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		defaultTM.shutdown();
		alternativeTM.shutdown();
		agent1.addCommand(CommandedAgent.END);
		agent2.addCommand(CommandedAgent.END);
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
  
	/* Test methods */
	/**
	 * Test the publish, list services and unpublish methods of TraceInteract and
	 * the correct behavior of the default TraceManager in these cases.
	 * 
	 * The method uses two agents: Agent1 and Agent2, and does the next sequence of
	 * steps: 
	 *  - First, Agent1 asks the TraceManager to publish a trace service and waits 
	 *  until it accepts. 
	 *  - Then, Agent2 sends a message to the TraceManager to ask for a list with all 
	 *  the available trace services and checks that the service of Agent1 is among them.
	 *  - Next, Agent1 asks the TraceManager to unpublish the previous trace service and,
	 *  again, waits until it accepts.
	 *  - Finally, Agent2 sends again a message to ask for a list with all the available
	 *  trace services and checks that the service of Agent1 is NOT among them.
	 */
	@Test(timeout = 1000)
	public void testDefaultPublishAndUnpublishTracingService() {
		boolean servicePublished = false;
		boolean serviceAvailable = false;
		ArrayList<ACLMessage> messages;
		
		// Agent 1 publishes a tracing service.
		agent1.setServiceName(AGENT1_SUBSCRIPTION_SERVICE_NAME);
		agent1.setDescription(AGENT1_DESCRIPTION);
		agent1.addCommand(CommandedAgent.PUBLISH);
		
		// Check that the trace manager has answered Agent 1.
		while(!servicePublished) {
			messages = agent1.getReceivedMessages();
			for(ACLMessage msg : messages) {
				/*
				 * If the sender is the default trace manager and
				 * the content starts with the publish prefix and
				 * the agent1 service name is in the content...
				 */
				if(msg.getSender().toString().equals(defaultTM.getAid().toString()) &&
				   msg.getContent().startsWith(PUBLISH_PREFIX) &&
				   msg.getContent().contains(AGENT1_SUBSCRIPTION_SERVICE_NAME)) {
					if(msg.getPerformativeInt() == ACLMessage.AGREE) {
						// If the performative is AGREE, then the service has been published.
						servicePublished = true;
					} else {
						// Else, it has not been published.
						fail("Agent 1 service cannot be published.");
					}
				}
			}
		}
		agent1.clearReceivedMessages();
		
		// Agent 2 asks for a list with all available tracing services. 
		// It must have Agent 1 tracing service.
		agent2.addCommand(CommandedAgent.LIST_SERVICES);
		while(!serviceAvailable) {
			messages = agent2.getReceivedMessages();
			for(ACLMessage msg : messages) {
				/*  
				 * If the sender is the default trace manager and
				 * the content starts with the list of services prefix...
				 */
				if(msg.getSender().toString().equals(defaultTM.getAid().toString()) && 
				   msg.getContent().startsWith(LIST_SERVICES_PREFIX)) {
					if(msg.getPerformativeInt() == ACLMessage.AGREE) {
						// If the performative is AGREE...
						if(msg.getContent().contains(AGENT1_SUBSCRIPTION_SERVICE_NAME)) {
							// If the trace service of agent 1 is in the content, then the service is available.
							serviceAvailable = true;
						} else {
							// Else, it is not available.
							fail("Agent 1 service is not available.");
						}
					} else {
						// If the performative is not AGREE, it cannot be checked.
						fail("Agent 2 was unable to check the available tracing services.");
					}
				}
			}
		}
		agent2.clearReceivedMessages();
		
		// Agent 1 unpublishes its tracing service.
		agent1.addCommand(CommandedAgent.UNPUBLISH);
		
		// Check that the trace manager has answered Agent 1.
		while(servicePublished) {
			messages = agent1.getReceivedMessages();
			for(ACLMessage msg : messages) {
				/*
				 * If the sender is the default trace manager and
				 * the content starts with the unpublished prefix and
				 * the agent1 service name is in the content...
				 */
				if(msg.getSender().toString().equals(defaultTM.getAid().toString()) &&
				   msg.getContent().startsWith(UNPUBLISH_PREFIX) &&
				   msg.getContent().contains(AGENT1_SUBSCRIPTION_SERVICE_NAME)) {
					if(msg.getPerformativeInt() == ACLMessage.AGREE) {
						// If the performative is AGREE, then the service has been unpublished.
						servicePublished = false;
					} else {
						// Else, it is still published.
						fail("Agent 1 service cannot be unpublished.");
					}
				}
			}
		}
		agent1.clearReceivedMessages();
		
		// Agent 2 asks again for the tracing services list. 
		// The tracing service of agent 1 should not be available.
		agent2.addCommand(CommandedAgent.LIST_SERVICES);
		while(serviceAvailable) {
			messages = agent2.getReceivedMessages();
			for(ACLMessage msg : messages) {
				/*  
				 * If the sender is the default trace manager and
				 * the content starts with the list of services prefix...
				 */
				if(msg.getSender().toString().equals(defaultTM.getAid().toString()) && 
				   msg.getContent().startsWith(LIST_SERVICES_PREFIX)) {
					if(msg.getPerformativeInt() == ACLMessage.AGREE) {
						// If the performative is AGREE...
						if(msg.getContent().contains(AGENT1_SUBSCRIPTION_SERVICE_NAME)) {
							// If the trace service of agent 1 is in the content, then the service is still available.
							fail("Agent 1 service is still available. It should be unavailable.");
						} else {
							// Else, it is not available.
							serviceAvailable = false;
						}
					} else {
						// If the performative is not AGREE, it cannot be checked.
						fail("Agent 2 was unable to check again the available tracing services.");
					}
				}
			}
		}
		agent2.clearReceivedMessages();
	}
}
