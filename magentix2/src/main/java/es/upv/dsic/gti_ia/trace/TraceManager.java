package es.upv.dsic.gti_ia.trace;

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

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

import es.upv.dsic.gti_ia.trace.*;

public class TraceManager extends BaseAgent{
	
	private TracingServiceList DI_Tracing_Services;
	private TracingServiceList DD_Tracing_Services;
	
	private TracingServiceSubscriptionList Subscriptions;
	
	public TraceManager(AgentID aid) throws Exception{
		super(aid);
		
		// Create session and exchange for delivering events
        //this.session.exchangeDeclare("mgx.trace", "headers", "amq.direct", null);
        
        // Create session and exchange for trace manager coordination
        //this.session.exchangeDeclare("mgx.trace.manager", "fanout", "amq.direct", null);
        
        // Bind the original message queue to the TM coordination exchange
        //this.session.exchangeBind(aid.name, "mgx.trace.manager", aid.name+".tm", null);
        //this.session.sync();
        
        logger.info("[TRACE MANAGER]: Executing, I'm " + getName());
        
        initialize();
/*		this.session.messageSubscribe(aid.name, "listener_destination",
				MessageAcceptMode.NONE, MessageAcquireMode.PRE_ACQUIRED, null,
				0, null);
*/
	}
	
