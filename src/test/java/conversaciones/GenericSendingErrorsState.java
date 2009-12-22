package conversaciones;

import es.upv.dsic.gti_ia.cAgents.SendingErrorsState;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericSendingErrorsState extends SendingErrorsState{

	public GenericSendingErrorsState() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String run(ACLMessage exceptionMessage, String next) {
		System.out.println(exceptionMessage.getContent());
		next = "finalState";
		return next;
	}

}
