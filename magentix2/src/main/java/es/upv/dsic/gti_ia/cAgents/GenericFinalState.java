package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.core.ACLMessage;

public class GenericFinalState extends FinalState{

	public GenericFinalState(String n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ACLMessage run(CProcessor myProcessor) {
		// TODO Auto-generated method stub
		System.out.println("Fin "+myProcessor.getMyAgent().getName());
		return null;
	}

}
