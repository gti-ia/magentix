package Trace_ProdCons;


import org.apache.log4j.xml.DOMConfigurator;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;
import junit.framework.TestCase;

public class TestTrace_ProdCons extends TestCase {


	/**
	 * Instantiating the Trace Manager
	 */
	TraceManager tm = null;
	
	/**
	 * Instantiating the sender agent
	 */
	SenderAgent sender = null;

	/**
	 * Instantiating the consumer agent
	 */
	ConsumerAgent consumer = null;


	
	
	public TestTrace_ProdCons(String name) {
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

			/**
			 * Instantiating the Trace Manager
			 */
			tm = new TraceManager(new AgentID("TM"));
			
			/**
			 * Instantiating the sender agent
			 */
			sender = new SenderAgent(new AgentID("qpid://sender@localhost:8080"));

			/**
			 * Instantiating the consumer agent
			 */
			consumer = new ConsumerAgent(new AgentID("qpid://consumer@localhost:8080"));

			/**
			 * Execute the agents
			 */
			consumer.start();
			sender.start();
		

		} catch (Exception e) {
			fail(e.getMessage());
		}

		
	}

	public void testTrace_ProdCons()
	{

		
		while (consumer.getEvents() == null)
		{
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		while (consumer.getEvents().size() != 10)
		{
			System.out.println("Tamaño: "+ consumer.getEvents().size());
			try {
				Thread.sleep(1*1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		int i = 1;
		for(TraceEvent te : consumer.getEvents())
		{
			if (i!=1)
				assertEquals("Test trace event ("+i+")", te.getContent());
			i++;
		}
		
		
		
		
			
		


	}
	protected void tearDown() throws Exception {
		super.tearDown();
		
		tm.finalize();
		sender.finalize();
		consumer.finalize();
	}

}
