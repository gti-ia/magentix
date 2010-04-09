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

	private class ServiceProvider {
		private AgentID aid;
		private ServiceProvider next;
		
		public ServiceProvider () {
			this.aid=null;
			this.next=null;
		}
		
		public ServiceProvider (AgentID aid) {
			this.aid=aid;
			this.next=null;
		}
		
		public void setAID (AgentID aid) {
			this.aid=aid;
		}
		
		public void setNext(ServiceProvider nextProvider) {
			this.next=nextProvider;
		}
		
		public AgentID getAID () {
			return this.aid;
		}
		
		public ServiceProvider getNext() {
			return this.next;
		}
	}
	
	private class ServiceProviderList {
		private ServiceProvider first;
		private ServiceProvider last;
		private int nproviders;
		
		public ServiceProviderList () {
			this.first=null;
			this.last=null;
			this.nproviders=0;
		}
		
		public ServiceProviderList (ServiceProvider provider) {
			this.first=provider;
			this.last=provider;
			this.nproviders=1;
		}
		
		public ServiceProvider getFirst () {
			return this.first;
		}
		
		public ServiceProvider getLast () {
			return this.last;
		}
		
		public int getNProviders () {
			return this.nproviders;
		}
		
		public int addProvider (ServiceProvider newProvider) {
			// Returns position in which it was inserted
			this.getLast().setNext(newProvider);
			newProvider.setNext(null);
			this.last=newProvider;
			this.nproviders++;
			
			return this.nproviders-1;
		}
		
		public int addProviderAt (ServiceProvider newProvider, int position) {
			// position goes from 0 to (nproviders-1)
			int i;
			ServiceProvider sp;
			
			if (position > this.nproviders) {
				// Bad position
				return -1;
			}
			
			for (i=0, sp=this.getFirst(); i < position; i++, sp=sp.getNext());
			
			newProvider.setNext(sp.getNext());
			sp.setNext(newProvider);
			
			if (position == 0) {
				this.first=newProvider;
			}
			else if (position == nproviders) {
				this.last=newProvider;
			}
			
			this.nproviders++;
			
			return position;
		}
	}
	
	private class TracingService {
		private String name;
		private String eventType;
		private ServiceProviderList providers;
		private TracingService next;
		
		public TracingService () {
			this.name = null;
			this.eventType = null;
			this.providers = null;
			this.next = null;
		}
		
		public TracingService (String serviceName, String eventType) {
			this.name=serviceName;
			this.eventType=eventType;
			this.providers=null;
			this.next = null;
		}
		
		private void newSystemTracingService (String serviceName, String eventType) {
			this.name=serviceName;
			this.eventType=eventType;
			this.providers=null;
			this.next = null;
		}
		
		public void setName(String name) {
			this.name=name;
		}
		
		public void setEventType (String eventType) {
			this.eventType=eventType;
		}
		
		public void setNext (TracingService ts) {
			this.next=ts;
		}
		
		public String getName () {
			return this.name;
		}
		
		public String getEventType () {
			return this.eventType;
		}
		
		public ServiceProviderList getProviders () {
			return this.providers;
		}
		
		public TracingService getNext (){
			return this.next;
		}
		
		public void addProvider (ServiceProvider provider) {
			this.providers.addProvider(provider);
		}
		
	}
	
	private class TracingServiceList {
		private TracingService first;
		private TracingService last;
		private int nTracingServices;
		
		public TracingServiceList () {
			this.first = null;
			this.last = null;
			this.nTracingServices = 0;
		}
		
		public TracingServiceList (TracingService tService) {
			this.first = tService;
			this.last = tService;
			this.nTracingServices = 1;
		}
		
		public int addTracingService (TracingService newService) {
			if (this.nTracingServices < 0){
				// Error mucho gordo
				return -1;
			}
			else if (this.nTracingServices == 0) {
				this.first=newService;
			}
			else {
				this.last.setNext(newService);
			}
			
			this.last = newService;
			newService.next = null;
			this.nTracingServices++;
			
			return this.nTracingServices-1;
		}
		
		public int addTracingServiceAtPosition (TracingService newService, int position) {
			// position goes from 0 to (nTracingServices-1)
			int i;
			TracingService ts;
			
			if ((position > this.nTracingServices) || (position < 0)) {
				// Bad position
				return -1;
			}
			
			for (i=0, ts=this.getFirst(); i < position; i++, ts=ts.getNext());
			
			newService.setNext(ts.getNext());
			ts.setNext(newService);
			
			if (position == 0) {
				this.first=newService;
			}
			else if (position == nTracingServices) {
				this.last=newService;
			}
			
			this.nTracingServices++;
			
			return position;
		}
		
		public TracingService getFirst () {
			return this.first;
		}
		
		public TracingService getLast () {
			return this.last;
		}
		
		public int getNTracingServices () {
			return this.nTracingServices;
		}
		
		public String listAllTracingServices () {
			String list = new String();
			int i;
			TracingService ts;
			
			for (i=0, ts=this.getFirst(); (i < this.getNTracingServices()) && (ts.getNext() != null); i++, ts=ts.getNext()){
				list = list + "\n" + ts.eventType.toString();
			}
			list = list + "\n";
			
			return list;
		}
	}
	
	private class TracingServiceSubscription {
		AgentID subscriptor;
		TracingService tracingService;
		TracingServiceSubscription next;
		
		public void TracingServiceSubscription () {
			this.subscriptor = null;
			this.tracingService = null;
			this.next = null;
		}
		
		public void TracingServiceSubscription (AgentID subscriptor, TracingService tracingService) {
			this.subscriptor = subscriptor;
			this.tracingService = tracingService;
			this.next = null;
		}
		
		public void setSubscriptor (AgentID aid) {
			this.subscriptor = aid;
		}
		
		public void setTracingService (TracingService service) {
			this.tracingService = service;
		}
		
		public void setNext (TracingServiceSubscription subscription) {
			this.next = subscription;
		}
		
		public AgentID getSubscriptor () {
			return this.subscriptor;
		}
		
		public TracingService getService () {
			return this.tracingService;
		}
		
		public TracingServiceSubscription getNext () {
			return this.next;
		}
	}
	
	private class TracingServiceSubscriptionList {
		private TracingServiceSubscription first;
		private TracingServiceSubscription last;
		private int nsubscriptions;
		
		public void TracingServiceSubscriptionList (){
			this.first = null;
			this.last = null;
			this.nsubscriptions = 0;
		}
		
		public void TracingServiceSubscriptionList (TracingServiceSubscription subscription) {
			this.first = subscription;
			this.last = subscription;
			this.nsubscriptions = 1;
		}
		
		public TracingServiceSubscription getFirst () {
			return this.first;
		}
		
		public TracingServiceSubscription getLast () {
			return this.last;
		}
		
		public int getNSubscriptions () {
			return this.nsubscriptions;
		}
		
		public int addSubscription (TracingServiceSubscription newSubscription) {
			// Returns position in which it was inserted
			this.getLast().setNext(newSubscription);
			newSubscription.setNext(null);
			this.last=newSubscription;
			this.nsubscriptions++;
			
			return this.nsubscriptions-1;
		}
		
		public int addSubscriptionAt (TracingServiceSubscription newSubscription, int position) {
			// position goes from 0 to (nproviders-1)
			int i;
			TracingServiceSubscription sb;
			
			if (position > this.nsubscriptions) {
				// Bad position
				return -1;
			}
			
			for (i=0, sb=this.getFirst(); i < position; i++, sb=sb.getNext());
			
			newSubscription.setNext(sb.getNext());
			sb.setNext(newSubscription);
			
			if (position == 0) {
				this.first=newSubscription;
			}
			else if (position == nsubscriptions) {
				this.last=newSubscription;
			}
			
			this.nsubscriptions++;
			
			return position;
		}
	}
	
	private class TracingServiceAuthorization {
		
	}
	
	private class TracingServiceAuthorizationGraph {
		
	}
	
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
		DI_Tracing_Services.addTracingService(new TracingService("TRACE_ERROR", "TRACE_ERROR"));
		DI_Tracing_Services.addTracingService(new TracingService("TRACE_START", "TRACE_START"));
		DI_Tracing_Services.addTracingService(new TracingService("TRACE_STOP", "TRACE_STOP"));
		DI_Tracing_Services.addTracingService(new TracingService("SUBSCRIBED", "SUBSCRIBED"));
		DI_Tracing_Services.addTracingService(new TracingService("UNSUBSCRIBED", "UNSUBSCRIBED"));
		DI_Tracing_Services.addTracingService(new TracingService("STREAM_OVERFLOW", "STREAM_OVERFLOW"));
		DI_Tracing_Services.addTracingService(new TracingService("STREAM_RESUME", "STREAM_RESUME"));
		DI_Tracing_Services.addTracingService(new TracingService("STREAM_FLUSH_START", "STREAM_FLUSH_START"));
		DI_Tracing_Services.addTracingService(new TracingService("STREAM_FLUSH_STOP", "STREAM_FLUSH_STOP"));
		
		// Life cycle of Tracing Entities
		DI_Tracing_Services.addTracingService(new TracingService("NEW_AGENT", "NEW_AGENT"));
		//DI_Tracing_Services.addTracingService(new TracingService("NEW_ARTIFACT", "NEW_ARTIFACT"));
		//DI_Tracing_Services.addTracingService(new TracingService("NEW_AGGREGATION", "NEW_AGGREGATION"));
		DI_Tracing_Services.addTracingService(new TracingService("AGENT_SUSPENDED", "AGENT_SUSPENDED"));
		DI_Tracing_Services.addTracingService(new TracingService("AGENT_RESUMED", "AGENT_RESUMED"));
		DI_Tracing_Services.addTracingService(new TracingService("AGENT_DESTROYED", "AGENT_DESTROYED"));
		//DI_Tracing_Services.addTracingService(new TracingService("AGENT_ENTERS_AGGREGATION", "AGENT_ENTERS_AGGREGATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("AGENT_LEAVES_AGGREGATION", "AGENT_LEAVES_AGGREGATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("ARTIFACT_ENTERS_AGGREGATION", "ARTIFACT_ENTERS_AGGREGATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("ARTIFACT_LEAVES_AGGREGATION", "ARTIFACT_LEAVES_AGGREGATION"));
		
		// Messaging among Tracing Entities
		DI_Tracing_Services.addTracingService(new TracingService("MESSAGE_SENT", "MESSAGE_SENT"));
		DI_Tracing_Services.addTracingService(new TracingService("MESSAGE_RECEIVED", "MESSAGE_RECEIVED"));
		DI_Tracing_Services.addTracingService(new TracingService("MESSAGE_UNDELIVERABLE", "MESSAGE_UNDELIVERABLE"));
		
		// OMS related Trace Events
		//DI_Tracing_Services.addTracingService(new TracingService("ROLE_REGISTRATION", "ROLE_REGISTRATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("ROLE_DEREGISTRATION", "ROLE_DEREGISTRATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("NORM_REGISTRATION", "NORM_REGISTRATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("NORM_DEREGISTRATION", "NORM_DEREGISTRATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("UNIT_REGISTRATION", "UNIT_REGISTRATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("UNIT_DEREGISTRATION", "UNIT_DEREGISTRATION"));
		//DI_Tracing_Services.addTracingService(new TracingService("ROLE_ACQUIRE", "ROLE_ACQUIRE"));
		//DI_Tracing_Services.addTracingService(new TracingService("ROLE_LEAVE", "ROLE_LEAVE"));
		//DI_Tracing_Services.addTracingService(new TracingService("ROLE_EXPULSION", "ROLE_EXPULSION"));
		//DI_Tracing_Services.addTracingService(new TracingService("NORM_VIOLATION", "NORM_VIOLATION"));
		
		// Tracing System related Tracing Services
		DI_Tracing_Services.addTracingService(new TracingService("PUBLISHED_TRACING_SERVICE", "PUBLISHED_TRACING_SERVICE"));
		DI_Tracing_Services.addTracingService(new TracingService("UNPUBLISHED_TRACING_SERVICE", "UNPUBLISHED_TRACING_SERVICE"));
		DI_Tracing_Services.addTracingService(new TracingService("TRACING_SERVICE_REQUEST", "TRACING_SERVICE_REQUEST"));
		DI_Tracing_Services.addTracingService(new TracingService("TRACING_SERVICE_CANCEL", "TRACING_SERVICE_CANCEL"));
		DI_Tracing_Services.addTracingService(new TracingService("AUTHORIZATION_REQUEST", "AUTHORIZATION_REQUEST"));
		DI_Tracing_Services.addTracingService(new TracingService("AUTHORIZATION_ADDED", "AUTHORIZATION_ADDED"));
		DI_Tracing_Services.addTracingService(new TracingService("AUTHORIZATION_REMOVED", "AUTHORIZATION_REMOVED"));
		
		//System.out.print("DI Tracing services:\n" + DI_Tracing_Services.listAllTracingServices());
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
	
	public void execute() {
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
		int index;
		TraceEvent tEvent; // = new TraceEvent();
		ACLMessage response_msg;
		
		//logger.info("[TRACE MANAGER]: Received [" + msg.getPerformativeInt() + "] -> " + msg.getContent());
		
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
		    	
		    	if (!originEntity.equals("any")) {
		    		arguments.put("origin_entity", originEntity);
		    	}
		    			    	
		    	this.session.exchangeBind(msg.getSender().toString()+".trace", "amq.match", eventType + "#" + originEntity, arguments);
		    	logger.info("[TRACE MANAGER]: binding " + msg.getSender().toString()+".trace");
		    	//this.session.exchangeBind(msg.getSender().toString()+".trace", "mgx.trace", eventType + "#" + originEntity.toString(), arguments);
		    	// confirm completion
		    	//this.session.sync();
		    	
//		    	tEvent = new TraceEvent("system_notify", this.getAid(), "SUBSCRIBED#" + eventType + "#" + originEntity);
//		    	sendSystemTraceEvent(tEvent, msg.getSender().toString());
		    	/**
				 * Building a ACLMessage
				 */
				//response_msg = new ACLMessage(ACLMessage.AGREE);
				response_msg=msg.createReply();
				response_msg.setPerformative(ACLMessage.AGREE);
				response_msg.setContent("subscription#" + eventType + "#" + originEntity);
				logger.info("[TRACE MANAGER]: sending message to " + msg.getReceiver().toString());
				/**
				 * Sending a ACLMessage
				 */
				//send(response_msg);						
				
		    	break;
		    	
			case ACLMessage.CANCEL:
				// Unsubscription from a tracing service
				content = msg.getContent();
				
				index = content.indexOf('#', 0);
				//length = Integer.parseInt(content.substring(0, index));
				eventType = content.substring(0, index);
				originEntity = content.substring(index + 1);
				
				this.session.exchangeUnbind(msg.getSender()+".trace", "amq.match", eventType + "#" + originEntity.toString(), Option.NONE);
				//this.session.exchangeUnbind(msg.getSender()+".trace", "mgx.trace", eventType + "#" + originEntity.toString(), Option.NONE);
				
		    	// confirm completion
		    	this.session.sync();
		    	
		    	tEvent = new TraceEvent("system_notify", this.getAid(), "UNSUBSCRIBED#" + eventType + "#" + originEntity);
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
