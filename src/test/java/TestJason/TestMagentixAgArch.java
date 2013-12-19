package TestJason;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import jason.asSyntax.ASSyntax;
import jason.asSyntax.StringTermImpl;
import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

public class TestMagentixAgArch extends TestCase {

	private SimpleArchitecture arch;
	private JasonAgent agent;
	private JasonAgent bob;
	private JasonAgent maria;
	private JasonAgent receiverJ;
	private JasonAgent senderJ;
	Process qpid_broker;

	public TestMagentixAgArch(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * Setting the Logger
		 */
		// Comentarlo para test?
		DOMConfigurator.configure("configuration/loggin.xml");
		
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		arch = new SimpleArchitecture();
		// agent = new JasonAgent(new AgentID("test"),
		// "./src/test/java/TestJason/demo.asl", arch);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		
		AgentsConnection.disconnect();
		
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	
	}

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
    /*
	public void testCheckMail() {

		try {
			MagentixAgArch arch = new SimpleArchitecture();
			bob = new JasonAgent(new AgentID("bob"),
					"./src/test/java/TestJason/bob.asl", arch);

			MagentixAgArch arch2 = new SimpleArchitecture();
			maria = new JasonAgent(new AgentID("maria"),
					"./src/test/java/TestJason/maria.asl", arch2);

		} catch (Exception e) {

			e.printStackTrace();
		}

		maria.start();

		waitAg(3);

		maria.getAgArch().checkMail();
		System.out.println(maria.getAgArch().getTS().getC().getMailBox()
				.toString());

		waitAg(3);

		bob.start();

		waitAg(3);
		// TODO

		// while(maria.getAgArch().getTS().getC().getMailBox().isEmpty()){}

		// System.out.println(maria.getAgArch().getTS().getC().getMailBox().toString());

		maria.Shutdown();
		bob.Shutdown();

		assertTrue(true); // TODO
	}*/

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

		ACLMessage m = new ACLMessage();

		// Performative "Inform"
		String ilForce = "tell";
		String sender = senderJ.getName();
		String receiver = receiverJ.getName();
		String replyWith = "";
		m.setContent("vl(10)");
		Object propCont = translateContentToJason(m);
		jason.asSemantics.Message im = new jason.asSemantics.Message(ilForce,
				sender, receiver, propCont, replyWith);

		try {
			arch.sendMsg(im);
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


	protected Object translateContentToJason(ACLMessage m) {
		Object propCont = null;
		try {
			propCont = m.getContentObject();
			if (propCont instanceof String) {
				// try to parse as term
				try {
					propCont = ASSyntax.parseTerm((String) propCont);
				} catch (Exception e) { // no problem
				}
			}
		} catch (Exception e) { // no problem try another thing
		}

		if (propCont == null) { // still null
			// try to parse as term
			try {
				propCont = ASSyntax.parseTerm(m.getContent());
			} catch (Exception e) {
				// not AS messages are treated as string
				propCont = new StringTermImpl(m.getContent());
			}
		}
		return propCont;
	}
	private void waitAg(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
	}

	/*
	public void testInit() {
		assertTrue(true); // TODO
	}

	public void testRun() {
		assertTrue(true); // TODO
	}

	public void testPerceive() {
		assertTrue(true); // TODO
	}

	public void testActActionExecListOfActionExec() {
		assertTrue(true); // TODO
	}

	public void testTranslateContentToJason() {
		assertTrue(true); // TODO
	}

	public void testBroadcastMessage() {
		assertTrue(true); // TODO
	}

	public void testAddMessage() {
		assertTrue(true); // TODO
	}
	*/
}
