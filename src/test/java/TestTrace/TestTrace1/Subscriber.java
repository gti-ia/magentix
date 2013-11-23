package TestTrace.TestTrace1;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import TestTrace.TestTraceBasic.TestTraceBasic;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.*;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/*****************************************************************************************
/*                                      TraceTest_1                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with two agents: a PUBLISHER agent and a SUBSCRIBER agent.
    
    The SUBSCRIBER agent subscribes to the tracing service 'DD_Test_TS' and waits for 10
    seconds for trace events to arrive. After this time, the SUBSCRIBER agent
    unsubscribes from the tracing service and says 'Bye!'. Each time a trace event is
    received, the SUBSCRIBER prints its content on the screen.
*****************************************************************************************/

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Subscriber extends BaseAgent{

	static Semaphore contExec;
	private ArrayList<ACLMessage> messages;
	private ArrayList<TraceEvent> events;
	
	public Subscriber(AgentID aid) throws Exception {
		super(aid);
		contExec = new Semaphore(0);
		messages = new ArrayList<ACLMessage>();
		events = new ArrayList<TraceEvent>();
		updateTraceMask();
		
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("\n[SUBSCRIBER]: Basic test start...");
		
		System.out.println("[SUBSCRIBER]: Subscribing to tracing service...");
		TraceInteract.requestTracingService(this, "DD_Test_TS");
		contExec.acquire();
		System.out.println("[SUBSCRIBER]: Done!");
	
	}

	public void execute() {
		
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		try {

			System.out.println("\n[SUBSCRIBER]: Executing...");
			contExec.acquire();
			
			System.out.println("\n[SUBSCRIBER]: Now unsubscribing from tracing services...");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS");
			contExec.acquire();
			
		} catch (TraceServiceNotAllowedException e1) {
			
			e1.printStackTrace();
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("[SUBSCRIBER]: Bye!");
	}

	public void onTraceEvent(TraceEvent tEvent) {
		
		events.add(tEvent);
		
		System.out.println("[SUBSCRIBER]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getTracingService() + " " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		
		messages.add(msg);
		
		System.out.println("[SUBSCRIBER]: Received from " + msg.getSender().name + ": [ " + msg.getPerformative() + " " + msg.getContent() + " ]");
		contExec.release();
	}
	
	public ArrayList<ACLMessage> getMessages() {
		return this.messages;
	}
	
	public ArrayList<TraceEvent> getEvents() {
		return this.events;
	}
}
