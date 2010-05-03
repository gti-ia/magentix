package TraceExchangeTest;

import java.util.HashMap;
import java.util.Map;

import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageProperties;
import org.apache.qpid.transport.MessageTransfer;

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
		TraceEvent tEvent;
		MessageTransfer xfr;
		
		logger.info("[SENDER " + getName() +"]: Executing...");
		
		for (int i=0; i < 10; i++) {
			try {
				logger.info("[SENDER " + getName() + "]: Waiting (" + (i+1) + ")...");
				
				tEvent = new TraceEvent("TRACE_TEST", this.getAid(), "Test trace event (" + (i+1) + ")");

				sendTraceEvent(tEvent);
				
				Thread.currentThread().sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Thread.currentThread().sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.info("[SENDER " + getName() + "]: Bye!");		
	}

}
