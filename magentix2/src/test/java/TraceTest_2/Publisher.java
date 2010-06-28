package TraceTest_2;

import java.lang.System;
//import java.util.Random;

//import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/*****************************************************************************************
/*                                      Trace_Basic                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent, a SUBSCRIBER agent and a
    COORDINATOR agent.
    
    The PUBLISHER agent publishes a tracing service called 'DD_Test_TS' which content is
    just a string. Each second, the publisher generates the trace event and then sends a
    message to the COORDINATOR agent with the content of the trace event. After 10
    seconds, the PUBLISHER unpublishes the tracing service and says 'Bye!'. 
*****************************************************************************************/
public class Publisher extends BaseAgent {
	private final int N_EVENTS = 10;
//	private AgentID coordinatorAid;

	public Publisher(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
//		coordinatorAid = new AgentID("qpid://coordinator@localhost:8080");
		
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Basic test start...");
		
		System.out.println("[PUBLISHER "+ this.getName() + "]: Publishing tracing services...");
		TraceInteract.publishTracingService(this, this.getName()+"<DD_Test_TS_1>", this.getName() + " Domain Dependent Test Tracing Service 1");
		TraceInteract.publishTracingService(this, this.getName()+"<DD_Test_TS_2>", this.getName() + " Domain Dependent Test Tracing Service 2");
		//System.out.println("[PUBLISHER "+ this.getName() + "]: Done!");
	}

	public void execute() {
		TraceEvent tEvent;
		int i;
//		ACLMessage coordination_msg;
		
		System.out.println("[PUBLISHER "+ this.getName() + "]: Sending trace events");
		for (i=0; i < N_EVENTS; i++) {
			try {
				tEvent = new TraceEvent(this.getName()+"<DD_Test_TS_1>", this.getAid(), this.getName()+"<DD_Test_TS_1> " + (i+1) + " of " + N_EVENTS);
				// Generating the trace event
				sendTraceEvent(tEvent);
				System.out.println("[PUBLISHER "+ this.getName() + "]: Event sent");
//				coordination_msg = new ACLMessage(ACLMessage.INFORM);
//				coordination_msg.setSender(this.getAid());
//				coordination_msg.setReceiver(coordinatorAid);
//				coordination_msg.setContent("SENT:" + tEvent.getContent());
//				send(coordination_msg);
//				tEvent = new TraceEvent(this.getName()+"<DD_Test_TS_2>", this.getAid(), this.getName()+"<DD_Test_TS_2> " + (i+1) + " of " + N_EVENTS);
				// Generating the trace event
//				sendTraceEvent(tEvent);
//				System.out.println("[PUBLISHER "+ this.getName() + "]: Event sent");
//				coordination_msg = new ACLMessage(ACLMessage.INFORM);
//				coordination_msg.setSender(this.getAid());
//				coordination_msg.setReceiver(coordinatorAid);
//				coordination_msg.setContent("SENT:" + tEvent.getContent());
//				send(coordination_msg);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		System.out.println("[PUBLISHER "+ this.getName() + "]: Done!");

		System.out.println("[PUBLISHER "+ this.getName() + "]: Now unpublishing tracing services...");
		TraceInteract.unpublishTracingService(this, this.getName()+"<DD_Test_TS_1>");
		TraceInteract.unpublishTracingService(this, this.getName()+"<DD_Test_TS_2>");
//		System.out.println("[PUBLISHER "+ this.getName() + "]: Done!");

		System.out.println("[PUBLISHER "+ this.getName() + "]: Bye!");
	}
}
