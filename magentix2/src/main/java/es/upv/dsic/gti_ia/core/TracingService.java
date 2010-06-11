package es.upv.dsic.gti_ia.core;

import es.upv.dsic.gti_ia.trace.TracingEntity;
import es.upv.dsic.gti_ia.trace.TracingEntityList;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscription;
import es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Definition of Tracing Service
 */

public class TracingService {	
	/**
	 * Domain Independent Tracing Service names
	 */
	// System Trace Events
	public static final int TRACE_ERROR = 0;
	public static final int TRACE_START = 1;
	public static final int TRACE_STOP = 2;
	public static final int SUBSCRIBE = 3;
	public static final int UNSUBSCRIBE = 4;
	public static final int UNAVAILABLE_TS = 5;
	public static final int STREAM_OVERFLOW = 6;
	public static final int STREAM_RESUME = 7;
	public static final int STREAM_FLUSH_START = 8;
	public static final int STREAM_FLUSH_STOP = 9;
	// Life cycle of Tracing Entities
	public static final int NEW_AGENT = 10;
//	public static final int NEW_ARTIFACT = 11;
//	public static final int NEW_AGGREGATION = 12;
	public static final int AGENT_SUSPENDED = 11;
	public static final int AGENT_RESUMED = 12;
	public static final int AGENT_DESTROYED = 13;
//	public static final int AGENT_ENTERS_AGGREGATION = 10;
//	public static final int AGENT_LEAVES_AGGREGATION = 10;
//	public static final int ARTIFACT_ENTERS_AGGREGATION = 10;
//	public static final int ARTIFACT_LEAVES_AGGREGATION = 10;
	// Messaging among Tracing Entities
	public static final int MESSAGE_SENT = 14;
	public static final int MESSAGE_RECEIVED = 15;
	public static final int MESSAGE_UNDELIVERABLE = 16;
	// OMS related Trace Events
//	public static final int ROLE_REGISTRATION = 10;
//	public static final int ROLE_DEREGISTRATION = 10;
//	public static final int NORM_REGISTRATION = 10;
//	public static final int NORM_DEREGISTRATION = 10;
//	public static final int UNIT_REGISTRATION = 10;
//	public static final int UNIT_DEREGISTRATION = 10;
//	public static final int ROLE_ACQUIRE = 10;
//	public static final int ROLE_LEAVE = 10;
//	public static final int ROLE_EXPULSION = 10;
//	public static final int NORM_VIOLATION = 10;
	// Tracing System related Tracing Services
	public static final int PUBLISHED_TRACING_SERVICE = 17;
	public static final int UNPUBLISHED_TRACING_SERVICE = 18;
	// These two seem redundant with "SUBSCRIBED" and "UNSUBSCRIBED"
	//public static final int TRACING_SERVICE_REQUEST = 10;
	//public static final int TRACING_SERVICE_CANCEL = 10;
	public static final int AUTHORIZATION_REQUEST = 19;
	public static final int AUTHORIZATION_GRANTED = 20;
	public static final int AUTHORIZATION_DENIED = 21;
	
	public static final int MAX_DI_TS = 22;
	
