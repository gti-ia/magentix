package TestTrace.TestTraceProdCons;

import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TraceInteract;
import es.upv.dsic.gti_ia.trace.TraceServiceNotAllowedException;

/**
 * SenderAgent class defines the structure of a sender BaseAgent
 * 
 * Sends a trace event of the type "TRACE_TEST" each 1 second
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Vicente Ruiz Cepeda (jruiz1@dsic.upv.es)
 */
public class SenderAgent extends BaseAgent {
	
	static Semaphore contExec;
	
	public SenderAgent(AgentID aid) throws Exception {
		super(aid);
		contExec = new Semaphore(0);
	}
	
	public void execute() {
		TraceEvent tEvent;
		System.out.println("[SENDER " + getName() + "]: Executing...");
		try {
			TraceInteract.publishTracingService(this, "TRACE_TEST",
					"A simple test tracing service");
		} catch (TraceServiceNotAllowedException e2) {
			e2.printStackTrace();
		}
		ConsumerAgent.contExec.release();
		try {
			this.contExec.acquire();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		
		for (int i = 0; i < 10; i++) {
			try {
				System.out.println("[SENDER " + getName() + "]: Waiting ("
						+ (i + 1) + ")...");
				
				// Create a trace event of type "TRACE_TEST"
				tEvent = new TraceEvent("TRACE_TEST", this.getAid(),
						"Test trace event (" + (i + 1) + ")");
				
				// Generating the trace event
				sendTraceEvent(tEvent);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (TraceServiceNotAllowedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("[SENDER " + getName()
				+ "]: Unpublishing tracing service TRACE TEST");
		try {
			TraceInteract.unpublishTracingService(this, "TRACE_TEST");
		} catch (TraceServiceNotAllowedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("[SENDER " + getName() + "]: Bye!");
		TestTraceProdCons.end.release();
	}
	
	@Override
	public void terminate() {
		super.terminate();
	}
	
	public void onTraceEvent(TraceEvent tEvent) {
		System.out.println("[SENDER " + this.getName() + "]: Received from "
				+ tEvent.getOriginEntity().getAid().toString() + ": "
				+ tEvent.getTracingService() + " " + tEvent.getContent());
	}
	
	public void onMessage(ACLMessage msg) {
		System.out.println("[SENDER " + this.getName() + "]: Received from "
				+ msg.getSender().toString() + ": " + msg.getPerformative()
				+ " " + msg.getContent());
	}
}
