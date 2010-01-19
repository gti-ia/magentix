package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.cAgents.TerminatedFatherState;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericTerminatedFatherState extends TerminatedFatherState{

	public GenericTerminatedFatherState() {
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
