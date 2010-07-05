package Trace_ProdCons;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.TraceInteract;

/**
 * SenderAgent class defines the structure of a sender BaseAgent
 * 
 * Sends a trace event of the type "TRACE_TEST" each 1 second
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 */
public class SenderAgent extends BaseAgent {

	public SenderAgent(AgentID aid) throws Exception {
		super(aid);
		TraceInteract.publishTracingService(this, "TRACE_TEST", "Tracing service with no other use than testing the system");
	}

	public void execute() {
		TraceEvent tEvent;
		
		System.out.println("[SENDER " + getName() +"]: Executing...");
		
		for (int i=0; i < 10; i++) {
			try {
				System.out.println("[SENDER " + getName() + "]: Waiting (" + (i+1) + ")...");
				
				// Create a trace event of type "TRACE_TEST"
				tEvent = new TraceEvent("TRACE_TEST", this.getAid(), "Test trace event (" + (i+1) + ")");

				// Generating the trace event
				sendTraceEvent(tEvent);
				
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("[SENDER " + getName() + "]: Unpublishing tracing service TRACE TEST");
		TraceInteract.unpublishTracingService(this, "TRACE_TEST");
		
		System.out.println("[SENDER " + getName() + "]: Bye!");		
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		System.out.println("[SENDER " + this.getName() + "]: Received from " + tEvent.getOriginEntity().getAid().name + ": " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[SENDER " + this.getName() + "]: Received from " + msg.getSender().toString() + ": " + msg.getContent());
	}
}
