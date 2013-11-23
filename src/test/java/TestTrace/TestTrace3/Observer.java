package TestTrace.TestTrace3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
//import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.*;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.Semaphore;

/*****************************************************************************************
/*                                      TraceTest3                                       *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************

	Initialization:
    
    OBSERVER:
      - Subscribes to all available tracing services
      - Unsubscribes from MESSAGE_SENT and MESSAGE RECEIVED because they are
        redundant with MESSAGE_SENT_DETAIL and MESSAGE_RECEIVED_DETAIL
        
    Execution:
    
    OBSERVER:
      - Each time a new tracing service is published, it subscribes to it
      - Each time a trace event arrives, it prints it on screen
      
*****************************************************************************************/

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Observer extends BaseAgent {
	
	static Semaphore contExec;
	private ArrayList<ACLMessage> messages;
	private ArrayList<TraceEvent> events;
	
	public Observer(AgentID aid) throws Exception {
		
		super(aid);
		contExec = new Semaphore(0);
		messages = new ArrayList<ACLMessage>();
		events = new ArrayList<TraceEvent>();
		updateTraceMask();
		
		System.out.println("[OBSERVER " + this.getName() + "]: Executing...");
		TraceInteract.requestAllTracingServices(this);
		
		TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_SENT");
		TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_RECEIVED");
	}

	public void execute(){
		
		try {
			contExec.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		
		events.add(tEvent);
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tEvent.getTimestamp());
		
		if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
			tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")) {
			
			ACLMessage msg = ACLMessage.fromString(tEvent.getContent());
			System.out.println("[OBSERVER " + formatter.format(calendar.getTime()) + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": ");
			System.out.println("\t" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver());
			System.out.println("\tCONTENT:" + msg.getContent());
			
		} else {
			
			System.out.println("[OBSERVER " + formatter.format(calendar.getTime()) + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": " + tEvent.getContent());
			
			if (tEvent.getTracingService().contentEquals("PUBLISHED_TRACING_SERVICE")) {
				
				try {					
					TraceInteract.requestTracingService(this, tEvent.getContent());
				} catch (TraceServiceNotAllowedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void onMessage(ACLMessage msg){
		
		messages.add(msg);
		System.out.println("[OBSERVER " + this.getName() + "]: Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + ":" + msg.getContent());
		
		if (msg.getContent().contentEquals("STOP"))
			contExec.release();
	}
}
