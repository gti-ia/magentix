package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

/**
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
