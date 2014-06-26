package TestCore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for an example of BaseAgent, SingleAgent
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestSingleAgent {

	SenderAgent2 senderAgent2;
	ConsumerAgent2 consumerAgent2;
	Process qpid_broker;

	@Before
	public void setUp() throws Exception {
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);

		/**
		 * Setting the configuration
		 */
		DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating a sender agent
			 */
			senderAgent2 = new SenderAgent2(new AgentID(
					"qpid://emisor@localhost:8080"));

			/**
			 * Instantiating a consumer agent
			 */
			consumerAgent2 = new ConsumerAgent2(new AgentID(
					"qpid://consumer@localhost:8080"));

			/**
			 * Execute the agents
			 */
			senderAgent2.start();
			consumerAgent2.start();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Testing the message sent by senderAgent2
	 */
	@Test(timeout = 8000)
	public void testSingleAgent() {

		while (consumerAgent2.getMessage() == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		assertEquals("Hello, I'm "
				+ consumerAgent2.getMessage().getSender().getLocalName(),
				consumerAgent2.getMessage().getContent());

	}
	@After
	public void tearDown() throws Exception {
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}