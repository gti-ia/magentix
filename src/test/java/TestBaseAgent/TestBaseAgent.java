package TestBaseAgent;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class TestBaseAgent extends TestCase {

	SenderAgent senderAgent = null;
	ConsumerAgent consumerAgent = null;
	Process qpid_broker;

	public TestBaseAgent(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
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

	protected void tearDown() throws Exception {
		super.tearDown();

		consumerAgent.finalize();

		AgentsConnection.disconnect();

		qpidManager.UnixQpidManager.stopQpid(qpid_broker);

	}

}
