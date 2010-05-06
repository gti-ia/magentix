package Trace_ProdCons;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

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
	}

	public void execute() {
		TraceEvent tEvent;
		
		logger.info("[SENDER " + getName() +"]: Executing...");
		
		for (int i=0; i < 10; i++) {
			try {
				logger.info("[SENDER " + getName() + "]: Waiting (" + (i+1) + ")...");
				
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
		
		logger.info("[SENDER " + getName() + "]: Bye!");		
	}

}
