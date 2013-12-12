package TestCAgents;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for a basic CAgent based on the example MyFirstCAgent
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestCAgent extends TestCase {

	HelloWorldAgentClass helloWorldAgent;
	Process qpid_broker;

	public TestCAgent(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		super.setUp();
		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));

		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}

		try {

			/**
			 * Setting the configuration
			 */
			DOMConfigurator.configure("configuration/loggin.xml");

			/**
			 * Connecting to Qpid Broker, default localhost.
			 */
			AgentsConnection.connect();

			/**
			 * Instantiating the CAgent
			 */
			helloWorldAgent = new HelloWorldAgentClass(new AgentID(
					"helloWorldAgent"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * 
	 * /** Testing welcome message sent by the platform
	 */
	public void testWelcomeMessage() {
		helloWorldAgent.start();

		// If Agent has not received the welcome message
		while (helloWorldAgent.welcomeMsg.equalsIgnoreCase("")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		assertEquals(helloWorldAgent.getName()
				+ ": the welcome message is Welcome to this platform",
				helloWorldAgent.welcomeMsg);

	}

	/**
	 * Testing finalize message sent by the platform
	 */
	public void testFinalizeMessage() {
		System.out.println("Segundo test");

		helloWorldAgent.start();

		// If Agent has not received the finalize message
		while (helloWorldAgent.finalizeMsg.equalsIgnoreCase("")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		assertEquals(helloWorldAgent.getName()
				+ ": the finalize message is See you",
				helloWorldAgent.finalizeMsg);
	}

	protected void tierDown() throws Exception {
		AgentsConnection.disconnect();

		qpid_broker.destroy();
	}
}