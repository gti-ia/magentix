package TestCAgents;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarrySignalTestClass;
import TestCAgents.Agents.SallySignalTestClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for CProcessor Factories based on the example
 * myfirstCProcessorFactories
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class TestCAgentSignal {

	HarrySignalTestClass Harry;
	SallySignalTestClass Sally;
	Process qpid_broker;
	
	CountDownLatch finished = new CountDownLatch(2);
	
	Logger logger = Logger.getLogger(TestCAgentSignal.class);

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
			 * Instantiating the CAgents
			 */
			Harry = new HarrySignalTestClass(new AgentID("Harry"), finished);
			Sally = new SallySignalTestClass(new AgentID("Sally"), finished);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	/**
	 * Testing propose message sent by the initiator factory in Harry
	 */
	@Test(timeout = 60000)
	public void testSallyGTHarry() {

		Sally.setTimeout(30);

		Harry.setTimeout(10);

		Sally.start();

		Harry.start();

		// If Agent has not received the message
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("PROPOSE: Will you come with me to a movie?",
				Sally.receivedMsg);
	}

	@Test(timeout = 60000)
	public void testSallyLTHarry() {

		Sally.setTimeout(10);

		Harry.setTimeout(30);

		Sally.start();

		Harry.start();

		// If Agent has not received the message
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("PROPOSE: Will you come with me to a movie?",
				Sally.receivedMsg);
	}

	@Test(timeout = 60000)
	public void testSallyETHarry() {

		Sally.setTimeout(30);

		Harry.setTimeout(30);

		Sally.start();

		Harry.start();

		// If Agent has not received the message
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("PROPOSE: Will you come with me to a movie?",
				Sally.receivedMsg);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
