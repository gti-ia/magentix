package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class FinalState extends State{

	public FinalState(String n) {
		super(n);
		type = State.FINAL;
	}
	
	protected abstract ACLMessage run(CProcessor myProcessor);
	
}
