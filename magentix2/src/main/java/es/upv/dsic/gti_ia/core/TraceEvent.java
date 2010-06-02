package es.upv.dsic.gti_ia.core;

//import java.util.Date;
import java.lang.System;
import java.io.Serializable;

import es.upv.dsic.gti_ia.trace.TracingService;

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
	
	private static final long serialVersionUID = 1L;
	
	// System Trace Events
	public static final int TRACE_ERROR = 0;
	public static final int TRACE_START = 1;
	public static final int TRACE_STOP = 2;
	public static final int SUBSCRIBE = 3;
	public static final int UNSUBSCRIBE = 4;
	public static final int UNAVAILABLE_TS = 5;
	public static final int STREAM_OVERFLOW = 6;
	public static final int STREAM_RESUME = 7;
	public static final int STREAM_FLUSH_START = 8;
	public static final int STREAM_FLUSH_STOP = 9;
	
	// Life cycle of Tracing Entities
	public static final int NEW_AGENT = 10;
//	public static final int NEW_ARTIFACT = 11;
//	public static final int NEW_AGGREGATION = 12;
	public static final int AGENT_SUSPENDED = 13;
	public static final int AGENT_RESUMED = 14;
	public static final int AGENT_DESTROYED = 15;
//	public static final int AGENT_ENTERS_AGGREGATION = 10;
//	public static final int AGENT_LEAVES_AGGREGATION = 10;
//	public static final int ARTIFACT_ENTERS_AGGREGATION = 10;
//	public static final int ARTIFACT_LEAVES_AGGREGATION = 10;
	
	// Messaging among Tracing Entities
	public static final int MESSAGE_SENT = 16;
	public static final int MESSAGE_RECEIVED = 17;
	public static final int MESSAGE_UNDELIVERABLE = 18;
	
	// OMS related Trace Events
//	public static final int ROLE_REGISTRATION = 10;
//	public static final int ROLE_DEREGISTRATION = 10;
//	public static final int NORM_REGISTRATION = 10;
//	public static final int NORM_DEREGISTRATION = 10;
//	public static final int UNIT_REGISTRATION = 10;
//	public static final int UNIT_DEREGISTRATION = 10;
//	public static final int ROLE_ACQUIRE = 10;
//	public static final int ROLE_LEAVE = 10;
//	public static final int ROLE_EXPULSION = 10;
//	public static final int NORM_VIOLATION = 10;
	
	// Tracing System related Tracing Services
	public static final int PUBLISHED_TRACING_SERVICE = 19;
	public static final int UNPUBLISHED_TRACING_SERVICE = 20;
	// These two seem redundant with "SUBSCRIBED" and "UNSUBSCRIBED"
	//public static final int TRACING_SERVICE_REQUEST = 10;
	//public static final int TRACING_SERVICE_CANCEL = 10;
	public static final int AUTHORIZATION_REQUEST = 21;
	public static final int AUTHORIZATION_GRANTED = 22;
	public static final int AUTHORIZATION_DENIED = 23;
	
	private static final String[][] DI_EventTypes = new String[100][2];
	static { // Initialization of the array of Domain Independent Tracing Services
		DI_EventTypes[TRACE_ERROR][0] = "TRACE_ERROR";
		DI_EventTypes[TRACE_START][0] = "TRACE_START";
		DI_EventTypes[TRACE_STOP][0] = "TRACE_STOP";
		DI_EventTypes[SUBSCRIBE][0] = "TRACE_SUBSCRIBE";
		DI_EventTypes[UNSUBSCRIBE][0] = "TRACE_UNSUBSCRIBE";
		DI_EventTypes[UNAVAILABLE_TS][0] = "UNAVAILABLE_TS";
		DI_EventTypes[STREAM_OVERFLOW][0] = "STREAM_OVERFLOW";
		DI_EventTypes[STREAM_FLUSH_START][0] = "STREAM_FLUSH_START";
		DI_EventTypes[STREAM_FLUSH_STOP][0] = "STREAM_FLUSH_STOP";
		DI_EventTypes[NEW_AGENT][0] = "NEW_AGENT";
		DI_EventTypes[AGENT_SUSPENDED][0] = "AGENT_SUSPENDED";
		DI_EventTypes[AGENT_RESUMED][0] = "AGENT_RESUMED";
		DI_EventTypes[AGENT_DESTROYED][0] = "AGENT_DESTROYED";
		DI_EventTypes[MESSAGE_SENT][0] = "MESSAGE_SENT";
		DI_EventTypes[MESSAGE_RECEIVED][0] = "MESSAGE_RECEIVED";
		DI_EventTypes[MESSAGE_UNDELIVERABLE][0] = "MESSAGE_UNDELIVERABLE";
		DI_EventTypes[PUBLISHED_TRACING_SERVICE][0] = "PUBLISHED_TRACING_SERVICE";
		DI_EventTypes[UNPUBLISHED_TRACING_SERVICE][0] = "UNPUBLISHED_TRACING_SERVICE";
		DI_EventTypes[AUTHORIZATION_REQUEST][0] = "AUTHORIZATION_REQUEST";
		DI_EventTypes[AUTHORIZATION_GRANTED][0] = "AUTHORIZATION_GRANTED";
		DI_EventTypes[AUTHORIZATION_DENIED][0] = "AUTHORIZATION_DENIED";
	}
	
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
