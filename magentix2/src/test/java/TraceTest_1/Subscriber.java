package TraceTest_1;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

import es.upv.dsic.gti_ia.trace.*;

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
public class Subscriber extends BaseAgent{

	public Subscriber(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[SUBSCRIBER]: Basic test start...");
		System.out.println("[SUBSCRIBER]: Subscribing to tracing service...");
		TraceInteract.requestTracingService(this, "DD_Test_TS");
		System.out.println("[SUBSCRIBER]: Done!");
	}

	public void execute() {
		int i;
		System.out.println("[SUBSCRIBER]: Executing...");
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println("[SUBSCRIBER]: Now unsubscribing from tracing services...");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("[SUBSCRIBER]: Bye!");
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		System.out.println("[SUBSCRIBER]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[SUBSCRIBER]: Received from " + msg.getSender().name + ": [ " + msg.getPerformative() + " " + msg.getContent() + " ]");
	}
}
