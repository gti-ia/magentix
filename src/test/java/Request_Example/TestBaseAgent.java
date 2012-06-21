package Request_Example;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.TestCase;

public class TestBaseAgent extends TestCase {

	Hospital hos  = null;
	witness tes = null;
	
	public TestBaseAgent(String name) {
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



		try {
			
			hos = new Hospital(new AgentID("HospitalAgent"));

			
			tes = new witness(new AgentID("witnesAgent"));

		

		} catch (Exception e) {
			fail();
		}


	}

	public void testBaseAgent()
	{

		/**
		 * Execute the agents
		 */
		hos.start();
		tes.start();



		while(tes.getStatus() == null)
		{

			
		}			
		assertEquals("OK",tes.getStatus());

		

	}
	protected void tearDown() throws Exception {
		super.tearDown();
		
		
		
		
	}

}
