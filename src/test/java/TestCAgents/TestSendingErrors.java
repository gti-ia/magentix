package TestCAgents;

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarrySendingErrorsClass;
import TestCAgents.Agents.SallyClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for CProcessor Factories based on the example
 * myfirstCProcessorFactories
 * 
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class TestSendingErrors {

	Process qpid_broker;

	SallyClass Sally;

	CountDownLatch finished;

	Logger logger = Logger.getLogger(TestSendingErrors.class);

	@Test(timeout = 150000)
	public void testSendingErrorsDefault() {

		finished = new CountDownLatch(1);
		HarrySendingErrorsClass HarryM = null;

		try {

			HarryM = new HarrySendingErrorsClass(new AgentID("HarryM"),
					finished, true);

			HarryM.start();

		} catch (Exception e1) {
			fail("Exception with Sending errors");
			e1.printStackTrace();
			logger.error(e1.getMessage());
		}
		try {
			finished.await();
		} catch (InterruptedException e) {

			fail("timeout");
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		// assertEquals("May you give me your phone number?", Harry.refuseMsg);
		// assertEquals("refuse", Sally.refuseMsg);

	}

	@Test(timeout = 150000)
	public void testSendingErrorsCustom() {

		finished = new CountDownLatch(1);
		HarrySendingErrorsClass HarryM = null;

		try {

			HarryM = new HarrySendingErrorsClass(new AgentID("HarryM"),
					finished, false);

			HarryM.start();

		} catch (Exception e1) {
			fail("Exception with Sending errors");
			e1.printStackTrace();
		}

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}
		assertTrue(true);

	}

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

			Sally = new SallyClass(new AgentID("Sally"), finished);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

}
