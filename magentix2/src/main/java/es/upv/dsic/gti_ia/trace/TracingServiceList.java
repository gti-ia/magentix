package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.TracingService;

public class TracingServiceList {
	
	private class TS_Node {
		private TracingService TService;
		private TS_Node prev;
		private TS_Node next;
		
		public TS_Node(){
			this.TService=null;
			this.prev=null;
			this.next=null;
		}
		
		public TS_Node(TracingService ts){
			this.TService=ts;
			this.prev=null;
			this.next=null;
		}
		
		public void setNext(TS_Node next){
			this.next = next;
		}
		
		public void setPrev(TS_Node prev){
			this.prev = prev;
		}
		
		public TracingService getTService(){
			return this.TService;
		}
		
		public TS_Node getPrev(){
			return this.prev;
		}
		
		public TS_Node getNext(){
			return this.next;
		}
	}
	
	private TS_Node first;
	private TS_Node last;
	private int length;
	
	public TracingServiceList () {
		this.first = null;
		this.last = null;
		this.length = 0;
	}
	
	public TS_Node getFirst () {
		return this.first;
	}
	
	public TS_Node getLast () {
		return this.last;
	}
	
	public int getLength () {
		return this.length;
	}
	
	private TS_Node getTS_NodeByName(String name){
		int i;
		TS_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if (node.getTService().getName().contentEquals(name)){
				return node;
			}
		}
		
		return null;
	}
	
	public TracingService getTSByName(String name){
		int i;
		TS_Node node;
		
		for (i=0, node=this.first; i < this.length; i++, node=node.getNext()){
			if (node.getTService().getName().contentEquals(name)){
				return node.getTService();
			}
		}
		
		return null;
	}
	
	/**
	 * Determines if a tracing service with that name already exists in the list
	 * 
	 * @param name
	 * 		Name of the tracing service
	 * 
	 * @return true
	 * 		A tracing service with the specified name
	 * 		exists in the list.
	 * 
	 * @return false
	 * 		It does not exists a tracing service with
	 * 		that name in the list.
	 */
	public boolean existsTSByName(String name){
		if (this.getTSByName(name) != null){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Determines if a tracing service already exists in the list
	 * 
	 * @param tService
	 * 		TracingService
	 * 
	 * @return true
	 * 		A tracing service with the specified name
	 * 		exists in the list.
	 * 
	 * @return false
	 * 		It does not exists a tracing service with
	 * 		that name in the list.
	 */
	public boolean existsTS(TracingService tService){
		TracingService ts;
		if ((ts=this.getTSByName(tService.getName())) != null){
			if (ts.getDescription().contentEquals(tService.getDescription())){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Add a new TS to the list
	 * 
	 * @param newTracingService
	 * 		TracingService to be added to the list
	 * 
	 * @return	0
	 * 		Success: The new tracing service has been added at
	 * 			the end of the list
	 * 
	 * @return -1
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 * 
	 * @return -2
	 * 		Duplicate name: A tracing service with the specified
	 * 			name already exists in the list
	 */
	public int addTS(TracingService newTracingService){
		TS_Node ts_node;
		
		if (this.length < 0){
			// Error mucho gordo
			return -1;
		}
		else if (this.length == 0) {
			ts_node = new TS_Node(newTracingService);
			this.first=ts_node;
		}
		else if (this.existsTS(newTracingService)) {
			return -2;
		}
		else {
			ts_node = new TS_Node(newTracingService);
			this.last.setNext(ts_node);
			ts_node.setPrev(this.last);
		}
		
		ts_node.setNext(null);
		this.last = ts_node;
		this.length++;
				
		return 0;
	}
	
	/**
	 * Remove the TS with the specified name from the list
	 * 
	 * @param name
	 * 		Name of the tracing service which has to be removed
	 * 
	 * @return 0
	 * 		Success: The tracing service has been removed from
	 * 			the end of the list
	 * 
	 * @return -2
	 * 		Name not found
	 */
	public int removeTS(String name){
		TS_Node ts;
		
		if ((ts=this.getTS_NodeByName(name)) == null){
			// Tracing service does not exist
			return -2;
		}
		else{
			if (ts.getPrev() == null){
				// ts is the first in the list
				if (this.length == 1){
					// Empty the list
					this.first=null;
					this.last=null;
				}
				else{
					ts=this.first;
					this.first=ts.getNext();
					ts.setNext(null);
					this.first.setPrev(null);
				}
			}
			else if (ts.getNext() == null){
				// ts is the last provider in the list
				ts=this.last;
				this.last=ts.getPrev();
				this.last.setNext(null);
				ts.setPrev(null);
			}
			else{
				ts.getPrev().setNext(ts.getNext());
				ts.getNext().setPrev(ts.getPrev());
				ts.setPrev(null);
				ts.setNext(null);
			}
		}
		
		this.length--;
		return 0;
	}
	
	/**
	 * List all tracing services in the list 
	 * 
	 * @return A string with all tracing services in the list, separated by '\n'
	 * 
	 * @return null
	 */
	public String listAllTracingServices () {
		String list = new String();
		int i;
		TS_Node ts_node;
				
		for (i=0, ts_node=this.getFirst(); (i < this.getLength()) && (ts_node.getNext() != null); i++, ts_node=ts_node.getNext()){
			list = list + ts_node.getTService().getName() + "\n";
		}
		
		//list = list + "\n";
		
		return list;
	}
	
	public int initializeWithDITracingServices(){
		int i;
		
		for (i=0; i < TracingService.MAX_DI_TS; i++){
			if (this.addTS(new TracingService(TracingService.DI_TracingServices[i].getName(),
					TracingService.DI_TracingServices[i].getDescription())) != 0){
				return -1;
			}
		}
		return 0;
	}
}
