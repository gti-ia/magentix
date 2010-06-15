package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public class BeginState extends State {

	private BeginStateMethod Method;

	public BeginState(String n) {
		super(n);
		type = State.BEGIN;
	}

	public void setMethod(BeginStateMethod method) {
		Method = method;
	}

	public BeginStateMethod getMethod() {
		return Method;
	}

}
