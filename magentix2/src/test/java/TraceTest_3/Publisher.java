package TraceTest_3;

import java.lang.System;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/*****************************************************************************************
/*                                      TraceTest_1                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent and a SUBSCRIBER agent.
    
    The PUBLISHER agent publishes a tracing service called 'DD_Test_TS' which content is
    just a string. Each second, the publisher generates the trace event. After 10
    seconds, the PUBLISHER unpublishes the tracing service and says 'Bye!'. 
*****************************************************************************************/
public class Publisher extends BaseAgent {
	private final int N_EVENTS = 100;
	
	public Publisher(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[PUBLISHER]: Basic test start...");
		
		System.out.println("[PUBLISHER]: Publishing tracing services:");
		TraceInteract.publishTracingService(this, "DD_Test_TS1", "Domain Dependent Test Tracing Service");
		TraceInteract.publishTracingService(this, "DD_Test_TS2", "Domain Dependent Test Tracing Service");
		TraceInteract.publishTracingService(this, "DD_Test_TS3", "Domain Dependent Test Tracing Service");
		TraceInteract.publishTracingService(this, "DD_Test_TS4", "Domain Dependent Test Tracing Service");
		TraceInteract.publishTracingService(this, "DD_Test_TS5", "Domain Dependent Test Tracing Service");
		System.out.println("[PUBLISHER]: Done!");
	}

	public void execute() {
		TraceEvent tEvent;
		int i;
		
		System.out.println("[PUBLISHER]: Sending trace events");
		for (i=0; i < N_EVENTS; i++) {
			try {
				
				// Generating the trace event
				tEvent = new TraceEvent("DD_Test_TS1", this.getAid(), "DD_Test_TS1: Event " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS2", this.getAid(), "DD_Test_TS2: Event " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS3", this.getAid(), "DD_Test_TS3: Event " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS4", this.getAid(), "DD_Test_TS4: Event " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS5", this.getAid(), "DD_Test_TS5: Event " + (i+1) + " of " + N_EVENTS);
				sendTraceEvent(tEvent);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("[PUBLISHER]: Done!");

		System.out.println("[PUBLISHER]: Now unpublishing tracing services...");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS1");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS2");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS3");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS4");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS5");
		System.out.println("[PUBLISHER]: Done!");

		System.out.println("[PUBLISHER]: Bye!");
	}
}
