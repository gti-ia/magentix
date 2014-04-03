package TestCAgents;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import TestCAgents.Agents.SallyContractNetParticipantClass;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for CProcessor Factories based on the example
 * myfirstCProcessorFactories
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 */

public class TestSendingErrors extends TestCase {

	Process qpid_broker;
	ReentrantLock mutex = new ReentrantLock();
	Condition HarryFinished = mutex.newCondition();
	Condition SallyFinished = mutex.newCondition();
	CountDownLatch finished;
	SallyContractNetParticipantClass Sally;

//	@Test
//	public void testSendingErrors() {
//
//		finished = new CountDownLatch(1);
//		HarrySendingErrorsClass HarryM = null;
//		try {
//
//			HarryM = new HarrySendingErrorsClass(new AgentID("HarryM"),
//					finished);
//
//		} catch (Exception e1) {
//			fail("Exception with multiple agents");
//			e1.printStackTrace();
//		}
//
//		HarryM.start();
//
//		try {
//			finished.await();
//		} catch (InterruptedException e) {
//
//			e.printStackTrace();
//		}
//		// assertEquals("May you give me your phone number?", Harry.refuseMsg);
//		// assertEquals("refuse", Sally.refuseMsg);
//
//		finished = new CountDownLatch(2);
//	}

	//

	//
	// public TestSendingErrors(String name) {
	// super(name);
	//
	// }
	//
	public void setUp() throws Exception {
		super.setUp();
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

	protected void tearDown() throws Exception {
		super.tearDown();
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
	//
	// // /**
	// // * Testing propose message sent by the initiator factory in Harry
	// // */
	// // @Test
	// // public void testProposeMessage() {
	// // Sally.start();
	// // // Temporal wait for resolving issues
	// // /*
	// // * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
	// // * Auto-generated catch block e.printStackTrace(); }
	// // */
	// // Harry.start();
	// //
	// // // If Agent has not received the message
	// // while (Sally.receivedMsg.equalsIgnoreCase("")) {
	// // try {
	// // Thread.sleep(15 * 1000);
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // }
	// //
	// // // assertEquals("PROPOSE: Will you come with me to a movie?",
	// // // Sally.receivedMsg);
	// // while (Harry.isRunning())
	// // ;
	// //
	// // try {
	// // Thread.sleep(10 * 1000);
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // }
	//
	// /**
	// * Testing propose message sent by the initiator factory in Harry
	// */
	// @Test
	// public void testProposeMessage() {
	//
	// Sally.start();
	// // Temporal wait for resolving issues
	// /*
	// * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
	// * Auto-generated catch block e.printStackTrace(); }
	// */
	// Harry.start();
	//
	// try {
	// finished.await();
	// // SallyFinished.await();
	// } catch (InterruptedException e) {
	//
	// e.printStackTrace();
	// }
	//
	// try {
	// Thread.sleep(10 * 1000);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// }
	//
	// //
	// // /**
	// // * Testing refuse message sent by the participant factory in Sally
	// // */
	// // @Test
	// // public void testRefuseMessage() {
	// // System.out.println("SEGUNDO TEST");
	// //
	// // Sally.start();
	// // // Temporal wait to resolve issues
	// // /*
	// // * try { Thread.sleep(3000); } catch (InterruptedException e) { // TODO
	// // * Auto-generated catch block e.printStackTrace(); }
	// // */
	// // Harry.start();
	// //
	// // // If Agent has not received the message
	// // while (Harry.receivedMsg.equalsIgnoreCase("")) {
	// // try {
	// // Thread.sleep(100);
	// // } catch (InterruptedException e) {
	// // // TODO Auto-generated catch block
	// // e.printStackTrace();
	// // }
	// // }
	// //
	// // assertEquals("REFUSE: Maybe someday", Harry.receivedMsg);
	// // }
	//

}
