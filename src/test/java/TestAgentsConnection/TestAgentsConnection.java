/**
 * 
 */
package TestAgentsConnection;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

/**
 * @author Javier Jorge Cano
 * 
 */
public class TestAgentsConnection {

	private Logger logger;

	public TestAgentsConnection() {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");

		logger = Logger.getLogger(TestAgentsConnection.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link es.upv.dsic.gti_ia.core.AgentsConnection#connect()}.
	 */

	@Test
	public void testMultipleJasonAgentsConnection() {

		// Throws 1000 Jason agents
		List<JasonAgent> agentList = new ArrayList<JasonAgent>();

		int agNumber = 1000;
		String agName = "ag";
		MagentixAgArch agentArchitecture;
		JasonAgent agent = null;

		for (int i = 0; i < agNumber; i++) {

			agentArchitecture = new SimpleArchitecture();
			try {
				agent = new JasonAgent(new AgentID(agName + i),
						"./src/test/java/TestAgentsConnection/demo.asl",
						agentArchitecture);
				agentList.add(agent);

			} catch (Exception e) {
				e.printStackTrace();
				fail("Should not have failed");
			}
			agent.start();

		}

		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		for (int i = 0; i < agNumber; i++)
			agentList.get(i).Shutdown();

	}

	/**
	 * Test method for
	 * {@link es.upv.dsic.gti_ia.core.AgentsConnection#connect()}.
	 */
	@Test
	public void testMultipleCAgentsConnection() {

		// Throws 1000 Cagents
		List<CAgent> agentList = new ArrayList<CAgent>();

		int agNumber = 1000;
		String agName = "CAg";
		CAgent agent = null;

		for (int i = 0; i < agNumber; i++) {

			try {
				agent = new HelloWorldAgentClass(new AgentID(agName + i));
				agentList.add(agent);

			} catch (Exception e) {
				e.printStackTrace();
				fail("Should not have failed");
			}
			agent.start();

		

		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail("Should not have failed");
		}

		for (int i = 0; i < agNumber; i++)
			agentList.get(i).Shutdown();

	}



}
