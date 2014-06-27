package TestBaseAgent;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class TestBaseAgent {

	SenderAgent senderAgent = null;
	ConsumerAgent consumerAgent = null;
	Process qpid_broker;
	
	//Method before updating to junit4
	//
	//public TestBaseAgent(String name) {
	//	super(name);
	//}
	
	@Before
	public void setUp() throws Exception {
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		

		/**
		 * Setting the Logger
		 */
		// DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating a sender agent
			 */
			senderAgent = new SenderAgent(new AgentID(
					"qpid://emisor@localhost:8080"));

			/**
			 * Instantiating a consumer agent
			 */
			consumerAgent = new ConsumerAgent(new AgentID("consumerAgent"));

			/**
			 * Execute the agents
			 */
			senderAgent.start();
			consumerAgent.start();

		} catch (Exception e) {
			fail();
		}

	}
	@Test(timeout = 5 * 1000)
	public void testBaseAgent() {

		while (consumerAgent.getMessage() == null) {
			// System.out.println("Busco:");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		assertEquals("Hello, I'm emisor", consumerAgent.getMessage()
				.getContent());

	}
	@After
	public void tearDown() throws Exception {
		//super.tearDown();

		consumerAgent.finalize();

		AgentsConnection.disconnect();

		qpidManager.UnixQpidManager.stopQpid(qpid_broker);

	}

}
