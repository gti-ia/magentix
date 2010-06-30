package es.upv.dsic.gti_ia.trace;

import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.TracingService;

public class TracingServiceSubscriptionList extends ArrayList<TracingServiceSubscription> {
	private static final long serialVersionUID = 1L;

	/**
	 * Obtain a tracing service subscription in the list
	 * @param subscriberEntity
	 * 		Subscriber entity of the subscription.
	 * @param originEntity
	 * 		Origin entity of the subscription. A null value is interpreted as an "any" subscription
	 * @param tService
	 * 		Tracing service
	 * @return
	 * 		The corresponding TracingServiceSusbscription in case it exists
	 * 		or null otherwise
	 */
	public TracingServiceSubscription getTSS(TracingEntity subscriberEntity, TracingEntity originEntity, TracingService tService){
		TracingServiceSubscription tServiceSubscription;
		Iterator<TracingServiceSubscription> iter = this.iterator();
		
		while (iter.hasNext()){
			tServiceSubscription=iter.next();
			if (originEntity == null){
				if (tServiceSubscription.getAnyProvider() &&
					tServiceSubscription.getSubscriptorEntity().equals(subscriberEntity) &&
					tServiceSubscription.getTracingService().equals(tService)){
					return tServiceSubscription;
				}
			}
			else{
				if (tServiceSubscription.getOriginEntity().equals(originEntity) &&
					tServiceSubscription.getSubscriptorEntity().equals(subscriberEntity) &&
					tServiceSubscription.getTracingService().equals(tService)){
					return tServiceSubscription;
				}
			}
		}
		return null;
	}
}