	public static final TracingService[] DI_TracingServices = new TracingService[]{
		new TracingService("TRACE_ERROR", "General error in the tracing process."),
		new TracingService("TRACE_START", "The ER entity started tracing."),
		new TracingService("TRACE_STOP", "The ER entity stoppped tracing."),
		new TracingService("SUBSCRIBE", "The ER entity subscribed to a tracing service."),
		new TracingService("UNSUBSCRIBE", "The ER entity unsubscribed from a tracing service."),
		new TracingService("UNAVAILABLE_TS", "The tracing service which was requested does not exist or it has been un published and thus, it is not avilable anymore"),
		new TracingService("STREAM_OVERFLOW", "The stream where trace events were being stored for the ER to recover them is full."),
		new TracingService("STREAM_RESUME", "The ER entity began to trace events after having stoppped."),
		new TracingService("STREAM_FLUSH_START", "The ER entity started flushing the stream where it was receiving events."),
		new TracingService("STREAM_FLUSH_STOP", "The flushing process previously started has arrived to its end."),
		new TracingService("NEW_AGENT", "A new agent was registered in the system."),
		new TracingService("AGENT_SUSPENDED", "An agent was suspended."),
		new TracingService("AGENT_RESUMED", "An agent restarted after a suspension."),
		new TracingService("AGENT_DESTROYED", "An agent was destroyed."),
		new TracingService("MESSAGE_SENT", "A FIPA-ACL message was sent."),
		new TracingService("MESSAGE_RECEIVED", "A FIPA-ACL message was received."),
		new TracingService("MESSAGE_UNDELIVERABLE", "A FIPA-ACL message was impossible to deliver."),
		new TracingService("PUBLISHED_TRACING_SERVICE", "A new tracing service has been published by an ES entity."),
		new TracingService("UNPUBLISHED_TRACING_SERVICE", "A tracing service is not being offered by an ER entity."),
		new TracingService("AUTHORIZATION_REQUEST", "An entity requested authorization for a tracing service."),
		new TracingService("AUTHORIZATION_GRANTED", "An entity added an authorization for a tracing service."),
		new TracingService("AUTHORIZATION_DENIED", "An authorization for a tracing service was removed.")
	};
	
//	public static final TracingService[] DI_TracingServices = new TracingService[MAX_DI_TS];
//	static{
//		DI_TracingServices[TRACE_ERROR].name="TRACE_ERROR";
//		DI_TracingServices[TRACE_ERROR].description="General error in the tracing process.";
//		DI_TracingServices[TRACE_START].name="TRACE_START";
//		DI_TracingServices[TRACE_START].description="The ER entity started tracing.";
//		DI_TracingServices[TRACE_STOP].name="TRACE_STOP";
//		DI_TracingServices[TRACE_STOP].description="The ER entity stoppped tracing.";
//		DI_TracingServices[SUBSCRIBE].name="SUBSCRIBE";
//		DI_TracingServices[SUBSCRIBE].description="The ER entity subscribed to a tracing service.";
//		DI_TracingServices[UNSUBSCRIBE].name="UNSUBSCRIBE";
//		DI_TracingServices[UNSUBSCRIBE].description="The ER entity unsubscribed from a tracing service.";
//		DI_TracingServices[UNAVAILABLE_TS].name="UNAVAILABLE_TS";
//		DI_TracingServices[UNAVAILABLE_TS].description="The tracing service which was requested does not exist or it has been un published and thus, it is not avilable anymore";
//		DI_TracingServices[STREAM_OVERFLOW].name="STREAM_OVERFLOW";
//		DI_TracingServices[STREAM_OVERFLOW].description="The stream where trace events were being stored for the ER to recover them is full.";
//		DI_TracingServices[STREAM_RESUME].name="STREAM_RESUME";
//		DI_TracingServices[STREAM_RESUME].description="The ER entity began to trace events after having stoppped.";
//		DI_TracingServices[STREAM_FLUSH_START].name="STREAM_FLUSH_START";
//		DI_TracingServices[STREAM_FLUSH_START].description="The ER entity started flushing the stream where it was receiving events.";
//		DI_TracingServices[STREAM_FLUSH_STOP].name="STREAM_FLUSH_STOP";
//		DI_TracingServices[STREAM_FLUSH_STOP].description="The flushing process previously started has arrived to its end.";
//		DI_TracingServices[NEW_AGENT].name="NEW_AGENT";
//		DI_TracingServices[NEW_AGENT].description="A new agent was registered in the system.";
//		DI_TracingServices[AGENT_SUSPENDED].name="AGENT_SUSPENDED";
//		DI_TracingServices[AGENT_SUSPENDED].description="An agent was suspended.";
//		DI_TracingServices[AGENT_RESUMED].name="AGENT_RESUMED";
//		DI_TracingServices[AGENT_RESUMED].description="An agent restarted after a suspension.";
//		DI_TracingServices[AGENT_DESTROYED].name="AGENT_DESTROYED";
//		DI_TracingServices[AGENT_DESTROYED].description="An agent was destroyed.";
//		DI_TracingServices[MESSAGE_SENT].name="MESSAGE_SENT";
//		DI_TracingServices[MESSAGE_SENT].description="A FIPA-ACL message was sent.";
//		DI_TracingServices[MESSAGE_RECEIVED].name="MESSAGE_RECEIVED";
//		DI_TracingServices[MESSAGE_RECEIVED].description="A FIPA-ACL message was received.";
//		DI_TracingServices[MESSAGE_UNDELIVERABLE].name="MESSAGE_UNDELIVERABLE";
//		DI_TracingServices[MESSAGE_UNDELIVERABLE].description="A FIPA-ACL message was impossible to deliver.";
//		DI_TracingServices[PUBLISHED_TRACING_SERVICE].name="PUBLISHED_TRACING_SERVICE";
//		DI_TracingServices[PUBLISHED_TRACING_SERVICE].description="A new tracing service has been published by an ES entity.";
//		DI_TracingServices[UNPUBLISHED_TRACING_SERVICE].name="UNPUBLISHED_TRACING_SERVICE";
//		DI_TracingServices[UNPUBLISHED_TRACING_SERVICE].description="A tracing service is not being offered by an ER entity.";
//		DI_TracingServices[AUTHORIZATION_REQUEST].name="AUTHORIZATION_REQUEST";
//		DI_TracingServices[AUTHORIZATION_REQUEST].description="An entity requested authorization for a tracing service.";
//		DI_TracingServices[AUTHORIZATION_GRANTED].name="AUTHORIZATION_GRANTED";
//		DI_TracingServices[AUTHORIZATION_GRANTED].description="An entity added an authorization for a tracing service.";
//		DI_TracingServices[AUTHORIZATION_DENIED].name="AUTHORIZATION_DENIED";
//		DI_TracingServices[AUTHORIZATION_DENIED].description="An authorization for a tracing service was removed.";
//	}
	
