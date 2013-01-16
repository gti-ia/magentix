package TestContractNetFactory;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import junit.framework.TestCase;

public class TestContractNetFactory extends TestCase {


	SallyClass Sally = null;
	SallyClass Sally2 = null;
	HarryClass Harry = null;
	
	
	public TestContractNetFactory(String name) {
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

			Sally = new SallyClass(new AgentID("Sally"));
			
			Sally2 = new SallyClass(new AgentID("Mary"));

			Harry = new HarryClass(new AgentID("Harry"));
		

		} catch (Exception e) {
			fail();
		}


	}

	public void testContractNet()
	{
		Sally.start();
		Sally2.start();
		Harry.start();

		while (Sally.propose == -1 && Sally2.propose != -1)
		{
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}

		while (Harry.propose == null)
		{
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		

		if (Sally.propose > Sally2.propose)
			{
			assertEquals("I'm Mary. Ok. See you tomorrow!", Harry.propose.getContent());
			}
		else 	{
			assertEquals("I'm Sally. Ok. See you tomorrow!", Harry.propose.getContent());
			}


	}
	protected void tearDown() throws Exception {
		super.tearDown();

		//Sally.finalize();
		//Sally2.finalize();
		//Harry.finalize();


	}

}
