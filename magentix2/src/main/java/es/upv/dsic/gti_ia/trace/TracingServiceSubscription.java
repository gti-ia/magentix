package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

public class TracingServiceSubscription {
	AgentID subscriptor;
	TracingService tracingService;
	TracingServiceSubscription prev;
	TracingServiceSubscription next;
	
	public void TracingServiceSubscription () {
		this.subscriptor = null;
		this.tracingService = null;
		this.prev=null;
		this.next = null;
	}
	
	public void TracingServiceSubscription (AgentID subscriptor, TracingService tracingService) {
		this.subscriptor = subscriptor;
		this.tracingService = tracingService;
		this.prev=null;
		this.next = null;
	}
	
	public void setSubscriptor (AgentID aid) {
		this.subscriptor = aid;
	}
	
	public void setTracingService (TracingService service) {
		this.tracingService = service;
	}
	
	public void setPrev (TracingServiceSubscription subscription) {
		this.prev = subscription;
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
	
	public TracingServiceSubscription getPrev () {
		return this.prev;
	}
	
	public TracingServiceSubscription getNext () {
		return this.next;
	}
}
