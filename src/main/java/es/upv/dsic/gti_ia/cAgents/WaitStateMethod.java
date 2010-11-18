// CAMBIOS EN ESTA CLASE

// Sustituido el metodo run por registrar objeto SendStateMethod


package es.upv.dsic.gti_ia.cAgents;

import java.util.Date;

/**
 * This interface represents a method of a wait state
 * @author ricard
 *
 */

public interface WaitStateMethod {
	
	/**
	 * This method returns the moment when this state's timeout is reached
	 * @param timeout
	 * @return Date when the timeout ends
	 */
	public Date run(long timeout);
	
}
