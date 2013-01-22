package TestThomas;


//import omsTests.DatabaseAccess;

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
		//DOMConfigurator.configure("configuration/loggin.xml");


		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();


		oms = new OMS(new AgentID("OMS"));

		sf =  new SF(new AgentID("SF"));

		dbA = new DatabaseAccess();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

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

	public void testThomas()
	{
		/**
		 * Execute the agents
		 */
		iniAgent.start();
		//Monitor m = new Monitor();
		int counter = 20;
		/**
		 * Waiting the initialization
		 */

		while(!iniAgent.started)
		{
			try{
				Thread.sleep(5*1000);
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter--;
		}

		assertTrue(iniAgent.started);

		//m.waiting(5 * 1000);
		proAgent.start();
		addAgent.start();

		counter = 20;
		while ((!addAgent.started) && counter>0)
		{
			try{
				Thread.sleep(5*1000);
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter--;
		}

		//assertTrue(addAgent.started);
		if (addAgent.started==false) fail("Addition Agent FAILED!");
		counter = 40;
		while ((!proAgent.started) && counter>0)
		{
			try{
				Thread.sleep(5*1000);
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter--;
		}

		//assertTrue(proAgent.started);
		if (proAgent.started==false) fail("Product Agent FAILED!");
		//m.waiting(30 * 1000);


		try{
				Thread.sleep(10*1000);
			}catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		jamAgent.start();


		assertNotNull(iniAgent);


		counter = 10;

		while(jamAgent.getMessage() == null && counter>0)
		{

			try {
				Thread.sleep(5 * 1000);
				counter--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
		assertEquals("OK",jamAgent.getMessage());

		counter = 10;
		while(iniAgent.getMessage() == null && counter>0)
		{

			try {
				Thread.sleep(5 * 1000);
				counter--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			

		assertEquals("OK",iniAgent.getMessage());

		counter=10;
		while(addAgent.getMessage() == null && counter>0)
		{

			try {
				Thread.sleep(5 * 1000);
				counter--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		assertEquals("OK",addAgent.getMessage());

		counter=10;
		while(proAgent.getMessage() == null && counter>0)
		{

			try {
				Thread.sleep(5 * 1000);
				counter--;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		assertEquals("OK",proAgent.getMessage());

	}
	protected void tearDown() throws Exception {
		super.tearDown();
		
		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

		//dbA.removeJenaTables();

		oms.terminate();
		sf.terminate();
		
		oms = null;
		sf = null;
	}

}
