package conversaciones;

import es.upv.dsic.gti_ia.cAgents.CancelState;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericCancelState extends CancelState{

	protected GenericCancelState() {
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