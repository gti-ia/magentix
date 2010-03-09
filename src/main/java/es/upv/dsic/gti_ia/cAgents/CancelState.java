package es.upv.dsic.gti_ia.cAgents;

/**
 * 
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
