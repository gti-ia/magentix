package TestTraceProdCons;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.trace.*;

/**
 * ConsumerAgent class defines the structure of a consumer BaseAgent
 * 
 * Subscribes to the event_type "TRACE_TEST" and waits for 10 seconds for events
 * to arrive. When a TRACE_TEST event arrives, ConsumerAgent prints its content
 * on the screen. After the 10 seconds, ConsumerAgent unsubscribes from the
 * event type and then ends its execution
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 */
public class ConsumerAgent extends BaseAgent {
	
	static Semaphore contExec;
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	ArrayList<TraceEvent> events;
	
	public ConsumerAgent(AgentID aid) throws Exception {
		super(aid);
		contExec = new Semaphore(0);
	}
	
	public void execute() {
		System.out.println("[CONSUMER " + getName() + "]: Executing...");
		
		events = new ArrayList<TraceEvent>();
		/**
		 * This agent has no definite work. Wait infinitely the arrival of new
		 * messages.
		 */
		
		// Subscribe to trace events of the type "TRACE_TEST", coming from any
		// tracing entity
		try {
			contExec.acquire();
			TraceInteract.requestTracingService(this, "TRACE_TEST");
			Thread.sleep(1000);
			SenderAgent.contExec.release();
		} catch (TraceServiceNotAllowedException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < 10; i++) {
			try {
				System.out.println("[CONSUMER " + getName() + "]: Waiting ("
						+ (i + 1) + ")...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Unsubscribe to trace events of the type "TRACE_TEST", coming from any
		// tracing entity
		System.out.println("[CONSUMER " + getName()
				+ "]: Unsubscribing from the tracing service TRACE TEST");
		try {
			TraceInteract.cancelTracingServiceSubscription(this, "TRACE_TEST");
		} catch (TraceServiceNotAllowedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("[CONSUMER " + getName() + "]: Bye!");
		TestTraceProdCons.end.release();
	}
	
	@Override
	public void terminate() {
		super.terminate();
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		events.add(tEvent);
		System.out.println("[CONSUMER " + this.getName() + "]: Received from "
				+ tEvent.getOriginEntity().getAid().toString() + ": "
				+ tEvent.getTracingService() + " " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg) {
		System.out.println("[CONSUMER " + this.getName() + "]: Received from "
				+ msg.getSender().toString() + ": " + msg.getPerformative()
				+ msg.getContent());
	}
	
	public ArrayList<TraceEvent> getEvents() {
		return this.events;
	}
}
