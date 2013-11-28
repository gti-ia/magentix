package TestTrace.TestTrace1;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.*;
import static org.junit.Assert.*;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTrace1 {
	
	static Semaphore end;
	
	/**
	 * Instantiating the Trace Manager
	 */
	TraceManager tm = null;

	/**
	 * Instantiating the publisher agent
	 */
	Publisher publisher = null;

	/**
	 * Instantiating the subscriber agent
	 */
	Subscriber subscriber = null;

	@Before
	public void setUp() throws Exception {
		
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();
		
		end = new Semaphore(0);
		
		/**
		 * Instantiating the Trace Manager
		 */
		tm = new TraceManager(new AgentID("TM"));

		/**
		 * Instantiating the publisher agent
		 */
		publisher = new Publisher(new AgentID("qpid://publisher@localhost:8080"));
			
		/**
		 * Instantiating the subscriber agent
		 */
		subscriber = new Subscriber(new AgentID("qpid://subscriber@localhost:8080"));
			
		/**
		 * Execute the agents
		 */
		tm.start();
		publisher.start();
		subscriber.start();
		
	}
	
	@Test(timeout=15000)
	public void testTrace1() {
		//BEGIN
		int i = 0, j = 0;
		
		//Check that Publisher has received the message for DD_Test_TS publication in its initialization.
		ArrayList<ACLMessage> pMessages = publisher.getMessages();
		ArrayList<String> controlPM = new ArrayList<String>();
		ACLMessage msg;
		
		while(i < pMessages.size()){
			msg = pMessages.get(i++);
			controlPM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
		}
		
		assertEquals("Received from "+ tm.getAid() +": AGREE publish#DD_Test_TS", controlPM.get(0));
		assertEquals(controlPM.size(), 1);
		
		//Check that Subscriber has received the message for DD_Test_TS subscription in its initialization.
		ArrayList<ACLMessage> sMessages = subscriber.getMessages();
		ArrayList<TraceEvent> sEvents = subscriber.getEvents();
		ArrayList<String> controlSM = new ArrayList<String>();
		ArrayList<String> controlSE = new ArrayList<String>();
		TraceEvent tEvent;
				
		while(j < sMessages.size()){
			msg = sMessages.get(j);
			tEvent = sEvents.get(j++);
			controlSM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
			controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
		}
		
		assertEquals("Received from "+ tm.getAid() +": AGREE subscribe#10#DD_Test_TS#any", controlSM.get(0));
		assertEquals("SUBSCRIBE: DD_Test_TS#37#Domain Dependent Test Tracing Service#any", controlSE.get(0));
		assertEquals(controlSM.size(), 1);
		assertEquals(controlSE.size(), 1);
		

		Publisher.contExec.release();
		
		try {
			//Wait to Publisher generate all Trace Events.
			end.acquire();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		//Check that Subscriber has received all events
		sEvents = subscriber.getEvents();
		
		while(j < sEvents.size()){
			tEvent = sEvents.get(j++);
			controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
		}
		
		for(int k=1; k <= 10; k++)
			assertTrue(controlSE.contains("DD_Test_TS: Event "+ k +" of 10"));
		assertEquals(controlSE.size(), 11);
		
		//Check that Subscriber has cancel its DD_Test_TS subscription.
		Subscriber.contExec.release();
		
		while(subscriber.getEvents().size() <= j || subscriber.getMessages().size() <= j-10){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		sMessages = subscriber.getMessages();
		sEvents = subscriber.getEvents();
		
		while(j < sEvents.size()){
			msg = sMessages.get(j-10);
			tEvent = sEvents.get(j++);
			controlSM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
			controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
		}
		
		assertEquals("Received from "+ tm.getAid() +": AGREE unsubscribe#10#DD_Test_TS#any", controlSM.get(1));
		assertEquals("UNSUBSCRIBE: DD_Test_TS#any", controlSE.get(11));
		assertEquals(controlSM.size(), 2);
		assertEquals(controlSE.size(), 12);
		
		//Check that Publisher has cancel its DD_Test_TS publication.
		Publisher.contExec.release();
		
		while(publisher.getMessages().size() <= i){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		pMessages = publisher.getMessages();
		
		while(i < pMessages.size()){
			msg = pMessages.get(i++);
			controlPM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
		}
		
		assertEquals("Received from "+ tm.getAid() +": AGREE unpublish#DD_Test_TS", controlPM.get(1));
		assertEquals(controlPM.size(), 2);
		
		//END
	}
	
	@After
	public void tearDown() throws Exception {
		
		tm.shutdown();
	}
}

