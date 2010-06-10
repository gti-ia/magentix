package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TracingService;

public class TracingServiceSubscriptionList {
	private class TSS_Node {
		private TracingServiceSubscription TSSubscription;
		private TSS_Node prev;
		private TSS_Node next;
		
		public TSS_Node(){
			this.TSSubscription=null;
			this.prev=null;
			this.next=null;
		}
		
		public TSS_Node(TracingServiceSubscription tss){
			this.TSSubscription=tss;
			this.prev=null;
			this.next=null;
		}
		
		public TSS_Node(TracingEntity subscriptorEntity, TracingEntity originEntity, TracingService tService){
			this.TSSubscription = new TracingServiceSubscription(subscriptorEntity, originEntity, tService);
			this.prev=null;
			this.next=null;
		}
		
		public void setNext(TSS_Node next){
			this.next = next;
		}
		
		public void setPrev(TSS_Node prev){
			this.prev = prev;
		}
		
		public void setTSSubscription(TracingServiceSubscription TSSubscription){
			this.TSSubscription=TSSubscription;
		}
		
		public TSS_Node getPrev(){
			return this.prev;
		}
		
		public TSS_Node getNext(){
			return this.next;
		}
		
		public TracingServiceSubscription getTSSubscription(){
			return this.TSSubscription;
		}
	}
	
	private TSS_Node first;
	private TSS_Node last;
	private int length;
	
	public TracingServiceSubscriptionList (){
		this.first = null;
		this.last = null;
		this.length = 0;
	}
	
	public TracingServiceSubscriptionList (AgentID owner){
		this.first = null;
		this.last = null;
		this.length = 0;
	}
	
	public TSS_Node getFirst(){
		return this.first;
	}
	
	public TracingServiceSubscription getFirstSubscription(){
		return this.first.getTSSubscription();
	}
	
	public TSS_Node getLast(){
		return this.last;
	}
	
	public TracingServiceSubscription getLastSubscription(){
		return this.last.getTSSubscription();
	}
	
	public int getLength(){
		return this.length;
	}
	
