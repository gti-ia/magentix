package es.upv.dsic.gti_ia.trace;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TracingService;

public class TracingServiceList extends ArrayList<TracingService> {
	private static final long serialVersionUID = 1L;
	/**
	 * The logger variable considers to print any event that occurs
	 */
	protected Logger logger = Logger.getLogger(BaseAgent.class);
	
	public boolean initializeWithDITracingServices(){
		int i;
	
		logger.info("Initialising with DI Tracing Services: Adding " + TracingService.MAX_DI_TS + " services");

		for (i=0; i < TracingService.MAX_DI_TS; i++){
			if (!this.add(TracingService.DI_TracingServices[i])){
				return true;
			}
		}
		return true;
	}
	
	public TracingService getTS(String name){
		TracingService tService;
		Iterator<TracingService> iter = this.iterator();
		
		while (iter.hasNext()){
			tService=iter.next();
			if (tService.getName().contentEquals(name)){
				return tService;
			}
		}
		return null;
	}
}
