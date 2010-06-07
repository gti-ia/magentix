package es.upv.dsic.gti_ia.core;

//import java.util.Date;
import java.lang.System;
import java.io.Serializable;

import es.upv.dsic.gti_ia.trace.TracingEntity;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Definition of Trace Event
 */

public class TraceEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String tService;
	private long timestamp;
	private TracingEntity originEntity;
	private String content;
	
	public TraceEvent(){
		this.tService=null;
		this.originEntity=null;
		this.timestamp=-1;
		this.content=null;
	}
	
	public TraceEvent(String tService, TracingEntity originEntity, String content){
		this.tService=tService;
		this.originEntity=originEntity;
		this.timestamp=System.currentTimeMillis();
		this.content=content;
	}
	
	public void setTracingService(String tService){
		this.tService=tService;
	}
	
	public void setTimestamp(long timestamp){
		this.timestamp=timestamp;
	}
	
	public void setOriginEntity(TracingEntity originEntity){
		this.originEntity=originEntity;
	}
	
	public void setContent(String content){
		this.content=content;
	}

	public String getTracingService(){
		return this.tService;
	}
	
	public TracingEntity getOriginEntity(){
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
			this.getTracingService() + " from " + this.getOriginEntity().toReadableString() + " Content: " +
			this.getContent();
		
		return event_str;
	}
}
