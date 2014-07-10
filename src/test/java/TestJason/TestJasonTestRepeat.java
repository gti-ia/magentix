package TestJason;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;

public class TestJasonTestRepeat extends TestCase {

	SimpleArchitecture arch = null;

	JasonAgent agent = null;

	Process qpid_broker;

	public TestJasonTestRepeat(String name) {
		super(name);
	}

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * Setting the Logger
		 */
		// DOMConfigurator.configure("configuration/loggin.xml");

		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

	}

	@Test (timeout=5000)
	public void testJasonAgentRepeat() {

		try {
			arch = new SimpleArchitecture();

			agent = new JasonAgent(new AgentID("test_duplicate"),
					"./src/test/java/TestJason/demo.asl", arch);
			agent.start();

			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			agent.Shutdown();

			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			arch = new SimpleArchitecture();

			agent = new JasonAgent(new AgentID("test_duplicate"),
					"./src/test/java/TestJason/demo.asl", arch);
			agent.start();

			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			agent.Shutdown();

			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);

	}

}
