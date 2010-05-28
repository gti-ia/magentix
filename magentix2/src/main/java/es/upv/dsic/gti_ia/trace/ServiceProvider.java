package es.upv.dsic.gti_ia.trace;

import es.upv.dsic.gti_ia.core.AgentID;

public class ServiceProvider {
	private AgentID aid;
	private ServiceProvider prev;
	private ServiceProvider next;
	
	public ServiceProvider () {
		this.aid=null;
		this.prev=null;
		this.next=null;
	}
	
	public ServiceProvider (AgentID aid) {
		this.aid=aid;
		this.prev=null;
		this.next=null;
	}
	
	public void setAID (AgentID aid) {
		this.aid=aid;
	}
	
	public void setPrev(ServiceProvider previousProvider) {
		this.prev=previousProvider;
	}
	
	public void setNext(ServiceProvider nextProvider) {
		this.next=nextProvider;
	}
	
	public AgentID getAID () {
		return this.aid;
	}
	
	public ServiceProvider getPrev() {
		return this.prev;
	}
	
	public ServiceProvider getNext() {
		return this.next;
	}
}
