package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Double linked list of Tracing Entities
 * identified by their AgentID  
 */
public class TracingEntityList {

	private class TE_Node {
		private AgentID aid;
		private TE_Node prev;
		private TE_Node next;
		
		public TE_Node(){
			this.aid=null;
			this.prev=null;
			this.next=null;
		}
		
		public TE_Node(AgentID aid){
			this.aid=aid;
			this.prev=null;
			this.next=null;
		}
		
		public void setAid(AgentID aid){
			this.aid = aid;
		}
		
		public void setNext(TE_Node next){
			this.next = next;
		}
		
		public void setPrev(TE_Node prev){
			this.prev = prev;
		}
		
		public AgentID getAid(){
			return this.aid;
		}
		
		public TE_Node getPrev(){
			return this.prev;
		}
		
		public TE_Node getNext(){
			return this.next;
		}
	}

	private TE_Node first;
	private TE_Node last;
	private int length;
	
	public TracingEntityList(){
		this.first=null;
		this.last=null;
		this.length=0;
	}
	
	private void setFirst(TE_Node first){
		this.first=first;
	}
	
	private void setLast(TE_Node last){
		this.last=last;
	}
	
	private TE_Node getFirst(){
		return this.first;
	}
	
	private TE_Node getLast(){
		return this.last;
	}
	
	private TE_Node getTEByAid(AgentID aid){
		int i;
		TE_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if (node.getAid().equals(aid)){
				return node;
			}
		}
		
		return null;
	}
	
	/**
	 * Determines if a tracing entity already exists in the list
	 * 
	 * @param aid
	 * 		AgentID of the tracing entity
	 * 
	 * @return true
	 * 		A tracing entity with the specified AgentID
	 * 		exists in the list.
	 * @return false
	 * 		It does not exists a tracing entity with
	 * 		that AgentID in the list.
	 */
	public boolean existsTE(AgentID aid){
		if (this.getTEByAid(aid) != null){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Add a new TE with the specified AgentID to the list
	 * @param aid
	 * 		AgentID of the tracing entity which has to be added
	 * @return 0
	 * 		Success: The new tracing entity has been added at
	 * 			the end of the list
	 * @return -1
	 * 		Duplicate AgentID: A tracing entity with the specified
	 * 			AgentID already exists in the list
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
	public int addTE(AgentID aid){
		TE_Node te;
		
		if (this.length < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.length == 0) {
			te = new TE_Node(aid);
			this.first=te;
		}
		else if (this.existsTE(aid)) {
			return -1;
		}
		else {
			te = new TE_Node(aid);
			this.last.setNext(te);
			te.setPrev(this.last);
		}
		
		te.setNext(null);
		this.last = te;
		this.length++;
				
		return 0;
	}
	
	/**
	 * Remove the TE with the specified AgentID from the list
	 * @param aid
	 * 		AgentID of the tracing entity which has to be removed
	 * @return 0
	 * 		Success: The new tracing entity has been added at
	 * 			the end of the list
	 * @return -1
	 * 		AgentID not found
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
	public int removeTE(AgentID aid){
		TE_Node te;
		
		if ((te=this.getTEByAid(aid)) == null){
			// Service provider does not exist
			return -1;
		}
		else{
			if (te.getPrev() == null){
				// te is the first in the list
				if (this.length == 1){
					// Empty the list
					this.first=null;
					this.last=null;
				}
				else{
					te=this.first;
					this.first=te.getNext();
					te.setNext(null);
					this.first.setPrev(null);
				}
			}
			else if (te.getNext() == null){
				// te is the last provider in the list
				te=this.last;
				this.last=te.getPrev();
				this.last.setNext(null);
				te.setPrev(null);
			}
			else{
				te.getPrev().setNext(te.getNext());
				te.getNext().setPrev(te.getPrev());
				te.setPrev(null);
				te.setNext(null);
			}
		}
		
		this.length--;
		return 0;
	}
}
