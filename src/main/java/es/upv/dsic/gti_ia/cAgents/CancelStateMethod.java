package es.upv.dsic.gti_ia.cAgents;

/**
 * This interface class represents a method of a cancel state
 * @author Ricard Lopez Fogues
 *
 */

import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class CancelStateMethod {
	
	protected abstract String run(CProcessor myProcessor, ACLMessage cancellationState);
	
}
