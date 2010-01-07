package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class SendingErrorsState extends State{

	public SendingErrorsState() {
		super("SENDING_ERRORS_STATE");
		type = State.SENDING_ERRORS;
	}
	
	protected abstract String run(ACLMessage exceptionMessage, String next);
}
