package es.upv.dsic.gti_ia.core;

import java.lang.System;
import java.io.Serializable;

import es.upv.dsic.gti_ia.trace.TracingEntity;

/**
 * Definition of Trace Event.
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
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
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity
	 */
	private TracingEntity originEntity;
	/**
	 * Content of the trace event. This will depend on {@link tService}
	 */
	private String content;
	
	/**
	 * Void constructor which sets all attributes to null
	 * except for {@link timestamp}, which is set to -1
	 */
	public TraceEvent(){
		this.tService=null;
		this.originEntity=null;
		this.timestamp=-1;
		this.content=null;
	}
	
	/**
	 * Creates a new trace event and sets its attributes according to the 
	 * specified parameters, except for {@link timestamp}, which is set to
	 * the current time using {@link java.lang.System#currentTimeMillis()}
	 *  
	 * @param tService		Tracing service name
	 * @param originEntity	Tracing entity which originated the trace event
	 * @param content		Content of the trace event
	 * 
	 * @see java.lang.System.currentTimeMillis()
	 */
	public TraceEvent(String tService, TracingEntity originEntity, String content){
		this.tService=tService;
		this.originEntity=originEntity;
		this.timestamp=System.currentTimeMillis();
		this.content=content;
	}
	
	/**
	 * Creates a new trace event and sets its attributes according to the 
	 * specified parameters, except for {@link timestamp}, which is set to
	 * {@link java.lang.System.currentTimeMillis()}
	 * A new tracing entity is created for the specified AgentID
	 *  
	 * @param tService		Tracing service name
	 * @param originEntity	Tracing entity which originated the trace event
	 * @param content		Content of the trace event
	 * 
	 * @see java.lang.System#currentTimeMillis()
	 */
	public TraceEvent(String tService, AgentID originAid, String content){
		this.tService=tService;
		this.originEntity=new TracingEntity(TracingEntity.AGENT, originAid);
		this.timestamp=System.currentTimeMillis();
		this.content=content;
	}
	
	/**
	 * Sets the tracing service of the trace event to the specified one.
	 * 
	 * @param tService Name of the tracing service
	 */
	public void setTracingService(String tService){
		this.tService=tService;
	}
	
	/**
	 * Sets the time stamp of the trace event to the specified one.
	 * 
	 * @param timestamp Time at which the trace event was thrown
	 */
	public void setTimestamp(long timestamp){
		this.timestamp=timestamp;
	}
	
	/**
	 * Sets the origin entity of the trace event to the specified one.
	 * 
	 * @param originEntity Tracing entity which originated the trace event
	 */
	public void setOriginEntity(TracingEntity originEntity){
		this.originEntity=originEntity;
	}
	
	/**
	 * Sets the content of the trace event to the specified one.
	 * 
	 * @param content Content of the trace event
	 */
	public void setContent(String content){
		this.content=content;
	}

	/**
	 * Returns the tracing service of the trace event.
	 * 
	 * @return tService Name of the tracing Service
	 */
	public String getTracingService(){
		return this.tService;
	}
	
	/**
	 * Returns the origin entity of the trace event.
	 * 
	 * @return originEntity Tracing entity which originated the trace event
	 */
	public TracingEntity getOriginEntity(){
		return this.originEntity;
	}
	
	/**
	 * Returns the content of the trace event.
	 * 
	 * @return content Content of the trace event
	 */
	public String getContent(){
		return this.content;
	}
	
	/**
	 * Returns the time stamp of the trace event.
	 * 
	 * @return timestamp Time at which the trace event was generated
	 */
	public long getTimestamp(){
		return this.timestamp;
	}
	
	/**
	 * Converts the trace event to a human readable string.
	 * 
	 * @return A readable string containing all information of the trace event
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity#toReadableString()
	 */
	public String toReadableString() {
		String event_str = String.valueOf(this.getTimestamp()) + ": " +
			this.getTracingService() + " from " + this.getOriginEntity().toReadableString() + " Content: " +
			this.getContent();
		
		return event_str;
	}
}
