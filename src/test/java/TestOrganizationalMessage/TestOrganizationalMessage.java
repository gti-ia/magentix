package TestOrganizationalMessage;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

public class TestOrganizationalMessage {

	Creator iniAgent = null;
	Noisy ruiAgent = null;

	Addition sumAgent = null;
	Summation sumtAgent = null;
	Display visAgent = null;
	Product proAgent = null;

	OMS oms = null;
	SF sf = null;

	DatabaseAccess dbA = null;
	private Process qpid_broker;
	
	//Method before updating to junit4
	//
	//public TestOrganizationalMessage(String name) {
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

		oms = new OMS(new AgentID("OMS"));

		sf = new SF(new AgentID("SF"));

		oms.start();
		sf.start();

		dbA = new DatabaseAccess();

		// ------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		// --------------------------------------------//

		dbA.removeJenaTables();

		/**
		 * Instantiating agents
		 */

		iniAgent = new Creator(new AgentID("CreatorAgent"));
		ruiAgent = new Noisy(new AgentID("Noisy"));

		sumAgent = new Addition(new AgentID("Addition"));
		sumtAgent = new Summation(new AgentID("Summation"));
		visAgent = new Display(new AgentID("Display"));
		proAgent = new Product(new AgentID("Product"));

		/**
		 * Execute the agents
		 */
		iniAgent.start();
		Monitor m = new Monitor();

		m.waiting(5 * 1000);
		ruiAgent.start();
		proAgent.start();
		sumAgent.start();
		visAgent.start();
		sumtAgent.start();

	}
	
	@Test(timeout = 5 * 60 * 1000)
	public void testThomas() {

		assertNotNull(iniAgent);

		while (visAgent.messages.size() != 4) {

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		assertEquals("6 3", visAgent.messages.get(0).getContent());
		assertEquals("27", visAgent.messages.get(1).getContent());
		assertEquals("5 3", visAgent.messages.get(2).getContent());
		assertEquals("15", visAgent.messages.get(3).getContent());

	}
	
	@After
	public void tearDown() throws Exception {

		// ------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

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
