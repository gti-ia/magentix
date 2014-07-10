package TestJason;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

public class TestMagentixAgArch extends TestCase {

	private SimpleArchitecture arch;
	private JasonAgent agent;

	private JasonAgent receiverJ;
	private JasonAgent senderJ;
	Process qpid_broker;

	public TestMagentixAgArch(String name) {
		super(name);
	}

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * Setting the Logger
		 */
		// Comentarlo para test?
		DOMConfigurator.configure("configuration/loggin.xml");

		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		arch = new SimpleArchitecture();
		// agent = new JasonAgent(new AgentID("test"),
		// "./src/test/java/TestJason/demo.asl", arch);

	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();

		AgentsConnection.disconnect();

		qpidManager.UnixQpidManager.stopQpid(qpid_broker);

	}

	@Test (timeout=5000)
	public void testStopAg() {

		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test_magentixArch"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		agent.start();

		assertEquals(true, agent.getAgArch().isRunning());

		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		agent.getAgArch().stopAg();

		assertEquals(false, agent.getAgArch().isRunning());

		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		agent.Shutdown();

	}

	@Test (timeout=5000)
	public void testCanSleep() {

		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test_magentixArch"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		agent.start();

		assertEquals(true, agent.getAgArch().canSleep());

		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		agent.Shutdown();

	}

	@Test (timeout=5000)
	public void testIsRunning() {

		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test_magentixArch"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		agent.start();

		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		assertEquals(true, agent.getAgArch().isRunning());

		agent.getAgArch().stopAg();

		assertEquals(false, agent.getAgArch().isRunning());

		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		agent.Shutdown();

	}

	@Test (timeout=5000)
	public void testGetAgName() {
		arch = new SimpleArchitecture();

		try {
			agent = new JasonAgent(new AgentID("test_magentixArch"),
					"./src/test/java/TestJason/demo.asl", arch);
		} catch (Exception e1) {

			e1.printStackTrace();
		}
		agent.start();

		assertEquals("test_magentixArch", agent.getAgArch().getAgName());

		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		agent.Shutdown();

	}

	@Test (timeout=5000)
	public void testSendMsgMessage() {
		MagentixAgArch arch = null;
		MagentixAgArch arch2 = null;
		try {
			arch = new MagentixAgArch();
			receiverJ = new JasonAgent(new AgentID("receiverJ"),
					"./src/test/java/TestJason/receiver.asl", arch);

			arch2 = new MagentixAgArch();
			senderJ = new JasonAgent(new AgentID("senderJ"),
					"./src/test/java/TestJason/sender.asl", arch2);

		} catch (Exception e) {

			e.printStackTrace();
		}

		receiverJ.start();
		senderJ.start();

		String ilForce = "tell";
		String sender = senderJ.getName();
		String receiver = receiverJ.getName();
		String replyWith = "";

		ACLMessage m = new ACLMessage();
		m.setContent("vl(10)");

		Class<MagentixAgArch> magentixAgArch = MagentixAgArch.class;
		try {
			Method method = magentixAgArch.getDeclaredMethod(
					"translateContentToJason", ACLMessage.class);
			method.setAccessible(true);

			// Performative "Inform"

			// Object propCont = translateContentToJason(m);
			Object propCont = method.invoke(new MagentixAgArch(), m);
			jason.asSemantics.Message im = new jason.asSemantics.Message(
					ilForce, sender, receiver, propCont, replyWith);
			arch.sendMsg(im);
		} catch (IllegalAccessException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (SecurityException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		waitAg(5);

		@SuppressWarnings("deprecation")
		String belief = receiverJ.getAgArch().getTS().getAg().getBB().getAll()
				.next().toString();

		assertTrue(belief.equals("vl(10)[source(self)]"));

		receiverJ.Shutdown();
		senderJ.Shutdown();

	}

	@Test (timeout=5000)
	public void testPerceive() {
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

	@Test (timeout=5000)
	public void testRun() {
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
		assertEquals(true, agent.getAgArch().isRunning());

		mA.stopAg();

		// Check if
		assertEquals(false, agent.getAgArch().isRunning());

		agent.stopReasoning();

		try {
			Thread.sleep(2 * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		agent.Shutdown();

	}

	@Test (timeout=5000)
	public void testInit() {
		@SuppressWarnings("unused")
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

	@Test (timeout=5000)
	public void testTranslateContentToJason() {
		MagentixAgArch arch = null;
		MagentixAgArch arch2 = null;
		try {
			arch = new MagentixAgArch();
			receiverJ = new JasonAgent(new AgentID("receiverJ"),
					"./src/test/java/TestJason/receiver.asl", arch);

			arch2 = new MagentixAgArch();
			senderJ = new JasonAgent(new AgentID("senderJ"),
					"./src/test/java/TestJason/sender.asl", arch2);

		} catch (Exception e) {

			e.printStackTrace();
		}

		receiverJ.start();
		senderJ.start();

		String ilForce = "tell";
		String sender = senderJ.getName();
		String receiver = receiverJ.getName();
		String replyWith = "";

		ACLMessage m = new ACLMessage();
		m.setContent("vl(10)");

		Class<MagentixAgArch> magentixAgArch = MagentixAgArch.class;
		try {
			Method method = magentixAgArch.getDeclaredMethod(
					"translateContentToJason", ACLMessage.class);
			method.setAccessible(true);

			// Performative "Inform"

			// Object propCont = translateContentToJason(m);
			Object propCont = method.invoke(new MagentixAgArch(), m);
			jason.asSemantics.Message im = new jason.asSemantics.Message(
					ilForce, sender, receiver, propCont, replyWith);
			arch.sendMsg(im);

			assertEquals("vl(10)", propCont.toString());

		} catch (IllegalAccessException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (SecurityException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

		waitAg(5);

		receiverJ.Shutdown();
		senderJ.Shutdown();

	}

	@Test (timeout=5000)
	public void testAddMessage() {
		MagentixAgArch arch = null;
		MagentixAgArch arch2 = null;
		try {
			arch = new MagentixAgArch();
			receiverJ = new JasonAgent(new AgentID("receiverJ"),
					"./src/test/java/TestJason/receiver.asl", arch);

			arch2 = new MagentixAgArch();
			senderJ = new JasonAgent(new AgentID("senderJ"),
					"./src/test/java/TestJason/sender.asl", arch2);

		} catch (Exception e) {

			e.printStackTrace();
		}

		receiverJ.start();
		senderJ.start();

		String ilForce = "tell";
		String sender = senderJ.getName();
		String receiver = receiverJ.getName();
		String replyWith = "";

		ACLMessage m = new ACLMessage();
		m.setContent("test");
		m.setPerformative(ilForce);
		m.setSender(new AgentID(sender));
		m.setReceiver(new AgentID(receiver));
		m.setReplyWith(replyWith);
		Class<MagentixAgArch> magentixAgArch = MagentixAgArch.class;
		Field field = null;
		try {

			Method method = magentixAgArch.getDeclaredMethod("addMessage",
					ACLMessage.class);
			method.setAccessible(true);
			method.invoke(arch, m);

			field = magentixAgArch.getDeclaredField("messageList");
			field.setAccessible(true);

			@SuppressWarnings("unchecked")
			Queue<ACLMessage> messageList = (Queue<ACLMessage>) field.get(arch);

			assertEquals("test", messageList.peek().getContent());

		} catch (IllegalAccessException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (SecurityException e1) {
			fail("Reflection fail");
			e1.printStackTrace();
		} catch (NoSuchFieldException e) {
			fail("Reflection fail");
			e.printStackTrace();
		}

		waitAg(5);

		receiverJ.Shutdown();
		senderJ.Shutdown();

	}

	private void waitAg(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

}
