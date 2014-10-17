package TestCAgents;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarryClass;
import TestCAgents.Agents.SallyClass;
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

public class TestCProcessorFactories {

	HarryClass Harry;
	SallyClass Sally;
	Process qpid_broker;
	CountDownLatch finished = new CountDownLatch(2);
	
	Logger logger = Logger.getLogger(TestCProcessorFactories.class);

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
			Harry = new HarryClass(new AgentID("Harry"), finished);
			Sally = new SallyClass(new AgentID("Sally"), finished);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	/**
	 * Testing propose message sent by the initiator factory in Harry
	 */
	@Test(timeout = 30000)
	public void testProposeMessage() {
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

	/**
	 * Testing refuse message sent by the participant factory in Sally
	 */
	@Test(timeout = 30000)
	public void testRefuseMessage() {

		Sally.start();

		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("REFUSE: Maybe someday", Harry.receivedMsg);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
