package TestJason;

import java.io.BufferedReader;
import java.io.InputStreamReader;

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

	protected void setUp() throws Exception {
		super.setUp();

		/**
		 * Setting the Logger
		 */
		// DOMConfigurator.configure("configuration/loggin.xml");

		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));

		String line = reader.readLine();
		
		while (!line.contains("Qpid Broker Ready")) {
			
			line = reader.readLine();
		}
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

	}

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

	protected void tearDown() throws Exception {
		super.tearDown();

		AgentsConnection.disconnect();

		qpid_broker.destroy();

	}

}