	private String name;
	private String description;
	private TracingEntityList providers;
	private TracingServiceSubscriptionList subscriptions;

	public TracingService () {
		this.name = null;
		this.description = null;
		this.providers = null;
		this.subscriptions = null;
	}
	
	public TracingService (String serviceName, String description) {
		this.name=serviceName;
		this.description=description;
		this.providers = new TracingEntityList();
		this.subscriptions = new TracingServiceSubscriptionList();
	}
	
	public void setName (String name) {
		this.name=name;
	}
	
	public void setDescription (String description) {
		this.description=description;
	}
	
	public String getName () {
		return this.name;
	}
	
	public String getDescription () {
		return this.description;
	}
	
	public TracingEntityList getProviders(){
		return this.providers;
	}
	
	public TracingServiceSubscriptionList getSubscriptions(){
		return this.subscriptions;
	}
	
	public int addServiceProvider(TracingEntity provider){
		return this.providers.addTE(provider);
	}
	
	public int addSubscription (TracingServiceSubscription subscription){
		return this.subscriptions.addTSS(subscription);
	}
	
	public int removeProvider(AgentID providerAid){
		return this.providers.removeTE(providerAid);
	}
	
//	public int removeSubscriptionsWithProvider(AgentID providerAid){
//		return this.subscriptions.removeAllTSSFromProvider(providerAid);
//	}
//	
//	public int removeSubscriptionsWithProvider(TracingEntity provider){
//		return this.subscriptions.removeAllTSSFromProvider(provider);
//	}
//	
//	public int removeSubscriptionsWithSubscriptor(AgentID subscriptorAid){
//		return this.subscriptions.removeAllTSSFromSubscriptor(subscriptorAid);
//	}
//	
//	public int removeSubscriptionsWithSubscriptor(TracingEntity subscriptor){
//		return this.subscriptions.removeAllTSSFromSubscriptor(subscriptor);
//	}
}