	private TSS_Node getTSS_Node(AgentID subscriptorAid, AgentID originAid, String serviceName){
		int i;
		TSS_Node node;
		
		if (originAid != null){
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if ((node.getTSSubscription().getSubscriptorEntity().getAid().equals(subscriptorAid)) &&
					(node.getTSSubscription().getOriginEntity().getAid().equals(originAid)) &&
					 node.getTSSubscription().getTracingService().getName().contentEquals(serviceName)){
					
					return node;
				}
			}
		}
		else{
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if ((node.getTSSubscription().getSubscriptorEntity().getAid().equals(subscriptorAid)) &&
					 node.getTSSubscription().getAnyProvider() &&
					 node.getTSSubscription().getTracingService().getName().contentEquals(serviceName)){
					
					return node;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Obtain a tracing service subscription in the list
	 * @param subscriptorAid
	 * 		Aid of the agent which is subscribed
	 * @param originAid
	 * 		Aid of the origin agent. A null value is interpreted as an "any" subscription
	 * @param serviceName
	 * 		Name of the tracing service
	 * @return
	 * 		The corresponding TracingServiceSusbscription in case it exists
	 * 		or null otherwise
	 */
	public TracingServiceSubscription getTSS(AgentID subscriptorAid, AgentID originAid, String serviceName){
		int i;
		TSS_Node node;
		
		if (originAid != null){
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if ((node.getTSSubscription().getSubscriptorEntity().getAid().equals(subscriptorAid)) &&
					(node.getTSSubscription().getOriginEntity().getAid().equals(originAid)) &&
					 node.getTSSubscription().getTracingService().getName().contentEquals(serviceName)){
					
					return node.getTSSubscription();
				}
			}
		}
		else{
			for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
				if ((node.getTSSubscription().getSubscriptorEntity().getAid().equals(subscriptorAid)) &&
					 node.getTSSubscription().getAnyProvider() &&
					 node.getTSSubscription().getTracingService().getName().contentEquals(serviceName)){
					return node.getTSSubscription();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Determines if a tracing service subscription already exists in the list
	 * 
	 * @param subscriptorAid
	 * 		AgentID of the subscriptor entity
	 * 
	 * @param providerAid
	 * 		AgentID of the origin tracing entity. A null value is
	 * 		interpreted as an "any" subscription
	 * 
	 * @param serviceName
	 * 		Tracing service name
	 * 
	 * @return true
	 * 		A subscription for the specified tracing service provided by the
	 * 		specified origin entity exists in the list
	 * 
	 * @return false
	 * 		It does not exists a subscription to the specified tracing service
	 * 		provided the specified origin entity.
	 */
	public boolean existsTSS(AgentID subscriptorAid, AgentID providerAid, String serviceName){
		if (this.getTSS(subscriptorAid, providerAid, serviceName) != null){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Add a new TSS for the specified tracing service provided by the specified
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
	public int addTSS(TracingEntity subscriptorEntity, TracingEntity originEntity, TracingService service){
		TSS_Node tss;
		
		if (this.length < 0){
			// Error mucho gordo
			return -1;
		}
		else if (this.length == 0) {
			tss = new TSS_Node(subscriptorEntity, originEntity, service);
			this.first=tss;
			return 0;
		}
		else{
			if (this.existsTSS(subscriptorEntity.getAid(), originEntity.getAid(), service.getName())){
				return -2;
			}
			else{
				tss = new TSS_Node(subscriptorEntity, originEntity, service);
				this.last.setNext(tss);
				tss.setPrev(this.last);
				return 0;
			}
		}
	}
	
	/**
	 * Add a new TSS to the list
	 * @param newTracingServiceSubscription
	 * 		TracingServiceSubscription to be added to the list
	 * @return 0
	 * 		Success: The new subscription has been added at
	 * 			the end of the list
	 * @return -1
	 * 		Duplicate subscription: A tracing entity with the specified
	 * 			origin entity and tracing service already exists in the list
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
	public int addTSS(TracingServiceSubscription newSubscription){
		TSS_Node tss_node;
		
		if (this.length < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.length == 0) {
			tss_node = new TSS_Node(newSubscription);
			this.first=tss_node;
		}
		else if (this.existsTSS(newSubscription.getSubscriptorEntity().getAid(), newSubscription.getOriginEntity().getAid(), newSubscription.getTracingService().getName())) {
			return -1;
		}
		else {
			tss_node = new TSS_Node(newSubscription);
			this.last.setNext(tss_node);
			tss_node.setPrev(this.last);
		}
		
		tss_node.setNext(null);
		this.last = tss_node;
		this.length++;
				
		return 0;
	}
	
	public int removeFirstTSS(){
		TSS_Node tss_node;
		
		if (this.length < 0){
			// This should never happen
			return -1;
		}
		else if (this.length == 0){
			// The list is empty
			return -2;
		}
		else{
			tss_node=this.first;
			this.first=tss_node.getNext();
			this.first.setPrev(null);
			tss_node.setNext(null);
			tss_node.TSSubscription=null;
			
			return 0;
		}
	}
	
	/**
	 * Remove the specified Subscription from the list
	 * 
	 * @param aid
	 * 		AgentID of the origin entity which provides the tracing service
	 * 
	 * @param serviceName
	 * 		Name of the tracing service in the subscription
	 * 
	 * @return 0
	 * 		Success: The tracing service subscription has been removed from
	 * 			the end of the list
	 * 
	 * @return -1
	 * 		Subscription not found
	 * 
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
//	public int removeTSS(AgentID aid, String serviceName){
//		TSS_Node tss;
//		
//		if ((tss=this.getTSS_NodeByAidAndTServiceName(aid, serviceName)) == null){
//			// Service provider does not exist
//			return -1;
//		}
//		else{
//			if (tss.getPrev() == null){
//				// tss is the first in the list
//				if (this.length == 1){
//					// Empty the list
//					this.first=null;
//					this.last=null;
//				}
//				else{
//					tss=this.first;
//					this.first=tss.getNext();
//					tss.setNext(null);
//					this.first.setPrev(null);
//				}
//			}
//			else if (tss.getNext() == null){
//				// tss is the last provider in the list
//				tss=this.last;
//				this.last=tss.getPrev();
//				this.last.setNext(null);
//				tss.setPrev(null);
//			}
//			else{
//				tss.getPrev().setNext(tss.getNext());
//				tss.getNext().setPrev(tss.getPrev());
//				tss.setPrev(null);
//				tss.setNext(null);
//			}
//		}
//		
//		this.length--;
//		return 0;
//	}
	
	public TracingServiceSubscriptionList getAllTSSFromProvider(AgentID providerAid){
		int i;
		TSS_Node node;
		
		TracingServiceSubscriptionList returnList = new TracingServiceSubscriptionList();
		
		for (i=0, node=this.getFirst(); i < this.length; i++, node=node.next){
			if ((node.getTSSubscription().getOriginEntity().getAid().equals(providerAid)) ||
				node.getTSSubscription().getAnyProvider()){
				returnList.addTSS(node.getTSSubscription());
			}
		}
		
		return returnList;
	}
	
	public TracingServiceSubscriptionList getAllTSSFromSubscriptor(AgentID providerAid){
		int i;
		TSS_Node node;
		
		TracingServiceSubscriptionList returnList = new TracingServiceSubscriptionList();
		
		for (i=0, node=this.getFirst(); i < this.length; i++, node=node.next){
			if (node.getTSSubscription().getSubscriptorEntity().getAid().equals(providerAid)){
				returnList.addTSS(node.getTSSubscription());
			}
		}
		
		return returnList;
	}
	
	public TracingServiceSubscriptionList removeAllTSSFromProvider(AgentID providerAid){
		int i;
		TSS_Node node, removed_node;
		
		TracingServiceSubscriptionList returnList = new TracingServiceSubscriptionList();
		
		if (this.getLength() == 1){
			if (this.getFirst().getTSSubscription().getOriginEntity().getAid().equals(providerAid)){
				// Only one provider -> Remove all subscriptions
				for (i=0, node=this.first; i < this.length; i++){
					returnList.addTSS(node.TSSubscription);
					removed_node=node;
					node=node.getNext();
					removed_node.setNext(null);
					removed_node.setPrev(null);
					removed_node.setTSSubscription(null);
				}
				this.first=null;
				this.last=null;
				this.length=0;
				return returnList;
			}
			else{
				// Error: The origin entity does not exist in the list
				return null;
			}
		}
		else{
			for (i=0, node=this.first; i < this.length; i++){
				if ((node.getTSSubscription().getOriginEntity().getAid().equals(providerAid))){
					returnList.addTSS(node.TSSubscription);
					removed_node=node;
					node=node.getNext();
					if (removed_node.getPrev() == null){
						// removed_node is the first in the list
						if (this.length == 1){
							// Empty the list
							this.first=null;
							this.last=null;
						}
						else{
							this.first=removed_node.getNext();
							this.first.setPrev(null);
						}
					}
					else if (removed_node.getNext() == null){
						// removed_node is the last provider in the list
						this.last=removed_node.getPrev();
						this.last.setNext(null);
					}
					else{
						removed_node.getPrev().setNext(removed_node.getNext());
						removed_node.getNext().setPrev(removed_node.getPrev());
					}
					this.length--;
			    	
			    	removed_node.setNext(null);
					removed_node.setPrev(null);
					removed_node.setTSSubscription(null);
				}
				else{
					node=node.getNext();
				}
			}
			return returnList;
		}
	}
}
