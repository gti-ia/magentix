package TestThomas;

//import omsTests.DatabaseAccess;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

public class TestThomas {
	
	InitiatorAgent iniAgent = null;
	Addition addAgent = null;
	James jamAgent = null;
	Product proAgent = null;

	OMS oms = null;
	SF sf = null;

	DatabaseAccess dbA = null;
	private Process qpid_broker;

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

		oms = new OMS(new AgentID("OMS"));

		sf = new SF(new AgentID("SF"));

		dbA = new DatabaseAccess();

		// ------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		// --------------------------------------------//

		dbA.removeJenaTables();

		oms.start();
		sf.start();

		/**
		 * Instantiating agents
		 */

		iniAgent = new InitiatorAgent(new AgentID("InitiatorAgent"));
		addAgent = new Addition(new AgentID("AdditionAgent"));
		jamAgent = new James(new AgentID("JamesAgent"));
		proAgent = new Product(new AgentID("ProductAgent"));

	}

	@Test(timeout=200000)
	public void testThomas() {
		/**
		 * Execute the agents
		 */
		iniAgent.start();
		/**
		 * Waiting the initialization
		 */
		iniAgent.waitInitialization();

		proAgent.start();
		addAgent.start();

		proAgent.waitInitialization();
		addAgent.waitInitialization();

		jamAgent.start();

		assertNotNull(iniAgent);

		jamAgent.waitReplay();
		assertEquals("OK", jamAgent.getMessage());

		iniAgent.waitReplay();
		assertEquals("OK", iniAgent.getMessage());

		addAgent.waitReplay();
		assertEquals("OK", addAgent.getMessage());

		proAgent.waitReplay();
		assertEquals("OK", proAgent.getMessage());

	}

	@After
	public void tearDown() throws Exception {

		// ------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		// --------------------------------------------//

		dbA.removeJenaTables();

		oms.Shutdown();
		sf.Shutdown();

		oms.await();
		sf.await();

		oms = null;
		sf = null;
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}

}
