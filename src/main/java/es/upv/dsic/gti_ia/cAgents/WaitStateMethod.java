// CAMBIOS EN ESTA CLASE

// Sustituido el metodo run por registrar objeto SendStateMethod


package es.upv.dsic.gti_ia.cAgents;

import java.util.Date;


public interface WaitStateMethod {
	
	public Date run(long timeout);
	
}
