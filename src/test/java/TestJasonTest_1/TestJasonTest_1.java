package TestJasonTest_1;


import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;

public class TestJasonTest_1 extends TestCase {

	SimpleArchitecture arch = null;
	
	JasonAgent agent = null;
	
	public TestJasonTest_1(String name) {
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


	}

	public void testBaseAgent()
	{
	
		try {
			arch = new SimpleArchitecture();
			
			agent = new JasonAgent(new AgentID("bob"), "./src/test/java/jasonTest_1/demo.asl", arch);
			agent.start();

		} catch (Exception e) {
			fail();
		}
		

	}
	protected void tearDown() throws Exception {
		super.tearDown();
		
		Thread.sleep(1 * 1000);
		agent.finalize();
		
		
	}

}
