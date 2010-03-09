package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */
public abstract class ActionState extends State {

	private ActionStateMethod Method;

	public ActionState(String n) {
		super(n);
		type = State.ACTION;
	}

	public void setMethod(ActionStateMethod method) {
		Method = method;
	}

	public ActionStateMethod getMethod() {
		return Method;
	}

}
