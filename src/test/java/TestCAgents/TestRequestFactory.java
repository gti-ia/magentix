package TestCAgents;

import junit.framework.TestCase;

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
 */

public class TestRequestFactory extends TestCase {

	HarryRequestInitiatorClass Harry;
	SallyRequestParticipantClass Sally;
	Process qpid_broker;

	public TestRequestFactory(String name) {
		super(name);
	}
	@Before
	public void setUp() throws Exception {
		super.setUp();
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);


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
			Harry = new HarryRequestInitiatorClass(new AgentID("Harry2"));
			Sally = new SallyRequestParticipantClass(new AgentID("Sally2"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
//
//	/**
//	 * Testing inform message sent by the participant Factory to Harry
//	 */
//	@Test(timeout = 30000)
//	public void testInformMessage() {
//		Sally.start();
//		Harry.start();
//
//		// If Agent has not received the inform message
//		while (Harry.informMsg.equalsIgnoreCase("")) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		assertEquals(Harry.getName() + ": " + Sally.getName()
//				+ " informs me Yes, my number is 666 456 855", Harry.informMsg);
//	}
//
//	/**
//	 * Testing ReceiveRequest in participant Factory in Sally
//	 */
//	@Test(timeout = 30000)
//	public void testRefuseMessage() {
//		Sally.start();
//		Harry.start();
//
//		// If Agent has not received the inform message
//		while (Harry.informMsg.equalsIgnoreCase("")) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//		assertTrue(Sally.acceptRequests);
//	}
	@After
	public void tearDown() throws Exception {
		super.tearDown();
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);	
		}
}
