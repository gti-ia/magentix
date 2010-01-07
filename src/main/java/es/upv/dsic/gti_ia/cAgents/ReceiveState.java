package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class ReceiveState extends State{
	public ReceiveState(String n) {
		super(n);
		type = State.RECEIVE;
	}

	private ACLMessage acceptFilter;
	
	public void setAcceptFilter(ACLMessage filter){
		acceptFilter = filter;
	}
	
	public ACLMessage getAcceptFilter(){
		return acceptFilter;
	}
	
	protected abstract String run(CProcessor myProcessor, ACLMessage msg);
}
