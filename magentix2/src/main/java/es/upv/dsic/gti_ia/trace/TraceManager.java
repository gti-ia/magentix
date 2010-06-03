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
	
	private class TSM_Node {
		private TracingService tService;
		private TracingEntityList subscribers;
		private TSM_Node prev;
		private TSM_Node next;
		
		public TSM_Node(){
			this.tService=null;
			this.subscribers=null; 
			this.prev=null;
			this.next=null;
		}
		
		public TSM_Node(TracingService ts){
			this.tService=ts;
			this.subscribers=new TracingEntityList();
			this.prev=null;
			this.next=null;
		}
		
		public TSM_Node(TracingService ts, TracingEntityList subs){
			this.tService=ts;
			this.subscribers=subs;
			this.prev=null;
			this.next=null;
		}
		
		public void setNext(TSM_Node next){
			this.next = next;
		}
		
		public void setPrev(TSM_Node prev){
			this.prev = prev;
		}
		
		public TracingService getTService(){
			return this.tService;
		}
		
		public TSM_Node getPrev(){
			return this.prev;
		}
		
		public TSM_Node getNext(){
			return this.next;
		}
	}
	
	private class TracingServiceManagementList {
		private TSM_Node first;
		private TSM_Node last;
		private int length;
		
		public TracingServiceManagementList(){
			this.first=null;
			this.last=null;
			this.length=0;
		}
				
		public TSM_Node getFirst(){
			return this.first;
		}
		
		public TSM_Node getLast(){
			return this.last;
		}
		
		public int getLength(){
			return this.length;
		}
		
		private TSM_Node getTSM_NodeByServiceName(String name){
			int i;
			TSM_Node node;
			
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if (node.getTService().getName().contentEquals(name)){
					return node;
				}
			}
			
			return null;
		}
		
		public TracingService getTSByName(String name){
			int i;
			TSM_Node node;
			
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if (node.getTService().getName().contentEquals(name)){
					return node.getTService();
				}
			}
			return null;
		}
		
		/**
		 * Determines if a tracing service already exists in the list
		 * 
		 * @param name
		 * 		Name of the tracing service
		 * 
		 * @return true
		 * 		A tracing service with the specified name
		 * 		exists in the list.
		 * @return false
		 * 		It does not exists a tracing service with
		 * 		that name in the list.
		 */
		public boolean existsTS(String name){
			if (this.getTSByName(name) != null){
				return true;
			}
			else {
				return false;
			}
		}
		
		/**
		 * Add a new TS without subscriptors to the list.
		 * If the TS already exists, the method returns error. 
		 * @param newTracingService
		 * 		TracingService to be added to the list
		 * @return 0
		 * 		Success: The new tracing service has been added at
		 * 			the end of the list
		 * @return -1
		 * 		Duplicate TS: The specified TS already exists in the list
		 * @return -2
		 * 		Internal values of the list are not correct. There is
		 * 		something really wrong if this happens :-S
		 */
		public int addTSM(TracingService newTracingService){
			TSM_Node tsm_node;
						
			if (this.length < 0){
				// Error mucho gordo
				return -2;
			}
					
			tsm_node=new TSM_Node(newTracingService);
			
			if (this.length == 0) {
				this.first=tsm_node;
			}
			else if (this.existsTS(newTracingService.getName())) {
				return -1;
			}
			else {
				this.last.setNext(tsm_node);
				tsm_node.setPrev(this.last);
			}
			
			tsm_node.setNext(null);
			this.last = tsm_node;
			this.length++;
				
			return 0;
		}
		
		/**
		 * Remove the TS from the from the list.
		 * 
		 * @param tService
		 * 
		 * @return 0
		 * 		Success: The TS has been removed from
		 * 			the list
		 * @return -1
		 * 		TS not found
		 */
		public int removeTS(TracingService tService){
			TSM_Node tsm;
			
			if ((tsm=this.getTSM_NodeByServiceName(tService.getName())) == null){
				// Service provider does not exist
				return -1;
			}
			else{
				if (tsm.getPrev() == null){
					// tsm is the first in the list
					if (this.length == 1){
						// Empty the list
						this.first=null;
						this.last=null;
					}
					else{
						tsm=this.first;
						this.first=tsm.getNext();
						tsm.setNext(null);
						this.first.setPrev(null);
					}
				}
				else if (tsm.getNext() == null){
					// tsm is the last in the list
					tsm=this.last;
					this.last=tsm.getPrev();
					this.last.setNext(null);
					tsm.setPrev(null);
				}
				else{
					tsm.getPrev().setNext(tsm.getNext());
					tsm.getNext().setPrev(tsm.getPrev());
					tsm.setPrev(null);
					tsm.setNext(null);
				}
			}
			
			this.length--;
			return 0;
		}
	}
	
	private class TEM_Node {
		private TracingEntity TEntity;
		private TracingServiceManagementList published_DI_TS;
		private TracingServiceManagementList published_DD_TS;
		private TracingServiceList isSubscribedAny;
		private TracingServiceSubscriptionList isSubscribed;
		private TEM_Node prev;
		private TEM_Node next;
		
		public TEM_Node(){
			this.TEntity=null;
			this.published_DI_TS = new TracingServiceManagementList();
			this.published_DD_TS = new TracingServiceManagementList();
			this.isSubscribedAny = new TracingServiceList();
			this.isSubscribed = new TracingServiceSubscriptionList();
			this.prev=null;
			this.next=null;
		}
		
		public TEM_Node(TracingEntity te){
			this.TEntity=te;
			this.published_DI_TS = new TracingServiceManagementList();
			this.published_DD_TS = new TracingServiceManagementList();
			this.isSubscribedAny = new TracingServiceList();
			this.isSubscribed = new TracingServiceSubscriptionList();
			this.prev=null;
			this.next=null;
		}
		
//		public void setNext(TEM_Node next){
//			this.next = next;
//		}
//		
//		public void setPrev(TEM_Node prev){
//			this.prev = prev;
//		}
		
		public TracingEntity getTEntity(){
			return this.TEntity;
		}
		
		public TEM_Node getPrev(){
			return this.prev;
		}
		
		public TEM_Node getNext(){
			return this.next;
		}
		
		public TracingServiceManagementList getPublished_DI_TS(){
			return this.published_DI_TS;
		}
		
		public TracingServiceManagementList getPublished_DD_TS(){
			return this.published_DD_TS;
		}
		
		public TracingServiceList getIsSubscribedAny(){
			return this.isSubscribedAny;
		}
		
		public TracingServiceSubscriptionList getIsSubscribed(){
			return this.isSubscribed;
		}
	}
	
	private class TracingEntityManagementList {
		private TEM_Node first;
		private TEM_Node last;
		private int length;
		
		public TracingEntityManagementList(){
			this.first=null;
			this.last=null;
			this.length=0;
		}
		
		public TEM_Node getFirst(){
			return this.first;
		}
		
		public TEM_Node getLast(){
			return this.getLast();
		}
		
		public int getLength(){
			return this.length;
		}
		
		public TEM_Node getTEM_NodeByAid(AgentID aid){
			int i;
			TEM_Node node;
			
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if (node.getTEntity().getAid().equals(aid)){
					return node;
				}
			}
			
			return null;
		}
		
		public TracingEntity getTEMByAid(AgentID aid){
			int i;
			TEM_Node node;
			
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if (node.getTEntity().getAid().equals(aid)){
					return node.getTEntity();
				}
			}
			
			return null;
		}
		
		/**
		 * Determines if a tracing entity already exists in the list
		 * 
		 * @param aid
		 * 		AgentID of the tracing entity
		 * 
		 * @return true
		 * 		A tracing entity with the specified AgentID
		 * 		exists in the list.
		 * @return false
		 * 		It does not exists a tracing entity with
		 * 		that AgentID in the list.
		 */
		public boolean existsTEM(AgentID aid){
			if (this.getTEMByAid(aid) != null){
				return true;
			}
			else {
				return false;
			}
		}
		
		/**
		 * Add a new TE to the list
		 * @param newTracingEntity
		 * 		TracingEntity to be added to the list
		 * @return 0
		 * 		Success: The new tracing entity has been added at
		 * 			the end of the list
		 * @return -1
		 * 		Duplicate AgentID: A tracing entity with the specified
		 * 			identifier already exists in the list
		 * @return -2
		 * 		Internal values of the list are not correct. There is
		 * 		something really wrong if this happens :-S
		 * @return -3
		 * 		Tracing entity type not supported yet
		 */
		public int addTEM(TracingEntity newTracingEntity){
			TEM_Node tem_node;
			
			if (newTracingEntity.getType() != TracingEntity.AGENT){
				// ARTIFACTS and AGGREGATIONS are not supported yet
				return -3;
			}
			
			if (this.length < 0){
				// Error mucho gordo
				return -2;
			}
			else if (this.length == 0) {
				tem_node = new TEM_Node(newTracingEntity);
				this.first=tem_node;
			}
			else if (this.existsTEM(newTracingEntity.getAid())) {
				return -1;
			}
			else {
				tem_node = new TEM_Node(newTracingEntity);
				this.last.next=tem_node;
				tem_node.prev=this.last;
			}
			
			tem_node.next=null;
			this.last = tem_node;
			this.length++;
					
			return 0;
		}
		
		/**
		 * Remove the TE with the specified AgentID from the list
		 * @param aid
		 * 		AgentID of the tracing entity which has to be removed
		 * @return 0
		 * 		Success: The tracing entity has been removed from
		 * 			the end of the list
		 * @return -1
		 * 		AgentID not found
		 * @return -2
		 * 		Internal values of the list are not correct. There is
		 * 		something really wrong if this happens :-S
		 */
		public int removeTEM(AgentID aid){
			TEM_Node tem;
			
			if ((tem=this.getTEM_NodeByAid(aid)) == null){
				// Service provider does not exist
				return -1;
			}
			else{
				if (tem.getPrev() == null){
					// tem is the first in the list
					if (this.length == 1){
						// Empty the list
						this.first=null;
						this.last=null;
					}
					else{
						tem=this.first;
						this.first=tem.getNext();
						tem.next=null;
						this.first.prev=null;
					}
				}
				else if (tem.getNext() == null){
					// tem is the last provider in the list
					tem=this.last;
					this.last=tem.getPrev();
					this.last.next=null;
					tem.prev=null;
				}
				else{
					tem.getPrev().next=tem.getNext();
					tem.getNext().prev=tem.getPrev();
					tem.prev=null;
					tem.next=null;
				}
			}
			
			this.length--;
			return 0;
		}
	}
	
	private TracingEntityManagementList TracingEntities;
	private TracingServiceList DD_Tracing_Services;
	//private TracingServiceList DI_Tracing_Services;
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
		
		TracingEntities = new TracingEntityManagementList();
		
		//DI_Tracing_Services = new TracingServiceList();
		DD_Tracing_Services = new TracingServiceList();
		
