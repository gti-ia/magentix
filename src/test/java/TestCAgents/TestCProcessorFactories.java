package TestCAgents;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for CProcessor Factories based on the example
 * myfirstCProcessorFactories
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestCProcessorFactories extends TestCase {

	HarryClass Harry;
	SallyClass Sally;
	Process qpid_broker;

	public TestCProcessorFactories(String name) {
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
			 * Instantiating the CAgents
			 */
			Harry = new HarryClass(new AgentID("Harry"));
			Sally = new SallyClass(new AgentID("Sally"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Testing propose message sent by the initiator factory in Harry
	 */
	@Test
	public void testProposeMessage() {
		Sally.start();
		// Temporal wait for resolving issues
		/*
		 * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		Harry.start();

		// If Agent has not received the message
		while (Sally.receivedMsg.equalsIgnoreCase("")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertEquals("PROPOSE: Will you come with me to a movie?",
				Sally.receivedMsg);
	}

	/**
	 * Testing refuse message sent by the participant factory in Sally
	 */
	@Test
	public void testRefuseMessage() {
		System.out.println("SEGUNDO TEST");

		Sally.start();
		// Temporal wait to resolve issues
		/*
		 * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		Harry.start();

		// If Agent has not received the message
		while (Harry.receivedMsg.equalsIgnoreCase("")) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		assertEquals("REFUSE: Maybe someday", Harry.receivedMsg);
	}

	protected void tierDown() throws Exception {
		AgentsConnection.disconnect();

		qpid_broker.destroy();
	}
}
