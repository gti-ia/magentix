package es.upv.dsic.gti_ia.trace;

import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * List of Tracing Entities
 *  
 * @author L Burdalo (lburdalo@dsic.upv.es)
 * 
 * @see es.upv.dsic.gti_ia.trace.TracingEntity
 * @see java.util.Arraylist
 */
public class TracingEntityList extends ArrayList<TracingEntity>{

	private static final long serialVersionUID = 1L;
	
	/**
	 * Returns a tracing entity from the list whose identifier corresponds
	 * with the specified in the parameters.
	 * 
	 * @param aid AgentID of the agent which is to be found in the list 
	 * 
	 * @return returns the tracing entity identified by the specified AgentID
	 * 		or null in case it is not found
	 * 
	 * @see es.upv.dsic.gti_ia.trace.TracingEntity#hasTheSameAidAs(AgentID)
	 */
	public TracingEntity getTEByAid(AgentID aid){
		TracingEntity tEntity;
		Iterator<TracingEntity> iter = this.iterator();
		
		while (iter.hasNext()){
			tEntity=iter.next();
			if (tEntity.hasTheSameAidAs(aid)){
				return tEntity;
			}
		}
		return null;
	}
}
