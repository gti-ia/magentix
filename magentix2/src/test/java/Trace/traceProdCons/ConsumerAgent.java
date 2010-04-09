package traceProdCons;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

/**
 * ConsumerAgent class defines the structure of a consumer BaseAgent
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
		Map<String, Object> arguments = new HashMap<String, Object>();
		
		arguments.put("x-match", "any");
    	arguments.put("event_type", "TRACE_TEST");
		this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match", "TRACE_TEST" + "#any", arguments);
    	logger.info("[CONSUMER " + getName() +"]: Binding " + this.getAid().name+".trace");
		while (true) {

		}
	}

	public void onTraceEvent(TraceEvent tEvent) {
		/**
		 * When a trace event arrives, its shows it on the screen
		 */
		logger.info("[CONSUMER " + getName() +"]: Trace event received by onTraceEvent: " + tEvent.toReadableString());
	}

}
