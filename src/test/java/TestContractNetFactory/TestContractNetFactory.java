package TestContractNetFactory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class TestContractNetFactory {

	SallyClass Sally = null;
	SallyClass Sally2 = null;
	HarryClass Harry = null;
	private Process qpid_broker;

	//Method before updating to junit4
	//
	//public TestContractNetFactory(String name) {
	//	super(name);
	//}
		
	@Before
	public void setUp() throws Exception {
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);

		/**
		 * Setting the Logger
		 */
		// DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {

			Sally = new SallyClass(new AgentID("Sally"));

			Sally2 = new SallyClass(new AgentID("Mary"));

			Harry = new HarryClass(new AgentID("Harry"));

		} catch (Exception e) {
			fail();
		}

	}
	@Test(timeout = 5 * 1000)
	public void testContractNet() {
		Sally.start();
		Sally2.start();
		Harry.start();

		while (Sally.propose == -1 && Sally2.propose != -1) {
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}

		while (Harry.propose == null) {
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}

		if (Sally.propose > Sally2.propose) {
			assertEquals("I'm Mary. Ok. See you tomorrow!",
					Harry.propose.getContent());
		} else {
			assertEquals("I'm Sally. Ok. See you tomorrow!",
					Harry.propose.getContent());
		}
		
	}
	@After
	public void tearDown() throws Exception {

		// Sally.finalize();
		// Sally2.finalize();
		// Harry.finalize();
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

}
