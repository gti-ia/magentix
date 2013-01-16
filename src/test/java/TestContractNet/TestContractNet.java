package TestContractNet;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.TestCase;

public class TestContractNet extends TestCase {


	Client cliente = null;
	
	public TestContractNet(String name) {
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



		try {
			for (int i = 0; i < 5; i++) {
				/**
				 * Instantiating a consumer agent
				 */
				Concessionaire concesionario = new Concessionaire(new AgentID("Autos" + i));
				/**
				 * Execute the agents
				 */
				concesionario.start();
			}


			/**
			 * Instantiating a sender agent
			 */
			cliente = new Client(new AgentID("Client"));
		

		} catch (Exception e) {
			fail();
		}


	}

	public void testContractNet()
	{

		/**
		 * Execute the agents
		 */
		cliente.start();



		while(cliente.getState() == null)
		{

			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}			
		
		if (cliente.getState().equals("inform"))
			assertEquals("Sending purchase contract.",cliente.getStatus());
		if (cliente.getState().equals("failure"))
			assertEquals("Error on having sent contract.",cliente.getStatus());


	}
	protected void tearDown() throws Exception {
		super.tearDown();




	}

}
