package TestTrace.TestTrace3;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.*;

import TestTrace.TestTrace3.Publisher;
import TestTrace.TestTrace3.Subscriber;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.trace.TraceMask;

/** 
 * @author Jose Alemany Bordera  -  jalemany1@dsic.upv.es
 * 
 */

public class TestTrace3 {
	
	static Semaphore end;
	public String mask = "1111111100";
	
	/**
	 * Instantiating the Trace Manager
	 */
	TraceManager tm;
	
	/**
	 * Instantiating the observer agent
	 */
	Observer observer;

	/**
	 * Instantiating the publisher agent
	 */
	Publisher publisher;

	/**
	 * Instantiating the subscriber agent
	 */
	Subscriber subscriber;
	
	/**
	 * Instantiating the coordinator agent
	 */
	Coordinator coordinator;
	
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
		
		/**
		 * Instantiating the Trace Manager
		 */
		tm = new TraceManager(new AgentID("TM"), true);
		
		/**
		 * Change the permissions to allow the observer request for all events 
		 */
		tm.setTraceMask(new TraceMask(mask));

		/**
		 * Instantiating the observer agent
		 */
		observer = new Observer(new AgentID("qpid://observer@localhost:8080"));
		
		/**
		 * Instantiating publisher agents
		 */
		publisher = new Publisher(new AgentID("qpid://publisher@localhost:8080"));
			
		/**
		 * Instantiating the subscriber agents
		 */
		subscriber = new Subscriber(new AgentID("qpid://subscriber@localhost:8080"));
		
		/**
		 * Instantiating the coordinator agent
		 */
		coordinator = new Coordinator(new AgentID("qpid://coordinator@localhost:8080"), publisher.getAid(), observer.getAid());
		
