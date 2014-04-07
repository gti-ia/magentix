package TestCAgents;

import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarrySendingErrorsClass;
import TestCAgents.Agents.SallyContractNetParticipantClass;
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
	ReentrantLock mutex = new ReentrantLock();
	Condition HarryFinished = mutex.newCondition();
	Condition SallyFinished = mutex.newCondition();

	SallyContractNetParticipantClass Sally;

	CountDownLatch finished;

	@Test(timeout = 100)
	public void testSendingErrors() {

		finished = new CountDownLatch(1);
		HarrySendingErrorsClass HarryM = null;
		try {

			HarryM = new HarrySendingErrorsClass(new AgentID("HarryM"),
					finished);

			HarryM.start();

		} catch (Exception e1) {
			fail("Exception with Sending errors");
			e1.printStackTrace();
		}
		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		// assertEquals("May you give me your phone number?", Harry.refuseMsg);
		// assertEquals("refuse", Sally.refuseMsg);

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
			Sally = new SallyContractNetParticipantClass(new AgentID("Sally1"),
					finished);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

}
