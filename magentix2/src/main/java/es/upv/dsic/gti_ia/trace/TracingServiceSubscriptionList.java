package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.trace.TraceManager;

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
		
		public TracingServiceSubscription getTSSubscription(){
			return this.TSSubscription;
		}
		
		public TSS_Node getPrev(){
			return this.prev;
		}
		
		public TSS_Node getNext(){
			return this.next;
		}
	}
	
	private TSS_Node first;
	private TSS_Node last;
	private int length;
	private AgentID ownerAid;
	
	public TracingServiceSubscriptionList (){
		this.first = null;
		this.last = null;
		this.length = 0;
		this.ownerAid=null;
	}
	
	public TracingServiceSubscriptionList (AgentID owner){
		this.first = null;
		this.last = null;
		this.length = 0;
		this.ownerAid=owner;
	}
	
	public TSS_Node getFirst(){
		return this.first;
	}
	
	public TSS_Node getLast(){
		return this.last;
	}
	
	public int getLength(){
		return this.length;
	}
	
	public AgentID getOwnerAid(){
		return this.ownerAid;
	}
	
	private TSS_Node getTSS_NodeByAidAndTServiceName(AgentID subscriptorAid, AgentID originAid, String serviceName){
		int i;
		TSS_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if ((node.getTSSubscription().getSubscriptor().getAid().equals(subscriptorAid)) &&
				(node.getTSSubscription().getOriginEntity().getAid().equals(originAid)) &&
				 node.getTSSubscription().getTracingService().getName().contentEquals(serviceName)){
				return node;
			}
		}
		
		return null;
	}
	
	private TracingServiceSubscription getTSSByAidAndTServiceName(AgentID subscriptorAid, AgentID originAid, String serviceName){
		int i;
		TSS_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if ((node.getTSSubscription().getSubscriptor().getAid().equals(subscriptorAid)) &&
				(node.getTSSubscription().getOriginEntity().getAid().equals(originAid)) &&
					node.getTSSubscription().getTracingService().getName().contentEquals(serviceName)){
				return node.getTSSubscription();
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
	 * 		AgentID of the origin tracing entity
	 * 
	 * @param name
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
		if (this.getTSSByAidAndTServiceName(subscriptorAid, providerAid, serviceName) != null){
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
	 * @param originEntity
	 * 		Tracing entity which provides the tracing service
	 * 
	 * @param service
	 * 		Tracing Service 
	 * 
	 * @return 0
	 * 		Success: The new tracing service subscription has been added at
	 * 			the end of the list
	 * @return -1
	 * 		Duplicate AgentID: A subscription to the tracing service provided by the specified
	 * 			origin entity already exists in the list
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
	public int addTSS(TracingEntity subscriptorEntity, TracingEntity originEntity, TracingService service){
		TSS_Node tss;
		
		if (this.length < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.length == 0) {
			tss = new TSS_Node(subscriptorEntity, originEntity, service);
			this.first=tss;
		}
		else if (this.existsTSS(subscriptorEntity.getAid(), originEntity.getAid(), service.getName())) {
			return -1;
		}
		else {
			tss = new TSS_Node(subscriptorEntity, originEntity, service);
			this.last.setNext(tss);
			tss.setPrev(this.last);
		}
		
		tss.setNext(null);
		this.last = tss;
		this.length++;
				
		return 0;
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
		else if (this.existsTSS(newSubscription.getSubscriptor().getAid(), newSubscription.getOriginEntity().getAid(), newSubscription.getTracingService().getName())) {
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
	
	public int removeAllTSSFromProvider(AgentID providerAid){
		int i;
		TSS_Node node, removed_node;
//		int removed=0;
		TraceEvent tEvent;
		String receiver;
		
		if (this.getLength() == 1){
			if (this.getFirst().getTSSubscription().getOriginEntity().getAid().equals(providerAid)){
				// Only one provider -> Remove all subscriptions
				for (i=0, node=this.first; i < this.length; i++){
					removed_node=node;
					node=node.getNext();
					receiver=removed_node.getTSSubscription().getSubscriptor().getAid().toString();
					removed_node.setNext(null);
					removed_node.setPrev(null);
					removed_node.setTSSubscription(null);
//					removed++;
					tEvent = new TraceEvent(TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName(),
							new TracingEntity(TracingEntity.AGENT, this.getOwnerAid()),
							TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
							removed_node.getTSSubscription().getTracingService().getName() + "#" + providerAid.toString());
			    	TraceManager.sendSystemTraceEvent(tEvent, receiver);
				}
				this.first=null;
				this.last=null;
				this.length=0;
				return 0;
			}
			else{
				// Error: The origin entity does not exist in the list
				return -2;
			}
		}
		else{
			for (i=0, node=this.first; i < this.length; i++){
				if ((node.getTSSubscription().getOriginEntity().getAid().equals(providerAid))){
					removed_node=node;
					node=node.getNext();
					receiver=removed_node.getTSSubscription().getSubscriptor().getAid().toString();
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
//					removed++;
					
					tEvent = new TraceEvent(TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName(),
							new TracingEntity(TracingEntity.AGENT, this.getOwnerAid()),
							TracingService.DI_TracingServices[TracingService.UNAVAILABLE_TS].getName() + "#" +
							removed_node.getTSSubscription().getTracingService().getName() + "#" + providerAid.toString());
			    	TraceManager.sendSystemTraceEvent(tEvent, receiver);
			    	
			    	removed_node.setNext(null);
					removed_node.setPrev(null);
					removed_node.setTSSubscription(null);
				}
				else{
					node=node.getNext();
				}
			}
			return 0;
		}
	}
	
}
