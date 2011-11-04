// PENDIENTE:
// Valorar si cambiar este estado por un metodo de rechazo en estado WAIT

package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * This class is not currently used. It needs a revision in order to define its operation
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
	
	protected abstract int run(CProcessor myProcessor, ACLMessage exceptionMessage, String next);
	
	protected abstract String getNext(CProcessor myProcessor, String previousState);

}
