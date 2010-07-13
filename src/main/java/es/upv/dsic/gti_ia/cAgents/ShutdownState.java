package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

class ShutdownState extends State {

	private ShutdownStateMethod Method;

	protected ShutdownState() {
		super("SHUTDOWN");
		type = State.SHUTDOWN;
	}

	public void setMethod(ShutdownStateMethod method) {
		Method = method;
	}

	public ShutdownStateMethod getMethod() {
		return Method;
	}

}
