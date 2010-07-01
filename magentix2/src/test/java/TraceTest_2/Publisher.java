package TraceTest_2;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/*****************************************************************************************/
/*                                      TraceTest_2                                      */
/*****************************************************************************************/
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       */
/*****************************************************************************************/
/*                                     DESCRIPTION                                       */
/*****************************************************************************************

    Simple test with two types of agents: 100 PUBLISHER agents and 30 SUBSCRIBER agents.
    
    PUBLISHER agents publish 2 different DD tracing services each and generate 10 trace
    events for each tracing service during 10 seconds (one per second). After that,
    the tracing services are unpublished. Waiting times before and after unpublishing
    tracing services are there just to let SUBSCRIBER agents time enough to unsubscribe
    and to print messages on the screen. 
    
    Messages to be displayed on the screen during the execution have been commented in
    order to make the execution more easily readable.

*****************************************************************************************/
public class Publisher extends BaseAgent {
	private final int N_EVENTS = 10;

	public Publisher(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Publishing tracing services...");
		TraceInteract.publishTracingService(this, this.getName()+"<DD_Test_TS_1>", this.getName() + " Domain Dependent Test Tracing Service 1");
		TraceInteract.publishTracingService(this, this.getName()+"<DD_Test_TS_2>", this.getName() + " Domain Dependent Test Tracing Service 2");
	}

	public void execute() {
		TraceEvent tEvent;
		int i;
		
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Sending trace events");
		for (i=0; i < N_EVENTS; i++) {
			try {
				tEvent = new TraceEvent(this.getName()+"<DD_Test_TS_1>", this.getAid(), this.getName()+"<DD_Test_TS_1> " + (i+1) + " of " + N_EVENTS);
				// Generating trace events
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent(this.getName()+"<DD_Test_TS_2>", this.getAid(), this.getName()+"<DD_Test_TS_2> " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("[PUBLISHER "+ this.getName() + "]: Now unpublishing tracing services...");
		TraceInteract.unpublishTracingService(this, this.getName()+"<DD_Test_TS_1>");
		TraceInteract.unpublishTracingService(this, this.getName()+"<DD_Test_TS_2>");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Bye!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		System.out.println("[PUBLISHER]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[PUBLISHER]: Received from " + msg.getSender().name + ": [ " + msg.getPerformative() + " " + msg.getContent() + " ]");
	}
}
