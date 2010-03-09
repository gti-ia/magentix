// PENDIENTE:
// Valorar si cambiar este estado por un método de rechazo en estado WAIT

package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class NotAcceptedMessagesState extends State{
	
	/* MESSAGE TREATMENTS */
	protected final static int IGNORE = 1;
	protected final static int REPLY_NOT_UNDERSTOOD = 2;
	protected final static int KEEP = 3;

	public NotAcceptedMessagesState() {
		super("NOT_ACCEPTED_MESSAGES_STATE");
		type = State.NOT_ACCEPTED_MESSAGES;
	}
	
	protected abstract int run(ACLMessage exceptionMessage, String next);
	
	protected abstract String getNext(String next);

}
