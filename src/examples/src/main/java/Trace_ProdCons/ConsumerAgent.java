package Trace_ProdCons;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.*;
import es.upv.dsic.gti_ia.trace.exception.TraceServiceNotAllowedException;

/**
 * ConsumerAgent class defines the structure of a consumer BaseAgent
 * 
 * Subscribes to the event_type "TRACE_TEST" and waits for 10 seconds for events to arrive.
 * When a TRACE_TEST event arrives, ConsumerAgent prints its content on the screen.
 * After the 10 seconds, ConsumerAgent unsubscribes from the event type and then ends its execution
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 */
public class ConsumerAgent extends BaseAgent {

	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public ConsumerAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		System.out.println("[CONSUMER " + getName() +"]: Executing...");
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		
		try {
			// Subscribe to trace events of the type "TRACE_TEST", coming from any tracing entity
			TraceInteract.requestTracingService(this, "TRACE_TEST");
    	
			for (int i=0; i < 10; i++) {
				System.out.println("[CONSUMER " + getName() + "]: Waiting (" + (i+1) + ")...");
				Thread.sleep(1000);
			}
    	
			// Unsubscribe to trace events of the type "TRACE_TEST", coming from any tracing entity
			System.out.println("[CONSUMER " + getName() + "]: Unsubscribing from the tracing service TRACE TEST");
			TraceInteract.cancelTracingServiceSubscription(this, "TRACE_TEST");
		
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TraceServiceNotAllowedException e) {
			e.printStackTrace();
		}
		
		System.out.println("[CONSUMER " + getName() + "]: Bye!");
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		System.out.println("[CONSUMER " + this.getName() + "]: Received from " + tEvent.getOriginEntity().getAid().toString() + ": " + tEvent.getTracingService() + " " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("[CONSUMER " + this.getName() + "]: Received from " + msg.getSender().toString() + ": " + msg.getPerformative() + msg.getContent());
	}
}
