package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class TerminatedFatherState extends State{

	public TerminatedFatherState() {
		super("TERMINATED_FATHER_STATE");
		type = State.TERMINATED_FATHER;
	}
		
	protected abstract String run(ACLMessage exceptionMessage, String next);

}
