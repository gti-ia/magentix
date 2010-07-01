package TraceTest_3;

import java.lang.System;
import java.util.Random;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/*****************************************************************************************
/*                                      TraceTest_3                                      *
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
	private boolean finish=false;
	
	
	public Publisher(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[PUBLISHER " + this.getName() + "]: Basic test start...");
		
		System.out.println("[PUBLISHER " + this.getName() + "]: Publishing tracing services:");
		TraceInteract.publishTracingService(this, "DD_Test_TS1", "Domain Dependent Test Tracing Service1");
		TraceInteract.publishTracingService(this, "DD_Test_TS2", "Domain Dependent Test Tracing Service2");
		TraceInteract.publishTracingService(this, "DD_Test_TS3", "Domain Dependent Test Tracing Service3");
		TraceInteract.publishTracingService(this, "DD_Test_TS4", "Domain Dependent Test Tracing Service4");
		TraceInteract.publishTracingService(this, "DD_Test_TS5", "Domain Dependent Test Tracing Service5");
		System.out.println("[PUBLISHER " + this.getName() + "]: Done!");
	}

	public void execute() {
		TraceEvent tEvent;
		Random generator = new Random(System.currentTimeMillis());
		String tServiceName;
		
		System.out.println("[PUBLISHER " + this.getName() + "]: Sending trace events");
		
		while(!finish){
			try {
				//System.out.println("[PUBLISHER " + this.getName() + "]: Sending event...");
				tServiceName="DD_Test_TS" + (generator.nextInt(5)+1);
				tEvent = new TraceEvent(tServiceName, this.getAid(), tServiceName + ": Test...");
				sendTraceEvent(tEvent);
				Thread.sleep(generator.nextInt(250));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		System.out.println("[PUBLISHER " + this.getName() + "]: Now unpublishing tracing services...");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS1");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS2");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS3");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS4");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS5");

		System.out.println("[PUBLISHER " + this.getName() + "]: Bye!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		System.out.println("[PUBLISHER " + this.getName() + "]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[PUBLISHER " + this.getName() + "]: Received from " + msg.getSender().toString() + ": " + msg.getContent());
		if (msg.getContent().contentEquals("STOP")){
			finish=true;
		}
	}
}
