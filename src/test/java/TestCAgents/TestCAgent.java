package TestCAgents;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HelloWorldAgentClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for a basic CAgent based on the example MyFirstCAgent
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class TestCAgent {

	HelloWorldAgentClass helloWorldAgent;
	Process qpid_broker;
	CountDownLatch finished = new CountDownLatch(1);

	@Before
	public void setUp() throws Exception {

		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);
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
					"helloWorldAgent"), finished);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * 
	 * /** Testing welcome message sent by the platform
	 */
	@Test(timeout = 30000)
	public void testWelcomeMessage() {
		helloWorldAgent.start();

		// If Agent has not received the message
		System.out.println("Testing");
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		assertEquals(helloWorldAgent.getName()
				+ ": the welcome message is Welcome to this platform",
				helloWorldAgent.welcomeMsg);

	}

	/**
	 * Testing finalize message sent by the platform
	 */
	@Test(timeout = 30000)
	public void testFinalizeMessage() {
		System.out.println("Segundo test");

		helloWorldAgent.start();

		// If Agent has not received the message
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		assertEquals(helloWorldAgent.getName()
				+ ": the finalize message is See you",
				helloWorldAgent.finalizeMsg);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}