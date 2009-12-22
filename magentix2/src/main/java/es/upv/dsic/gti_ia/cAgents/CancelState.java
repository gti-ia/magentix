package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class CancelState extends State{
	
	protected CancelState() {
		super("CANCEL_STATE");
		type = State.CANCEL;
	}
		
	protected abstract String run(ACLMessage exceptionMessage, String next);
}
