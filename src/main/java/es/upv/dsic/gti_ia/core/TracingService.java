package es.upv.dsic.gti_ia.core;

import java.io.Serializable;

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

public class TracingService implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * Domain Independent Tracing Service constant identifiers
	 */
	// System Trace Events
	public static final int TRACE_ERROR = 0;
//	public static final int TRACE_START = 1;
//	public static final int TRACE_STOP = 2;
	public static final int SUBSCRIBE = 1;
	public static final int UNSUBSCRIBE = 2;
	public static final int UNAVAILABLE_TS = 3;
//	public static final int STREAM_OVERFLOW = 6;
//	public static final int STREAM_RESUME = 7;
//	public static final int STREAM_FLUSH_START = 8;
//	public static final int STREAM_FLUSH_STOP = 9;
	// Life cycle of Tracing Entities
	public static final int NEW_AGENT = 4;
//	public static final int NEW_ARTIFACT = 11;
//	public static final int NEW_AGGREGATION = 12;
//	public static final int AGENT_SUSPENDED = 11;
//	public static final int AGENT_RESUMED = 12;
	public static final int AGENT_DESTROYED = 5;
//	public static final int AGENT_ENTERS_AGGREGATION = 10;
//	public static final int AGENT_LEAVES_AGGREGATION = 10;
//	public static final int ARTIFACT_ENTERS_AGGREGATION = 10;
//	public static final int ARTIFACT_LEAVES_AGGREGATION = 10;
	// Messaging among Tracing Entities
	public static final int MESSAGE_SENT = 6;
	public static final int MESSAGE_SENT_DETAIL = 7;
	public static final int MESSAGE_RECEIVED = 8;
	public static final int MESSAGE_RECEIVED_DETAIL = 9;
//	public static final int MESSAGE_UNDELIVERABLE = 16;
//	public static final int MESSAGE_UNDELIVERABLE_DETAIL = 17;
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
	public static final int PUBLISHED_TRACING_SERVICE = 10;
	public static final int UNPUBLISHED_TRACING_SERVICE = 11;
	// These two seem redundant with "SUBSCRIBED" and "UNSUBSCRIBED"
	//public static final int TRACING_SERVICE_REQUEST = 10;
	//public static final int TRACING_SERVICE_CANCEL = 10;
