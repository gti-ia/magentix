package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class BeginState extends State{

	public BeginState(String n) {
		super(n);
		type = State.BEGIN;
	}
	
	protected abstract String run(CProcessor myProcessor, ACLMessage msg);
	
}
