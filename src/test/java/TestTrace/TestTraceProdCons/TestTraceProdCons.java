package TestTrace.TestTraceProdCons;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.junit.*;
import static org.junit.Assert.*;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;


public class TestTraceProdCons {
	
	static Semaphore end;
	
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
	
	@Before
	public void setUp() throws Exception {
		
		/**
		 * Setting the Logger
		 */
		// DOMConfigurator.configure("configuration/loggin.xml");
		
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();
		
		end = new Semaphore(-1);
		
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
		tm.start();
		consumer.start();
		sender.start();
		
	}
	
	
	@Test(timeout = 25000)
	public void testTraceProdCons() {
		
		while (consumer.getEvents() == null) {
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		while (consumer.getEvents().size() < 11) {
			System.out.println("Tamaño: " + consumer.getEvents().size());
			try {
				Thread.sleep(1 * 1000);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		ArrayList<TraceEvent> events = consumer.getEvents();
		for (int i = 1; i <= 10; ++i) {
			TraceEvent tEvent = events.get(i);
			assertEquals("Test trace event (" + i + ")", tEvent.getContent());
			// System.out.println(i + " " + tEvent.getContent());
		}
		try {
			end.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() throws Exception {
		
		tm.shutdown();
	}
}
