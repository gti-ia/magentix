// CAMBIOS EN ESTA CLASE

// Sustituido el metodo run por registrar objeto SendStateMethod


package es.upv.dsic.gti_ia.cAgents;

import java.util.Date;


public interface WaitStateMethod {
	
	/**
	 * The method to be executed by the action state
	 * @param timeout
	 * @return Date when the timeout ends
	 */
	public Date run(long timeout);
	
}
