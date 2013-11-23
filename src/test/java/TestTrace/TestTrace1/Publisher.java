package TestTrace.TestTrace1;

import java.lang.System;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/*****************************************************************************************
/*                                      TraceTest_1                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with two agents: a PUBLISHER agent and a SUBSCRIBER agent.
    
    The PUBLISHER agent publishes a tracing service called 'DD_Test_TS' which content is
    just a string. Each second, the publisher generates the trace event. After 10
    seconds, the PUBLISHER unpublishes the tracing service and says 'Bye!'. 
*****************************************************************************************/

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Publisher extends BaseAgent {
	
	static Semaphore contExec;
	private ArrayList<ACLMessage> messages;
	private ArrayList<TraceEvent> events;
	private final int N_EVENTS = 10;
	
	public Publisher(AgentID aid) throws Exception {
		
		super(aid);
		contExec = new Semaphore(0);
		messages = new ArrayList<ACLMessage>();
		events = new ArrayList<TraceEvent>();
		updateTraceMask();
		
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("\n[PUBLISHER]: Basic test start...");
		
		System.out.println("[PUBLISHER]: Publishing tracing service:");
		TraceInteract.publishTracingService(this, "DD_Test_TS", "Domain Dependent Test Tracing Service");
		contExec.acquire();
		System.out.println("[PUBLISHER]: Done!");
	
	}

	public void execute() {
		
		try {
			contExec.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("[PUBLISHER]: Sending trace events");
		for (int i=0; i < N_EVENTS; i++) {
			
			try {
				
				TraceEvent tEvent = new TraceEvent("DD_Test_TS", this.getAid(), "Event " + (i+1) + " of " + N_EVENTS);
				// Generating the trace event
				sendTraceEvent(tEvent);
				Thread.sleep(1000);
				
			} catch (TraceServiceNotAllowedException e1) {
				e1.printStackTrace();			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		TestTrace1.end.release();
		
		try {
			
			contExec.acquire();
			System.out.println("\n[PUBLISHER]: Now unpublishing tracing service:");
			TraceInteract.unpublishTracingService(this, "DD_Test_TS");
			contExec.acquire();
			
		} catch (TraceServiceNotAllowedException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("[PUBLISHER]: Bye!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		
		events.add(tEvent);
		
		System.out.println("[PUBLISHER]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		
		messages.add(msg);
		
		System.out.println("[PUBLISHER]: Received from " + msg.getSender().name + ": [ " + msg.getPerformative() + " " + msg.getContent() + " ]");
		contExec.release();
	}
	
	public ArrayList<ACLMessage> getMessages() {
		return this.messages;
	}
	
	public ArrayList<TraceEvent> getEvents() {
		return this.events;
	}
}
