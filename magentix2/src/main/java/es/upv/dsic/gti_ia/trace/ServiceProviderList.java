package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.trace.ServiceProvider;

public class ServiceProviderList {
	private ServiceProvider first;
	private ServiceProvider last;
	private int nProviders;
	
	public ServiceProviderList () {
		this.first=null;
		this.last=null;
		this.nProviders=0;
	}
	
	public ServiceProviderList (ServiceProvider provider) {
		this.first=provider;
		this.last=provider;
		provider.setPrev(null);
		provider.setNext(null);
		this.nProviders=1;
	}
	
	/**
	 * Add a service provider to the end of the service provider list.
	 * The AgentID of the service provider has to be unique within the list, otherwise,
	 * it will not be added and the method will return error.
	 * 
	 * @param newProvider
	 * 		Provider object to add at the end of the list
	 * 
	 * @return 0
	 * 		Success: Provider added.
	 * 
	 * @return -1
	 * 		Error: Duplicate service provider in the list.
	 * 			No changes were made to the corresponding ServiceProviderList
	 * 
	 * @return -2
	 * 		Error: Unknown error
	 */
	public int addProvider (ServiceProvider newProvider) {
		if (this.nProviders < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.nProviders == 0) {
			this.first=newProvider;
			newProvider.setPrev(null);
		}
		else if (this.existsProvider(newProvider.getAID())) {
			return -1;
		}
		else {
			this.last.setNext(newProvider);
			newProvider.setPrev(this.last);
		}
		
		newProvider.setNext(null);
		this.last = newProvider;
		this.nProviders++;
				
		return 0;
	}
	
	/**
	 * Add a service provider to the end of the service provider list.
	 * All attributes of the provider except for the AgentID will be set to null.
	 * 
	 * The AgentID of the service provider has to be unique within the list,
	 * otherwise, it will not be added and the method will return error.
	 * 
	 * @param aid
	 * 		AgentID of the provider which is to added
	 * 
	 * @return 0
	 * 		Success: Provider added.
	 * 
	 * @return -1
	 * 		Error: Duplicate service provider in the list.
	 * 			No changes were made to the corresponding ServiceProviderList
	 * 
	 * @return -2
	 * 		Error: Unknown error
	 */
	public int addProvider (AgentID aid) {
		ServiceProvider newProvider;
		if (this.nProviders < 0){
			// Error mucho gordo
			return -2;
		}
		else if (this.nProviders == 0) {
			newProvider=new ServiceProvider(aid);
			this.first=newProvider;
			newProvider.setPrev(null);
		}
		else if (this.existsProvider(aid)) {
			return -1;
		}
		else {
			newProvider=new ServiceProvider(aid);
			this.last.setNext(newProvider);
			newProvider.setPrev(this.last);
		}
		
		newProvider.setNext(null);
		this.last = newProvider;
		this.nProviders++;
				
		return 0;
	}
	
	private int addProviderAtPosition (ServiceProvider newProvider, int position) {
		// position goes from 0 to (nProvioders-1)
		int i;
		ServiceProvider sp;
		
		if ((position > this.nProviders) || (position < 0)) {
			// Out of range
			return -1;
		}
		
		if (position == 0) {
			// First position
			newProvider.setPrev(null);
			if (this.nProviders > 0){
				newProvider.setNext(this.first);
				this.first.setPrev(newProvider);
				this.first=newProvider;
			}
			else {
				newProvider.setNext(null);
				this.first=newProvider;
				this.last=newProvider;
			}
		}
		else if (position == this.nProviders){
			// Last position, but with at
			// least 1 tracing service already in the list
			newProvider.setNext(null);
			newProvider.setPrev(this.last);
			this.last.setNext(newProvider);
			this.last=newProvider;
		}
		else{
			for (i=0, sp=this.getFirst(); i < position; i++, sp=sp.getNext());
			
			newProvider.setNext(sp);
			sp.getPrev().setNext(newProvider);
			newProvider.setPrev(sp.getPrev());
			sp.setPrev(newProvider);
		}
		
		this.nProviders++;
		return 0;
	}
	
	private int addProviderAtPosition (AgentID aid, int position) {
		// position goes from 0 to (nProvioders-1)
		int i;
		ServiceProvider newProvider, sp;
		
		if ((position > this.nProviders) || (position < 0)) {
			// Out of range
			return -1;
		}
		
		if (position == 0) {
			// First position
			newProvider=new ServiceProvider(aid);
			//newProvider.setPrev(null);
			if (this.nProviders > 0){
				newProvider.setNext(this.first);
				this.first.setPrev(newProvider);
				this.first=newProvider;
			}
			else {
				//newProvider.setNext(null);
				this.first=newProvider;
				this.last=newProvider;
			}
		}
		else if (position == this.nProviders){
			// Last position, but with at
			// least 1 tracing service already in the list
			newProvider=new ServiceProvider(aid);
			//newProvider.setNext(null);
			newProvider.setPrev(this.last);
			this.last.setNext(newProvider);
			this.last=newProvider;
		}
		else{
			for (i=0, sp=this.getFirst(); i < position; i++, sp=sp.getNext());
			
			newProvider=new ServiceProvider(aid);
			newProvider.setNext(sp);
			sp.getPrev().setNext(newProvider);
			newProvider.setPrev(sp.getPrev());
			sp.setPrev(newProvider);
		}
		
		this.nProviders++;
		return 0;
	}
	
