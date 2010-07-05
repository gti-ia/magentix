package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.TracingService;

public class TracingServiceSubscription {
	private TracingEntity subscriptorEntity;
	private boolean any_provider;
	private TracingEntity originEntity;
	private TracingService tracingService;
	
	public TracingServiceSubscription () {
		this.subscriptorEntity=null;
		this.any_provider=false;
		this.originEntity=null;
		this.tracingService = null;
	}
	
	public TracingServiceSubscription (TracingEntity subscriptor, TracingEntity originEntity, TracingService tracingService) {
		this.subscriptorEntity=subscriptor;
		if (originEntity == null){
			this.any_provider=true;
		}
		else{
			this.originEntity=originEntity;
			this.any_provider=false;
		}
		this.tracingService = tracingService;
	}
	
	public TracingEntity getSubscriptorEntity(){
		return this.subscriptorEntity;
	}
	
	public boolean getAnyProvider(){
		return this.any_provider;
	}
	
	public TracingEntity getOriginEntity () {
		return this.originEntity;
	}
	
	public TracingService getTracingService () {
		return this.tracingService;
	}
}
