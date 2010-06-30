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
	
	private String name;
	private String description;
	private TracingEntityList providers;
	private TracingServiceSubscriptionList subscriptions;

	public TracingService () {
		this.name = null;
		this.description = null;
		this.providers = new TracingEntityList();
		this.subscriptions = new TracingServiceSubscriptionList();
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
	
	public boolean addServiceProvider(TracingEntity provider){
		return this.providers.add(provider);
	}
	
	public boolean addSubscription (TracingServiceSubscription subscription){
		return this.subscriptions.add(subscription);
	}
	
	public boolean removeProvider(AgentID providerAid){
		return this.providers.remove(new TracingEntity(TracingEntity.AGENT, providerAid));
	}
}
