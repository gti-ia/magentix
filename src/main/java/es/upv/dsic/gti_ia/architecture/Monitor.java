package es.upv.dsic.gti_ia.architecture;

import org.apache.log4j.Logger;

import es.upv.dsic.gti_ia.organization.OMS;



/**
 * 
 *  
 * The class Adviser allows to control the state of an agent (executing, suspended).
 * 
 * @author  Joan Bellver Faus
 * @version 2009.9.07
 */
public class Monitor {

	static Logger logger = Logger.getLogger(OMS.class);
	
	/**
	 * suspends the process
	 */
	public synchronized void waiting() {

		try {
			this.wait();

		} catch (InterruptedException e) {

			logger.error("ERROR: " + e.getMessage());
		}
	}

	/**
	 * suspends the process a time
	 * 
	 * @param timeout
	 *            Time of wait.
	 */
	public synchronized void waiting(long timeout) {

		try {
			this.wait(timeout);

		} catch (InterruptedException e) {

			logger.error("ERROR: " + e.getMessage());
		}
	}

	/**
	 * reactivates the process
	 */
	public synchronized void advise() {
		try {
			notifyAll();
		} catch (Exception e) {
			logger.error("ERROR :" + e.getMessage());
		}

	}

}
