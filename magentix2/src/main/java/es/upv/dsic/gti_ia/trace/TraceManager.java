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
import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.TracingEntityList;

public class TraceManager extends BaseAgent{
	
	private TracingEntityList TracingEntities;
	private TracingEntityList TSProviderEntities;
	private TracingEntityList TSSubscriptorEntities;
	private TracingServiceList TracingServices;
	
	//private TracingServiceList DI_TracingServices;
	//private TracingServiceSubscriptionList Subscriptions;
	
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
		Map<String, Object> arguments = new HashMap<String, Object>();
		
		TracingEntities = new TracingEntityList(this.getAid());
		TSProviderEntities = new TracingEntityList(this.getAid());
		TSSubscriptorEntities = new TracingEntityList(this.getAid());
		TracingServices = new TracingServiceList(this.getAid());
		
		// Add Trace Manager to the tracing entities list
		TracingEntities.addTE(this.getAid());
		if (TracingServices.initializeWithDITracingServices() != 0){
			logger.error("[TRACE MANAGER]: Error while initializing the tracing service list");
		}
		
		//DI_Tracing_Services = new TracingServiceList();
		//DD_TracingServices = new TracingServiceList();
		
	}
	
	/**
	 * Sends a trace event to the amq.match exchange
	 * @param tEvent
	 * 
	 * @param destination
	 * 		Tracing entity to which the trace event is directed to.
	 * 		If set to null, the system trace event is understood to be directed to all tracing
	 * 		entities.        
	 */
	public void sendSystemTraceEvent(TraceEvent tEvent, TracingEntity destination) {
		MessageTransfer xfr = new MessageTransfer();

		xfr.destination("amq.match");
		xfr.acceptMode(MessageAcceptMode.EXPLICIT);
		xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
		
		DeliveryProperties deliveryProps = new DeliveryProperties();
		
		// Serialize message content
		String body;
		// Timestamp
		body = String.valueOf(tEvent.getTimestamp()) + "#";
		// EventType
		body = body + tEvent.getTracingService().length() + "#"
				+ tEvent.getTracingService();
		// OriginEntiy
		body = body + tEvent.getOriginEntity().toString().length() + "#" + tEvent.getOriginEntity().toString();
		// Content
		body = body + tEvent.getContent().length() + "#" + tEvent.getContent();
		
		xfr.setBody(body);

		// set message headers
    	MessageProperties messageProperties = new MessageProperties();
    	Map<String, Object> messageHeaders = new HashMap<String, Object>();
    	// set the message property
    	messageHeaders.put("tracing_service", tEvent.getTracingService());
    	messageHeaders.put("origin_entity", "system");
    	if (destination == null){
    		messageHeaders.put("receiver", "all");
    	}
    	else if (destination.getType() == TracingEntity.AGENT){
    		messageHeaders.put("receiver", destination.getAid().name);
    	}
//    	else{
//    		// Other tracing entity types are not supported yet
//    		
//    	}
		
    	Header header = new Header(deliveryProps, messageProperties);
    	
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
		String content, serviceName, originEntity;
		Map<String, Object> arguments;
		int index, index2, length;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg = null;
		String command;
		
		TracingService tService;
		TracingEntity tEntity, originTEntity;
		TracingServiceSubscription tServiceSubscription;
		
		TracingServiceSubscriptionList removeList;
		
		AgentID originAid;
		int indice1 = 0;
		int indice2 = 0;
		int aidindice1 = 0;
		int aidindice2 = 0;
		int tam = 0;
		
		int error, i;
		String tEventContent;
		boolean agree_response=true;
		boolean added_TE=false;
		boolean added_TS=false;
		
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
					newService.setName(content.substring(index2 + 1, index2 + 1 + length));
					
					index = index2 + length + 1;
					newService.setDescription(content.substring(index));
					
					// Register tracing entity
					if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
						// Register a new tracing entity
						tEntity=new TracingEntity(TracingEntity.AGENT, msg.getSender());
						if ((error=TracingEntities.addTE(tEntity)) != 0){
							// Error adding the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#" + newService.getName() + ":Error registering tracing entity");
							logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
						}
						else{
							added_TE=true;
						}
					}
					
					// Add tracing service
					if (agree_response){
						if ((tService=TracingServices.getTS(newService)) == null){
							// The tracing service does not exist
							if ((error=TracingServices.addTS(newService)) != 0){
								// Impossible to add tracing service
								if (added_TE) {
									TracingEntities.removeTE(msg.getSender());
								}
								agree_response=false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("publish#" + newService.getName() + ":Error registering tracing service");
								logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
							}
							else{
								added_TS=true;
							}
						}
					}
					
					// Add the service provider to the tracing service
					if (agree_response){
						if ((error=newService.getProviders().addTE(tEntity)) != 0){
							if (added_TS){
								TracingServices.removeTS(newService);
							}
							if (added_TE){
								TracingEntities.removeTE(msg.getSender());
							}
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setReceiver(msg.getSender());
							switch (error){
								case -1:
									// Unknown error
									response_msg.setContent("publish#" + newService.getName() + ":Error adding a provider for the service");
									logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
									break;
								case -2:
									// Duplicate service provider
									response_msg.setContent("publish#" + newService.getName() + ":Service already published");
									logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
									break;
								default:
									response_msg.setContent("publish#" + newService.getName() + ":Error publishing tracing service");
									logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
							}
							agree_response=false;
						}
					}
					
					if (agree_response){
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + newService.getName());
						logger.info("[TRACE MANAGER]: sending AGREE message to " + msg.getReceiver().toString());
					}
				}
				else if (command.equals("unpublish")){
					// Remove publication of a tracing service
					serviceName=content.substring(index+1);
					
					if ((tEntity=TSProviderEntities.getTEByAid(msg.getSender())) == null){
						// The tracing entity is not providing any tracing service
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#" + serviceName + ":Tracing entity not publishing any service");
						logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
					}
					
					if (agree_response){
						if ((tService=TracingServices.getTSByName(serviceName)) == null){
							// The tracing service does not exist
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("unpublish#" + serviceName + ":Tracing service not found");
						}
						else{
							if (tService.getProviders().removeTE(msg.getSender()) != 0){
								// The service is not offered by the tracing entity
								agree_response=false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("unpublish#" + serviceName + ":Tracing entity not publishing the specified service");
							}
							else{
								// Remove subscriptions to that service
								if ((removeList=tService.getSubscriptions().removeAllTSSFromProvider(msg.getSender())) != null){
									while (removeList.getLength() > 0){
										tServiceSubscription=removeList.getFirstSubscription();
										// Send UNAVAILABLE_TS trace event
										if (tServiceSubscription.getAnyProvider()){
											tEventContent=TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
											serviceName + "#any";
										}
										else{
											tEventContent=TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
											serviceName + msg.getSender();
										}
										
								    	sendSystemTraceEvent(tEvent, tServiceSubscription.getSubscriptorEntity());
								    	
										if ((error=removeList.removeFirstTSS()) != 0){
											// Somethig went wrong
											agree_response=false;
											response_msg = new ACLMessage(ACLMessage.REFUSE);
											response_msg.setReceiver(msg.getSender());
											response_msg.setContent("unpublish#" + serviceName + ":Error sending system events");
										}
									}
								}
								
								if (tService.getProviders().getLength() == 0){
									// Tracing service was only offered by the specified tracing entity
									TracingServices.removeTS(tService);
								}
							}
						}
					}
					
					if (agree_response){
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#" + serviceName + ":Tracing service unpublished");
					}
					
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
				serviceName = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				if (!originEntity.contentEquals("any")){
					originAid = new AgentID();
					aidindice1 = 0;
					aidindice2 = originEntity.indexOf(':');
					if (aidindice2 - aidindice1 <= 0)
						originAid.protocol = "";
					else
						originAid.protocol = originEntity.substring(aidindice1, aidindice2);
					aidindice1 = aidindice2 + 3;
					aidindice2 = originEntity.indexOf('@', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.name = "";
					else
						originAid.name = originEntity.substring(aidindice1, aidindice2);
					aidindice1 = aidindice2 + 1;
					aidindice2 = originEntity.indexOf(':', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.host = "";
					else
						originAid.host = originEntity.substring(aidindice1, aidindice2);
					originAid.port = originEntity.substring(aidindice2 + 1);
				}
				else{
					originAid=null;
				}
					
				// Register tracing entity
				if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
					// Register a new tracing entity
					tEntity=new TracingEntity(TracingEntity.AGENT, msg.getSender());
					if ((error=TracingEntities.addTE(tEntity)) != 0){
						// Error adding the tracing entity
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#" + serviceName + ":Error registering tracing entity");
						logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
					}
					else{
						added_TE=true;
					}
				}
				
				// Check Tracing Service and Provider Entity
				if (agree_response){
					if ((tService=TracingServices.getTSByName(serviceName)) == null){
						// The tracing service does not exist
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#" + serviceName + ":Tracing service not found");
						logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
					}
					else if (originAid != null){
						if ((originTEntity=tService.getProviders().getTEByAid(originAid)) == null){
							// The specified tracing entity does not provide that tracing service
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("subscribe#" + serviceName + ":Tracing service not offered by the tracing entity");
							logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
				}
				
				if (agree_response){
					if (tService.getSubscriptions().existsTSS(msg.getSender(), originAid, serviceName)){
						// The subscription already exists
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("subscribe#" + serviceName + ":Subscription already existent");
						logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
					}
					else{
						// Add subscription
						tServiceSubscription=new TracingServiceSubscription(tEntity, null, tService);
						tService.addSubscription(tServiceSubscription);
					}
				}
				
				if (agree_response){
					arguments.put("x-match", "all");
			    	arguments.put("tracing_service", serviceName);
			    	
			    	if (!originEntity.equals("any")) {
			    		arguments.put("origin_entity", originEntity);
			    	}
			    	
			    	this.session.exchangeBind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + originEntity, arguments);
			    	//logger.info("[TRACE MANAGER]: binding " + msg.getSender().name+".trace to receive " + eventType);
					
			    	/**
					 * Building a ACLMessage
					 */
			    	response_msg = new ACLMessage(ACLMessage.AGREE);
			    	response_msg.setReceiver(msg.getSender());
					response_msg.setContent("subscription#" + serviceName + "#" + originEntity);
					//logger.info("[TRACE MANAGER]: sending message to " + msg.getReceiver().toString());
				}else{
					if (added_TE) {
						TracingEntities.removeTE(msg.getSender());
					}
				}
				/**
				 * Sending a ACLMessage
				 */
				send(response_msg);
				
		    	break;
		    	
			case ACLMessage.CANCEL:
				// Unsubscription from a tracing service
				
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				serviceName = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				if (!originEntity.contentEquals("any")){
					originAid = new AgentID();
					aidindice1 = 0;
					aidindice2 = originEntity.indexOf(':');
					if (aidindice2 - aidindice1 <= 0)
						originAid.protocol = "";
					else
						originAid.protocol = originEntity.substring(aidindice1, aidindice2);
					aidindice1 = aidindice2 + 3;
					aidindice2 = originEntity.indexOf('@', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.name = "";
					else
						originAid.name = originEntity.substring(aidindice1, aidindice2);
					aidindice1 = aidindice2 + 1;
					aidindice2 = originEntity.indexOf(':', aidindice1);
					if (aidindice2 - aidindice1 <= 0)
						originAid.host = "";
					else
						originAid.host = originEntity.substring(aidindice1, aidindice2);
					originAid.port = originEntity.substring(aidindice2 + 1);
				}
				else{
					originAid=null;
				}
				
				index = content.indexOf('#', 0);
				serviceName = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				// Check existence of subscriptor tracing entity
				if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
					// Tracing entity not found
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#" + serviceName + ":Subscriptor entity not found");
					logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
				}
				else if ((tService=TracingServices.getTSByName(serviceName)) == null){
					// Tracing Service not found
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#" + serviceName + ":Tracing service not found");
					logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
				}
				else if ((originAid != null) &&
						((originTEntity=tService.getProviders().getTEByAid(originAid)) == null)){
					// Origin tracing entity not found
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#" + serviceName + ":Provider entity not found");
					logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
				}
				else{
					// Check for the subscription to remove
					if ((tServiceSubscription=tService.getSubscriptions().getTSS(msg.getSender(), originAid, serviceName)) == null){
						// Subscription not found
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unsubscribe#" + serviceName + ":Subscription not found");
						logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
					}
					else{
						// Remove subscription
						this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + originEntity, Option.NONE);
						//logger.info("[TRACE MANAGER]: unbinding " + msg.getSender().name+".trace from " + eventType);
						
						// Generar evento correspondiente (y en el subscribe tb!!!)
						
					}
				}
				
				this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + originEntity, Option.NONE);
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
