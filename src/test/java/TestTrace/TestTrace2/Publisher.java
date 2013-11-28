package TestTrace.TestTrace2;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/*****************************************************************************************
/*                                      TraceTest_2                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************

    Simple test with two types of agents: 10 PUBLISHER agents and 5 SUBSCRIBER agents.
    
    PUBLISHER agents publish 2 different DD tracing services each and generate 10 trace
    events for each tracing service during 10 seconds (one per second). After that,
    the tracing services are unpublished. Waiting times before and after unpublishing
    tracing services are there just to let SUBSCRIBER agents time enough to unsubscribe
    and to print messages on the screen. 
    
    Messages to be displayed on the screen during the execution have been commented in
    order to make the execution more easily readable.

*****************************************************************************************/

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Publisher extends BaseAgent {
	
	public Semaphore contExec;
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
		TraceInteract.publishTracingService(this, this.getName()+"<DD_Test_TS_1>", this.getName() + " Domain Dependent Test Tracing Service 1");
		TraceInteract.publishTracingService(this, this.getName()+"<DD_Test_TS_2>", this.getName() + " Domain Dependent Test Tracing Service 2");
	
		TestTrace2.end.release();
	}

	public void execute() {
		
		try {
			contExec.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Sending trace events");
		for (int i=0; i < N_EVENTS; i++) {
			
			try {
				
				TraceEvent tEvent = new TraceEvent(this.getName()+"<DD_Test_TS_1>", this.getAid(), this.getName()+"<DD_Test_TS_1> " + (i+1) + " of " + N_EVENTS);
				// Generating trace events
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent(this.getName()+"<DD_Test_TS_2>", this.getAid(), this.getName()+"<DD_Test_TS_2> " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				Thread.sleep(1000);
				
			} catch (TraceServiceNotAllowedException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}

		TestTrace2.end.release();
		
		try {
			
			contExec.acquire();
			
			System.out.println("[PUBLISHER "+ this.getName() + "]: Now unpublishing tracing services...");
			TraceInteract.unpublishTracingService(this, this.getName()+"<DD_Test_TS_1>");
			TraceInteract.unpublishTracingService(this, this.getName()+"<DD_Test_TS_2>");

			contExec.acquire(2);
			
		} catch (TraceServiceNotAllowedException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Bye!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		events.add(tEvent);
		//System.out.println("[PUBLISHER]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getTracingService() + ":" + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		messages.add(msg);
		//System.out.println("[PUBLISHER]: Received from " + msg.getSender().name + ": [ " + msg.getPerformative() + " " + msg.getContent() + " ]");
	}
	
	public ArrayList<ACLMessage> getMessages() {
		return this.messages;
	}
	
	public ArrayList<TraceEvent> getEvents() {
		return this.events;
	}
}
