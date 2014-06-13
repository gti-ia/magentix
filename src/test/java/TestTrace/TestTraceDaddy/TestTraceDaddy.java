package TestTrace.TestTraceDaddy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.*;

import static org.junit.Assert.*;
import edu.emory.mathcs.backport.java.util.Collections;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.trace.TraceMask;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTraceDaddy {
	
	static Semaphore contExec;
	static Semaphore end;
	
	/**
	 * Instantiating the Trace Manager
	 */
	TraceManager tm = null;
	
	/**
	 * Instantiating Dad
	 */
	Daddy dad = null;
	
	/**
	 * Instantiating sons
	 */
	Boy olderSon = null, youngerSon = null;
	
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

		contExec = new Semaphore(0);
		end = new Semaphore(-2);

		/**
		 * Instantiating the Trace Manager
		 */
		tm = new TraceManager(new AgentID("TM"));
		tm.setTraceMask(new TraceMask("1001000100"));

		System.out.println("INITIALIZING...");
			
		/**
		 * Instantiating Dad
		 */
		dad = new Daddy(new AgentID("qpid://MrSmith@localhost:8080"));
			
		/**
		 * Instantiating sons
		 */
		youngerSon = new Boy(new AgentID("qpid://Bobby@localhost:8080"), 5, dad.getAid());
		olderSon = new Boy(new AgentID("qpid://Timmy@localhost:8080"), 7, dad.getAid());
			
		/**
		 * Execute the agents
		 */
		tm.start();
		dad.start();
		youngerSon.start();
		olderSon.start();
		
	}
	
	@Test(timeout = 50000)
	public void testTraceDaddy() {
		//BEGIN
		
		int i = 0;
		
		//Check that Dad is registered to listen the NEW_AGENT TraceEvent
		ArrayList<TraceEvent> events = dad.getEvents();
		TraceEvent tEvent = events.get(i++);
		assertEquals("NEW_AGENT#41#A new agent was registered in the system.#any", tEvent.getContent());
		
		while (dad.getEvents().size() < 5) {
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		//Check that Dad receives the events of creation of their Boys. 
		//Also it is found that Dad subscription to listen to their Boys has been accepted.
		events = dad.getEvents();
		ArrayList<String> controlChildren = new ArrayList<String>();
		
		while(i < events.size()) {
			tEvent = events.get(i++);
			controlChildren.add(tEvent.getTracingService() + ": " + tEvent.getContent());
		}
		
		assertTrue(controlChildren.contains("NEW_AGENT: " + youngerSon.getAid()));
		assertTrue(controlChildren.contains("SUBSCRIBE: MESSAGE_SENT_DETAIL#59#A FIPA-ACL message was sent. Message included in the event.#" + youngerSon.getAid()));
		assertTrue(controlChildren.contains("NEW_AGENT: " + olderSon.getAid()));
		assertTrue(controlChildren.contains("SUBSCRIBE: MESSAGE_SENT_DETAIL#59#A FIPA-ACL message was sent. Message included in the event.#" + olderSon.getAid()));
		
		controlChildren.clear();
	
		youngerSon.contExec.release();
		olderSon.contExec.release();
		
		try {
			contExec.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Check that Dad heard the whole conversation of their Boys.
		events = dad.getEvents();
		ACLMessage msg;
		
		while(i < events.size()) {
			msg = ACLMessage.fromString(events.get(i++).getContent());
			controlChildren.add(msg.getSender() + " " + msg.getPerformative() + ": "+ msg.getContent());
		}
		
		assertEquals(Collections.frequency(controlChildren, youngerSon.getAid() + " REQUEST: Give me your toy..."), 5);
		assertEquals(Collections.frequency(controlChildren, olderSon.getAid() + " REFUSE: NO!"), 5);
		assertTrue(controlChildren.contains(youngerSon.getAid() + " REQUEST: GUAAAAAA..!"));
		
		controlChildren.clear();
		
		dad.contExec.release();
		
		try {
			end.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//Check that Dad unsubscription to listen to their children was successful.
		events = dad.getEvents();
		
		while(i < events.size()) {
			tEvent = events.get(i++);
			controlChildren.add(tEvent.getTracingService() + ": " + tEvent.getContent());
		}
		
		assertTrue(controlChildren.contains("UNSUBSCRIBE: MESSAGE_SENT_DETAIL#" + youngerSon.getAid()));
		assertTrue(controlChildren.contains("UNSUBSCRIBE: MESSAGE_SENT_DETAIL#" + olderSon.getAid()));
		
		//END
	}
	
	@After
	public void tearDown() throws Exception {

		tm.shutdown();
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
