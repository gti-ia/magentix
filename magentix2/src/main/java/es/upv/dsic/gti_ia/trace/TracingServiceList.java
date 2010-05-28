package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

public class TracingServiceList {
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
		tService.setPrev(null);
		tService.setNext(null);
		this.nTracingServices = 1;
	}
	
	/**
	 * Add a tracing service to the end of the tracing service list.
	 * 
	 * @param publisher
	 * 		AgentID of the agent who wants to publish the tracing service.
	 * 		If publisher is null, the tracing service is added without publishers.
	 * 
	 * @param newService
	 * 		TracingService to be published.
	 * 		If the service was already published and publisher was not null,
	 * 		a new provider for that service will be added.
	 * 
	 * @return 0
	 * 		Success: Service published, with or without publisher, depending on
	 * 			the parameters.
	 * 
	 * @return 1
	 * 		Success: The service was already published and only a new publisher
	 * 			had to be added.
	 * 
	 * @return -1
	 * 		Error: Duplicate tracing service name and publisher (if specified)
	 * 			No changes were made to the corresponding TracingServiceList
	 * 
	 * @return -2
	 * 		Error: Unknown error
	 */
	public int addTracingService (AgentID publisher, TracingService newService) {
		TracingService existingService;
		
		if (this.nTracingServices < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.nTracingServices == 0) {
			this.first=newService;
			newService.setPrev(null);
			if (publisher != null){
				if (newService.addProvider(publisher) != 0){
					// This should never happen!
					return -2;
				}
			}
		}
		else if ((existingService=this.getServiceByName(newService.getName())) != null) {
			if (existingService.addProvider(publisher) == 0){
				// Add the provider to the tracing service
				return 1;
			}
			else {
				// The provider had already published the tracing service
				return -1;
			}
		}
		else {
			this.last.setNext(newService);
			newService.setPrev(this.last);
			if (publisher != null){
				if (newService.addProvider(publisher) != 0){
					// This should never happen!
					return -2;
				}
			}
		}
		
		newService.setNext(null);
		this.last = newService;
		this.nTracingServices++;
		
		return 0;
	}
	
	private int addTracingServiceAtPosition (TracingService newService, int position) {
		// TODO: Comprobar los valores devueltos para que sean coherentes con los del metodo
		// 		 addTracingService (...) antes especificado
		
		// position goes from 0 to (nTracingServices-1)
		int i;
		TracingService ts;
		
		if ((position > this.nTracingServices) || (position < 0)) {
			// Out of range
			return -1;
		}
		
		if (position == 0) {
			// First position
			newService.setPrev(null);
			if (this.nTracingServices > 0){
				newService.setNext(this.first);
				this.first.setPrev(newService);
				this.first=newService;
			}
			else {
				newService.setNext(null);
				this.first=newService;
				this.last=newService;
			}
		}
		else if (position == this.nTracingServices){
			// Last position, but with at
			// least 1 tracing service already in the list
			newService.setNext(null);
			newService.setPrev(this.last);
			this.last.setNext(newService);
			this.last=newService;
		}
		else{
			for (i=0, ts=this.getFirst(); i < position; i++, ts=ts.getNext());
			
			newService.setNext(ts);
			ts.getPrev().setNext(newService);
			newService.setPrev(ts.getPrev());
			ts.setPrev(newService);
		}
		
		this.nTracingServices++;
		return 0;
	}
		
	public int removeTracingService(String serviceName){
		TracingService ts;
		
		if ((ts=this.getServiceByName(serviceName)) == null){
			// Tracing service does not exist
			return -1;
		}
		else{
			if (ts.getPrev() == null){
				// ts is the first in the list
				if (this.nTracingServices == 1){
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
		
		this.nTracingServices--;
		return 0;
	}
	
	private int removeTracingServiceByPosition(int position){
		// position goes from 0 to (nTracingServices-1)
		int i;
		TracingService ts;
		
		if ((position >= this.nTracingServices) || (position < 0)) {
			// Out of range
			return -1;
		}
		else if (position == 0){
			// Remove the tracing service in the first position of the list
			if (this.nTracingServices == 1){
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
		else if (position == this.nTracingServices-1){
			// Remove the tracing service in the last position of the list
			ts=this.last;
			this.last=ts.getPrev();
			this.last.setNext(null);
			ts.setPrev(null);
		}
		else {
			for (i=0, ts=this.getFirst(); i < position; i++, ts=ts.getNext());
			
			ts.getPrev().setNext(ts.getNext());
			ts.getNext().setPrev(ts.getPrev());
			ts.setPrev(null);
			ts.setNext(null);
		}
		
		
		this.nTracingServices--;
		return 0;
	}
	
	/**
	 * Search for a tracing service by its name.
	 * 
	 * Return values:
	 *   - Service found: The method returns the position at which the service was found [0 .. (nTracingServices-1)]
	 *   - Service not found: The method returns -1
	 */
	public int getServicePositionByName(String name){
		int i;
		TracingService service;
		
		for (i=0, service=this.first; i < this.nTracingServices; i++, service=service.getNext()){
			if (service.getName().equalsIgnoreCase(name)){
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Search for a tracing service by its name.
	 * 
	 * Return values:
	 *   - Service found: The method returns the tracing service
	 *   - Service not found: The method returns null
	 */
	public TracingService getServiceByName(String name){
		int i;
		TracingService service;
		
		for (i=0, service=this.first; i < this.nTracingServices; i++, service=service.getNext()){
			if (service.getName().equalsIgnoreCase(name)){
				return service;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if a tracing service with the specified name exists in the list
	 * or false in case it does not exist a service with that name.
	 */
	public boolean existsService(String name){
		if (this.getServiceByName(name) != null){
			return true;
		}
		else {
			return false;
		}
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
	
	public String listAllTracingServices () {
		String list = new String();
		int i;
		TracingService ts;
		
		for (i=0, ts=this.getFirst(); (i < this.getNTracingServices()) && (ts.getNext() != null); i++, ts=ts.getNext()){
			list = list + ts.getName() + " (" + ts.getEventType() + "): " + ts.getDescription() + "\n";
		}
		
		//list = list + "\n";
		
		return list;
	}
}
