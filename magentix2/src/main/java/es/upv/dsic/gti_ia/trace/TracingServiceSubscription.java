package es.upv.dsic.gti_ia.trace;

public class TracingServiceSubscription {
	private TracingEntity originEntity;
	private TracingService tracingService;
	
	public TracingServiceSubscription () {
		this.originEntity=null;
		this.tracingService = null;
	}
	
	public TracingServiceSubscription (TracingEntity originEntity, TracingService tracingService) {
		this.originEntity=originEntity;
		this.tracingService = tracingService;
	}
	
	public TracingEntity getOriginEntity () {
		return this.originEntity;
	}
	
	public TracingService getTracingService () {
		return this.tracingService;
	}
}
