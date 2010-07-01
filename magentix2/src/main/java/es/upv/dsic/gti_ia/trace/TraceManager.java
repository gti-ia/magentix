package es.upv.dsic.gti_ia.trace;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageProperties;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Option;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;

import es.upv.dsic.gti_ia.trace.TracingEntityList;

public class TraceManager extends BaseAgent{
	
	private TracingEntityList TracingEntities;
	private TracingEntityList TSProviderEntities;
	private TracingEntityList TSSubscriberEntities;
	private TracingServiceList TracingServices;
	
	public TraceManager(AgentID aid) throws Exception{
		super(aid);
        
        logger.info("[TRACE MANAGER]: Executing, I'm " + getName());
        
        logger.setLevel(Level.OFF);
        
        initialize();
	}
	
	public void initialize (){
		TracingEntities = new TracingEntityList();
		TSProviderEntities = new TracingEntityList();
		TSSubscriberEntities = new TracingEntityList();
		TracingServices = new TracingServiceList();
		
		// Add Trace Manager to the tracing entities list
		TracingEntities.add(new TracingEntity(TracingEntity.AGENT, this.getAid()));

		if (!TracingServices.initializeWithDITracingServices()){
			logger.error("[TRACE MANAGER]: Error while initializing the tracing service list");
		}
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
		body = body + tEvent.getOriginEntity().getType() + "#";
		body = body + this.getAid().toString().length() + "#" + this.getAid().toString();
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
		
    	messageProperties.setApplicationHeaders(messageHeaders);
    	
    	Header header = new Header(deliveryProps, messageProperties);
    	
    	this.traceSession.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT, MessageAcquireMode.PRE_ACQUIRED,
                header, xfr.getBodyString());
	}
	
