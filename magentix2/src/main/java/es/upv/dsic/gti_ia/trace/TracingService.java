package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

public class TracingService {
	private String name;
	private String eventType;
	private String description;
	private TracingServiceSubscriptionList subscriptorsAll;
	private TracingEntityList providers;
	private TracingService prev;
	private TracingService next;
	
	public TracingService () {
		this.name = null;
		this.eventType = null;
		this.subscriptorsAll = null;
		this.providers = new TracingEntityList();
		this.prev=null;
		this.next = null;
	}
	
	public TracingService (String serviceName, String eventType, String description) {
		this.name=serviceName;
		this.eventType=eventType;
		this.description=description;
		this.subscriptorsAll = null;
		this.providers=new TracingEntityList();
		this.prev=null;
		this.next = null;
	}
	
	private void newSystemTracingService (String serviceName, String eventType, String description) {
		this.name=serviceName;
		this.eventType=eventType;
		this.description=description;
		this.subscriptorsAll = null;
		this.providers=new TracingEntityList();
		this.prev=null;
		this.next = null;
	}
	
	public void setName(String name) {
		this.name=name;
	}
	
	public void setEventType (String eventType) {
		this.eventType=eventType;
	}
	
	public void setDescription (String description) {
		this.description=description;
	}
	
	public void setPrev (TracingService ts) {
		this.prev=ts;
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
	
	public String getDescription () {
		return this.description;
	}
	
	public TracingServiceSubscriptionList getSubscriptorsAll () {
		return this.subscriptorsAll;
	}
	
	public TracingEntityList getProviders () {
		return this.providers;
	}
	
	public TracingService getPrev (){
		return this.prev;
	}
	
	public TracingService getNext (){
		return this.next;
	}
	
	/**
	 * Add a service provider to the service provider list
	 * of the tracing service.
	 * The service provider has to be unique within the list, otherwise,
	 * it will not be added and the method will return error.
	 * 
	 * The new service provider is added at the end of the list.
	 * 
	 * Return values:
	 *   - SUCCESS: Method returns 0
	 *   - DUPLICATE PROVIDER: Method returns -1
	 *   - OTHER ERROR: Method returns -2
	 */
	public int addProvider (TracingEntity provider) {
		return this.providers.addTE(provider);
	}
	
	/**
	 * Add a service provider to the service provider list
	 * of the tracing service.
	 * The service provider has to be unique within the list, otherwise,
	 * it will not be added and the method will return error.
	 * 
	 * The new service provider is added at the end of the list.
	 * 
	 * Return values:
	 *   - SUCCESS: Method returns 0
	 *   - DUPLICATE PROVIDER: Method returns -1
	 *   - OTHER ERROR: Method returns -2
	 */
	public int addProvider (AgentID aid) {
		return this.providers.addTE(aid);
	}
	
	public int removeProvider (AgentID provider){
		return this.providers.removeTE(provider);
	}
	
	public int addSubscriptionAll (TracingServiceSubscription newSubscription) {
		return this.subscriptorsAll.addSubscription(newSubscription);
	}
	
	public int addSubscriptionAll (AgentID subscriptor){
		return this.subscriptorsAll.addSubscription(subscriptor, this.name);
	}
	
	public int addSubscription (String tracingService, AgentID provider) {
		TracingEntity te;
		
		if ((te=this.providers.getTEByAid(provider)) == null){
			// Provider not found
			return -1;
		}
		else {
			return 0;
		}
	}
	
	public int addSubscription (String tracingService) {
		return 0;
	}
}
