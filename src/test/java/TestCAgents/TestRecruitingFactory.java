package TestCAgents;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarryRecruitingInitiatorClass;
import TestCAgents.Agents.OtherParticipantClass;
import TestCAgents.Agents.SallyRecruitingParticipantClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for Request factory template (FIPA protocol) based on the example
 * requestFactory
 * 
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class TestRecruitingFactory {

	HarryRecruitingInitiatorClass Harry;
	SallyRecruitingParticipantClass Sally;
	OtherParticipantClass theOther;
	Process qpid_broker;
	ReentrantLock mutex = new ReentrantLock();
	Condition HarryFinished = mutex.newCondition();
	Condition SallyFinished = mutex.newCondition();
	CountDownLatch finished = new CountDownLatch(2);

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
			Harry = new HarryRecruitingInitiatorClass(new AgentID("Harry"),
					finished);
			Sally = new SallyRecruitingParticipantClass(new AgentID("Sally"),
					finished);
			theOther = new OtherParticipantClass(new AgentID("other"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test(timeout = 100)
	public void testProtocol() {
		Sally.start();
		Sally.setMode(1);
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// assertEquals("COMPLETE", Harry.informMsg);
		// assertEquals("COMPLETE", Sally.informMsg);
	}

	@Test(timeout = 100)
	public void testAgree() {
		Sally.start();
		Sally.setMode(0);
		Harry.start();
		theOther.start();

		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals("Nup", Harry.agreeMsg);
		assertEquals("Nup", Sally.agreeMsg);
	}

	@Test(timeout = 100)
	public void testRefuse() {
		Sally.start();
		Sally.setMode(1);
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals("Nup", Harry.refuseMsg);
		assertEquals("Nup", Sally.refuseMsg);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
