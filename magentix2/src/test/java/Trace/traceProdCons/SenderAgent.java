package traceProdCons;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

/**
 * EmisorAgent class defines the structure of a sender BaseAgent
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 */
public class SenderAgent extends BaseAgent {

	public SenderAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		TraceEvent tEvent = new TraceEvent("TRACE_TEST", this.getAid(), "Test trace event");
		/**
		 * Event type: TRACE TEST
		 * Origin entity: this.getAid()
		 * Content: "Test trace event"
		 */
		
		logger.info("[SENDER " + getName() +"]: Executing...");
		
		for (int i=0; i < 10; i++){
			sendTraceEvent(tEvent);
			logger.info("[SENDER " + getName() +"]: Sending trace event...");
		}
		
	}

}
