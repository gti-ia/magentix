package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Definition of Tracing Entity
 *
 * TODO: Artifacts and aggregations are not yet supported
 * 		far beyond the constants defined in the class 
 */
public class TracingEntity {
	public static final int AGENT = 0;
	public static final int ARTIFACT = 1;
	public static final int AGGREGATION = 2;
	
	private int type;
	private AgentID aid;
	private TracingServiceList publishedTS;
	private TracingServiceSubscriptionList subscribedToTS;
	
	public TracingEntity (int type, AgentID aid){
		this.type=AGENT;
		this.aid=aid;
		this.publishedTS=new TracingServiceList();
		this.subscribedToTS=new TracingServiceSubscriptionList();
	}
	
	public int getType(){
		return this.type;
	}
	
	public AgentID getAid(){
		if (this.type == AGENT){
			return this.aid;
		}
		else{
			return null;
		}
	}
	
	public TracingServiceList getPublishedTS(){
		return this.publishedTS;
	}
	
	public TracingServiceSubscriptionList getSubscribedToTS(){
		return this.subscribedToTS;
	}
	
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
	 * @param newTracingServiceSubscription
	 * 		TracingServiceSubscription to be added to the list
	 */
	public boolean addSubscription (TracingServiceSubscription newSubscription){
		return this.subscribedToTS.add(newSubscription);
	}
	
	/**
	 * Add a new subscription to the tracing entity for the specified tracing service provided by the specified
	 * origin AgentID to the list
	 * 
	 * @param subscriptorEntity
	 * 		Tracing entity which wants to subscribe to the tracing service
	 *  
	 * @param originEntity
	 * 		Tracing entity which provides the tracing service.
	 * 		For subscribing to any provider, a null value has to be specified
	 * 
	 * @param service
	 * 		Tracing Service 
	 * 
	 * @return 0
	 * 		Success: The new tracing service subscription has been added at
	 * 			the end of the list
	 * @return -1
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 * 
	 * @return -2
	 * 		Duplicate AgentID: A subscription to the tracing service provided by the specified
	 * 			origin entity already exists in the list
	 */
//	public int addSubscription (TracingEntity subscriptorEntity, TracingEntity originEntity, TracingService service){
//		return this.subscribedToTS.addTSS(subscriptorEntity, originEntity, service);
//	}
	
	/**
	 * Remove a tracing service subscription from the list
	 * @param subscriptorAid
	 * 		Aid of the agent which is subscribed
	 * @param originAid
	 * 		Aid of the origin agent. A null value is interpreted as an "any" subscription
	 * @param serviceName
	 * 		Name of the tracing service
	 * 
	 * @return 0
	 * 		Success: The tracing service subscription has been removed from
	 * 			the end of the list
	 * 
	 * @return -1
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 * 
	 * @return -2
	 * 		Subscription not found
	 */
//	public int removeSubscription(AgentID subscriptorAid, AgentID originAid, String serviceName){
//		return this.subscribedToTS.removeTSS(subscriptorAid, originAid, serviceName);
//	}
	
	/**
	 * Remove a tracing service subscription from the list
	 * @param TSSubscription
	 * 		Subscription to be removed
	 * 
	 * @return 0
	 * 		Success: The tracing service subscription has been removed from
	 * 			the end of the list
	 * 
	 * @return -1
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 * 
	 * @return -2
	 * 		Subscription not found
	 */
	public boolean removeSubscription(TracingServiceSubscription TSSubscription){
		return this.subscribedToTS.remove(TSSubscription);
	}
	
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