//	public void execute() {
//		while(true){}
//	}
	
	public void onMessage(ACLMessage msg) {
		String content, serviceName, serviceDescription, originEntity;
		Map<String, Object> arguments;
		int index, index2, length;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg = null;
		String command;
		
		TracingService tService=null;
		TracingEntity tEntity=null, originTEntity=null;
		TracingServiceSubscription tServiceSubscription=null;
		
		Iterator<TracingServiceSubscription> TSS_iter;
		
		AgentID originAid;
		int aidindice1 = 0;
		int aidindice2 = 0;
		
		String tEventContent;
		boolean agree_response=true;
		boolean added_TE=false;
		boolean added_TS=false;
		boolean added_TSP=false;
		boolean linked_TE_TS=false;
		boolean added_TSS=false;
		
		logger.info("[TRACE MANAGER]: Received [" + msg.getPerformativeInt() + "] -> " + msg.getContent());
		
		switch (msg.getPerformativeInt()){
		
			case ACLMessage.REQUEST:
				
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				command = content.substring(0, index);
				
				if (command.equals("publish")) {
					// Publication of a tracing service
					index2 = content.indexOf('#', index+1);
					length = Integer.parseInt(content.substring(index + 1, index2));
					serviceName=content.substring(index2 + 1, index2 + 1 + length);
					index = index2 + length + 1;
					serviceDescription=content.substring(index);

					// Register tracing entity
					if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
						tEntity=new TracingEntity(TracingEntity.AGENT, msg.getSender());
						if (TracingEntities.add(tEntity)){
							added_TE=true;
						}
						else{
							// Error adding the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#" + serviceName + ":Error registering tracing entity");
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
					
					// Register tracing entity as service provider
					if (agree_response && (TSProviderEntities.indexOf(tEntity) < 0)){
						if (TSProviderEntities.add(tEntity)){
							added_TSP=true;
						}
						else{
							// Error adding the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#" + serviceName + ":Error registering tracing entity as provider");
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
					
					// Add tracing service
					if (agree_response){
						if ((tService=TracingServices.getTS(serviceName)) == null){
							// The tracing service does not exist
							tService = new TracingService(serviceName, serviceDescription);
							if (TracingServices.add(tService)){
								added_TS=true;
							}
							else{
								// Impossible to add tracing service
								agree_response=false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("publish#" + serviceName + ":Error registering tracing service");
								logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
							}
						}
					}
					
					// Link service provider and tracing service
					if (agree_response){
						if ((tService.getProviders().indexOf(tEntity) >= 0) ||
							tEntity.getPublishedTS().indexOf(tService) >= 0){
							// Tracing service already published by the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("publish#" + serviceName + ":Tracing service already published");
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
						else{
							if ((!tService.getProviders().add(tEntity)) ||
								(!tEntity.getPublishedTS().add(tService))){
								// Impossible to link properly tracing service and provider
								agree_response=false;
								response_msg = new ACLMessage(ACLMessage.REFUSE);
								response_msg.setSender(this.getAid());
								response_msg.setReceiver(msg.getSender());
								response_msg.setContent("publish#" + serviceName + ":Error adding provider to the tracing service");
								logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
							}
							else{
								linked_TE_TS=true;
							}
						}
					}

					if (agree_response){
						tEventContent=TracingService.DI_TracingServices[TracingService.PUBLISHED_TRACING_SERVICE].getName() + "#" +
						serviceName;
						tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.PUBLISHED_TRACING_SERVICE].getName(),
								tEntity, tEventContent);
						sendSystemTraceEvent(tEvent, tEntity);
						
						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + serviceName);
						logger.info("[TRACE MANAGER]: Sending AGREE message to " + msg.getReceiver().toString());
					}
					else{
						if (linked_TE_TS){
							tService.getProviders().remove(tEntity);
							tEntity.getPublishedTS().remove(tService);
						}
						if (added_TS){
							TracingServices.remove(tService);
						}
						if (added_TSP) {
							TSProviderEntities.remove(tEntity);
						}
						if (added_TE){
							TracingEntities.remove(tEntity);
						}
					}
				}
				else if (command.equals("unpublish")){
					// Remove publication of a tracing service
					serviceName=content.substring(index+1);
					
					if ((tEntity=TSProviderEntities.getTEByAid(msg.getSender())) == null){
						// The tracing entity does not offer the tracing service
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#" + serviceName + ":Tracing service not offered by the entity");
						logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
					}
					
					if (agree_response && ((tService=TracingServices.getTS(serviceName)) == null)){
						// The tracing service does not exist
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + serviceName + ":Error registering tracing service");
						logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
					}
					
					if (agree_response &&
						((tService.getProviders().indexOf(tEntity) < 0) ||
						tEntity.getPublishedTS().indexOf(tService) < 0)){
						// Tracing service not published by the tracing entity
						agree_response=false;
						response_msg = new ACLMessage(ACLMessage.REFUSE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("publish#" + serviceName + ":Tracing service not published by the tracing entity");
						logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
					}
					
					// Remove all subscriptions and send the corresponding trace events to subscriptors
					if (agree_response){
						TSS_iter = tService.getSubscriptions().iterator();
						
						if (tService.getProviders().size() == 1){
							// Just one provider: remove all subscriptions
							while(TSS_iter.hasNext()){
								tServiceSubscription=TSS_iter.next();
								
								tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().remove(tServiceSubscription);
								if (tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().size() == 0){
									TSSubscriberEntities.remove(tServiceSubscription.getSubscriptorEntity());
								}
								TSS_iter.remove();
								
								if (tServiceSubscription.getAnyProvider()){
									tEventContent=TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
									serviceName + "#any";
									// Remove subscription
									this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", serviceName + "#any", Option.NONE);
								}
								else{
									tEventContent=TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
									serviceName + msg.getSender();
									// Remove subscription
									this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + msg.getSender(), Option.NONE);
								}
								
								tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName(),
										tEntity, tEventContent);
								sendSystemTraceEvent(tEvent, tServiceSubscription.getSubscriptorEntity());
							}
						}
						else{
							while(TSS_iter.hasNext()){
								tServiceSubscription=TSS_iter.next();
								if (!tServiceSubscription.getAnyProvider() &&
									tServiceSubscription.getOriginEntity().equals(tEntity)){
									
									tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().remove(tServiceSubscription);
									if (tServiceSubscription.getSubscriptorEntity().getSubscribedToTS().size() == 0){
										TSSubscriberEntities.remove(tServiceSubscription.getSubscriptorEntity());
									}
									TSS_iter.remove();
									
									tEventContent=TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
									serviceName + msg.getSender();
									// Remove subscription
									this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + msg.getSender(), Option.NONE);
									
									tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName(),
											tEntity, tEventContent);
									sendSystemTraceEvent(tEvent, tServiceSubscription.getSubscriptorEntity());
								
								}
							}
						}
						
						tEntity.getPublishedTS().remove(tService);
						if (tEntity.getPublishedTS().size() == 0){
							TSProviderEntities.remove(tEntity);
						}
						
						tEventContent=TracingService.DI_TracingServices[TracingService.UNPUBLISHED_TRACING_SERVICE].getName() +
							"#" + serviceName + "#" + tEntity.getAid();
						tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.UNPUBLISHED_TRACING_SERVICE].getName(),
								tEntity, tEventContent);
						sendSystemTraceEvent(tEvent, tEntity);

						response_msg = new ACLMessage(ACLMessage.AGREE);
						response_msg.setSender(this.getAid());
						response_msg.setReceiver(msg.getSender());
						response_msg.setContent("unpublish#" + serviceName + ":Tracing service unpublished");
						logger.info("[TRACE MANAGER]: Sending AGREE message to " + msg.getReceiver().toString());
					}
				}
				else {
					/**
					 * Building a ACLMessage
					 */
			    	response_msg = new ACLMessage(ACLMessage.UNKNOWN);
			    	response_msg.setSender(this.getAid());
			    	response_msg.setReceiver(msg.getSender());
					response_msg.setContent(content);
					logger.info("[TRACE MANAGER]: Returning UNKNOWN message to " + msg.getReceiver().toString());
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
					originTEntity=null;
				}
				
				if ((tService=TracingServices.getTS(serviceName)) == null){
					// The tracing service does not exist
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("subscribe#" + serviceName + "@" + originEntity + ":Tracing service not found");
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				
				if (agree_response && (originAid != null) && ((originTEntity=tService.getProviders().getTEByAid(originAid))) == null){
					// Tracing service not published by the origin tracing entity
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("subscribe#" + serviceName + "@" + originEntity + ":Tracing service not published by the tracing entity");
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				
				// Check if the subscription already exists
				if (agree_response &&
					((tEntity=TSSubscriberEntities.getTEByAid(msg.getSender())) != null) &&
					(tEntity.getSubscribedToTS().getTSS(tEntity, originTEntity, tService) != null)){
					// The subscription already exists
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("subscribe#" + serviceName + "@" + originEntity + ":Subscription already existent");
					logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
				}
				
				if (agree_response && (tEntity == null)){
					// Register tracing entity
					if ((tEntity=TracingEntities.getTEByAid(msg.getSender())) == null){
						tEntity=new TracingEntity(TracingEntity.AGENT, msg.getSender());
						if (TracingEntities.add(tEntity)){
							added_TE=true;
						}
						else{
							// Error adding the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("subscribe#" + serviceName + "@" + originEntity + ":Error registering tracing entity");
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
					
					// Register tracing entity as subscriber
					if (agree_response && (TSSubscriberEntities.indexOf(tEntity) < 0)){
						if (TSSubscriberEntities.add(tEntity)){
							added_TSS=true;
						}
						else{
							// Error adding the tracing entity
							agree_response=false;
							response_msg = new ACLMessage(ACLMessage.REFUSE);
							response_msg.setSender(this.getAid());
							response_msg.setReceiver(msg.getSender());
							response_msg.setContent("subscribe#" + serviceName + "@" + originEntity + ":Error registering tracing entity as subscriber");
							logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
						}
					}
				}

				if (agree_response){
					// Add subscription
					tServiceSubscription=new TracingServiceSubscription(tEntity, originTEntity, tService);
					tService.addSubscription(tServiceSubscription);
					tEntity.addSubscription(tServiceSubscription);
					
					arguments.put("x-match", "all");
			    	arguments.put("tracing_service", serviceName);
			    	
			    	if (!originEntity.contentEquals("any")) {
			    		arguments.put("origin_entity", originEntity);
			    	}
			    	
			    	this.session.exchangeBind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + originEntity, arguments);
			    	
			    	// Send system trace event
					tEntity=new TracingEntity(TracingEntity.AGENT, this.getAid());
					
					tEventContent=TracingService.DI_TracingServices[TracingService.SUBSCRIBE].getName() + "#" +
					serviceName + "#" + originEntity;
					
					tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.SUBSCRIBE].getName(),
							tEntity, tEventContent);
					sendSystemTraceEvent(tEvent, tServiceSubscription.getSubscriptorEntity());
			    	
			    	/**
					 * Building a ACLMessage
					 */
			    	response_msg = new ACLMessage(ACLMessage.AGREE);
			    	response_msg.setSender(this.getAid());
			    	response_msg.setReceiver(msg.getSender());
					response_msg.setContent("subscription#" + serviceName + "#" + originEntity);
					//logger.info("[TRACE MANAGER]: sending message to " + msg.getReceiver().toString());
				}else{
					if (added_TE){
						TracingEntities.remove(tEntity);
					}
					if (added_TSS){
						TSSubscriberEntities.remove(tEntity);
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
					originTEntity=null;
				}
				
				if ((tService=TracingServices.getTS(serviceName)) == null){
					// The tracing service does not exist
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#" + serviceName + "@" + originEntity + ":Tracing service not found");
					logger.info("[TRACE MANAGER]: Sending REFUSE message to " + msg.getReceiver().toString());
				}
				
				// Check if the subscription exists
				if (agree_response &&
					(((tEntity=TSSubscriberEntities.getTEByAid(msg.getSender())) == null) ||
					((originAid != null) && (originTEntity=tService.getProviders().getTEByAid(originAid)) == null) ||
					((tServiceSubscription=tEntity.getSubscribedToTS().getTSS(tEntity, originTEntity, tService)) == null))){
					
					// The subscription does not exist
					agree_response=false;
					response_msg = new ACLMessage(ACLMessage.REFUSE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#" + serviceName + "@" + originEntity + ":Subscription not found");
					logger.info("[TRACE MANAGER]: sending REFUSE message to " + msg.getReceiver().toString());
				}
				
				if (agree_response){
					// Remove subscription
					tEntity.getSubscribedToTS().remove(tServiceSubscription);
					if (tEntity.getSubscribedToTS().size() == 0){
						TSSubscriberEntities.remove(tEntity);
					}
					tService.getSubscriptions().remove(tServiceSubscription);
					this.session.exchangeUnbind(msg.getSender().name+".trace", "amq.match", serviceName + "#" + originEntity, Option.NONE);
					//logger.info("[TRACE MANAGER]: unbinding " + msg.getSender().name+".trace from " + eventType);
					
					// Send system trace event
					tEntity=new TracingEntity(TracingEntity.AGENT, this.getAid());
					
					tEventContent=TracingService.DI_TracingServices[TracingService.UNSUBSCRIBE].getName() + "#" +
					serviceName + "#" + originEntity;
					
					tEvent=new TraceEvent(TracingService.DI_TracingServices[TracingService.UNSUBSCRIBE].getName(),
							tEntity, tEventContent);
					sendSystemTraceEvent(tEvent, tEntity);
					
					/**
					 * Building a ACLMessage
					 */
					response_msg = new ACLMessage(ACLMessage.AGREE);
					response_msg.setSender(this.getAid());
					response_msg.setReceiver(msg.getSender());
					response_msg.setContent("unsubscribe#" + serviceName + "#" + originEntity);
					logger.info("[TRACE MANAGER]: sending AGREE message to " + msg.getReceiver().toString());
				}
				
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
		    	response_msg.setSender(this.getAid());
		    	response_msg.setReceiver(msg.getSender());
				response_msg.setContent(msg.getContent());
				logger.info("Mensaje received in " + this.getName()
						+ " agent, by onMessage: " + msg.getContent());
				logger.info("[TRACE MANAGER]: returning UNKNOWN message to " + msg.getReceiver().toString());
				send(response_msg);
		}
		
	}
	
}
