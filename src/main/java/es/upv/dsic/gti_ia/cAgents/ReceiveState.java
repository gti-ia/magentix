package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public abstract class ReceiveState extends State {

	private ACLMessage acceptFilter;
	private ReceiveStateMethod methodToRun;

	public ReceiveState(String n) {
		super(n);
		type = State.RECEIVE;
	}

	public void setAcceptFilter(ACLMessage filter) {
		acceptFilter = filter;
	}

	public ACLMessage getAcceptFilter() {
		return acceptFilter;
	}

	public void setMethod(ReceiveStateMethod method) {
		methodToRun = method;
	}

	public ReceiveStateMethod getMethod() {
		return methodToRun;
	}

}
