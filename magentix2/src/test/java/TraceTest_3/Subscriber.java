package TraceTest_3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

import es.upv.dsic.gti_ia.trace.*;

/*****************************************************************************************
/*                                      TraceTest_3                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent and a SUBSCRIBER agent.
    
    The SUBSCRIBER agent subscribes to the tracing service 'DD_Test_TS' and waits for 10
    seconds for trace events to arrive. After this time, the SUBSCRIBER agent
    unsubscribes from the tracing service and says 'Bye!'. Each time a trace event is
    received, the SUBSCRIBER prints its content on the screen.
*****************************************************************************************/
public class Subscriber extends BaseAgent{
	private boolean finish=false;
	private AgentID publisherAid = new AgentID("qpid://publisher@localhost:8080");
	
	public Subscriber(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Basic test start...");
		
		try {
			System.out.println("[SUBSCRIBER " + this.getName() + "]: First, basic subscriptions and unsubscription operations:");
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TSSS1 from any entity (THIS SHOULD FAIL)...");
			TraceInteract.requestTracingService(this, "DD_Test_TSSS1");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TSSS1 from 'publisher' entity (THIS SHOULD FAIL)...");
			TraceInteract.requestTracingService(this, "DD_Test_TSSS1", publisherAid);
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS1 from any entity...");
			TraceInteract.requestTracingService(this, "DD_Test_TS1");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 from any entity (THIS SHOULD FAIL)...");
			TraceInteract.requestTracingService(this, "DD_Test_TS1");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS1 from 'publisher' entity...");
			TraceInteract.requestTracingService(this, "DD_Test_TS1", publisherAid);
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 from 'publisher' entity (THIS SHOULD FAIL)...");
			TraceInteract.requestTracingService(this, "DD_Test_TS1", publisherAid);
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 from a false provider (me myself) (THIS SHOULD FAIL)...");
			TraceInteract.requestTracingService(this, "DD_Test_TS1", this.getAid());
			Thread.sleep(500);
			
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from tracing services...");
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS2 (THIS SHOULD FAIL)...");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS2");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS1 from a false publisher (me myself) (THIS SHOULD FAIL)...");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS2", this.getAid());
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS1 from the real publisher...");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1", publisherAid);
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS1 from any publisher...");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1");
			Thread.sleep(500);
			
			
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("[SUBSCRIBER " + this.getName() + "]: OK! Ready to execute...");
	}

	public void execute() {
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Executing...");
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS1...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1");
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 (THSI SHOULD FAIL)...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1");
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Done!");
		
		while(!finish){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
    	System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from tracing services...");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS2");
    	System.out.println("[SUBSCRIBER " + this.getName() + "]: Done!");
		
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Bye!");
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Received event from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Received msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + ":" + msg.getContent());
		if (msg.getContent().contentEquals("STOP")){
			finish=true;
		}
	}
}