//	public static final int AUTHORIZATION_REQUEST = 22;
//	public static final int AUTHORIZATION_GRANTED = 23;
//	public static final int AUTHORIZATION_DENIED = 24;
	
	/**
	 * Number of supported Domain Independent Tracing Services
	 */
	public static final int MAX_DI_TS = 12;
	
	/**
	 * Array of Domain Independent Tracing Services
	 */
	public static final TracingService[] DI_TracingServices = new TracingService[]{
		new TracingService("TRACE_ERROR", true, false, "General error in the tracing process."),
//		new TracingService("TRACE_START", true, false, "The ER entity started tracing."),
//		new TracingService("TRACE_STOP", true, false, "The ER entity stoppped tracing."),
		new TracingService("SUBSCRIBE", true, false, "The ER entity subscribed to a tracing service."),
		new TracingService("UNSUBSCRIBE", true, false, "The ER entity unsubscribed from a tracing service."),
		new TracingService("UNAVAILABLE_TS", true, false, "The tracing service which was requested does not exist or it has been un published and thus, it is not avilable anymore"),
//		new TracingService("STREAM_OVERFLOW", true, false, "The stream where trace events were being stored for the ER to recover them is full."),
//		new TracingService("STREAM_RESUME", true, false, "The ER entity began to trace events after having stoppped."),
//		new TracingService("STREAM_FLUSH_START", true, false, "The ER entity started flushing the stream where it was receiving events."),
//		new TracingService("STREAM_FLUSH_STOP", true, false, "The flushing process previously started has arrived to its end."),
		new TracingService("NEW_AGENT", true,  true,"A new agent was registered in the system."),
//		new TracingService("AGENT_SUSPENDED", true, true, "An agent was suspended."),
//		new TracingService("AGENT_RESUMED", true, true, "An agent restarted after a suspension."),
		new TracingService("AGENT_DESTROYED", true, true, "An agent was destroyed."),
		new TracingService("MESSAGE_SENT", true, true, "A FIPA-ACL message was sent."),
		new TracingService("MESSAGE_SENT_DETAIL", true, true, "A FIPA-ACL message was sent. Message included in the event."),
		new TracingService("MESSAGE_RECEIVED", true, true, "A FIPA-ACL message was received."),
		new TracingService("MESSAGE_RECEIVED_DETAIL", true, true, "A FIPA-ACL message was received. Message included in the event."),
//		new TracingService("MESSAGE_UNDELIVERABLE", true, false, "A FIPA-ACL message was impossible to deliver."),
//		new TracingService("MESSAGE_UNDELIVERABLE_DETAIL", true, false, "A FIPA-ACL message was impossible to deliver. Message included in the event."),
		new TracingService("PUBLISHED_TRACING_SERVICE", true, true, "A new tracing service has been published by an ES entity."),
		new TracingService("UNPUBLISHED_TRACING_SERVICE", true, true, "A tracing service is not being offered by an ER entity.")
//		new TracingService("AUTHORIZATION_REQUEST", true, false, "An entity requested authorization for a tracing service."),
//		new TracingService("AUTHORIZATION_GRANTED", true, false, "An entity added an authorization for a tracing service."),
//		new TracingService("AUTHORIZATION_DENIED", true, false, "An authorization for a tracing service was removed.")
	};
	
	/**
	 * Tracing Service name, which has to be unique
	 */
	private String name;
	/**
	 * Flag that indicates that the tracing service cannot be unpublished
	 */
	private boolean mandatory; // Cannot be unpublished
	/**
	 * Flag that indicates that the tracing service is requestable
	 * (system tracing services such TRACE_ERROR or SUBSCRIBE are non requestable
	 * since tracing entities automatically receive them when necessary)
	 */
	private boolean requestable;
	/**
	 * Human oriented description of the tracing service (next versions of the event
	 * trace support will change this description in order to be tracing entity oriented)
	 */
	private String description;
	/**
	 * List of tracing entities which provide the tracing service
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity
	 */
	private TracingEntityList providers;
	/**
	 * List of subscriptions to the tracing service
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscription
	 */
	private TracingServiceSubscriptionList subscriptions;

	/**
	 * Void constructor which creates an empty tracing service, without
	 * any provider nor subscriptor, which will be requestable
	 * ( @link{es.upv.dsic.gti_ia.trace.TracingService#requestable} == true ),
	 * and not mandatory ( @link{es.upv.dsic.gti_is.trace.TracingService#mandatory} == false )
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscription
	 */
	public TracingService () {
		this.name = null;
		this.mandatory = false;
		this.requestable = true;
		this.description = null;
		this.providers = new TracingEntityList();
		this.subscriptions = new TracingServiceSubscriptionList();
	}
	
	/**
	 * Constructor which creates an tracing service with the specified
	 * service name and description, without any provider nor subscriptor,
	 * which will be requestable
	 * ( @link{es.upv.dsic.gti_ia.trace.TracingService#requestable} == true ),
	 * and not mandatory ( @link{es.upv.dsic.gti_is.trace.TracingService#mandatory} == false )
	 * 
	 * @param serviceName	Name of the tracing service
	 * @param description	Description of the tracing service
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscription
	 */
	public TracingService (String serviceName, String description) {
		this.name=serviceName;
		this.mandatory = false;
		this.requestable = true;
		this.description=description;
		this.providers = new TracingEntityList();
		this.subscriptions = new TracingServiceSubscriptionList();
	}
	
	/**
	 * Constructor which creates an tracing service with the specified
	 * service name and description, without any provider nor subscriptor.
	 * The tracing service will be requestable and mandatory depending on
	 * the input parameters
	 * 
	 * @param serviceName	Name of the tracing service
	 * @param mandatory		Flag which determines if the tracing service can be unpublished
	 * @param requestable	Flag which determines if the tracing service can be requested
	 * @param description	Description of the tracing service
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscription
	 */
	private TracingService (String serviceName, boolean mandatory, boolean requestable, String description) {
		this.name=serviceName;
		this.mandatory = mandatory;
		this.requestable = requestable;
		this.description=description;
		this.providers = new TracingEntityList();
		this.subscriptions = new TracingServiceSubscriptionList();
	}
	
	/**
	 * Sets the name of the tracing service
	 * 
	 * @param name	New name of the tracing service
	 */
	public void setName (String name) {
		this.name=name;
	}
	
	/**
	 * Sets the description of the tracing service
	 * 
	 * @param description	New description of the tracing service
	 */
	public void setDescription (String description) {
		this.description=description;
	}
	
	/**
	 * Returns the name of the tracing service
	 * 
	 * @return Name of the tracing service
	 */
	public String getName () {
		return this.name;
	}
	
	/**
	 * Returns true if the tracing service is mandatory (i.e: it cannot
	 * be unpublished by any tracing entity)
	 * 
	 * @return Value of the 'mandatory' attribute of the TracingService object 
	 */
	public boolean getMandatory () {
		return this.mandatory;
	}
	
	/**
	 * Returns true if the tracing service is requestable
	 * 
	 * @return Value of the 'requestable' attribute of the TracingService object 
	 */
	public boolean getRequestable () {
		return this.requestable;
	}
	
	/**
	 * Returns the description of the tracing service
	 * 
	 * @return Description of the tracing service
	 */
	public String getDescription () {
		return this.description;
	}
	
	/**
	 * Returns the list of tracing entities which provide the tracing service
	 * 
	 * @return Value of the 'providers' attribute of the TracingService object
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList
	 */
	public TracingEntityList getProviders(){
		return this.providers;
	}
	
	/**
	 * Returns the list of subscriptions to that tracing service
	 *  
	 * @return Value of the 'subscriptions' attribute of the TracingService object
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscription
	 */
	public TracingServiceSubscriptionList getSubscriptions(){
		return this.subscriptions;
	}
	
	/**
	 * Adds a provider to the tracing service
	 * 
	 * @param provider	Tracing entity which will provide the tracing service
	 * 
	 * @return true if the provider is correctly added or false otherwise
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList#add(TracingEntity)
	 */
	public boolean addServiceProvider(TracingEntity provider){
		return this.providers.add(provider);
	}
	
	/**
	 * Adds a subscription to the tracing service
	 * 
	 * @param subscription	Subscription to the tracing service
	 * 
	 * @return true if the subscription is correctly added or false otherwise
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList#add(TracingServiceSubscription)
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscription
	 */
	public boolean addSubscription (TracingServiceSubscription subscription){
		return this.subscriptions.add(subscription);
	}
	
	/**
	 * Removes provider from a tracing service
	 * 
	 * @param providerAid	AgentID of the agent to be removed from the
	 * 		'providers' list of the tracing service
	 * 
	 * @return true if the provider is correctly removed or false otherwise
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntityList#remove(Object)
	 */
	public boolean removeProvider(AgentID providerAid){
		return this.providers.remove(new TracingEntity(TracingEntity.AGENT, providerAid));
	}
}