	public void initialize (){
		DI_Tracing_Services = new TracingServiceList();
		DD_Tracing_Services = new TracingServiceList();
		
		// System Trace Events
		DI_Tracing_Services.addTracingService(null, new TracingService("TRACE_ERROR", "TRACE_ERROR", "General error in the tracing process."));
		DI_Tracing_Services.addTracingService(null, new TracingService("TRACE_START", "TRACE_START", "The ER entity started tracing."));
		DI_Tracing_Services.addTracingService(null, new TracingService("TRACE_STOP", "TRACE_STOP", "The ER entity stoppped tracing."));
		DI_Tracing_Services.addTracingService(null, new TracingService("SUBSCRIBED", "SUBSCRIBED", "The ER entity subscribed to a tracing service."));
		DI_Tracing_Services.addTracingService(null, new TracingService("UNSUBSCRIBED", "UNSUBSCRIBED", "The ER entity unsubscribed from a tracing service."));
		DI_Tracing_Services.addTracingService(null, new TracingService("UNAVAILABLE_TS", "UNAVAILABLE_TS", "The tracing service which was requested does not exist or it has been un published and thus, it is not avilable anymore"));
		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_OVERFLOW", "STREAM_OVERFLOW", "The stream where trace events were being stored for the ER to recover them is full."));
		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_RESUME", "STREAM_RESUME", "The ER entity began to trace events after having stoppped."));
		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_FLUSH_START", "STREAM_FLUSH_START", "The ER entity started flushing the stream where it was receiving events."));
		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_FLUSH_STOP", "STREAM_FLUSH_STOP", "The flushing process previously started has arrived to its end."));
		
		// Life cycle of Tracing Entities
		DI_Tracing_Services.addTracingService(null, new TracingService("NEW_AGENT", "NEW_AGENT", "A new agent was registered in the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("NEW_ARTIFACT", "NEW_ARTIFACT", "A new artifact was registered in the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("NEW_AGGREGATION", "NEW_AGGREGATION", "A new aggregation was registered in the system."));
		DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_SUSPENDED", "AGENT_SUSPENDED", "An agent was suspended."));
		DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_RESUMED", "AGENT_RESUMED", "An agent restarted after a suspension."));
		DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_DESTROYED", "AGENT_DESTROYED", "An agent was destroyed."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_ENTERS_AGGREGATION", "AGENT_ENTERS_AGGREGATION", "An agent enters an aggregation."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_LEAVES_AGGREGATION", "AGENT_LEAVES_AGGREGATION", "An agent leaves an aggregation."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("ARTIFACT_ENTERS_AGGREGATION", "ARTIFACT_ENTERS_AGGREGATION", "An artifact starts being part of an aggregation."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("ARTIFACT_LEAVES_AGGREGATION", "ARTIFACT_LEAVES_AGGREGATION", "An artifact stops being part of an aggregation."));
		
		// Messaging among Tracing Entities
		DI_Tracing_Services.addTracingService(null, new TracingService("MESSAGE_SENT", "MESSAGE_SENT", "A FIPA-ACL message was sent."));
		DI_Tracing_Services.addTracingService(null, new TracingService("MESSAGE_RECEIVED", "MESSAGE_RECEIVED", "A FIPA-ACL message was received."));
		DI_Tracing_Services.addTracingService(null, new TracingService("MESSAGE_UNDELIVERABLE", "MESSAGE_UNDELIVERABLE", "A FIPA-ACL message was impossible to deliver."));
		
		// OMS related Trace Events
		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_REGISTRATION", "ROLE_REGISTRATION", "A new role has been registered in the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_DEREGISTRATION", "ROLE_DEREGISTRATION", "An existing role has been removed from the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("NORM_REGISTRATION", "NORM_REGISTRATION", "A new norm has been registered in the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("NORM_DEREGISTRATION", "NORM_DEREGISTRATION", "A norm has been removed from the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("UNIT_REGISTRATION", "UNIT_REGISTRATION", "A new organisational unit has been registered in the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("UNIT_DEREGISTRATION", "UNIT_DEREGISTRATION", "An existing organizational unit has been removed from the system."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_ACQUIRE", "ROLE_ACQUIRE", "A role has been acquired by an entity."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_LEAVE", "ROLE_LEAVE", "An entity in the system has voluntarily stoppped playing a specific role."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_EXPULSION", "ROLE_EXPULSION", "An entity in the system has been expulsed from playing a specific role."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("NORM_VIOLATION", "NORM_VIOLATION", "A norm in the system has been violated."));
		
		// Tracing System related Tracing Services
		DI_Tracing_Services.addTracingService(null, new TracingService("PUBLISHED_TRACING_SERVICE", "PUBLISHED_TRACING_SERVICE", "A new tracing service has been published by an ES entity."));
		DI_Tracing_Services.addTracingService(null, new TracingService("UNPUBLISHED_TRACING_SERVICE", "UNPUBLISHED_TRACING_SERVICE", "A tracing service is not being offered by an ER entity."));
		// These two seem redundant with "SUBSCRIBED" and "UNSUBSCRIBED"
		//DI_Tracing_Services.addTracingService(null, new TracingService("TRACING_SERVICE_REQUEST", "TRACING_SERVICE_REQUEST", "An ER entity requested a tracing service."));
		//DI_Tracing_Services.addTracingService(null, new TracingService("TRACING_SERVICE_CANCEL", "TRACING_SERVICE_CANCEL", "An ER entity cancelled the subscription to a tracing service."));
		DI_Tracing_Services.addTracingService(null, new TracingService("AUTHORIZATION_REQUEST", "AUTHORIZATION_REQUEST", "An entity requested authorization for a tracing service."));
		DI_Tracing_Services.addTracingService(null, new TracingService("AUTHORIZATION_ADDED", "AUTHORIZATION_ADDED", "An entity added an authorization for a tracing service."));
		DI_Tracing_Services.addTracingService(null, new TracingService("AUTHORIZATION_REMOVED", "AUTHORIZATION_REMOVED", "An authorization for a tracing service was removed."));
		
		//logger.info("DI Tracing services:\n" + DI_Tracing_Services.listAllTracingServices());
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

		xfr.destination("amq.match");
		//xfr.destination("mgx.trace");
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
		//body = body + tEvent.getEventType() + "#";
		// OriginEntiy
		body = body + tEvent.getOriginEntity().toString().length() + "#" + tEvent.getOriginEntity().toString();
		//body = body + tEvent.getOriginEntity().toString() + "#";
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
		//body = body + tEvent.getContent();
		
		xfr.setBody(body);
//		xfr.setBody("Trace Event");
		
//		deliveryProps.setRoutingKey(msg.getReceiver(i).name);
		
		// set message headers
    	MessageProperties messageProperties = new MessageProperties();
    	Map<String, Object> messageHeaders = new HashMap<String, Object>();
    	// set the message property
    	messageHeaders.put("event_type", tEvent.getEventType());
    	messageHeaders.put("origin_entity", "system");
    	messageHeaders.put("receiver", destination);
		
    	Header header = new Header(deliveryProps, messageProperties);
    	
    	//xfr.header(new Header(deliveryProps, messageProperties));
    	this.traceSession.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
                header, xfr.getBodyString());
	}
	
	public void execute() {
		while(true){}
	}
	
//	public void onMessage(ACLMessage msg) {
//		/**
//		 * When a message arrives, its shows on screen
//		 */
//		logger.info("[TRACE MANAGER]: Mensaje received in " + this.getName()
//				+ " agent, by onMessage: " + msg.getContent());
//	}
	
	public void onMessage(ACLMessage msg) {
		String content, eventType, originEntity;
		Map<String, Object> arguments;
		int index, index2, length;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg = null;
		String command;
		
		int error;
		
		//logger.info("[TRACE MANAGER]: Received [" + msg.getPerformativeInt() + "] -> " + msg.getContent());
		
		switch (msg.getPerformativeInt()){
		
			case ACLMessage.REQUEST:
				
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				command = content.substring(0, index);
				
				if (command.equals("publish")) {
					// Publication of a tracing service
					TracingService newService = new TracingService();
					
					index2 = content.indexOf('#', index+1);
					length = Integer.parseInt(content.substring(index + 1, index2));
					newService.setName(content.substring(index2 + 1, index2 + 1 + length));
					
					index = index2 + length + 1;
					index2 = content.indexOf('#', index);
					length = Integer.parseInt(content.substring(index, index2));
					newService.setEventType(content.substring(index2 + 1, index2 + 1 + length));
					
					index = index2 + length + 1;
					newService.setDescription(content.substring(index));
					
					if ((error = DD_Tracing_Services.addTracingService(msg.getSender(), newService)) >= 0){
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + newService.getName());
						logger.info("[TRACE MANAGER]: sending AGREE message to " + msg.getReceiver().toString());
					}
					else{
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + newService.getName());
						logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
					}
				}
				else if (command.equals("unpublish")){
					// Remove publication of a tracing service
					String serviceName=content.substring(index+1);
					TracingService ts;
					
					if ((ts=DD_Tracing_Services.getServiceByName(serviceName)) == null){
						// Service not found
						response_msg = new ACLMessage(ACLMessage.REFUSE);
					}
					else{
						if ((error=ts.removeProvider(msg.getSender())) == 0){
							// Provider removed
					    	response_msg = new ACLMessage(ACLMessage.AGREE);
						}
						else if (error == 1){
							// Provider removed, but now the tracing service has run out of providers
							if ((error = DD_Tracing_Services.removeTracingService(serviceName)) == 0){
								// Tracing service also removed
								response_msg = new ACLMessage(ACLMessage.AGREE);
							}
							else{
								// This should never happen
								response_msg = new ACLMessage(ACLMessage.REFUSE);
							}
						}
						else{
							// This should never happen
							response_msg = new ACLMessage(ACLMessage.REFUSE);
						}
					}
					
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unpublish#" + serviceName);
					logger.info("[TRACE MANAGER]: sending message to " + msg.getReceiver().toString());
				}
				else {
					/**
					 * Building a ACLMessage
					 */
			    	response_msg = new ACLMessage(ACLMessage.UNKNOWN);
			    	response_msg.setReceiver(msg.getSender());
					response_msg.setContent(content);
					logger.info("[TRACE MANAGER]: returning UNKNOWN message to " + msg.getReceiver().toString());
				}
				
				send(response_msg);
				
				break;
				
			case ACLMessage.SUBSCRIBE:
				// Subscription to a tracing service
								
				arguments = new HashMap<String, Object>();
								
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				eventType = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				arguments.put("x-match", "all");
		    	arguments.put("event_type", eventType);
		    	
		    	if (!originEntity.equals("any")) {
		    		arguments.put("origin_entity", originEntity);
		    	}
		    			    	
		    	this.session.exchangeBind(msg.getSender().name+".trace", "amq.match", eventType + "#" + originEntity, arguments);
		    	//logger.info("[TRACE MANAGER]: binding " + msg.getSender().name+".trace to receive " + eventType);

		    	/**
				 * Building a ACLMessage
				 */
		    	response_msg = new ACLMessage(ACLMessage.AGREE);
		    	response_msg.setReceiver(msg.getSender());
				response_msg.setContent("subscription#" + eventType + "#" + originEntity);
				//logger.info("[TRACE MANAGER]: sending message to " + msg.getReceiver().toString());
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);						
				
