package TestTrace.TestTraceBasic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

public class TestTraceBasic {
	
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
	
	/**
	 * Instantiating the coordinator agent
	 */
	Coordinator coordinator = null;
	
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
		
		end = new Semaphore(-1);
		
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
		 * Instantiating the coordinator agent
		 */
		coordinator = new Coordinator(new AgentID("qpid://coordinator@localhost:8080"), publisher.getAid());
			
		/**
		 * Execute the agents
		 */
		tm.start();
		publisher.start();
		subscriber.start();
		coordinator.start();
		
	}
	
	@Test(timeout = 50000)
	public void testTraceBasic() {
		//BEGIN
		int i = 0, j = 0;
		
		//Check that Publisher has received messages for publications and publications cancellations in its initialization.
		ArrayList<ACLMessage> pMessages = publisher.getMessages();
		ArrayList<String> controlPM = new ArrayList<String>();
		ACLMessage msg;
		
		while(i < pMessages.size()){
			msg = pMessages.get(i++);
			controlPM.add("Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
		}
		
		assertEquals("Received from "+ tm.getAid() +": AGREE publish#DD_Test_TS1", controlPM.get(0));
		assertEquals("Received from "+ tm.getAid() +": REFUSE publish#11#DD_Test_TS16", controlPM.get(1));
		assertEquals("Received from "+ tm.getAid() +": REFUSE unpublish#11#DD_Test_TS23", controlPM.get(2));
		assertEquals("Received from "+ tm.getAid() +": AGREE unpublish#DD_Test_TS1", controlPM.get(3));
		
		for(int k = 4; k < 9; k++) {
			assertEquals("Received from "+ tm.getAid() +": AGREE publish#DD_Test_TS" + (k-3), controlPM.get(k));
		}
		assertEquals(controlPM.size(), 9);
		
		//Check that Subscriber has received messages for subscriptions and subscriptions cancellations in its initialization.
		ArrayList<ACLMessage> sMessages = subscriber.getMessages();
		ArrayList<TraceEvent> sEvents = subscriber.getEvents();
		ArrayList<String> controlSM = new ArrayList<String>();
		ArrayList<String> controlSE = new ArrayList<String>();
		TraceEvent tEvent;
		
		while(j < sMessages.size()){
			msg = sMessages.get(j++);
			controlSM.add("Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
			if(j <= sEvents.size()) {
				tEvent = sEvents.get(j-1);
				controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
			}
		}
		
		//Messages for subscriptions and subscriptions cancellations.
		assertEquals("Msg from "+ tm.getAid() +": REFUSE subscribe#13#DD_Test_TSSS13#any3", controlSM.get(0));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE subscribe#13#DD_Test_TSSS131#"+ publisher.getAid() +"3", controlSM.get(1));
		assertEquals("Msg from "+ tm.getAid() +": AGREE subscribe#11#DD_Test_TS1#any", controlSM.get(2));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE subscribe#11#DD_Test_TS13#any7", controlSM.get(3));
		assertEquals("Msg from "+ tm.getAid() +": AGREE subscribe#11#DD_Test_TS1#"+ publisher.getAid(), controlSM.get(4));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE subscribe#11#DD_Test_TS131#"+ publisher.getAid() +"7", controlSM.get(5));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE subscribe#11#DD_Test_TS132#"+ subscriber.getAid() +"3", controlSM.get(6));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE unsubscribe#11#DD_Test_TS23#any4", controlSM.get(7));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE unsubscribe#11#DD_Test_TS132#"+ subscriber.getAid() +"4", controlSM.get(8));
		assertEquals("Msg from "+ tm.getAid() +": AGREE unsubscribe#11#DD_Test_TS1#"+ publisher.getAid(), controlSM.get(9));
		assertEquals("Msg from "+ tm.getAid() +": AGREE unsubscribe#11#DD_Test_TS1#any", controlSM.get(10));
		assertEquals("Msg from "+ tm.getAid() +": REFUSE unpublish#11#DD_Test_TS19", controlSM.get(11));
		assertEquals(controlSM.size(), 12);
		
		//Events for subscriptions and subscriptions cancellations that returning as agree.
		assertEquals("SUBSCRIBE: DD_Test_TS1#38#Domain Dependent Test Tracing Service1#any", controlSE.get(0));
		assertEquals("SUBSCRIBE: DD_Test_TS1#38#Domain Dependent Test Tracing Service1#" + publisher.getAid(), controlSE.get(1));
		assertEquals("UNSUBSCRIBE: DD_Test_TS1#" + publisher.getAid(), controlSE.get(2));
		assertEquals("UNSUBSCRIBE: DD_Test_TS1#any", controlSE.get(3));
		assertEquals(controlSE.size(), 4);

		//Agents execution
		Publisher.contExec.release();
		
		//Verify that Subscriber receives the traces of events that are subscribed while Publisher generates events.
		//(k=0)Subscriber subscribes to DD_Test_TS1 Event. Receiving [DD_Test_TS1]
		//(k=1)Subscriber subscribes to DD_Test_TS2 Event. Receiving [DD_Test_TS1, DD_Test_TS2]
		//(k=2)Subscriber subscribes to DD_Test_TS3 Event. Receiving [DD_Test_TS1, DD_Test_TS2, DD_Test_TS3]
		//(k=3)Subscriber cancels the subscription to DD_Test_TS3 Event. Receiving [DD_Test_TS1, DD_Test_TS2]
		for(int k = 0; k < 4; k++){
			
			subscriber.clearEvents();
			Subscriber.contExec.release();
		
			while(subscriber.getEvents().size() < 7){
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
		
			sMessages = subscriber.getMessages();
			pMessages = publisher.getMessages();
			sEvents = subscriber.getEvents();
			controlSE.clear();
		
			if (k < 3) {
				msg = sMessages.get(j++);
				assertEquals("Msg from "+ tm.getAid() +": AGREE subscribe#11#DD_Test_TS"+ (k+1) +"#any", "Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
				assertEquals(sMessages.size(), j);
			} else {
				msg = pMessages.get(10);
				assertEquals("Msg from "+ tm.getAid() +": AGREE unpublish#DD_Test_TS3", "Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
				assertEquals(pMessages.size(), 11);
			}
		
			for(i = 0; i < 7; i++){
				tEvent = sEvents.get(i);
				controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
			}
			
			if (k < 3)
				assertTrue(controlSE.contains("SUBSCRIBE: DD_Test_TS"+ (k+1) +"#38#Domain Dependent Test Tracing Service"+ (k+1) +"#any"));
			else
				assertTrue(controlSE.contains("UNAVAILABLE_TS: DD_Test_TS3#any"));
			
			for(int z = 0; z <= k; z++){
				if(k == 3 && z > 1) break;
				assertTrue(controlSE.contains("DD_Test_TS"+ (z+1) +": Test"));
			}
		}
		
		//Subscriber cancels the subscription to DD_Test_TS1 and DD_Test_TS2.
		//Then, Subscriber finalize.
		subscriber.clearEvents();
		subscriber.clearMessages();
		Subscriber.contExec.release();
		
		while(subscriber.getEvents().size() < 2 || subscriber.getMessages().size() < 2){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		sMessages = subscriber.getMessages();
		sEvents = subscriber.getEvents();
		controlSE.clear();
		controlSM.clear();
		
		for(i = 0; i < 2; i++){
			msg = sMessages.get(i);
			tEvent = sEvents.get(i);
			controlSE.add(tEvent.getTracingService() + ": " + tEvent.getContent());
			controlSM.add("Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
		}
		
		for(i = 0; i < 2; i++){
			assertTrue(controlSE.contains("UNSUBSCRIBE: DD_Test_TS"+ (i+1) +"#any"));
			assertTrue(controlSM.contains("Msg from "+ tm.getAid() +": AGREE unsubscribe#11#DD_Test_TS"+ (i+1) +"#any"));
		}
		
		Subscriber.contExec.release();
		//Activate Coordinator to send STOP ACLMessage to Publisher. Publisher will stop generating events.
		Coordinator.contExec.release();
		
		//Publisher cancels the publications to DD_Test_TS1, DD_Test_TS2, DD_Test_TS3, DD_Test_TS4 and DD_Test_TS5.
		//Then, Publisher finalize.
		controlPM.clear();
		
		while(publisher.getMessages().size() < 17){
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		pMessages = publisher.getMessages();
		
		for(i = 11; i < pMessages.size(); i++){
			msg = pMessages.get(i);
			controlPM.add("Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
		}
		
		assertEquals("Msg from "+ coordinator.getAid() +": REQUEST STOP", controlPM.get(0));
		
		for(int k = 1; k < 6; k++) {
			if (k != 3)
				assertEquals("Msg from "+ tm.getAid() +": AGREE unpublish#DD_Test_TS"+ k, controlPM.get(k));
			else
				assertEquals("Msg from "+ tm.getAid() +": REFUSE unpublish#11#DD_Test_TS"+ k + "3", controlPM.get(k));
		}
		assertTrue(controlPM.size()==6);
		
		Publisher.contExec.release();
		
		//END
	}
	
	@After
	public void tearDown() throws Exception {
		
		tm.shutdown();
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
