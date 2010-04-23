package TraceExchangeTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;


import java.util.HashMap;
import java.util.Map;

import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageProperties;


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
		
		AgentID aid = this.getAid();
		int i;
		MessageTransfer xfr;
		String body;
		TraceEvent tEvent;
		DeliveryProperties deliveryProps;
		MessageProperties messageProperties;
		Map<String, Object> messageHeaders;
		
		arguments.put("x-match", "any");
    	arguments.put("event_type", "TRACE_TEST");
		this.traceSession.exchangeBind(this.getAid().name+".trace", "amq.match", "TRACE_TEST" + "#any", arguments);
    	logger.info("[CONSUMER " + getName() +"]: Binding " + this.getAid().name+".trace");
		
    	for (i=0; i < 10; i++) {
			try {
				logger.info("[CONSUMER " + getName() + "]: Waiting (" + (i+1) + ")...");
				
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setReceiver(this.getAid());
				msg.setSender(this.getAid());
				msg.setLanguage("ACL");
				msg.setContent("Hello, I'm " + getName());
				/**
				 * Sending a ACLMessage
				 */
				send(msg);
				
				//tEvent = new TraceEvent("TRACE_TEST", this.getAid(), "Test trace event (" + (i+1) + ")");
				
				tEvent = new TraceEvent("TRACE_TEST", aid, "Test trace event (" + (i+1) + ")");
				
				sendTraceEvent(tEvent);
				
//				xfr = new MessageTransfer();
//				
//				xfr.destination("amq.match");
//				xfr.acceptMode(MessageAcceptMode.EXPLICIT);
//				xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
//								
//				deliveryProps = new DeliveryProperties();
//				
//				// Serialize message content
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
//				messageProperties = new MessageProperties();
//				messageHeaders = new HashMap<String, Object>();
//				// set the message property
//				messageHeaders.put("event_type", tEvent.getEventType());
//				//messageHeaders.put("origin_entity", tEvent.getOriginEntity().name);
//				messageProperties.setApplicationHeaders(messageHeaders);
//								
//				xfr.header(new Header(deliveryProps, messageProperties));
//						    	
//				this.traceSession.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
//						xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
////				this.traceSession.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT,
////				MessageAcquireMode.PRE_ACQUIRED, xfr.getHeader(), body);				
////				this.traceSession.sync();
				
				
				
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
