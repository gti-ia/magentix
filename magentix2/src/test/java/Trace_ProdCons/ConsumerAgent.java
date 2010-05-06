package Trace_ProdCons;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

import es.upv.dsic.gti_ia.trace.*;

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
		logger.info("[CONSUMER " + getName() +"]: Executing...");
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		
		// Subscribe to trace events of the type "TRACE_TEST", coming from any tracing entity
		TraceInteract.requestTracingService(this, "TRACE_TEST");
    	
    	for (int i=0; i < 10; i++) {
			try {
				logger.info("[CONSUMER " + getName() + "]: Waiting (" + (i+1) + ")...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	// Unsubscribe to trace events of the type "TRACE_TEST", coming from any tracing entity
    	TraceInteract.cancelTracingServiceSubscription(this, "TRACE_TEST");
		
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("[CONSUMER " + getName() + "]: Bye!");
		
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, its shows it on the screen
		 */
		logger.info("[CONSUMER " + getName() +"]: Trace event received by onTraceEvent: " + tEvent.toReadableString());
	}
	
	public void onMessage(ACLMessage msg){
		logger.info("[CONSUMER " + getName() +"]: Message received by onMessage: " + msg.getContent());
	}

}
