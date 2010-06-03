package es.upv.dsic.gti_ia.trace;

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
	 * Determines if a tracing service already exists in the list
	 * 
	 * @param name
	 * 		Name of the tracing service
	 * 
	 * @return true
	 * 		A tracing service with the specified name
	 * 		exists in the list.
	 * @return false
	 * 		It does not exists a tracing service with
	 * 		that name in the list.
	 */
	public boolean existsTS(String name){
		if (this.getTSByName(name) != null){
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Add a new TS to the list
	 * @param newTracingService
	 * 		TracingService to be added to the list
	 * @return 0
	 * 		Success: The new tracing service has been added at
	 * 			the end of the list
	 * @return -1
	 * 		Duplicate name: A tracing service with the specified
	 * 			name already exists in the list
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
	public int addTS(TracingService newTracingService){
		TS_Node ts_node;
		
		if (this.length < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.length == 0) {
			ts_node = new TS_Node(newTracingService);
			this.first=ts_node;
		}
		else if (this.existsTS(newTracingService.getName())) {
			return -1;
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
	 * @param name
	 * 		Name of the tracing service which has to be removed
	 * @return 0
	 * 		Success: The tracing service has been removed from
	 * 			the end of the list
	 * @return -1
	 * 		Name not found
	 * @return -2
	 * 		Internal values of the list are not correct. There is
	 * 		something really wrong if this happens :-S
	 */
	public int removeTS(String name){
		TS_Node ts;
		
		if ((ts=this.getTS_NodeByName(name)) == null){
			// Tracing service does not exist
			return -1;
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
	
//	/**
//	 * Add a tracing service at the end of the tracing sevices list.
//	 * The AgentID of the service publisher has to be unique within the list, otherwise,
//	 * it will not be added and the method will return error.
//	 * 
//	 * @param publisher
//	 * 		AgentID of the agent which publishes the tracing service 
//	 * 
//	 * @param newService
//	 * 		TracingService object to add at the end of the list
//	 * 
//	 * @return 0
//	 * 		Success: Tracing service added.
//	 * 
//	 * @return 1
//	 * 		Success: The tracing service was already published by another provider.
//	 * 			The provider was added to the list of providers of the service.
//	 * 
//	 * @return -1
//	 * 		Error: Tracing service already published by the specified provider in the list.
//	 * 			No changes were made to the corresponding ServiceProviderList
//	 * 
//	 * @return -2
//	 * 		Error: Unknown error
//	 */
//	public int addTracingService (AgentID publisher, TracingService newService) {
//		TracingService existingService;
//		
//		if (this.nTracingServices < 0){
//			// Error mucho gordo
//			return -2;
//		}
//		else if (this.nTracingServices == 0) {
//			this.first=newService;
//			newService.setPrev(null);
//			if (publisher != null){
//				if (newService.addProvider(publisher) != 0){
//					// This should never happen!
//					return -2;
//				}
//			}
//		}
//		else if ((existingService=this.getServiceByName(newService.getName())) != null) {
//			if (existingService.addProvider(publisher) == 0){
//				// Add the provider to the tracing service
//				return 1;
//			}
//			else {
//				// The provider had already published the tracing service
//				return -1;
//			}
//		}
//		else {
//			this.last.setNext(newService);
//			newService.setPrev(this.last);
//			if (publisher != null){
//				if (newService.addProvider(publisher) != 0){
//					// This should never happen!
//					return -2;
//				}
//			}
//		}
//		
//		newService.setNext(null);
//		this.last = newService;
//		this.nTracingServices++;
//		
//		return 0;
//	}
//	
//	private int addTracingServiceAtPosition (TracingService newService, int position) {
//		// TODO: Comprobar los valores devueltos para que sean coherentes con los del metodo
//		// 		 addTracingService (...) antes especificado
//		
//		// position goes from 0 to (nTracingServices-1)
//		int i;
//		TracingService ts;
//		
//		if ((position > this.nTracingServices) || (position < 0)) {
//			// Out of range
//			return -1;
//		}
//		
//		if (position == 0) {
//			// First position
//			newService.setPrev(null);
//			if (this.nTracingServices > 0){
//				newService.setNext(this.first);
//				this.first.setPrev(newService);
//				this.first=newService;
//			}
//			else {
//				newService.setNext(null);
//				this.first=newService;
//				this.last=newService;
//			}
//		}
//		else if (position == this.nTracingServices){
//			// Last position, but with at
//			// least 1 tracing service already in the list
//			newService.setNext(null);
//			newService.setPrev(this.last);
//			this.last.setNext(newService);
//			this.last=newService;
//		}
//		else{
//			for (i=0, ts=this.getFirst(); i < position; i++, ts=ts.getNext());
//			
//			newService.setNext(ts);
//			ts.getPrev().setNext(newService);
//			newService.setPrev(ts.getPrev());
//			ts.setPrev(newService);
//		}
//		
//		this.nTracingServices++;
//		return 0;
//	}
//		
//	public int removeTracingService(String serviceName){
//		TracingService ts;
//		
//		if ((ts=this.getServiceByName(serviceName)) == null){
//			// Tracing service does not exist
//			return -1;
//		}
//		else{
//			if (ts.getPrev() == null){
//				// ts is the first in the list
//				if (this.nTracingServices == 1){
//					// Empty the list
//					this.first=null;
//					this.last=null;
//				}
//				else{
//					ts=this.first;
//					this.first=ts.getNext();
//					ts.setNext(null);
//					this.first.setPrev(null);
//				}
//			}
//			else if (ts.getNext() == null){
//				// ts is the last provider in the list
//				ts=this.last;
//				this.last=ts.getPrev();
//				this.last.setNext(null);
//				ts.setPrev(null);
//			}
//			else{
//				ts.getPrev().setNext(ts.getNext());
//				ts.getNext().setPrev(ts.getPrev());
//				ts.setPrev(null);
//				ts.setNext(null);
//			}
//		}
//		
//		this.nTracingServices--;
//		return 0;
//	}
//	
//	private int removeTracingServiceByPosition(int position){
//		// position goes from 0 to (nTracingServices-1)
//		int i;
//		TracingService ts;
//		
//		if ((position >= this.nTracingServices) || (position < 0)) {
//			// Out of range
//			return -1;
//		}
//		else if (position == 0){
//			// Remove the tracing service in the first position of the list
//			if (this.nTracingServices == 1){
//				// Empty the list
//				this.first=null;
//				this.last=null;
//			}
//			else{
//				ts=this.first;
//				this.first=ts.getNext();
//				ts.setNext(null);
//				this.first.setPrev(null);
//			}
//		}
//		else if (position == this.nTracingServices-1){
//			// Remove the tracing service in the last position of the list
//			ts=this.last;
//			this.last=ts.getPrev();
//			this.last.setNext(null);
//			ts.setPrev(null);
//		}
//		else {
//			for (i=0, ts=this.getFirst(); i < position; i++, ts=ts.getNext());
//			
//			ts.getPrev().setNext(ts.getNext());
//			ts.getNext().setPrev(ts.getPrev());
//			ts.setPrev(null);
//			ts.setNext(null);
//		}
//		
//		
//		this.nTracingServices--;
//		return 0;
//	}
//	
//	/**
//	 * Search for a tracing service by its name.
//	 * 
//	 * Return values:
//	 *   - Service found: The method returns the position at which the service was found [0 .. (nTracingServices-1)]
//	 *   - Service not found: The method returns -1
//	 */
//	public int getServicePositionByName(String name){
//		int i;
//		TracingService service;
//		
//		for (i=0, service=this.first; i < this.nTracingServices; i++, service=service.getNext()){
//			if (service.getName().equalsIgnoreCase(name)){
//				return i;
//			}
//		}
//		
//		return -1;
//	}
//	
//	/**
//	 * Search for a tracing service by its name.
//	 * 
//	 * Return values:
//	 *   - Service found: The method returns the tracing service
//	 *   - Service not found: The method returns null
//	 */
//	public TracingService getServiceByName(String name){
//		int i;
//		TracingService service;
//		
//		for (i=0, service=this.first; i < this.nTracingServices; i++, service=service.getNext()){
//			if (service.getName().equalsIgnoreCase(name)){
//				return service;
//			}
//		}
//		
//		return null;
//	}
//	
//	/**
//	 * Returns true if a tracing service with the specified name exists in the list
//	 * or false in case it does not exist a service with that name.
//	 */
//	public boolean existsService(String name){
//		if (this.getServiceByName(name) != null){
//			return true;
//		}
//		else {
//			return false;
//		}
//	}
	
	
}
