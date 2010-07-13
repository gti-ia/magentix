package es.upv.dsic.gti_ia.trace;

import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.core.AgentID;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Double linked list of Tracing Entities
 */
public class TracingEntityList extends ArrayList<TracingEntity>{

	private static final long serialVersionUID = 1L;
	
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
