package es.upv.dsic.gti_ia.trace;

import java.io.Serializable;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Definition of Tracing Entity
 *
 * TODO: Artifacts and aggregations are not yet supported
 * 		far beyond the constants defined in the class 
 */
public class TracingEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constants to identify the tracing entity as an agent, artifact or aggregation
	 */
	public static final int AGENT = 0;
	public static final int ARTIFACT = 1;
	public static final int AGGREGATION = 2;
	
	/**
	 * Type of tracing entity: AGENT, ARTIFACT or AGGREGATION
	 */
	private int type;

	/**
	 * AgentID of the tracing entity, if the tracing entity is an agent
	 */
	private AgentID aid;
	
	/**
	 * List of tracing services offered by the tracing entity
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceList
	 */
	private TracingServiceList publishedTS;
	
	/**
	 * List of subscriptions made by the tracing entity
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingServiceSubscription
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceSubscriptionList
	 */
	private TracingServiceSubscriptionList subscribedToTS;
	
	/**
	 * Constructor which creates an empty tracing entity, with
	 * its type attribute set to -1, its aid set to null and without
	 * published tracing services nor subscriptions
	 */
	public TracingEntity (){
		this.type=-1;
		this.aid=null;
		this.publishedTS=new TracingServiceList();
		this.subscribedToTS=new TracingServiceSubscriptionList();
	}
	
	/**
	 * Constructor which creates a new tracing entity without
	 * published tracing services nor subscriptions.<p>
	 * 
	 * Only agents are supported; so, if the tracing entity is
	 * an artifact or an aggregation, aid is set to null  
	 */
	public TracingEntity (int type, AgentID aid){
		this.type=type;
		
		if (type == AGENT){
			this.aid=aid;
		}
		else{
			// Other tracing entity types not supported yet
			this.aid=null;
		}
		this.publishedTS=new TracingServiceList();
		this.subscribedToTS=new TracingServiceSubscriptionList();
	}
	
	/**
	 * Get the type of the tracing entity
	 * 
	 * @return Type of the tracing entity: {@link AGENT},
	 * 		{@link ARTIFACT} or {@link AGGREGATION}
	 */
	public int getType(){
		return this.type;
	}
	
	/**
	 * Get the AgentID of the tracing entity
	 * 
	 * @return AgentID of the tracing entity if it is an agent or null otherwise
	 */
	public AgentID getAid(){
		if (this.type == AGENT){
			return this.aid;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Get the list of tracing services published by the tracing entity
	 * 
	 * @return List of published tracing services
	 * 
	 * @see es.upv.dsic.gti_ia.core.TracingService
	 * @see es.upv.dsic.gti_ia.trace.TracingServiceList
	 */
	public TracingServiceList getPublishedTS(){
		return this.publishedTS;
	}
	
	/**
	 * Get the list of subscriptions made by the tracing entity
	 * 
	 * @return List of subscriptions
	 * 
	 * @see es.upv.dsic.gti_ia.TracingServiceSubscription
	 * @see es.upv.dsic.gti_ia.TracingServiceSubscriptionList
	 */
	public TracingServiceSubscriptionList getSubscribedToTS(){
		return this.subscribedToTS;
	}
	
	/**
	 * Set the type of the tracing entity
	 * 
	 * @param type	Allowed types are {@linkplain AGENT},
	 * 		{@linkplain ARTIFACT} and {@linkplain AGGREGATION}}
	 * 
	 * @return	Returns -1 if the type is not valid or 0 otherwise
	 */
	public int setType(int type){
		if ((type < 0) || (type > 2)){
			return -1;
		}
		else {
			this.type=type;
			return 0;
		}
	}
	
	/**
	 * Add a new subscription to the tracing entity
	 * 
	 * @param newTracingServiceSubscription
	 * 		TracingServiceSubscription to be added to the list
	 */
	public boolean addSubscription (TracingServiceSubscription newSubscription){
		return this.subscribedToTS.add(newSubscription);
	}
	
	/**
	 * Remove a tracing service subscription from the list
	 * 
	 * @param TSSubscription
	 * 		Subscription to be removed
	 * 
	 * @return Return values:<p>
	 * 		0 : Success. The tracing service subscription has been removed from
	 * 			the end of the list<p>
	 * 
	 *		-1 : Internal values of the list are not correct. There is
	 * 			something really wrong if this happens :-S<p>
	 * 
	 * 		-2 : Subscription not found
	 * 
	 * @see es.upv.dsic.gti_ia.TracingServiceSubscriptionList
	 */
	public boolean removeSubscription(TracingServiceSubscription TSSubscription){
		return this.subscribedToTS.remove(TSSubscription);
	}
	
	/**
	 * Determine if a tracing entity has the same AgentID as the specified
	 * 
	 * @param aid AgentID to compare with the tracing entity
	 * 
	 * @return Returns true if the tracing entity is an agent and has the same AgentID as
	 * 		the one specified in the parameters or false otherwise. 
	 */
	public boolean hasTheSameAidAs(AgentID aid){
		if (this.getAid().host.contentEquals(aid.host) &&
			this.getAid().name.contentEquals(aid.name) &&
			this.getAid().port.contentEquals(aid.port) &&
			this.getAid().protocol.contentEquals(aid.protocol)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Converts the identifying data of the tracing entity to a human readable string.
	 * The present version only supports agents; so, the method will return null if
	 * invoked for a different tracing entity type.
	 *  
	 * @return String with identifying information in human readable format or null
	 * 		if the tracing entity is an artifact or an aggregation.
	 * 
	 * @see es.upv.dsic.gti_ia.core.AgentID#toString()
	 */
	public String toReadableString(){
		switch (this.type){
			case TracingEntity.AGENT:
				return this.aid.toString();
				//break;
			default:
				// ARTIFACT and AGGREGATION not supported
				return null;
		}
	}
}
