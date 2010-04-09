package es.upv.dsic.gti_ia.core;

//import java.util.Date;
import java.lang.System;
import java.io.Serializable;

public class TraceEvent implements Serializable {
	/*
	public static final int SYSTEM = 0;
	public static final int INDEPENDENT = 1;
	public static final int DEPENDENT = 2;
	
	private static final String[] domains = new String[3];
	static {
		domains[SYSTEM] = "system";
		domains[INDEPENDENT] = "independent";
		domains[DEPENDENT] = "dependent";
	}
	*/
	
	//private int domain;
	private String eventType;
	private long timestamp;
	private AgentID originEntity;
	private String content;
	
	public TraceEvent(){
		//this.domain=-1;
		this.eventType=null;
		this.originEntity=null;
		this.timestamp=-1;//Long.valueOf(-1);
	}
	
	public TraceEvent(String eventType, AgentID originEntity, String content){
		this.eventType=eventType;
		this.originEntity=originEntity;
		this.timestamp=System.currentTimeMillis();
		this.content=content;
	}
	
	/*	
	public void setDomain(String domain){
		for (int i = 0; i < domains.length; i++) {
			if (domain.compareTo(domains[i]) == 0) {
				this.domain = i;
				break;
			}
		}
	}
	*/
	
	public void setEventType(String eventType){
		this.eventType=eventType;
	}
	
	public void setTimestamp(long timestamp){
		this.timestamp=timestamp;
	}
	
	public void setOriginEntity(AgentID originEntity){
		this.originEntity=originEntity;
	}
	
	public void setContent(String content){
		this.content=content;
	}
		
	/*
	public String getDomain(){
		return domains[this.domain];
	}
	*/
	
	public String getEventType(){
		return this.eventType;
	}
	
	public AgentID getOriginEntity(){
		return this.originEntity;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public long getTimestamp(){
		return this.timestamp;
	}
	
	public String toReadableString() {
		String event_str = String.valueOf(this.getTimestamp()) + ": " +
			this.getEventType() + " from " + this.getOriginEntity().toString() + " Content: " +
			this.getContent();
		
		return event_str;
	}
}
