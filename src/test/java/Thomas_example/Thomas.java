package Thomas_example;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.TestCase;

public class Thomas extends TestCase {

	InitiatorAgent iniAgent = null;
	Addition addAgent = null;
	James jamAgent = null;
	Product proAgent = null;

	public Thomas(String name) {
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
		//AgentsConnection.connect();


	

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
	}

}
