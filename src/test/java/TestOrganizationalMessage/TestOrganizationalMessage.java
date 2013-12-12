package TestOrganizationalMessage;

//import omsTests.DatabaseAccess;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

public class TestOrganizationalMessage extends TestCase {

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

	public TestOrganizationalMessage(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));

		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}
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

		iniAgent = new Creator(new AgentID("agente_creador"));
		ruiAgent = new Noisy(new AgentID("agente_ruidoso"));

		sumAgent = new Addition(new AgentID("agente_suma"));
		sumtAgent = new Summation(new AgentID("agente_sumatorio"));
		visAgent = new Display(new AgentID("agente_visor"));
		proAgent = new Product(new AgentID("agente_producto"));

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

	protected void tearDown() throws Exception {
		super.tearDown();

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
		qpid_broker.destroy();
	}

}