		    	break;
		    	
			case ACLMessage.CANCEL:
				// Unsubscription from a tracing service
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				eventType = content.substring(0, index);
				originEntity = content.substring(index + 1);
				//System.out.println("JAAAAAAR " + this.session.exchangeBound("amq.match", msg.getSender().name+".trace", eventType+"#"+originEntity.toString(), null, Option.NONE).);
				this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", eventType + "#" + originEntity.toString(), Option.NONE);
				//logger.info("[TRACE MANAGER]: unbinding " + msg.getSender().name+".trace from " + eventType);
		    	
		    	tEvent = new TraceEvent("system_notify", this.getAid(), "UNSUBSCRIBED#" + eventType + "#" + originEntity);
		    	sendSystemTraceEvent(tEvent, msg.getSender().toString());
		    	
		    	/**
				 * Building a ACLMessage
				 */
		    	response_msg = new ACLMessage(ACLMessage.AGREE);
		    	response_msg.setReceiver(msg.getSender());
				response_msg.setContent("unsubscription#" + eventType + "#" + originEntity);
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);	
				
				break;
				
			default:
				/**
				 * Building a ACLMessage
				 */
		    	response_msg = new ACLMessage(ACLMessage.UNKNOWN);
		    	response_msg.setReceiver(msg.getSender());
				response_msg.setContent(msg.getContent());
//				logger.info("Mensaje received in " + this.getName()
//						+ " agent, by onMessage: " + msg.getContent());
//				logger.info("[TRACE MANAGER]: returning UNKNOWN message to " + msg.getReceiver().toString());
				send(response_msg);
		}
		
	}
	
}
