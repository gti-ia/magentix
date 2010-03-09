package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public class FinalState extends State{

	public FinalState(String n) {
		super(n);
		type = State.FINAL;
	}

	private FinalStateMethod methodToRun;
	
	public void setMethod(FinalStateMethod method) {
		methodToRun = method;
	}
	public FinalStateMethod getMethod() {
		return methodToRun;
	}
	
}
