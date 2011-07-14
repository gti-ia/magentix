package TraceBasic;

import java.util.Random;
import java.lang.System;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/*****************************************************************************************/
/*                                      Trace_Basic                                      */
/*****************************************************************************************/
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       */
/*****************************************************************************************/
/*                                     DESCRIPTION                                       */
/*****************************************************************************************

    Simple battery of requests to make sure the Trace Manager does not let erroneous
    publication/unpublication nor subscription/unsubscription
    
    Initialization:
    
    PUBLISHER:
      - Publishes the tracing service DD_Test_TS1 (OK)
      - Publishes AGAIN the tracing service DD_Test_TS1 (FAIL!)
      - Unpublishes DD_Test_TS2 (FAIL!)
      - Unpublishes DD-Test_TS1 (OK)
      - Publishes 5 tracing services: DD_Test_TS1 to DD_Test_TS5 (OK)
      
    Execution:
    
    PUBLISHER:
    	- Generates a trace event of each tracing service every second.
    	- When the SUBSCRIBER requests it (via ACLMessage), it unpublishes DD_Test_TS3
    	- When a STOP message arrives from the main Run thread, it stops generating trace
    	  events and unpublishes all tracing services (DD_Test_TS3 unpublication should fail
    	  since it should be already unpublished).

*****************************************************************************************/
public class Publisher extends BaseAgent {
private boolean finish=false;
	
	
	public Publisher(AgentID aid) throws Exception {
		super(aid);
		/**
		 * Initializing tracing services and stuff
		 */
		System.out.println("[PUBLISHER " + this.getName() + "]: Basic test start...");
		
		try{
			System.out.println("[PUBLISHER " + this.getName() + "]: First, basic publication and unpublication operations:");
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[PUBLISHER " + this.getName() + "]: Publishing DD_Test_TS1...");
			TraceInteract.publishTracingService(this, "DD_Test_TS1", "Domain Dependent Test Tracing Service1");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[PUBLISHER " + this.getName() + "]: Publishing AGAIN DD_Test_TS1 (THIS SHOULD FAIL)...");
			TraceInteract.publishTracingService(this, "DD_Test_TS1", "Domain Dependent Test Tracing Service1");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[PUBLISHER " + this.getName() + "]: Unpublishing DD_Test_TS2 (THIS SHOULD FAIL)...");
			TraceInteract.unpublishTracingService(this, "DD_Test_TS2");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[PUBLISHER " + this.getName() + "]: Unpublishing DD_Test_TS1...");
			TraceInteract.unpublishTracingService(this, "DD_Test_TS1");
			Thread.sleep(500);
			
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println("[PUBLISHER " + this.getName() + "]: Ok, now publish 5 tracing services:");
						
			TraceInteract.publishTracingService(this, "DD_Test_TS1", "Domain Dependent Test Tracing Service1");
			Thread.sleep(500);
			TraceInteract.publishTracingService(this, "DD_Test_TS2", "Domain Dependent Test Tracing Service2");
			Thread.sleep(500);
			TraceInteract.publishTracingService(this, "DD_Test_TS3", "Domain Dependent Test Tracing Service3");
			Thread.sleep(500);
			TraceInteract.publishTracingService(this, "DD_Test_TS4", "Domain Dependent Test Tracing Service4");
			Thread.sleep(500);
			TraceInteract.publishTracingService(this, "DD_Test_TS5", "Domain Dependent Test Tracing Service5");
			Thread.sleep(500);
			System.out.println("[PUBLISHER " + this.getName() + "]: Done!");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void execute() {
		TraceEvent tEvent;
		Random generator = new Random(System.currentTimeMillis());
		
		System.out.println("[PUBLISHER " + this.getName() + "]: Sending trace events");
		
		while(!finish){
			try {
				tEvent = new TraceEvent("DD_Test_TS1", this.getAid(), "Test");
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS2", this.getAid(), "Test");
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS3", this.getAid(), "Test");
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS4", this.getAid(), "Test");
				sendTraceEvent(tEvent);
				tEvent = new TraceEvent("DD_Test_TS5", this.getAid(), "Test");
				sendTraceEvent(tEvent);
				Thread.sleep(generator.nextInt(1000));
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		System.out.println("[PUBLISHER " + this.getName() + "]: Now unpublishing tracing services\n\t(one of them will probably fail because it was already unpublished)...");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS1");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS2");
		// This one has probably been already unpublished in the onMessage method
		TraceInteract.unpublishTracingService(this, "DD_Test_TS3");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS4");
		TraceInteract.unpublishTracingService(this, "DD_Test_TS5");
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			System.err.println("Error while waiting for services to be unpublished.");
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("[PUBLISHER " + this.getName() + "]: Bye!");
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		System.out.println("[PUBLISHER " + this.getName() + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		int index;
		String serviceName;
		
		if (msg.getPerformativeInt() == ACLMessage.REQUEST){
			if ((index=msg.getContent().indexOf("#")) >= 0){
				serviceName=msg.getContent().substring(index+1);
				System.out.println("[PUBLISHER " + this.getName() + "]: Now unpublishing tracing service " + serviceName);
				TraceInteract.unpublishTracingService(this, serviceName);
			}
			else if (msg.getContent().contentEquals("STOP")){
				finish=true;
			}
		}else{
			System.out.println("[PUBLISHER " + this.getName() +
				"]: Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + " " + msg.getContent());
		}	
	}
}
