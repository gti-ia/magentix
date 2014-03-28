package TestTrace.TestTrace2;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.xml.DOMConfigurator;

import static org.junit.Assert.*;

import org.junit.*;

import TestTrace.TestTrace2.Publisher;
import TestTrace.TestTrace2.Subscriber;
import edu.emory.mathcs.backport.java.util.Collections;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;


/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTrace2 {

	static Semaphore end;
	final int N_PUBLISHERS = 10, N_SUBSCRIBERS = 5;
	
	/**
	 * Instantiating the Trace Manager
	 */
	TraceManager tm;

	/**
	 * Instantiating the publisher agent
	 */
	Publisher publishers[] = new Publisher[N_PUBLISHERS];

	/**
	 * Instantiating the subscriber agent
	 */
	Subscriber subscribers[] = new Subscriber[N_SUBSCRIBERS];
	
	Process qpid_broker;
	
	@Before
	public void setUp() throws Exception {
		
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		
		/**
		 * Connecting to Qpid Broker
		 */
		qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);
		
		
		AgentsConnection.connect();
		
		end = new Semaphore(0);
		
		System.out.println("\nINITIALIZING...");
			
		/**
		 * Instantiating the Trace Manager
		 */
		tm = new TraceManager(new AgentID("TM"));

		/**
		 * Instantiating publisher agents
		 */
		for (int i=0; i < N_PUBLISHERS; i++)
			publishers[i] = new Publisher(new AgentID("qpid://publisher"+ (i+1) +"@localhost:8080"));
			
		/**
		 * Instantiating the subscriber agents
		 */
		for (int i=0; i < N_SUBSCRIBERS; i++)
			subscribers[i] = new Subscriber(new AgentID("qpid://subscriber"+ (i+1) +"@localhost:8080"));
			
		/**
		 * Execute the agents
		 */
		tm.start();
		
		for (int i=0; i < N_SUBSCRIBERS; i++)
			subscribers[i].start();
			
		for (int i=0; i < N_PUBLISHERS; i++)
			publishers[i].start();
		
	}
	
	@Test(timeout = 50000)
	public void testTrace2() {
		//BEGIN
		System.out.println("\nEXECUTIZING...");
		
		int i = 0, j = 0;
		ArrayList<ACLMessage> pMessages;
		ArrayList<String> controlPM = new ArrayList<String>();
		//Save the Trace Events for each subscriber that is subscribed.
		String tE[][] = new String[N_SUBSCRIBERS][2];
		ACLMessage msg;
		
		try {
			//Wait to Publishers and Subscribers finalize their initialization.
			end.acquire(N_PUBLISHERS+N_SUBSCRIBERS);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		//Check that Publisher[nP] has received the message for <DD_Test_TS_1> and <DD_Test_TS_2> publication in its initialization.
		for (int nP = 0; nP < N_PUBLISHERS; nP++) {
			
			i = 0;
			pMessages = publishers[nP].getMessages();
				
			while (i < pMessages.size()) {
				msg = pMessages.get(i++);
				controlPM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
			}
				
			assertTrue(controlPM.contains("Received from "+ tm.getAid() +": AGREE publish#"+ publishers[nP].getName() +"<DD_Test_TS_1>"));
			assertTrue(controlPM.contains("Received from "+ tm.getAid() +": AGREE publish#"+ publishers[nP].getName() +"<DD_Test_TS_2>"));
			assertEquals(controlPM.size(), 2);
			
			controlPM.clear();
		}
		
		ArrayList<ACLMessage> sMessages;
		ArrayList<TraceEvent> sEvents;
		ArrayList<String> controlSM = new ArrayList<String>();
		ArrayList<String> controlSE = new ArrayList<String>();
		TraceEvent tEvent;
		
		//Check that Subscriber[nS] has received the message for its subscriptions in its initialization.
		for (int nS = 0; nS < N_SUBSCRIBERS; nS++) {
			
			j = 0;
			sMessages = subscribers[nS].getMessages();
			sEvents = subscribers[nS].getEvents();
						
			while (j < sMessages.size()) {
				msg = sMessages.get(j);
				tEvent = sEvents.get(j);
				//We can not know which events have subscribed because it was random
				controlSM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative()/* + " " + msg.getContent()*/);
				controlSE.add(tEvent.getTracingService()/*+ ": " + tEvent.getContent()*/);
				tE[nS][j++] = tEvent.getContent().substring(0, tEvent.getContent().indexOf('#'));
			}
				
			assertEquals(Collections.frequency(controlSM, "Received from "+ tm.getAid() +": AGREE"), 2);
			assertEquals(Collections.frequency(controlSE, "SUBSCRIBE"), 2);
			assertEquals(controlSM.size(), 2);
			assertEquals(controlSE.size(), 2);
			
			controlSM.clear();
			controlSE.clear();
		}
		
		
		for (int nP = 0; nP < N_PUBLISHERS; nP++)
			publishers[nP].contExec.release();
		
		try {
			//Wait to Publisher generate all Trace Events.
			end.acquire(N_PUBLISHERS);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		int jAux = j;
		
		//Check that Subscriber[nS] has received all events.
		for (int nS = 0; nS < N_SUBSCRIBERS; nS++) {
			
			j = jAux;
			sEvents = subscribers[nS].getEvents();
				
			while (j < sEvents.size()) {
				tEvent = sEvents.get(j++);
				controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
			}
				
			for (int k=1; k <= 10; k++) {
				assertTrue(controlSE.contains(tE[nS][0] +": "+ tE[nS][0] +" "+ k +" of 10"));
				assertTrue(controlSE.contains(tE[nS][1] +": "+ tE[nS][1] +" "+ k +" of 10"));
			}
			assertEquals(controlSE.size(), 20);
			
			controlSE.clear();
		}
		

		for (int nS = 0; nS < N_SUBSCRIBERS; nS++)
			subscribers[nS].contExec.release();
		
		jAux = j;
		
		//Check that Subscriber[nS] has cancel its subscriptions.
		for (int nS = 0; nS < N_SUBSCRIBERS; nS++) {
			
			j = jAux;
			
			while(subscribers[nS].getEvents().size() <= j+1 || subscribers[nS].getMessages().size() <= j-20){
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
			
			sMessages = subscribers[nS].getMessages();
			sEvents = subscribers[nS].getEvents();
				
			while(j < sEvents.size()){
				msg = sMessages.get(j-20);
				tEvent = sEvents.get(j++);
				controlSM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
				controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
			}
				
			assertTrue(controlSM.contains("Received from "+ tm.getAid() +": AGREE unsubscribe#"+ tE[nS][0].length() +"#"+ tE[nS][0] +"#any"));
			assertTrue(controlSM.contains("Received from "+ tm.getAid() +": AGREE unsubscribe#"+ tE[nS][1].length() +"#"+ tE[nS][1] +"#any"));
			assertTrue(controlSE.contains("UNSUBSCRIBE: "+ tE[nS][0] +"#any"));
			assertTrue(controlSE.contains("UNSUBSCRIBE: "+ tE[nS][1] +"#any"));
			assertEquals(controlSM.size(), 2);
			assertEquals(controlSE.size(), 2);
			
			controlSM.clear();
			controlSE.clear();
		}
		
		//Check that Publisher[nP] has cancel its publications.
		for (int nP = 0; nP < N_PUBLISHERS; nP++)
			publishers[nP].contExec.release();
		
		try {
			//Wait to Publisher cancel its publications.
			end.acquire(N_PUBLISHERS);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		int iAux = i;
		
		for (int nP = 0; nP < N_PUBLISHERS; nP++) {
			
			i = iAux;
			
			while (publishers[nP].getMessages().size() <= i+1) {
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
				
			pMessages = publishers[nP].getMessages();
				
			while (i < pMessages.size()) {
				msg = pMessages.get(i++);
				controlPM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
			}
			
			assertTrue(controlPM.contains("Received from "+ tm.getAid() +": AGREE unpublish#"+ publishers[nP].getName() +"<DD_Test_TS_1>"));
			assertTrue(controlPM.contains("Received from "+ tm.getAid() +": AGREE unpublish#"+ publishers[nP].getName() +"<DD_Test_TS_2>"));
			assertEquals(controlPM.size(), 2);
			
			controlPM.clear();
		}
		
		//END
	}
	
	@After
	public void tearDown() throws Exception {
		
		tm.shutdown();
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