//		// System Trace Events
//		DI_Tracing_Services.addTracingService(null, new TracingService("TRACE_ERROR", "TRACE_ERROR", "General error in the tracing process."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("TRACE_START", "TRACE_START", "The ER entity started tracing."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("TRACE_STOP", "TRACE_STOP", "The ER entity stoppped tracing."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("SUBSCRIBED", "SUBSCRIBED", "The ER entity subscribed to a tracing service."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("UNSUBSCRIBED", "UNSUBSCRIBED", "The ER entity unsubscribed from a tracing service."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("UNAVAILABLE_TS", "UNAVAILABLE_TS", "The tracing service which was requested does not exist or it has been un published and thus, it is not avilable anymore"));
//		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_OVERFLOW", "STREAM_OVERFLOW", "The stream where trace events were being stored for the ER to recover them is full."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_RESUME", "STREAM_RESUME", "The ER entity began to trace events after having stoppped."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_FLUSH_START", "STREAM_FLUSH_START", "The ER entity started flushing the stream where it was receiving events."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("STREAM_FLUSH_STOP", "STREAM_FLUSH_STOP", "The flushing process previously started has arrived to its end."));
//		
//		// Life cycle of Tracing Entities
//		DI_Tracing_Services.addTracingService(null, new TracingService("NEW_AGENT", "NEW_AGENT", "A new agent was registered in the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("NEW_ARTIFACT", "NEW_ARTIFACT", "A new artifact was registered in the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("NEW_AGGREGATION", "NEW_AGGREGATION", "A new aggregation was registered in the system."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_SUSPENDED", "AGENT_SUSPENDED", "An agent was suspended."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_RESUMED", "AGENT_RESUMED", "An agent restarted after a suspension."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_DESTROYED", "AGENT_DESTROYED", "An agent was destroyed."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_ENTERS_AGGREGATION", "AGENT_ENTERS_AGGREGATION", "An agent enters an aggregation."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("AGENT_LEAVES_AGGREGATION", "AGENT_LEAVES_AGGREGATION", "An agent leaves an aggregation."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ARTIFACT_ENTERS_AGGREGATION", "ARTIFACT_ENTERS_AGGREGATION", "An artifact starts being part of an aggregation."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ARTIFACT_LEAVES_AGGREGATION", "ARTIFACT_LEAVES_AGGREGATION", "An artifact stops being part of an aggregation."));
//		
//		// Messaging among Tracing Entities
//		DI_Tracing_Services.addTracingService(null, new TracingService("MESSAGE_SENT", "MESSAGE_SENT", "A FIPA-ACL message was sent."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("MESSAGE_RECEIVED", "MESSAGE_RECEIVED", "A FIPA-ACL message was received."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("MESSAGE_UNDELIVERABLE", "MESSAGE_UNDELIVERABLE", "A FIPA-ACL message was impossible to deliver."));
//		
//		// OMS related Trace Events
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_REGISTRATION", "ROLE_REGISTRATION", "A new role has been registered in the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_DEREGISTRATION", "ROLE_DEREGISTRATION", "An existing role has been removed from the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("NORM_REGISTRATION", "NORM_REGISTRATION", "A new norm has been registered in the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("NORM_DEREGISTRATION", "NORM_DEREGISTRATION", "A norm has been removed from the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("UNIT_REGISTRATION", "UNIT_REGISTRATION", "A new organisational unit has been registered in the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("UNIT_DEREGISTRATION", "UNIT_DEREGISTRATION", "An existing organizational unit has been removed from the system."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_ACQUIRE", "ROLE_ACQUIRE", "A role has been acquired by an entity."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_LEAVE", "ROLE_LEAVE", "An entity in the system has voluntarily stoppped playing a specific role."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("ROLE_EXPULSION", "ROLE_EXPULSION", "An entity in the system has been expulsed from playing a specific role."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("NORM_VIOLATION", "NORM_VIOLATION", "A norm in the system has been violated."));
//		
//		// Tracing System related Tracing Services
//		DI_Tracing_Services.addTracingService(null, new TracingService("PUBLISHED_TRACING_SERVICE", "PUBLISHED_TRACING_SERVICE", "A new tracing service has been published by an ES entity."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("UNPUBLISHED_TRACING_SERVICE", "UNPUBLISHED_TRACING_SERVICE", "A tracing service is not being offered by an ER entity."));
//		// These two seem redundant with "SUBSCRIBED" and "UNSUBSCRIBED"
//		//DI_Tracing_Services.addTracingService(null, new TracingService("TRACING_SERVICE_REQUEST", "TRACING_SERVICE_REQUEST", "An ER entity requested a tracing service."));
//		//DI_Tracing_Services.addTracingService(null, new TracingService("TRACING_SERVICE_CANCEL", "TRACING_SERVICE_CANCEL", "An ER entity cancelled the subscription to a tracing service."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("AUTHORIZATION_REQUEST", "AUTHORIZATION_REQUEST", "An entity requested authorization for a tracing service."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("AUTHORIZATION_GRANTED", "AUTHORIZATION_GRANTED", "An entity added an authorization for a tracing service."));
//		DI_Tracing_Services.addTracingService(null, new TracingService("AUTHORIZATION_DENIED", "AUTHORIZATION_DENIED", "An authorization for a tracing service was removed."));
		
		//logger.info("DI Tracing services:\n" + DI_Tracing_Services.listAllTracingServices());
		
		// Subscribe to tracing entities life cycle related tracing services
		arguments.clear();
		arguments.put("x-match", "any");
		arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.NEW_AGENT].getName());
		arguments.put("tracing_service", TracingService.DI_TracingServices[TracingService.AGENT_DESTROYED].getName());
    	this.traceSession.exchangeBind(this.getName() + ".trace", "amq.match", this.getName() + ".control.direct", arguments);
    	// confirm completion
    	this.traceSession.sync();
	}
	
	/**
	 * Sends a trace event to the amq.match exchange
	 * @param tEvent
	 * 
	 * @param destination
	 * 		"all"  : System trace events which are to be received by all tracing entities
	 * 
	 * 		!"all" : agent name of the agent which has to receive that system trace event        
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
		body = body + tEvent.getTracingService().length() + "#"
				+ tEvent.getTracingService();
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
    	messageHeaders.put("tracing_service", tEvent.getTracingService());
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
		
		TracingService ts;
		TSM_Node tsm_node;
		TEM_Node tem_node;
		
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
					newService.setName(content.substring(index2 + 1, index2 + 1 + length));
					
					index = index2 + length + 1;
					newService.setDescription(content.substring(index));
					
					if ((error = DD_Tracing_Services.addTS(newService)) >= 0){
						tem_node=TracingEntities.getTEM_NodeByAid(msg.getSender());
						if (tem_node.published_DD_TS.addTSM(newService) >= 0){
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
					
					if((tsm_node=TracingEntities.getTEM_NodeByAid(msg.getSender()).published_DD_TS.getTSM_NodeByServiceName(serviceName)) == null){
						
					}
					
					if ((tem_node=DD_Tracing_Services.getTEM_NodeByName(serviceName)) == null){
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
				
				if ((ts=DI_Tracing_Services.getServiceByName(eventType)) != null){
					// Subscribe to a DI tracing service
					if (!originEntity.equals("any")) {
						ts.addSubscriptionAll(newSubscription)
					}
				}
				else if ((ts=DD_Tracing_Services.getServiceByName(eventType)) != null){
					// Subscribe to a DD tracing service
					
				}
				else{
					// Tracing service not available
				}
				
				arguments.put("x-match", "all");
		    	arguments.put("tracing_service", eventType);
		    	
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
