/**
 * 
 */
package TestJason.TestJason1;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

/**
 * @author Javier Jorge Cano
 * 
 */
public class TestJasonAgent1 {

	SimpleArchitecture arch = null;
	JasonAgent agent = null;
	Process qpid_broker;

	/**
	 * @param name
	 */
	//public TestJasonAgent1(String name) {
	//	super(name);
	//}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Before
	public void setUp() throws Exception {
		//super.setUp();
		/**
		 * Setting the Logger
		 */
		// Comentarlo para test?
		// DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */

		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);

		AgentsConnection.connect();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@After
	public void tearDown() throws Exception {
		//super.tearDown();
		agent.Shutdown();

		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

	/**
	 * Test method for
	 * {@link es.upv.dsic.gti_ia.jason.JasonAgent#JasonAgent(es.upv.dsic.gti_ia.core.AgentID, java.lang.String, es.upv.dsic.gti_ia.jason.MagentixAgArch)}
	 * .
	 */
	@Test (timeout=5000)
	public void testJasonAgent() {

		try {

			MagentixAgArch arch = new SimpleArchitecture();

			agent = new JasonAgent(new AgentID("bob"),
					"./src/test/java/TestJason/TestJason1/demo.asl", arch);

			agent.start();
			// Thread.sleep(6 * 1000);

			// Execute a reasoning cycle
			agent.getAgArch().getTS().reasoningCycle();

			// Obtain the beliefs (a perception obtained is the belief expected)
			String actual = agent.getAgArch().getTS().getAg().getBB()
					.getPercepts().next().toString();

			assertEquals("x(10)[source(percept)]", actual);

		} catch (Exception e) {

			fail("Should not have failed " + e.getMessage());
		}

	}

}