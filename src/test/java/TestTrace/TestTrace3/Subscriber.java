package TestTrace.TestTrace3;

import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
//import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.*;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/*****************************************************************************************
/*                                      TraceTest3                                       *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************

    Simple battery of requests to make sure the Trace Manager does not let erroneous
    publication/unpublication nor subscription/unsubscription
    
    Initialization:
    
    SUBSCRIBER:
    	- Subscribes to DD_Test_TSSS1 from any entity (FAIL!)
    	- Subscribes to DD_Test_TSSS1 from PUBLISHER entity (FAIL!)
    	- Subscribes to DD_Test_TS1 from any entity (OK)
    	- Subscribes AGAIN to DD_Test_TS1 from any entity (FAIL!)
    	- Subscribes to DD_Test_TS1 from PUBLISHER entity (OK)
    	- Subscribes AGAIN to DD_Test_TS1 from PUBLISHER entity (FAIL!)
    	- Subscribes to DD_Test_TS1 from SUBSCRIBER entity (FAIL!)
    	- Unsubscribes from DD_Test_TS2 from any entity (FAIL!)
    	- Unsubscribes from DD_Test_TS1 from SUBSCRIBER (FAIL!)
    	- Unsubscribes from DD_Test_TS1 from PUBLISHER (OK)
    	- Unsubscribes from DD_Test_TS1 from any entity (OK)
    	- Unpublishes DD_Test_TS1 (FAIL!)
      
    Execution:
    	  
    SUBSCRIBER:
    	- Subscribe to DD_Test_TS1 (OK: Receiving events from DD_Test_TS1)
    	- Subscribe to DD_Test_TS2 (OK: Receiving events from DD_Test_TS1 and DD_Test_TS2)
    	- Subscribe to DD_Test_TS3 (OK: Receiving events from DD_Test_TS1,DD_Test_TS2 and
    		DD_Test_TS3)
    	- Send a message to PUBLISHER requesting the unpublication of DD_Test_TS3
    	   (OK: Receiving events from DD_Test_TS1 and DD_Test_TS2)
    	- Unsubscribe from DD_Test_TS1 and DD_Test_TS2 (OK: No more event receiving)

*****************************************************************************************/

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Subscriber extends BaseAgent{
	
	static Semaphore contExec;
	private AgentID publisherAid = new AgentID("qpid://publisher@localhost:8080");
	
	public Subscriber(AgentID aid) throws Exception {
		
		super(aid);
		contExec = new Semaphore(0);
		updateTraceMask();
		
		/**
		 * Initializing tracing services and stuff
		 */
		contExec.acquire();
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Basic test start...");
		System.out.println("[SUBSCRIBER " + this.getName() + "]: First, basic subscriptions and unsubscription operations:");
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TSSS1 from any entity (THIS SHOULD FAIL)...");
		TraceInteract.requestTracingService(this, "DD_Test_TSSS1");
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TSSS1 from 'publisher' entity (THIS SHOULD FAIL)...");
		TraceInteract.requestTracingService(this, "DD_Test_TSSS1", publisherAid);
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS1 from any entity...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1");
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 from any entity (THIS SHOULD FAIL)...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1");
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS1 from 'publisher' entity...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1", publisherAid);
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 from 'publisher' entity (THIS SHOULD FAIL)...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1", publisherAid);
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing AGAIN to DD_Test_TS1 from a false provider (me myself) (THIS SHOULD FAIL)...");
		TraceInteract.requestTracingService(this, "DD_Test_TS1", this.getAid());
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from tracing services...");
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS2 (THIS SHOULD FAIL)...");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS2");
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS1 from a false publisher (me myself) (THIS SHOULD FAIL)...");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1", this.getAid());
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS1 from the real publisher...");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1", publisherAid);
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from DD_Test_TS1 from any publisher...");
		TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1");
		contExec.acquire();
			
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Now, we'll try to unpublish an existing tracing service which is not published:");
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Unpublishing DD_Test_TS1 (THIS SHOULD FAIL)...");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS1");
		contExec.acquire();
		
		System.out.println("\n[SUBSCRIBER " + this.getName() + "]: OK! Ready to execute...");
	}

	public void execute() {
		
		System.out.println("[SUBSCRIBER " + this.getName() + "]: Executing...");
		
		try {
			
			System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS1...\n\tReceiving [ DD_Test_TS1 ]\n");
			TraceInteract.requestTracingService(this, "DD_Test_TS1");
			Thread.sleep(3000);
			
			System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS2...\n\tReceiving [ DD_Test_TS1 DD_Test_TS2 ]\n");
			TraceInteract.requestTracingService(this, "DD_Test_TS2");
			Thread.sleep(3000);
			
			System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Subscribing to DD_Test_TS3...\n\tReceiving [ DD_Test_TS1 DD_Test_TS2 DD_Test_TS3 ]\n");
			TraceInteract.requestTracingService(this, "DD_Test_TS3");
			Thread.sleep(3000);
			
			System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Sending message to PUBLISHER to request unpublication of DD_Test_TS3\n");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	    	msg.setSender(this.getAid());
	    	msg.setReceiver(publisherAid);
			msg.setContent("UNPUBLISH#DD_Test_TS3");
			send(msg);
			System.out.println("\n[SUBSCRIBER " + this.getName() + "]: Message sent...\n\tReceiving [ DD_Test_TS1 DD_Test_TS2 ]\n");
			Thread.sleep(3000);
			
		
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Done!");
		
			System.out.println("[SUBSCRIBER " + this.getName() + "]: Now unsubscribing from tracing services...");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS1");
			TraceInteract.cancelTracingServiceSubscription(this, "DD_Test_TS2");
		
    		System.out.println("[SUBSCRIBER " + this.getName() + "]: Done!");
    		System.out.println("[SUBSCRIBER " + this.getName() + "]: Bye!");
    		
		} catch (TraceServiceNotAllowedException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		//System.err.println("[SUBSCRIBER " + this.getName() + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		contExec.release();
		//System.err.println("[SUBSCRIBER " + this.getName() + "]: Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + ":" + msg.getContent());
	}
}
