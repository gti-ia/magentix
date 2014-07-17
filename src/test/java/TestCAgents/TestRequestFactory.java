package TestCAgents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarryRequestInitiatorClass;
import TestCAgents.Agents.SallyRequestParticipantClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for Request factory template (FIPA protocol) based on the example
 * requestFactory
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class TestRequestFactory {

	HarryRequestInitiatorClass Harry;
	SallyRequestParticipantClass Sally;
	CountDownLatch finished = new CountDownLatch(2);
	CountDownLatch ready = new CountDownLatch(2);
	
	Process qpid_broker;
	Logger logger = Logger.getLogger(TestRequestFactory.class);

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
			Harry = new HarryRequestInitiatorClass(new AgentID("Harry2"),
					finished, ready);
			Sally = new SallyRequestParticipantClass(new AgentID("Sally2"),
					finished, ready);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	/**
	 * Testing inform message sent by the participant Factory to Harry
	 */
	@Test(timeout = 30000)
	public void testInformMessage() {
		Sally.start();
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals(Harry.getName() + ": " + Sally.getName()
				+ " informs me Yes, my number is 666 456 855", Harry.informMsg);
	}

	/**
	 * Testing ReceiveRequest in participant Factory in Sally
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

		assertTrue(Sally.acceptRequests);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
