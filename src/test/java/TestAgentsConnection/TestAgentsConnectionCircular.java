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
public class TestAgentsConnectionCircular {

	private Logger logger;

	public TestAgentsConnectionCircular() {
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

		logger = Logger.getLogger(TestAgentsConnectionCircular.class);

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

	/**
	 * Test method for
	 * {@link es.upv.dsic.gti_ia.core.AgentsConnection#connect()}.
	 */
	@Test
	public void testMultipleBaseAgentsConnectionCircular() {

		long now = System.currentTimeMillis();

		// Concurrency
		// Mutex
		ReentrantLock mutex = new ReentrantLock();

		Condition allFinished = mutex.newCondition();

		CyclicBarrier agentsSt = null;

		CountDownLatch finished = null;

		int[] testNum = { 1, 100, 127, 128, 255, 256, 512, 1000};

		for (int agNumber : testNum) {

			logger.info("Iteracion: " + agNumber);

			agentsSt = new CyclicBarrier(agNumber + 1 + 1); // agNumber
															// +
															// Controller
															// agent
															// +
															// Main
															// thread

			finished = new CountDownLatch(agNumber);

			SenderAgentLoop senderAgent = null;
			ControlAgent agent = null;

			try {
				agent = new ControlAgent(new AgentID("ContAg"), agNumber,
						allFinished, agentsSt, mutex, finished);
			} catch (Exception e) {
				e.printStackTrace();
				fail("Should not have failed");

			}

			// Starting controler
			agent.start();

			// Creating sender agents
			for (int i = 0; i < agNumber; i++) {

				try {

					senderAgent = new SenderAgentLoop(
							new AgentID("SendAg" + i), "SendAg"
									+ ((i + 1) % agNumber), "ContAg", agentsSt,
							finished);

					// And running
					senderAgent.start();

				} catch (Exception e) {
					e.printStackTrace();
					fail("Should not have failed");

				}

			}

			// All threads await on the "start point"
			try {
				agentsSt.await();
			} catch (InterruptedException e1) {

				e1.printStackTrace();
				fail("Should not have failed");
			} catch (BrokenBarrierException e1) {

				e1.printStackTrace();
				fail("Should not have failed");
			}

			// Awaits for the ending of all sender agents
			mutex.lock();

			try {
				allFinished.await();
			} catch (InterruptedException e) {

				e.printStackTrace();
				fail("Should not have failed");
			} finally {
				mutex.unlock();
			}

			// Awaits for QPid things
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {

				e.printStackTrace();
				fail("Should not have failed");
			}
		}

		// Timing
		long end = System.currentTimeMillis();
		logger.info("Time: " + (end - now) / 1000 + " segs ( "
				+ ((end - now) / 1000) / 60 + " min)");

	}

}
