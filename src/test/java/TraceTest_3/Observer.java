package TraceTest_3;

import java.awt.Color;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
//import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.*;

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
		System.out.println("[OBSERVER " + this.getName() + "]: Event from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + ": " + tEvent.getContent());
		if (tEvent.getTracingService().contentEquals("PUBLISHED_TRACING_SERVICE")){
			TraceInteract.requestTracingService(this, tEvent.getContent());
		}
	}
	
	public void onMessage(ACLMessage msg){
		if (msg.getContent().contentEquals("STOP")){
			finish=true;
		}
		System.out.println("[OBSERVER " + this.getName() + "]: Msg from " + msg.getSender().toString() + ": " + msg.getPerformative() + ":" + msg.getContent());
	}
}
