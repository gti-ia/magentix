package es.upv.dsic.gti_ia.core;

import java.lang.System;
import java.util.Date;
import java.io.Serializable;

import es.upv.dsic.gti_ia.trace.TracingEntity;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Definition of Trace Event
 */

public class TraceEvent implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the tracing service which provided the trace event 
	 */
	private String tService;
	/**
	 * Time stamp at which the trace event was thrown
	 */
	private long timestamp;
	/**
	 * Origin entity which threw the trace event.
	 * When the origin entity of the trace event is the system,
	 * originEntity.name="system"
	 */
	private TracingEntity originEntity;
	/**
	 * Content of the trace event. This will depend on @see{tService}
	 */
	private String content;
	
	/**
	 * Void constructor which sets all attributes to null
	 * except for @see{timestamp}, which is set to -1
	 */
	public TraceEvent(){
		this.tService=null;
		this.originEntity=null;
		this.timestamp=-1;
		this.content=null;
	}
	
	/**
	 * Creates a new trace event and sets its attributes according to the 
	 * specified parameters, except for @see{timestamp}, which is set to
	 * @see{java.lang.System.currentTimeMillis()}
	 *  
	 * @param tService 
	 * @param originEntity
	 * @param content
	 */
	public TraceEvent(String tService, TracingEntity originEntity, String content){
		this.tService=tService;
		this.originEntity=originEntity;
		this.timestamp=System.currentTimeMillis();
		this.content=content;
	}
	
	/**
	 * Creates a new trace event and sets its attributes according to the 
	 * specified parameters, except for @see{timestamp}, which is set to
	 * @see{java.lang.System.currentTimeMillis()}
	 * A new tracing entity is created for the specified AgentID
	 *  
	 * @param tService 
	 * @param originAid
	 * @param content
	 */
	public TraceEvent(String tService, AgentID originAid, String content){
		this.tService=tService;
		this.originEntity=new TracingEntity(TracingEntity.AGENT, originAid);
		this.timestamp=System.currentTimeMillis();
		this.content=content;
	}
	
	/**
	 * Sets the tracing service of the trace event to the specified one
	 * 
	 * @param tService
	 */
	public void setTracingService(String tService){
		this.tService=tService;
	}
	
	/**
	 * Sets the time stamp of the trace event to the specified one
	 * 
	 * @param timestamp
	 */
	public void setTimestamp(long timestamp){
		this.timestamp=timestamp;
	}
	
	/**
	 * Sets the origin entity of the trace event to the specified one
	 * 
	 * @param originEntity
	 */
	public void setOriginEntity(TracingEntity originEntity){
		this.originEntity=originEntity;
	}
	
	/**
	 * Sets the content of the trace event to the specified one
	 * 
	 * @param content
	 */
	public void setContent(String content){
		this.content=content;
	}

	/**
	 * Returns the tracing service of the trace event
	 * 
	 * @return tService
	 */
	public String getTracingService(){
		return this.tService;
	}
	
	/**
	 * Returns the origin entity of the trace event
	 * 
	 * @return originEntity
	 */
	public TracingEntity getOriginEntity(){
		return this.originEntity;
	}
	
	/**
	 * Returns the content of the trace event
	 * 
	 * @return content
	 */
	public String getContent(){
		return this.content;
	}
	
	/**
	 * Returns the time stamp of the trace event
	 * 
	 * @return timestamp
	 */
	public long getTimestamp(){
		return this.timestamp;
	}
	
	/**
	 * Converts the trace event to a human readable string
	 * @see{es.upv.dsic.gti_ia.trace.TracingEntity.toReadableString()}
	 * 
	 * @return A readable string containing all information of the trace event
	 */
	public String toReadableString() {
		String event_str = String.valueOf(this.getTimestamp()) + ": " +
			this.getTracingService() + " from " + this.getOriginEntity().toReadableString() + " Content: " +
			this.getContent();
		
		return event_str;
	}
}