		/**
		 * Execute the agents
		 */
		tm.start();
		observer.start();
		publisher.start();
		subscriber.start();
		coordinator.start();
		
	}
	
	
	@Test(timeout = 50000)
	public void testTrace3() {
		//BEGIN
		
		int i = 0, j = 0;
		ArrayList<ACLMessage> oMessages;
		ArrayList<TraceEvent> oEvents;
		ArrayList<String> controlOM = new ArrayList<String>();
		ArrayList<String> controlOE = new ArrayList<String>();
		ACLMessage msg;
		TraceEvent tEvent;
		
		try {
			//Wait to Agents finalize their initialization.
			end.acquire(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		oMessages = observer.getMessages();
		oEvents = observer.getEvents();
		
		while(j < oEvents.size()){
			
			tEvent = oEvents.get(j++);
			
			if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
				tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")) {
				msg = ACLMessage.fromString(tEvent.getContent());
				controlOE.add(tEvent.getTracingService() + ":" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver() + ", " + msg.getContent());
			} else {
				if(tEvent.getContent().indexOf('#') != -1)
					controlOE.add(tEvent.getTracingService() + ": " + ((j-1 < TracingService.MAX_DI_TS) ? tEvent.getContent().substring(0, tEvent.getContent().indexOf('#')) : tEvent.getContent()));
			}
			//if(j > 25 && j < 40) System.out.println(tEvent.getTracingService() + ": " + ((j-1 < TracingService.MAX_DI_TS) ? tEvent.getContent().substring(0, tEvent.getContent().indexOf('#')) : tEvent.getContent()));
			if(i < oMessages.size()) {
				msg = oMessages.get(i++);
				controlOM.add("Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
			}
		}
		
		//Check that Observer is subscribe to all services.
		for (TracingService service : TracingService.DI_TracingServices)
			assertTrue(controlOE.contains("SUBSCRIBE: " + service.getName()));
		assertTrue(controlOM.contains("Msg from " + tm.getAid() + ": AGREE subscribe#3#all"));
		
		//Check that Observer cancel subscription to MESSAGE_SENT and MESSAGE_RECEIVED tracing service.
		String cancelSubscriptions[] = {"MESSAGE_SENT", "MESSAGE_RECEIVED"};
		for (String service : cancelSubscriptions) {
			assertTrue(controlOE.contains("UNSUBSCRIBE: " + service + "#any"));
			assertTrue(controlOM.contains("Msg from " + tm.getAid() + ": AGREE unsubscribe#" + service.length() + "#" + service + "#any"));
		}
		
		
		
		/*
		 * Check that Observer listen all Publisher initialization.
		 */
		//---------------------------------------------------------------------------------------------------------------------------------//
		//Check that Observer listen the NEW_AGENT event produced by the creation of Publisher agent.
		assertTrue(controlOE.contains("NEW_AGENT: " + publisher.getAid()));
				
		//Check that Observer listen Publisher agent request UpdateMask to TM.
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", UpdateMask#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", UpdateMask#" + mask));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", UpdateMask#" + mask));
				
		//Publisher publish DD_Test_TS1.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", publish#11#DD_Test_TS1Domain Dependent Test Tracing Service1"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", publish#11#DD_Test_TS1Domain Dependent Test Tracing Service1"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", publish#DD_Test_TS1"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", publish#DD_Test_TS1"));
		assertTrue(controlOE.contains("PUBLISHED_TRACING_SERVICE: DD_Test_TS1"));
		
		//Observer subscribe to DD_Test_TS1.
		assertTrue(controlOE.contains("SUBSCRIBE: DD_Test_TS1#38#Domain Dependent Test Tracing Service1#any"));
		assertTrue(controlOM.contains("Msg from " + tm.getAid() + ": AGREE subscribe#11#DD_Test_TS1#any"));
		
		//Publisher publish again DD_Test_TS1(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", publish#11#DD_Test_TS1Domain Dependent Test Tracing Service1"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", publish#11#DD_Test_TS1Domain Dependent Test Tracing Service1"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + publisher.getAid() + ", publish#11#DD_Test_TS16"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + publisher.getAid() + ", publish#11#DD_Test_TS16"));
		
		//Publisher cancel publication DD_Test_TS2(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS2"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS2"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#11#DD_Test_TS23"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#11#DD_Test_TS23"));
		
		//Publisher cancel publication DD_Test_TS1.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS1"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS1"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS1"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS1"));
		assertTrue(controlOE.contains("UNPUBLISHED_TRACING_SERVICE: DD_Test_TS1"));
		assertTrue(controlOE.contains("UNAVAILABLE_TS: DD_Test_TS1#any"));
		
		//Publisher publish DD_Test_TS1, DD_Test_TS2, DD_Test_TS3, DD_Test_TS4 and DD_Test_TS5.
		for (int n = 1; n < 6; n++) {
			assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", publish#11#DD_Test_TS" + n + "Domain Dependent Test Tracing Service" + n));
			assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", publish#11#DD_Test_TS" + n + "Domain Dependent Test Tracing Service" + n));
			assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", publish#DD_Test_TS" + n));
			assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", publish#DD_Test_TS" + n));
			assertTrue(controlOE.contains("PUBLISHED_TRACING_SERVICE: DD_Test_TS" + n));
			
			//Observer subscribe to DD_Test_TS+'n'.
			assertTrue(controlOE.contains("SUBSCRIBE: DD_Test_TS" + n + "#38#Domain Dependent Test Tracing Service" + n + "#any"));
			assertTrue(controlOM.contains("Msg from " + tm.getAid() + ": AGREE subscribe#11#DD_Test_TS" + n + "#any"));
		}
		//---------------------------------------------------------------------------------------------------------------------------------//
		
		/*
		 * Check that Observer listen all Subscriber initialization.
		 */
		//---------------------------------------------------------------------------------------------------------------------------------//
		//Check that Observer listen the NEW_AGENT event produced by the creation of Subscriber agent.
		assertTrue(controlOE.contains("NEW_AGENT: " + subscriber.getAid()));
						
		//Check that Observer listen Subscriber agent request UpdateMask to TM.
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + subscriber.getAid() + " to " + tm.getAid() + ", UpdateMask#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", UpdateMask#" + mask));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", UpdateMask#" + mask));
				
		//Subscriber subscribing to DD_Test_TSSS1 from any entity(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TSSS1#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TSSS1#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#13#DD_Test_TSSS13#any3"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#13#DD_Test_TSSS13#any3"));
		
		//Subscriber subscribing to DD_Test_TSSS1 from Publisher entity(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TSSS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TSSS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#13#DD_Test_TSSS131#" + publisher.getAid() + "3"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#13#DD_Test_TSSS131#" + publisher.getAid() + "3"));
			
		//Subscriber subscribing to DD_Test_TS1 from any entity.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS1#any"));
		
		//Subscriber subscribing again to DD_Test_TS1 from any entity(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS13#any7"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS13#any7"));
				
		//Subscriber subscribing to DD_Test_TS1 from Publisher entity.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS1#" + publisher.getAid()));
						
		//Subscriber subscribing again to DD_Test_TS1 from Publisher entity(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS131#" + publisher.getAid() + "7"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS131#" + publisher.getAid() + "7"));
		
		//Subscriber subscribing again to DD_Test_TS1 from myself entity(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + subscriber.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + subscriber.getAid()));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS132#" + subscriber.getAid() + "3"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS132#" + subscriber.getAid() + "3"));
		
		//Subscriber cancel subscription to DD_Test_TS2(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS2#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS2#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS23#any4"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS23#any4"));
				
		//Subscriber cancel subscription to DD_Test_TS1 from myself entity(THIS SHOULD FAIL).
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + subscriber.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + subscriber.getAid()));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS132#" + subscriber.getAid() + "4"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS132#" + subscriber.getAid() + "4"));
			
		//Subscriber cancel subscription to DD_Test_TS1 from Publisher entity.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS1#" + publisher.getAid()));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS1#" + publisher.getAid()));
				
		//Subscriber cancel subscription to DD_Test_TS1 from any entity.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS1#any"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS1#any"));
			
		//Subscriber try to cancel publication DD_Test_TS1.
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + subscriber.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS1"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + subscriber.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS1"));
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", unpublish#11#DD_Test_TS19"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + subscriber.getAid() + ", unpublish#11#DD_Test_TS19"));
		//---------------------------------------------------------------------------------------------------------------------------------//
			
		//Check that Observer listen the NEW_AGENT event produced by the creation of Coordinator agent.
		assertTrue(controlOE.contains("NEW_AGENT: " + coordinator.getAid()));
		
		/*
		 * Check that Observer listen all Subscriber and Publisher execution.
		 */
		//---------------------------------------------------------------------------------------------------------------------------------//
		Publisher.contExec.release();
				
		//Subscriber receives the traces of events that are subscribed while Publisher generates events.
		//(k=0)Subscriber subscribes to DD_Test_TS1 Event.
		//(k=1)Subscriber subscribes to DD_Test_TS2 Event.
		//(k=2)Subscriber subscribes to DD_Test_TS3 Event.
		//(k=3)Subscriber cancels the subscription to DD_Test_TS3 Event.
		for(int k = 0; k < 4; k++){
					
			observer.clearEvents();
			Subscriber.contExec.release();
				
			while(observer.getEvents().size() < 9 + ((k == 3) ? 9 : 0)){
				try {
					Thread.sleep(1 * 50);
				} catch (InterruptedException e) {
					fail(e.getMessage());
				}
			}
				
			oEvents = observer.getEvents();
			controlOE.clear();
			
				
			for(i = 0; i < 9 + ((k == 3) ? 9 : 0); i++){
				
				tEvent = oEvents.get(i);
				
				if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
					tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")) {
						msg = ACLMessage.fromString(tEvent.getContent());
						controlOE.add(tEvent.getTracingService() + ":" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver() + ", " + msg.getContent());
				} else {
					controlOE.add(tEvent.getTracingService() + ": " +  tEvent.getContent());
				}
			}
			
			if (k < 3) {
				//Subscriber subscribing to DD_Test_TS'k' from any entity.
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS" + (k+1) + "#any"));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:SUBSCRIBE from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS" + (k+1) + "#any"));
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS" + (k+1) + "#any"));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", subscribe#11#DD_Test_TS" + (k+1) + "#any"));
			} else {
				//Subscriber cancel subscription to DD_Test_TS3 from any entity.
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + subscriber.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS" + k));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + subscriber.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS" + k));
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS" + k));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS" + k));
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS" + k));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS" + k));
				assertTrue(controlOE.contains("UNAVAILABLE_TS: DD_Test_TS" + k + "#any"));
				assertTrue(controlOE.contains("UNPUBLISHED_TRACING_SERVICE: DD_Test_TS" + k));
			}
			
			for (int n = 1; n < 6; n++) {
				if (k != 3 || (k == 3 && n != 3))
					assertTrue(controlOE.contains("DD_Test_TS" + n + ": Test"));
			}	
		}
		
		//Coordinator agent request Publisher stop to send events and finalize its execution.
		observer.clearEvents();
		Coordinator.contExec.release();
		
		while (observer.getEvents().size() < 3) {
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		oEvents = observer.getEvents();
		controlOE.clear();
		
		for (i = 0; i < 3; i++) {
			
			tEvent = oEvents.get(i);
			
			if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
				tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")) {
					msg = ACLMessage.fromString(tEvent.getContent());
					controlOE.add(tEvent.getTracingService() + ":" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver() + ", " + msg.getContent());
			} else {
				controlOE.add(tEvent.getTracingService() + ": " +  tEvent.getContent());
			}
		}
		
		assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + coordinator.getAid() + " to " + publisher.getAid() + ", STOP"));
		assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + coordinator.getAid() + " to " + publisher.getAid() + ", STOP"));
		assertTrue(controlOE.contains("AGENT_DESTROYED: " + coordinator.getAid()));

		
		//Subscriber cancels the subscription to DD_Test_TS1 and DD_Test_TS2.
		//Then, Subscriber finalize.
		observer.clearEvents();
		Subscriber.contExec.release();
		
		while (observer.getEvents().size() < 9) {
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		oEvents = observer.getEvents();
		controlOE.clear();
		
		for (i = 0; i < 9; i++) {
			
			tEvent = oEvents.get(i);
			
			if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
				tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")) {
					msg = ACLMessage.fromString(tEvent.getContent());
					controlOE.add(tEvent.getTracingService() + ":" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver() + ", " + msg.getContent());
			} else {
				controlOE.add(tEvent.getTracingService() + ": " +  tEvent.getContent());
			}
		}
	
		for (int n = 1; n < 3; n++) {
			//Subscriber cancel subscription to DD_Test_TS'n' from any entity.
			assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS" + n + "#any"));
			assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:CANCEL from " + subscriber.getAid() + " to " + tm.getAid() + ", DD_Test_TS" + n + "#any"));
			assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS" + n + "#any"));
			assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + subscriber.getAid() + ", unsubscribe#11#DD_Test_TS" + n + "#any"));
		}		
		
		assertTrue(controlOE.contains("AGENT_DESTROYED: " + subscriber.getAid()));
		
		//Publisher cancels the publications to DD_Test_TS1, DD_Test_TS2, DD_Test_TS3, DD_Test_TS4 and DD_Test_TS5.
		//Then, Publisher finalize.
		observer.clearEvents();
		Publisher.contExec.release();
				
		while (observer.getEvents().size() < 29) {
			try {
				Thread.sleep(1 * 50);
			} catch (InterruptedException e) {
				fail(e.getMessage());
			}
		}
		
		oEvents = observer.getEvents();
		controlOE.clear();
		
		for (i = 0; i < 29; i++) {
			
			tEvent = oEvents.get(i);
			
			if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
				tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")) {
					msg = ACLMessage.fromString(tEvent.getContent());
					controlOE.add(tEvent.getTracingService() + ":" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver() + ", " + msg.getContent());
			} else {
				controlOE.add(tEvent.getTracingService() + ": " +  tEvent.getContent());
			}
		}
		
		for (int n = 1; n < 6; n++) {
			//Publisher cancel publications to DD_Test_TS'n' from any entity.
			assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS" + n));
			assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REQUEST from " + publisher.getAid() + " to " + tm.getAid() + ", unpublish#DD_Test_TS" + n));
			if (n != 3) {
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS" + n));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:AGREE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#DD_Test_TS" + n));
				assertTrue(controlOE.contains("UNAVAILABLE_TS: DD_Test_TS" + n + "#any"));
				assertTrue(controlOE.contains("UNPUBLISHED_TRACING_SERVICE: DD_Test_TS" + n));
			} else {
				assertTrue(controlOE.contains("MESSAGE_SENT_DETAIL:REFUSE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#11#DD_Test_TS33"));
				assertTrue(controlOE.contains("MESSAGE_RECEIVED_DETAIL:REFUSE from " + tm.getAid() + " to " + publisher.getAid() + ", unpublish#11#DD_Test_TS33"));
			}
		}
		
		assertTrue(controlOE.contains("AGENT_DESTROYED: " + publisher.getAid()));
		//---------------------------------------------------------------------------------------------------------------------------------//

		//Observer finalize.
		Observer.contExec.release();
		
		//END
	}
	
	@After
	public void tearDown() throws Exception {
		
		tm.shutdown();
		
		AgentsConnection.disconnect();
		qpidManager.UnixQpidManager.stopQpid(qpid_broker);
	}
}
