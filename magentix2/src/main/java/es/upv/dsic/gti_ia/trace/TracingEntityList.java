package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Double linked list of Tracing Entities
 */
public class TracingEntityList {

	private class TE_Node {
		private TracingEntity TEntity;
		private TE_Node prev;
		private TE_Node next;
		
		public TE_Node(){
			this.TEntity=null;
			this.prev=null;
			this.next=null;
		}
		
		public TE_Node(TracingEntity te){
			this.TEntity=te;
			this.prev=null;
			this.next=null;
		}
		
		public TE_Node(AgentID aid){
			this.TEntity = new TracingEntity(TracingEntity.AGENT, aid);
			this.prev=null;
			this.next=null;
		}
		
		public void setNext(TE_Node next){
			this.next = next;
		}
		
		public void setPrev(TE_Node prev){
			this.prev = prev;
		}
		
		public TracingEntity getTEntity(){
			return this.TEntity;
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
//	private AgentID ownerAid; 
	
	public TracingEntityList(){
		this.first=null;
		this.last=null;
		this.length=0;
//		this.ownerAid=null;
	}
	
//	public TracingEntityList(AgentID owner){
//		this.first=null;
//		this.last=null;
//		this.length=0;
//		this.ownerAid=owner;
//	}
	
//	private void setFirst(TE_Node first){
//		this.first=first;
//	}
//	
//	private void setLast(TE_Node last){
//		this.last=last;
//	}
	
	public TracingEntity getFirst(){
		return this.first.getTEntity();
	}
	
	public TracingEntity getLast(){
		return this.last.getTEntity();
	}
	
	public int getLength(){
		return this.length;
	}
	
	private TE_Node getFirstNode(){
		return this.first;
	}
	
	private TE_Node getLastNode(){
		return this.last;
	}
	
//	public AgentID getOwnerAid(){
//		return this.ownerAid;
//	}
		
	private TE_Node getTE_NodeByAid(AgentID aid){
		int i;
		TE_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if (node.getTEntity().hasTheSameAidAs(aid)){
				return node;
			}
		}
		
		return null;
	}
	
	public TracingEntity getTEByAid(AgentID aid){
		int i;
		TE_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if (node.getTEntity().hasTheSameAidAs(aid)){
				return node.getTEntity();
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
	 * 
	 * @param aid
	 * 		AgentID of the tracing entity which has to be added
	 * 
	 * @return 0
	 * 		Success: The new tracing entity has been added at
	 * 			the end of the list
	 * 
	 * @return -1
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 * 
	 * @return -2
	 * 		Duplicate AgentID: A tracing entity with the specified
	 * 			AgentID already exists in the list

	 */
	public int addTE(AgentID aid){
		TE_Node te;
		
		if (this.length < 0){
			// Error mucho gordo
			return -1;
		}
		else if (this.length == 0) {
			te = new TE_Node(aid);
			this.first=te;
		}
		else if (this.existsTE(aid)) {
			return -2;
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
	 * Add a new TE to the list
	 * 
	 * @param newTracingEntity
	 * 		TracingEntity to be added to the list
	 * 
	 * @return 0
	 * 		Success: The new tracing entity has been added at
	 * 			the end of the list
	 * 
	 * @return -1
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 * 
	 * @return -2
	 * 		Duplicate AgentID: A tracing entity with the specified
	 * 			identifier already exists in the list
	 * 
	 * @return -3
	 * 		Tracing entity type not supported yet
	 */
	public int addTE(TracingEntity newTracingEntity){
		TE_Node te_node;
		
		if (newTracingEntity.getType() != TracingEntity.AGENT){
			// ARTIFACTS and AGGREGATIONS are not supported yet
			return -3;
		}
		
		if (this.length < 0){
			// Error mucho gordo
			return -1;
		}
		else if (this.length == 0) {
			te_node = new TE_Node(newTracingEntity);
			this.first=te_node;
		}
		else if (this.existsTE(newTracingEntity.getAid())) {
			return -2;
		}
		else {
			te_node = new TE_Node(newTracingEntity);
			this.last.setNext(te_node);
			te_node.setPrev(this.last);
		}
		
		te_node.setNext(null);
		this.last = te_node;
		this.length++;
				
		return 0;
	}
	
	/**
	 * Remove the TE with the specified AgentID from the list
	 * 
	 * @param aid
	 * 		AgentID of the tracing entity which has to be removed
	 * 
	 * @return 0
	 * 		Success: The tracing entity has been removed from
	 * 			the list
	 * 
	 * @return -2
	 * 		AgentID not found
	 */
	public int removeTE(AgentID aid){
		TE_Node te;
		
		if ((te=this.getTE_NodeByAid(aid)) == null){
			// Service provider does not exist
			return -2;
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
	
	/**
	 * List all tracing entities in the list
	 * 
	 * @return A string with all tracing entities in the list, separated by '\n'
	 * 		The format in which each tracing entity is returned is the following:
	 * 		(Tracing entity type) Entity identifier
	 * 
	 * @return null
	 */
	public String listAllTracingEntities () {
		String list = new String();
		int i;
		TE_Node te_node;
				
		for (i=0, te_node=this.getFirstNode(); (i < this.getLength()) && (te_node.getNext() != null); i++, te_node=te_node.getNext()){
			switch (te_node.getTEntity().getType()){
				case TracingEntity.AGENT:
					list = list + "(AGENT) " + te_node.getTEntity().getAid().toString() + "\n";
					break;
				case TracingEntity.ARTIFACT:
					break;
				case TracingEntity.AGGREGATION:
					break;
				default:
					// Unknown tracing entity type: This should never happen!
					return null;
			}
			
		}
		
		//list = list + "\n";
		
		return list;
	}
}