	/**
	 * Remove a service provider from the list
	 * 
	 * @param aid
	 * 		AgentID of the provider to be removed
	 * 
	 * @return 0
	 * 		Success: Provider removed.
	 * 
	 * @return -1
	 * 		Error: Provider not found in the list.
	 * 			No changes were made to the corresponding ServiceProviderList
	 */
	public int removeProvider(AgentID aid){
		ServiceProvider sp;
		
		if ((sp=this.getProviderByAid(aid)) == null){
			// Service provider does not exist
			return -1;
		}
		else{
			if (sp.getPrev() == null){
				// sp is the first in the list
				if (this.nProviders == 1){
					// Empty the list
					this.first=null;
					this.last=null;
				}
				else{
					sp=this.first;
					this.first=sp.getNext();
					sp.setNext(null);
					this.first.setPrev(null);
				}
			}
			else if (sp.getNext() == null){
				// sp is the last provider in the list
				sp=this.last;
				this.last=sp.getPrev();
				this.last.setNext(null);
				sp.setPrev(null);
			}
			else{
				sp.getPrev().setNext(sp.getNext());
				sp.getNext().setPrev(sp.getPrev());
				sp.setPrev(null);
				sp.setNext(null);
			}
		}
		
		this.nProviders--;
		return 0;
	}
	
	private int removeProviderAtPosition(int position){
		// position goes from 0 to (nTracingServices-1)
		int i;
		ServiceProvider sp;
		
		if ((position >= this.nProviders) || (position < 0)) {
			// Out of range
			return -1;
		}
		else if (position == 0){
			// Remove the tracing service in the first position of the list
			if (this.nProviders == 1){
				// Empty the list
				this.first=null;
				this.last=null;
			}
			else{
				sp=this.first;
				this.first=sp.getNext();
				sp.setNext(null);
				this.first.setPrev(null);
			}
		}
		else if (position == this.nProviders-1){
			// Remove the service provider in the last position of the list
			sp=this.last;
			this.last=sp.getPrev();
			this.last.setNext(null);
			sp.setPrev(null);
		}
		else {
			for (i=0, sp=this.getFirst(); i < position; i++, sp=sp.getNext());
			
			sp.getPrev().setNext(sp.getNext());
			sp.getNext().setPrev(sp.getPrev());
			sp.setPrev(null);
			sp.setNext(null);
		}
		
		
		this.nProviders--;
		return 0;
	}
	
	/**
	 * Search for a service provider by its AgentID.
	 * 
	 * Return values:
	 *   - Provider found: The method returns the position at which the service was found [0 .. (nTracingServices-1)]
	 *   - Provider not found: The method returns -1
	 */
	public int getProviderPositionByAid(AgentID aid){
		int i;
		ServiceProvider provider;
		
		for (i=0, provider=this.first; i < this.nProviders; i++, provider=provider.getNext()){
			if (provider.getAID().equals(aid)){
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Search for a service provider by its AgentID.
	 * 
	 * Return values:
	 *   - Provider found: The method returns the service provider
	 *   - Provider not found: The method returns null
	 */
	public ServiceProvider getProviderByAid(AgentID aid){
		int i;
		ServiceProvider provider;
		
		for (i=0, provider=this.first; i < this.nProviders; i++, provider=provider.getNext()){
			if (provider.getAID().equals(aid)){
				return provider;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if a service provider with the specified AgentID exists in the list
	 * or false in case it does not exist a service with that name.
	 */
	public boolean existsProvider(AgentID aid){
		if (this.getProviderByAid(aid) != null){
			return true;
		}
		else {
			return false;
		}
	}
	
	public ServiceProvider getFirst () {
		return this.first;
	}
	
	public ServiceProvider getLast () {
		return this.last;
	}
	
	public int getNProviders () {
		return this.nProviders;
	}
	
	public String listAllServiceProviders () {
		String list = new String();
		int i;
		ServiceProvider sp;
		
		for (i=0, sp=this.getFirst(); (i < this.getNProviders()) && (sp.getNext() != null); i++, sp=sp.getNext()){
			list = list + sp.getAID().toString() + "\n";
		}
		
		//list = list + "\n";
		
		return list;
	}
}
