package TraceTest_1;

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
	private final int N_EVENTS = 10;
	
	public Publisher(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[PUBLISHER]: Basic test start...");
		
		System.out.println("[PUBLISHER]: Publishing tracing service:");
		TraceInteract.publishTracingService(this, "DD_Test_TS", "Domain Dependent Test Tracing Service");
		System.out.println("[PUBLISHER]: Done!");
	}

	public void execute() {
		TraceEvent tEvent;
		int i;
		
		System.out.println("[PUBLISHER]: Sending trace events");
		for (i=0; i < N_EVENTS; i++) {
			try {
				tEvent = new TraceEvent("DD_Test_TS", this.getAid(), "Event " + (i+1) + " of " + N_EVENTS);
				// Generating the trace event
				sendTraceEvent(tEvent);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("[PUBLISHER]: Done!");

		System.out.println("[PUBLISHER]: Now unpublishing the tracing service...");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS");
		System.out.println("[PUBLISHER]: Done!");

		System.out.println("[PUBLISHER]: Bye!");
	}
}
