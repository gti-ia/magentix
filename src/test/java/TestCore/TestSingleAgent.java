package TestCore;



import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Test class for an example of BaseAgent, SingleAgent
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestSingleAgent extends TestCase {

	SenderAgent2 senderAgent2;
	ConsumerAgent2 consumerAgent2;
	
	public TestSingleAgent(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		/**
		 * Setting the configuration
		 */
		DOMConfigurator.configure("configuration/loggin.xml");


		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();



		try {
			/**
			 * Instantiating a sender agent
			 */
			senderAgent2 = new SenderAgent2(new AgentID(
					"qpid://emisor@localhost:8080"));

			/**
			 * Instantiating a consumer agent
			 */
			consumerAgent2  = new ConsumerAgent2(new AgentID(
					"qpid://consumer@localhost:8080"));

			/**
			 * Execute the agents
			 */
			senderAgent2.start();
			consumerAgent2.start();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}


	}

		
	/**
	 * Testing the message sent by senderAgent2
	 */
	public void testSingleAgent()
	{		

		while(consumerAgent2.getMessage() == null)
		{
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}			
		assertEquals("Hello, I'm "+consumerAgent2.getMessage().getSender().getLocalName()
				,consumerAgent2.getMessage().getContent());

	}
}