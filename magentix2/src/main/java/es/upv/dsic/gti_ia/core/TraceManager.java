package es.upv.dsic.gti_ia.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageCreditUnit;
import org.apache.qpid.transport.MessageProperties;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Option;
import org.apache.qpid.transport.Session;
import org.apache.qpid.transport.SessionException;
import org.apache.qpid.transport.SessionListener;

import es.upv.dsic.gti_ia.core.TraceEvent;

public class TraceManager extends BaseAgent{
	
	public TraceManager(AgentID aid) throws Exception{
		super(aid);
		
		// Create session and exchange for delivering events
        this.session.exchangeDeclare("mgx.trace", "headers", "amq.direct", null);
        
        // Create session and exchange for trace manager coordination
        this.session.exchangeDeclare("mgx.trace.manager", "fanout", "amq.direct", null);
        
        // Bind the original message queue to the TM coordination exchange
        this.session.exchangeBind(aid.name, "mgx.trace.manager", aid.name+".tm", null);
        this.session.sync();
        

/*		this.session.messageSubscribe(aid.name, "listener_destination",
				MessageAcceptMode.NONE, MessageAcquireMode.PRE_ACQUIRED, null,
				0, null);
*/
	}
	
	/**
	 * 
	 * Sends a trace event to the mgx.trace exchange
	 * @param tEvent
	 * 
	 * @param destination
	 * 		"all"  : System trace events which are to be received by all tracing entities
	 * 
	 * 		!"all" : agent name of the agent which has to receive that system trace event
	 *         
	 */
	public void sendSystemTraceEvent(TraceEvent tEvent, String destination) {
		MessageTransfer xfr = new MessageTransfer();

		xfr.destination("mgx.trace");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();

		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getEventType().length() + "#"
				+ tEvent.getEventType();
		// OriginEntiy
		body = body + tEvent.getOriginEntity().toString().length() + "#" + tEvent.getOriginEntity().toString();
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
		
		xfr.setBody(body);
		
		// set message headers
    	MessageProperties messageProperties = new MessageProperties();
    	Map<String, Object> messageHeaders = new HashMap<String, Object>();
    	// set the message property
    	messageHeaders.put("event_type", tEvent.getEventType());
    	messageHeaders.put("origin_entity", tEvent.getOriginEntity().toString());
    	messageHeaders.put("receiver", destination);
    	    	
    	messageProperties.setApplicationHeaders(messageHeaders);
		
    	xfr.header(new Header(deliveryProps, messageProperties));
		this.session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
				xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
	}
	
	public void onMessage(ACLMessage msg) {
		String content, eventType, originEntity;
		Map<String, Object> arguments;
		int index;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg;
		
		switch (msg.getPerformativeInt()){
		
			case ACLMessage.SUBSCRIBE:
				// Subscription to a tracing service
				
				arguments = new HashMap<String, Object>();
								
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				//length = Integer.parseInt(content.substring(0, index));
				eventType = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				arguments.put("x-match", "all");
		    	arguments.put("event_type", eventType);
		    	arguments.put("origin_entity", originEntity);
		    	
		    	this.session.exchangeBind(msg.getSender().toString()+".trace", "mgx.trace", eventType + "#" + originEntity.toString(), arguments);
		    	// confirm completion
		    	this.session.sync();
		    	
		    	tEvent = new TraceEvent("system_notify", this.getAid(), "subscribed#" + eventType + "#" + originEntity);
		    	sendSystemTraceEvent(tEvent, msg.getSender().toString());
		    			    	
		    	/**
				 * Building a ACLMessage
				 */
				//response_msg = new ACLMessage(ACLMessage.AGREE);
				response_msg=msg.createReply();
				response_msg.setPerformative(ACLMessage.AGREE);
				response_msg.setContent("subscription#" + eventType + "#" + originEntity);
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);						
				
		    	break;
		    	
			case ACLMessage.CANCEL:
				// Unsubscription from a tracing service
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				//length = Integer.parseInt(content.substring(0, index));
				eventType = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				this.session.exchangeUnbind(msg.getSender()+".trace", "mgx.trace", eventType + "#" + originEntity.toString(), Option.NONE);
				
		    	// confirm completion
		    	this.session.sync();
		    	
		    	tEvent = new TraceEvent("system_notify", this.getAid(), "unsubscribed#" + eventType + "#" + originEntity);
		    	sendSystemTraceEvent(tEvent, msg.getSender().toString());
		    	
		    	/**
				 * Building a ACLMessage
				 */
				response_msg=msg.createReply();
				response_msg.setPerformative(ACLMessage.FAILURE);
				response_msg.setContent("subscription#" + eventType + "#" + originEntity);
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);	
				
				break;
				
			default:
				logger.info("Mensaje received in " + this.getName()
						+ " agent, by onMessage: " + msg.getContent());
		}
		
		
	}
	
}
