package Thomas_example;


import omsTests.DatabaseAccess;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.TestCase;

public class TestThomas extends TestCase {

	InitiatorAgent iniAgent = null;
	Addition addAgent = null;
	James jamAgent = null;
	Product proAgent = null;
	
	OMS oms = null;
	SF sf = null;

	DatabaseAccess dbA = null;
	public TestThomas(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");


		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();


		oms = new OMS(new AgentID("OMS"));

		sf =  new SF(new AgentID("SF"));

		oms.start();
		sf.start();
		
		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

		dbA.removeJenaTables();


	

		/**
		 * Instantiating agents
		 */

		iniAgent = new InitiatorAgent(new AgentID("InitiatorAgent"));
		addAgent = new Addition(new AgentID("AdditionAgent"));
		jamAgent = new James(new AgentID("JamesAgent"));
		proAgent = new Product(new AgentID("ProductAgent"));


		/**
		 * Execute the agents
		 */
		iniAgent.start();
		Monitor m = new Monitor();

		/**
		 * Waiting the initialization
		 */
		m.waiting(5 * 1000);
		proAgent.start();
		addAgent.start();

		m.waiting(15 * 1000);
		jamAgent.start();


	}

	public void testThomas()
	{

		assertNotNull(iniAgent);



		while(jamAgent.getMessage() == null)
		{

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
		assertEquals("OK",jamAgent.getMessage());

		while(iniAgent.getMessage() == null)
		{

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			

		assertEquals("OK",iniAgent.getMessage());

		while(addAgent.getMessage() == null)
		{

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		assertEquals("OK",addAgent.getMessage());

		while(proAgent.getMessage() == null)
		{

			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		assertEquals("OK",proAgent.getMessage());

	}
	protected void tearDown() throws Exception {
		super.tearDown();
		
		oms.terminate();
		sf.terminate();
		
		oms = null;
		sf = null;
	}

}
