package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

public class TracingServiceSubscriptionList {
	private TracingServiceSubscription first;
	private TracingServiceSubscription last;
	private int nSubscriptions;
	
	public void TracingServiceSubscriptionList (){
		this.first = null;
		this.last = null;
		this.nSubscriptions = 0;
	}
	
	public void TracingServiceSubscriptionList (TracingServiceSubscription subscription) {
		this.first = subscription;
		this.last = subscription;
		this.nSubscriptions = 1;
	}
	
	public TracingServiceSubscription getFirst () {
		return this.first;
	}
	
	public TracingServiceSubscription getLast () {
		return this.last;
	}
	
	public int getNSubscriptions () {
		return this.nSubscriptions;
	}
	
	public int addSubscription (TracingServiceSubscription newSubscription) {
		if (this.nSubscriptions < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.nSubscriptions == 0) {
			this.first=newSubscription;
			newSubscription.setPrev(null);
		}
		else if (this.existsSubscriptor(newSubscription.getSubscriptor())) {
			return -1;
		}
		else {
			this.last.setNext(newSubscription);
			newSubscription.setPrev(this.last);
		}
		
		newSubscription.setNext(null);
		this.last = newSubscription;
		this.nSubscriptions++;
				
		return 0;
//		
//		// Returns position in which it was inserted
//		this.getLast().setNext(newSubscription);
//		newSubscription.setNext(null);
//		this.last=newSubscription;
//		this.nSubscriptions++;
//		
//		return this.nSubscriptions-1;
	}
	
	public int addSubscription (AgentID subscriber, String tracingServiceName){
		TracingServiceSubscription newSubscription;
		
		if (this.nSubscriptions < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.nSubscriptions == 0) {
			
			newSubscription = new TracingServiceSubscription(subscriber, ts);
			this.first=newSubscription;
			newSubscription.setPrev(null);
		}
		else if (this.existsSubscriptor(newSubscription.getSubscriptor())) {
			return -1;
		}
		else {
			this.last.setNext(newSubscription);
			newSubscription.setPrev(this.last);
		}
		
		newSubscription.setNext(null);
		this.last = newSubscription;
		this.nSubscriptions++;
				
		return 0;
	}
	
	private int addSubscriptionAt (TracingServiceSubscription newSubscription, int position) {
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
