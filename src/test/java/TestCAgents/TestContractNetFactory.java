package TestCAgents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import TestCAgents.Agents.HarryContractNetInitiatorClass;
import TestCAgents.Agents.HarryContractNetMultipletInitiatorClass;
import TestCAgents.Agents.SallyContractNetParticipantClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for Request factory template (FIPA protocol) based on the example
 * requestFactory
 * 
 * @author Javier Jorge - jjorge@dsic.upv.es
 */

public class TestContractNetFactory {

	HarryContractNetInitiatorClass Harry;
	SallyContractNetParticipantClass Sally;
	SallyContractNetParticipantClass Sally2;
	SallyContractNetParticipantClass Sally3;
	Process qpid_broker;
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
			Harry = new HarryContractNetInitiatorClass(new AgentID("Harry"),
					finished);
			Sally = new SallyContractNetParticipantClass(new AgentID("Sally"),
					finished);

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Test(timeout = 30000)
	public void testProtocolMultipleAgents() {

		finished = new CountDownLatch(4);
		HarryContractNetMultipletInitiatorClass HarryM = null;
		try {

			HarryM = new HarryContractNetMultipletInitiatorClass(new AgentID(
					"HarryM"), finished);

			Sally = new SallyContractNetParticipantClass(new AgentID("Sally1"),
					finished);

			Sally2 = new SallyContractNetParticipantClass(
					new AgentID("Sally2"), finished);

			Sally3 = new SallyContractNetParticipantClass(
					new AgentID("Sally3"), finished);

		} catch (Exception e1) {
			fail("Exception with multiple agents");
			e1.printStackTrace();
		}

		Sally.setMode(1); // REFUSE MODE
		Sally.start();
		Sally2.setMode(1); // REFUSE MODE
		Sally2.start();
		// ACCEPT MODE
		Sally3.start();
		HarryM.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		assertEquals("refuse", Sally.refuseMsg);
		assertEquals("refuse", Sally2.refuseMsg);
		assertEquals("COMPLETE", HarryM.informMsg);
		assertEquals("COMPLETE", Sally3.informMsg);
		finished = new CountDownLatch(2);
	}

	@Test(timeout = 30000)
	public void testProtocolMultipleAgentsRefuse() {

		finished = new CountDownLatch(4);
		HarryContractNetMultipletInitiatorClass HarryM = null;
		try {

			HarryM = new HarryContractNetMultipletInitiatorClass(new AgentID(
					"HarryM"), finished);

			Sally = new SallyContractNetParticipantClass(new AgentID("Sally1"),
					finished);

			Sally2 = new SallyContractNetParticipantClass(
					new AgentID("Sally2"), finished);

			Sally3 = new SallyContractNetParticipantClass(
					new AgentID("Sally3"), finished);

		} catch (Exception e1) {
			fail("Exception with multiple agents");
			e1.printStackTrace();
		}

		Sally.setMode(1); // REFUSE MODE
		Sally.start();
		Sally2.setMode(1); // REFUSE MODE
		Sally2.start();
		Sally3.setMode(1); // REFUSE MODE
		Sally3.start();

		HarryM.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		assertEquals("refuse", Sally.refuseMsg);
		assertEquals("refuse", Sally2.refuseMsg);
		assertEquals("refuse", Sally3.refuseMsg);
		assertEquals("May you give me your phone number?", HarryM.refuseMsg);

		finished = new CountDownLatch(2);
	}

	@Test(timeout = 30000)
	public void testProtocolMultipleAgentsAccept() {

		finished = new CountDownLatch(4);
		HarryContractNetMultipletInitiatorClass HarryM = null;
		try {

			HarryM = new HarryContractNetMultipletInitiatorClass(new AgentID(
					"HarryM"), finished);

			Sally = new SallyContractNetParticipantClass(new AgentID("Sally1"),
					finished);

			Sally2 = new SallyContractNetParticipantClass(
					new AgentID("Sally2"), finished);

			Sally3 = new SallyContractNetParticipantClass(
					new AgentID("Sally3"), finished);

		} catch (Exception e1) {
			fail("Exception with multiple agents");
			e1.printStackTrace();
		}
		// ACCEPT MODE
		Sally.start();
		Sally2.start();
		Sally3.start();
		HarryM.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		assertEquals("COMPLETE", Sally.informMsg);
		assertEquals("COMPLETE", Sally2.informMsg);
		assertEquals("COMPLETE", Sally3.informMsg);
		assertEquals("COMPLETE", HarryM.informMsg);

		finished = new CountDownLatch(2);
	}

	@Test(timeout = 30000)
	public void testProtocol() {
		Sally.start();
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		assertEquals("COMPLETE", Harry.informMsg);
		assertEquals("COMPLETE", Sally.informMsg);
	}

	@Test(timeout = 30000)
	public void testProtocolRefuseProposal() {

		Sally.setMode(1); // REFUSE MODE
		Sally.start();
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		assertEquals("May you give me your phone number?", Harry.refuseMsg);
		assertEquals("refuse", Sally.refuseMsg);
	}

	@Test(timeout = 30000)
	public void testProtocolRejectProposal() {

		Sally.start();
		Harry.setMode(1);
		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		assertEquals("NO,THANKS", Harry.rejectMsg);
		// REVISAR assertEquals("NO,THANKS", Sally.rejectMsg);// No se envia
		// nada cuando se
		// rechaza
		assertEquals("", Sally.rejectMsg);
	}

	@Test(timeout = 30000)
	public void testProtocolAcceptProposal() {

		Sally.start();

		Harry.setMode(0);

		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		assertEquals("OK", Harry.acceptMsg);
		assertEquals("OK", Sally.acceptMsg);
	}

	@Test(timeout = 30000)
	public void testNotUnderstood() {
		Sally.start();
		Sally.FAIL = true;

		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		// TO DO, devuelve null, no obtiene el mensaje, it's ok?
		assertEquals(null, Harry.notUnderstood);
		assertEquals(null, Sally.notUnderstood);
	}

	@Test(timeout = 30000)
	public void testProtocolRcvFailure() {

		Sally.start();

		Sally.FAIL = true;

		Harry.start();

		try {
			finished.await();
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		assertEquals("Error", Harry.receiveFailure);
		assertEquals("Error", Sally.receiveFailure);
	}

	@After
	public void tearDown() throws Exception {

		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
