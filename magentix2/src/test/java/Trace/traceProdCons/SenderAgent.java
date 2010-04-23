package traceProdCons;

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
		
		for (int i=0; i < 1; i++) {
			tEvent = new TraceEvent("TRACE_TEST", this.getAid(), "Test trace event (" + (i+1) + ")");
			/**
			 * Event type: TRACE TEST
			 * Origin entity: this.getAid()
			 * Content: "Test trace event"
			 */
			try {
				logger.info("[SENDER " + getName() +"]: Sending trace event... (" + (i+1) + ")...");
				//sendTraceEvent(tEvent);
				
//				xfr = new MessageTransfer();
//
//				xfr.destination("amq.match");
//				xfr.acceptMode(MessageAcceptMode.EXPLICIT);
//				xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
//				
//				DeliveryProperties deliveryProps = new DeliveryProperties();
//
//				// Serialize message content
//				String body;
//				// Timestamp
//				body = String.valueOf(tEvent.getTimestamp()) + "#";
//				// EventType
//				body = body + tEvent.getEventType().length() + "#"
//						+ tEvent.getEventType();
//				// OriginEntiy
//				body = body + tEvent.getOriginEntity().toString().length() + "#" + tEvent.getOriginEntity().toString();
//				// Content
//				body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
//				
//				xfr.setBody(body);
//				
//				// set message headers
//		    	MessageProperties messageProperties = new MessageProperties();
//		    	Map<String, Object> messageHeaders = new HashMap<String, Object>();
//		    	// set the message property
//		    	messageHeaders.put("event_type", tEvent.getEventType());
//		    	messageHeaders.put("origin_entity", tEvent.getOriginEntity().name);
//		    	messageProperties.setApplicationHeaders(messageHeaders);
//				
//		    	xfr.header(new Header(deliveryProps, messageProperties));
//		    	
//				this.traceSession.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
//						xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
				
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
