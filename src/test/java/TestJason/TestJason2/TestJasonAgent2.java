package TestJason.TestJason2;

import jason.asSyntax.Literal;
import jason.bb.BeliefBase;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

/**
 * @author Javier Jorge Cano
 * 
 */
public class TestJasonAgent2 extends TestCase {

	JasonAgent bob = null;
	JasonAgent maria = null;
	ArrayList<String> ExpectedBBBob = null;
	ArrayList<String> ExpectedBBMaria = null;
	boolean errorB = false;
	boolean errorM = false;
	Process qpid_broker;

	/**
	 * @param name
	 */
	public TestJasonAgent2(String name) {
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

		DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */

		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);

		AgentsConnection.connect();

		ExpectedBBBob = new ArrayList<String>();
		// Expected belief base for Bob and Maria

		ExpectedBBBob.add("vl(10)[source(maria)]");
		ExpectedBBBob.add("sentPlan[source(self)]");
		ExpectedBBBob.add("sentTell[source(self)]");
		ExpectedBBBob.add("receivedPlans[source(self)]");
		ExpectedBBBob.add("sentAchieve[source(self)]");
		ExpectedBBBob.add("sentUnachievePlan[source(self)]");
		ExpectedBBBob.add("sentAchieveNewPlan[source(self)]");
		ExpectedBBBob
				.add("answerAskAllKnow([vl(10),vl(1),vl(2)])[source(self)]");
		ExpectedBBBob.add("answerAskAllNotKnow([])[source(self)]");
		ExpectedBBBob.add("answerNotKnow(false)[source(self)]");
		ExpectedBBBob.add("answerAsyncAsk(10)[source(self)]");
		ExpectedBBBob.add("answerSyncAsk(10)[source(self)]");
		ExpectedBBBob.add("answerFullName(\"Maria dos Santos\")[source(self)]");
		ExpectedBBBob.add("answerNotKnowButHandle(t2(20))[source(self)]");

		ExpectedBBMaria = new ArrayList<String>();

		ExpectedBBMaria.add("vl(10)[source(bob)]");
		ExpectedBBMaria.add("vl(1)[source(self)]");
		ExpectedBBMaria.add("vl(2)[source(self)]");
		ExpectedBBMaria.add("sentAchieve(10,2)[source(self)]");
		ExpectedBBMaria.add("sentTell(10)[source(self)]");
		ExpectedBBMaria.add("receivedHowTo[source(self)]");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		bob.Shutdown();
		maria.Shutdown();

		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

	/**
	 * Test method for
	 * {@link es.upv.dsic.gti_ia.jason.JasonAgent#JasonAgent(es.upv.dsic.gti_ia.core.AgentID, java.lang.String, es.upv.dsic.gti_ia.jason.MagentixAgArch)}
	 * .
	 */
	public void testJasonAgent() {

		try {

			MagentixAgArch arch = new MagentixAgArch();

			bob = new JasonAgent(new AgentID("bob"),
					"./src/test/java/TestJason/TestJason2/bob.asl", arch);

			MagentixAgArch arch2 = new MagentixAgArch();
			maria = new JasonAgent(new AgentID("maria"),
					"./src/test/java/TestJason/TestJason2/maria.asl", arch2);

			bob.start();
			maria.start();

			// 5 seconds to reasoning...
			Thread.sleep(5 * 1000);

			// Obtain the belief base of Bob
			// Through architecture - Transition System - Agent(jason lib) -
			// Belief Base
			BeliefBase actualB = bob.getAgArch().getTS().getAg().getBB();

			// if (actualB.size() != ExpectedBBBob.size())
			// errorB = true;

			// Iterate and compare
			Iterator<Literal> itB = actualB.iterator();
			String beliefB = null;

			while (itB.hasNext() & !errorB) {
				beliefB = itB.next().toString();
				// bob.getAgArch().getTS().getLogger().info(beliefB);
				if (!ExpectedBBBob.contains(beliefB))
					errorB = true;
			}
			// Obtain the belief base of Maria
			// Through architecture - Transition System - Agent(jason lib) -
			// Belief Base
			BeliefBase actualM = maria.getAgArch().getTS().getAg().getBB();

			// Iterate and compare
			Iterator<Literal> itM = actualM.iterator();

			if (actualM.size() != ExpectedBBMaria.size())
				errorM = true;

			String beliefM = null;
			while (itM.hasNext() & !errorM) {

				beliefM = itM.next().toString();
				// maria.getAgArch().getTS().getLogger().info(beliefM);
				if (!ExpectedBBMaria.contains(beliefM))
					errorM = true;
			}

			// Verification
			assertFalse("Fail in the beliefs of Bob", errorB);
			assertFalse("Fail in the beliefs of Maria", errorM);

		} catch (Exception e) {

			fail("Should not have failed " + e.getMessage());
		}

	}
}
