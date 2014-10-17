package TestCAgents;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
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
 * Test class for Recruiting factory template (FIPA protocol)
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

	Logger logger = Logger.getLogger(TestRecruitingFactory.class);

	public final int AGREE = 0;
	public final int REFUSE = 1;
	public final int LOCATE = 0;
	public final int NO_LOCATE = 1;
	public final int SUCCESS = 0;
	public final int FAILURE = 1;

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
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	@Test(timeout = 30000)
	public void testAgreeAndInform() {
		Sally.start();
		Sally.setMode(AGREE);
		Sally.setModeLocate(LOCATE);
		Sally.setResultOfSubprotocol(SUCCESS);
		Harry.start();
		theOther.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("Done (by other)", Harry.informMsg);
		assertEquals("Done (by other)", Sally.informMsg);
	}

	@Test(timeout = 30000)
	public void testAgreeAndFailure() {

		Sally.start();
		Sally.setMode(AGREE);
		Sally.setModeLocate(LOCATE);
		Sally.setResultOfSubprotocol(FAILURE);
		Harry.start();
		theOther.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("SubProtocol failed :(", Harry.failureProxyMsg);
		assertEquals("SubProtocol failed :(", Sally.informMsg);
	}

	@Test(timeout = 60000)
	public void testRefuse() {

		finished = new CountDownLatch(2);
		
		Sally.setFinished(finished);
		Harry.setFinished(finished);

		Sally.start();
		Sally.setMode(REFUSE);
		Sally.setModeLocate(LOCATE);
		Sally.setResultOfSubprotocol(SUCCESS);
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("Nup", Harry.refuseMsg);
		assertEquals("Nup", Sally.refuseMsg);
	}

	@Test(timeout = 30000)
	public void testFailureNoMatch() {
		Sally.start();
		Sally.setMode(AGREE);
		Sally.setModeLocate(NO_LOCATE);
		Sally.setResultOfSubprotocol(SUCCESS);
		Harry.start();
		theOther.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("Agent not found", Harry.failureNoMatchMsg);
		assertEquals("Agent not found", Sally.failureNoMatchMsg);
	}

	@Test(timeout = 30000)
	public void testFailureProxy() {
		Sally.start();
		Sally.setMode(AGREE);
		Sally.setModeLocate(LOCATE);
		Sally.setResultOfSubprotocol(FAILURE);
		Harry.start();
		theOther.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
			logger.error(e.getMessage());
		}

		assertEquals("SubProtocol failed :(", Harry.failureProxyMsg);
		assertEquals("SubProtocol failed :(", Sally.failureMsg);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
