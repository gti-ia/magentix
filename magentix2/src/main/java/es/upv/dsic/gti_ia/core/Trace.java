package es.upv.dsic.gti_ia.core;

public class Trace {
	
	private class ServiceProvider {
		private AgentID aid;
		private ServiceProvider next;
		
		public ServiceProvider () {
			this.aid=null;
			this.next=null;
		}
		
		public ServiceProvider (AgentID aid) {
			this.aid=aid;
			this.next=null;
		}
		
		public void setAID (AgentID aid) {
			this.aid=aid;
		}
		
		public void setNext(ServiceProvider nextProvider) {
			this.next=nextProvider;
		}
		
		public AgentID getAID () {
			return this.aid;
		}
		
		public ServiceProvider getNext() {
			return this.next;
		}
	}
	
	private class ServiceProviderList {
		private ServiceProvider first;
		private ServiceProvider last;
		private int nproviders;
		
		public ServiceProviderList () {
			this.first=null;
			this.last=null;
			this.nproviders=0;
		}
		
		public ServiceProviderList (ServiceProvider provider) {
			this.first=provider;
			this.last=provider;
			this.nproviders=1;
		}
		
		public ServiceProvider getFirst () {
			return this.first;
		}
		
		public ServiceProvider getLast () {
			return this.last;
		}
		
		public int getNProviders () {
			return this.nproviders;
		}
		
		public int addProvider (ServiceProvider newProvider) {
			// Returns position in which it was inserted
			this.getLast().setNext(newProvider);
			newProvider.setNext(null);
			this.last=newProvider;
			this.nproviders++;
			
			return this.nproviders-1;
		}
		
		public int addProviderAt (ServiceProvider newProvider, int position) {
			// position goes from 0 to (nproviders-1)
			int i;
			ServiceProvider sp;
			
			if (position > this.nproviders) {
				// Bad position
				return -1;
			}
			
			for (i=0, sp=this.getFirst(); i < position; i++, sp=sp.getNext());
			
			newProvider.setNext(sp.getNext());
			sp.setNext(newProvider);
			
			if (position == 0) {
				this.first=newProvider;
			}
			else if (position == nproviders) {
				this.last=newProvider;
			}
			
			this.nproviders++;
			
			return position;
		}
	}
	
	private class TracingService {
		private String name;
		private String eventType;
		private ServiceProviderList providers;
		private TracingService next;
		
		public TracingService () {
			this.name = null;
			this.eventType = null;
			this.providers = null;
			this.next = null;
		}
		
		public TracingService (String serviceName, String eventType) {
			this.name=serviceName;
			this.eventType=eventType;
			this.providers=null;
			this.next = null;
		}
		
		public void setName(String name) {
			this.name=name;
		}
		
		public void setEventType (String eventType) {
			this.eventType=eventType;
		}
		
		public void setNext (TracingService ts) {
			this.next=ts;
		}
		
		public String getName () {
			return this.name;
		}
		
		public String getEventType () {
			return this.eventType;
		}
		
		public ServiceProviderList getProviders () {
			return this.providers;
		}
		
		public TracingService getNext (){
			return this.next;
		}
		
		public void addProvider (ServiceProvider provider) {
			this.providers.addProvider(provider);
		}
		
	}
	
	private class TracingServiceList {
		private TracingService first;
		private TracingService last;
		private int nTracingServices;
		
		public TracingServiceList () {
			this.first = null;
			this.last = null;
			this.nTracingServices = 0;
		}
		
		public TracingServiceList (TracingService tService) {
			this.first = tService;
			this.last = tService;
			this.nTracingServices = 1;
		}
		
		public int addTracingService (TracingService newService) {
			this.last.setNext(newService);
			this.last = newService;
			newService.next = null;
			this.nTracingServices++;
			
			return this.nTracingServices-1;
		}
		
		public int addTracingServiceAtPosition (TracingService newService, int position) {
			// position goes from 0 to (nproviders-1)
			int i;
			TracingService ts;
			
			if (position > this.nTracingServices) {
				// Bad position
				return -1;
			}
			
			for (i=0, ts=this.getFirst(); i < position; i++, ts=ts.getNext());
			
			newService.setNext(ts.getNext());
			ts.setNext(newService);
			
			if (position == 0) {
				this.first=newService;
			}
			else if (position == nTracingServices) {
				this.last=newService;
			}
			
			this.nTracingServices++;
			
			return position;
		}
		
		public TracingService getFirst () {
			return this.first;
		}
		
		public TracingService getLast () {
			return this.last;
		}
		
		public int getNTracingServices () {
			return this.nTracingServices;
		} 
	}
	
	private class TracingServiceSubscription {
		AgentID subscriptor;
		TracingService tracingService;
		TracingServiceSubscription next;
		
		public void TracingServiceSubscription () {
			this.subscriptor = null;
			this.tracingService = null;
			this.next = null;
		}
		
		public void TracingServiceSubscription (AgentID subscriptor, TracingService tracingService) {
			this.subscriptor = subscriptor;
			this.tracingService = tracingService;
			this.next = null;
		}
		
		public void setSubscriptor (AgentID aid) {
			this.subscriptor = aid;
		}
		
		public void setTracingService (TracingService service) {
			this.tracingService = service;
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
		
		public TracingServiceSubscription getNext () {
			return this.next;
		}
	}
	
	private class TracingServiceSubscriptionList {
		private TracingServiceSubscription first;
		private TracingServiceSubscription last;
		private int nsubscriptions;
		
		public void TracingServiceSubscriptionList (){
			this.first = null;
			this.last = null;
			this.nsubscriptions = 0;
		}
		
		public void TracingServiceSubscriptionList (TracingServiceSubscription subscription) {
			this.first = subscription;
			this.last = subscription;
			this.nsubscriptions = 1;
		}
		
		public TracingServiceSubscription getFirst () {
			return this.first;
		}
		
		public TracingServiceSubscription getLast () {
			return this.last;
		}
		
		public int getNSubscriptions () {
			return this.nsubscriptions;
		}
		
		public int addSubscription (TracingServiceSubscription newSubscription) {
			// Returns position in which it was inserted
			this.getLast().setNext(newSubscription);
			newSubscription.setNext(null);
			this.last=newSubscription;
			this.nsubscriptions++;
			
			return this.nsubscriptions-1;
		}
		
		public int addSubscriptionAt (TracingServiceSubscription newSubscription, int position) {
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
	
	private class TracingServiceAuthorization {
		
	}
	
	private class TracingServiceAuthorizationGraph {
		
	}
	
	private static final int MAX_DI_SERVICES = 10;
	private static final TracingService[] DI_Tracing_Services = new TracingService [MAX_DI_SERVICES];
	
	static {
		DI_Tracing_Services[0] = new TracingService();
//		DI_Tracing_Services [0] = {"TRACE_ERROR", "TRACE_ERROR", null, DI_Tracing_Services[1]};
	}
	
	private TracingServiceList tracingServices;
	
	private TracingServiceSubscriptionList subscriptions;
	
	public Trace (){
		tracingServices = new TracingServiceList();
		
		
	}
	
}
