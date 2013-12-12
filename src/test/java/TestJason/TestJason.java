package TestJason;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

public class TestJason extends TestCase {

	MagentixAgArch arch;
	JasonAgent agent;
	Process qpid_broker;
	
	public TestJason(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		/**
		 * Setting the Logger
		 */
		// Comentarlo para test?
		DOMConfigurator.configure("configuration/loggin.xml");
		
		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));

		String line = reader.readLine();
		
		while (!line.contains("Qpid Broker Ready")) {
			
			line = reader.readLine();
		}
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
		AgentsConnection.disconnect();
		
		qpid_broker.destroy();
	}

	public void testArch() {

		SimpleArchitecture archR = new SimpleArchitecture();

		JasonAgent agent1 = null;
		JasonAgent agent2 = null;
		try {
			agent1 = new JasonAgent(new AgentID("testR1"),
					"./src/test/java/TestJason/demo.asl", archR);
			agent2 = new JasonAgent(new AgentID("testR2"),
					"./src/test/java/TestJason/demo.asl", archR);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		// Start the agent execution

		agent2.stopReasoning();

		agent1.getAgArch().run();

		String beliefI = null;
		String belief1 = null;
		String belief2 = null;

		// REVISAR ESTO
		if (agent1.getAgArch().getTS().getAg().getBB().getPercepts().hasNext()) {
			beliefI = agent1.getAgArch().getTS().getAg().getBB().getPercepts()
					.next().toString();
		}

		agent1.stopReasoning();

		agent1.start();

		agent2.stopReasoning();

		agent2.start();

		agent1.getAgArch().getTS().reasoningCycle();
		agent2.getAgArch().getTS().reasoningCycle();
		agent1.stopReasoning();

		if (agent1.getAgArch().getTS().getAg().getBB().getPercepts().hasNext()) {
			belief1 = agent1.getAgArch().getTS().getAg().getBB().getPercepts()
					.next().toString();
		}

		if (agent2.getAgArch().getTS().getAg().getBB().getPercepts().hasNext()) {
			belief2 = agent2.getAgArch().getTS().getAg().getBB().getPercepts()
					.next().toString();
		}

		log(agent1, beliefI);
		log(agent1, belief1);
		log(agent2, belief2);
		assertEquals(belief1, belief2);
		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

	public void testCreateDefaultFactory() {

		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		// Start the agent execution

		SimpleArchitecture2 arch2 = new SimpleArchitecture2();
		// Agente para enviarle un mensaje de prueba
		JasonAgent agent2 = null;
		try {
			agent2 = new JasonAgent(new AgentID("sender"),
					"./src/test/java/TestJason/demo2.asl", arch2);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		agent.start();
		agent2.start();

		agent.getAgArch().getTS().reasoningCycle();
		agent2.getAgArch().getTS().reasoningCycle();

		String belief = null;

		if (agent.getAgArch().getTS().getAg().getBB().getPercepts().hasNext()) {
			belief = agent.getAgArch().getTS().getAg().getBB().getPercepts()
					.next().toString();
		}

		agent.stopReasoning();

		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		assertEquals("x(10)[source(percept)]", belief);

		agent.Shutdown();
		agent2.Shutdown();
	}

	public void testExecution() {

		try {
			arch = new SimpleArchitecture();
	
			try {
				agent = new JasonAgent(new AgentID("test"),
						"./src/test/java/TestJason/demo.asl", arch);
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
	
			agent.start();
	
			agent.getAgArch().getTS().reasoningCycle();
	
			agent.stopReasoning();
	
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
	
			agent.Shutdown();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Fail in testExecution");
		}

	}

	public void testJasonAgent() {

		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		// Start the agent execution
		agent.start();

		agent.getAgArch().getTS().reasoningCycle();

		String belief = null;

		if (agent.getAgArch().getTS().getAg().getBB().getPercepts().hasNext()) {
			belief = agent.getAgArch().getTS().getAg().getBB().getPercepts()
					.next().toString();
		}

		agent.stopReasoning();

		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		assertEquals("x(10)[source(percept)]", belief);

		agent.Shutdown();
	}

	public void testGetAgArch() {

		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}

		agent.start();

		MagentixAgArch mA = agent.getAgArch();

		assertEquals("test", mA.getAgName());

		agent.getAgArch().getTS().reasoningCycle();

		// Stop the agent by means of architecture
		mA.stopAg();

		// Check if

		assertEquals(false, agent.getAgArch().isRunning());

		String belief = null;

		if (agent.getAgArch().getTS().getAg().getBB().getPercepts().hasNext()) {
			belief = agent.getAgArch().getTS().getAg().getBB().getPercepts()
					.next().toString();
		}

		assertEquals("x(10)[source(percept)]", belief);

		agent.stopReasoning();

		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		agent.Shutdown();

	}

	public void testWrongConst() {

		JasonAgent agentNoArch = null;
		MagentixAgArch mA = new MagentixAgArch();

		try {
			agentNoArch = new JasonAgent(new AgentID("testNoArch"),
					"./src/test/java/TestJason/noexist.asl", mA);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(new AgentID("testNoArch"),
					"./src/test/java/TestJason/demo.asl", null);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(new AgentID("testNoArch"), null, mA);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(new AgentID("testNoArch"), null, null);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(null,
					"./src/test/java/TestJason/demo.asl", mA);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(null,
					"./src/test/java/TestJason/demo.asl", null);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(null, null, mA);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

		try {
			agentNoArch = new JasonAgent(null, null, null);

			fail("Should have failed");
		} catch (Exception e1) {
			
			assertTrue(true);
		}

	}

	public void testJasonAgentRepeat() {

		try {
			arch = new SimpleArchitecture();

			agent = new JasonAgent(new AgentID("test_duplicate"),
					"./src/test/java/TestJason/demo.asl", arch);
			agent.start();

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}

			agent.Shutdown();

			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}

			arch = new SimpleArchitecture();

			agent = new JasonAgent(new AgentID("test_duplicate"),
					"./src/test/java/TestJason/demo.asl", arch);
			agent.start();

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}

			agent.Shutdown();

			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail("Fail in multiple Jason Agent creation");
		}

	}

	public void log(JasonAgent agent, String msg) {
		agent.getAgArch().getTS().getAg().getLogger().info(msg);
	}

}
