package TraceTest_3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
//import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*****************************************************************************************/
/*                                      TraceTest3                                       */
/*****************************************************************************************/
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       */
/*****************************************************************************************/
/*                                     DESCRIPTION                                       */
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

public class Observer extends BaseAgent {
	private boolean finish=false;
	
	public Observer(AgentID aid) throws Exception {
		super(aid);
		
		System.out.println("[OBSERVER " + this.getName() + "]: Executing...");
		TraceInteract.requestAllTracingServices(this);
		
		TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_SENT");
		TraceInteract.cancelTracingServiceSubscription(this, "MESSAGE_RECEIVED");
	}

	public void execute(){
		while(!finish){
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, it prints it on the screen
		 */
		//DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
		DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(tEvent.getTimestamp());
		
		ACLMessage msg;
		
		if (tEvent.getTracingService().contentEquals("MESSAGE_SENT_DETAIL") ||
			tEvent.getTracingService().contentEquals("MESSAGE_RECEIVED_DETAIL")){
			msg = ACLMessage.fromString(tEvent.getContent());
			System.out.println("[OBSERVER " + formatter.format(calendar.getTime()) + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": ");
			System.out.println("\t" + msg.getPerformative() + " from " + msg.getSender().toString() + " to " + msg.getReceiver());
			System.out.println("\tCONTENT:" + msg.getContent());
		}
		else{
			System.out.println("[OBSERVER " + formatter.format(calendar.getTime()) + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": " + tEvent.getContent());
			if (tEvent.getTracingService().contentEquals("PUBLISHED_TRACING_SERVICE")){
				TraceInteract.requestTracingService(this, tEvent.getContent());
			}
		}
	}
	
	public void onMessage(ACLMessage msg){
		if (msg.getContent().contentEquals("STOP")){
			finish=true;
		}
		System.out.println("[OBSERVER " + this.getName() + "]: Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + ":" + msg.getContent());
	}
}
