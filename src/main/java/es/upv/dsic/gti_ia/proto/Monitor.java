

/**
 * 
 *  
 * The class Adviser allows to control the state of an agent (executing, suspended).
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.proto;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.fipa.ISO8601;


public class Monitor {

	
	 static Logger logger = Logger.getLogger(Monitor.class);
	
    /**
     * suspends the process
     */
	public synchronized void waiting()
	{
		
        try{	
			this.wait();

     	}catch(InterruptedException e){
				
				logger.info("Error on Monitor Class: " + e.getMessage());
			}
	}
	
	  /**
     * suspends the process a time
     * 
     * @param  timeout Time of wait.
     */
	public synchronized void waiting(long timeout)
	{
		
        try{	
			this.wait(timeout);

     	}catch(InterruptedException e){
				
     		logger.info("Error on Monitor Class: " + e.getMessage());
			}
	}
	
    /**
     * reactivates the process
     */
	public synchronized void advise()
	{
		try{
		notifyAll();
		}catch(Exception e)
		{
			logger.info("Error on Monitor Class: "+ e.getMessage());
		}
		
	}
	
	

	
	
}
