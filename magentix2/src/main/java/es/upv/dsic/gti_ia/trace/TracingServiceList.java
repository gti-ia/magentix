package es.upv.dsic.gti_ia.trace;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TracingService;

/**
 * List of tracing services
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 * 
 * @see es.upv.dsic.gti_ia.core.TracingService
 * @see java.util.Arraylist
 */
public class TracingServiceList extends ArrayList<TracingService> {
	private static final long serialVersionUID = 1L;
	/**
	 * The logger variable considers to print any event that occurs
	 */
	protected Logger logger = Logger.getLogger(BaseAgent.class);
	
	/**
	 * Initializes the tracing service list with domain independent
	 * tracing services in {@link es.upv.dsic.gti_ia.core.TracingService#DI_TracingServices}
	 * 
	 * @return Returns true if all domain independent tracing services were added
	 * 		to the list or false otherwise
	 */
	public boolean initializeWithDITracingServices(){
		int i;
	
		logger.info("Initialising with DI Tracing Services: Adding " + TracingService.MAX_DI_TS + " services");

		for (i=0; i < TracingService.MAX_DI_TS; i++){
			if (!this.add(TracingService.DI_TracingServices[i])){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets a tracing service from the list which has the same service name as the
	 * specified in the parameters
	 * 
	 * @param serviceName	Name of the tracing service which is to be returned
	 * 
	 * @return returns the tracing service which has that name if it exists in the list
	 * 		or null if no tracing service with the specified name is found 
	 */
	public TracingService getTS(String serviceName){
		TracingService tService;
		Iterator<TracingService> iter = this.iterator();
		
		while (iter.hasNext()){
			tService=iter.next();
			if (tService.getName().contentEquals(serviceName)){
				return tService;
			}
		}
		return null;
	}
}
