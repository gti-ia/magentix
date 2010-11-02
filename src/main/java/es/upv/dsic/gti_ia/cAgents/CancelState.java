package es.upv.dsic.gti_ia.cAgents;

/**
 * This class represents a cancel state during an interaction protocol.
 * When a conversation reaches this state it executes the state's method.
 * @author Ricard Lopez Fogues
 * 
 */

class CancelState extends State {

	private CancelStateMethod Method;

	protected CancelState() {
		super("CANCEL_STATE");
		type = State.CANCEL;
	}

	public void setMethod(CancelStateMethod method) {
		Method = method;
	}

	public CancelStateMethod getMethod() {
		return Method;
	}

}
