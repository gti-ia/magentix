package conversaciones;

import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;

public class GenericFinalState extends FinalState{

	public GenericFinalState(String n) {
		super(n);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String run(CProcessor myProcessor) {
		// TODO Auto-generated method stub
		System.out.println("Fin "+myProcessor.getMyAgent().getName());
		return null;
	}

}
