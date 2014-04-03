package TestCAgents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Ignore;
import org.junit.Test;

import TestCAgents.Agents.HarryRecruitingInitiatorClass;
import TestCAgents.Agents.HarryRequestInitiatorClass;
import TestCAgents.Agents.OtherParticipantClass;
import TestCAgents.Agents.SallyClass;
import TestCAgents.Agents.SallyRecruitingParticipantClass;
import TestCAgents.Agents.SallyRequestParticipantClass;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for Request factory template (FIPA protocol) based on the example
 * requestFactory
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 * @author Jose Manuel Mejias Rodriguez - jmejias@dsic.upv.es
 */

public class TestRecruitingFactory extends TestCase {
//
//	HarryRecruitingInitiatorClass Harry;
//	SallyRecruitingParticipantClass Sally;
//	OtherParticipantClass theOther;
//	Process qpid_broker;
//	ReentrantLock mutex = new ReentrantLock();
//	Condition HarryFinished = mutex.newCondition();
//	Condition SallyFinished = mutex.newCondition();
//	CountDownLatch finished = new CountDownLatch(2);
//
//	public TestRecruitingFactory(String name) {
//		super(name);
//	}
//
//	public void setUp() throws Exception {
//		super.setUp();
//		qpid_broker = qpidManager.UnixQpidManager.startQpid(
//				Runtime.getRuntime(), qpid_broker);
//
//		try {
//
//			/**
//			 * Setting the configuration
//			 */
//			DOMConfigurator.configure("configuration/loggin.xml");
//
//			/**
//			 * Connecting to Qpid Broker, default localhost.
//			 */
//			AgentsConnection.connect();
//
//			/**
//			 * Instantiating the CAgents
//			 */
//			Harry = new HarryRecruitingInitiatorClass(new AgentID("Harry"),
//					finished);
//			Sally = new SallyRecruitingParticipantClass(new AgentID("Sally"),
//					finished);
//			theOther = new OtherParticipantClass(new AgentID("other"));
//
//		} catch (Exception e) {
//			System.out.println(e.getMessage());
//		}
//
//	}
//
////	@Test
////	public void testProtocol() {
////		Sally.start();
////		Sally.setMode(1);
////		Harry.start();
////
////		try {
////			finished.await();
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////
////		// assertEquals("COMPLETE", Harry.informMsg);
////		// assertEquals("COMPLETE", Sally.informMsg);
////	}
//
//	@Test
//	public void testAgree() {
//		Sally.start();
//		Sally.setMode(0);
//		Harry.start();
//		theOther.start();
//
//		try {
//			finished.await();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		assertEquals("Nup", Harry.agreeMsg);
//		assertEquals("Nup", Sally.agreeMsg);
//	}
//	
////	@Test
////	public void testRefuse() {
////		Sally.start();
////		Sally.setMode(1);
////		Harry.start();
////
////		try {
////			finished.await();
////		} catch (InterruptedException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////
////		assertEquals("Nup", Harry.refuseMsg);
////		assertEquals("Nup", Sally.refuseMsg);
////	}
//
//	public void tearDown() throws Exception {
//		super.tearDown();
//		AgentsConnection.disconnect();
//		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
//	}
}
