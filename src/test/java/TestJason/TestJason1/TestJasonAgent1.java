/**
 * 
 */
package TestJason.TestJason1;

import jason.asSyntax.Literal;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;
import junit.framework.TestCase;

/**
 * @author Javier Jorge Cano
 * 
 */
public class TestJasonAgent1 extends TestCase {

	SimpleArchitecture arch = null;
	JasonAgent agent = null;

	/**
	 * @param name
	 */
	public TestJasonAgent1(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * Setting the Logger
		 */
		// Comentarlo para test?
		// DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		agent.Shutdown();
	}

	/**
	 * Test method for
	 * {@link es.upv.dsic.gti_ia.jason.JasonAgent#JasonAgent(es.upv.dsic.gti_ia.core.AgentID, java.lang.String, es.upv.dsic.gti_ia.jason.MagentixAgArch)}
	 * .
	 */
	public void testJasonAgent() {

		try {

			MagentixAgArch arch = new SimpleArchitecture();

			agent = new JasonAgent(new AgentID("bob"),
					"./src/test/java/TestJason/TestJason1/demo.asl", arch);

			agent.start();
//			Thread.sleep(6 * 1000);
			
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