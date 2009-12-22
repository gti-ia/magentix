package conversaciones;

import es.upv.dsic.gti_ia.cAgents.NotAcceptedMessagesState;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericNotAcceptedMessagesState extends NotAcceptedMessagesState{

	public GenericNotAcceptedMessagesState() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int run(ACLMessage exceptionMessage, String next) {
		System.out.println("Not_Accepted_Messages: Content: "+exceptionMessage.getContent());
		return NotAcceptedMessagesState.IGNORE;
	}

	@Override
	protected String getNext(String next) {
		next = "finalState";
		return next;
	}

}
