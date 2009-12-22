package es.upv.dsic.gti_ia.cAgents;

public abstract class FinalState extends State{

	public FinalState(String n) {
		super(n);
		type = State.FINAL;
	}
	
	protected abstract String run(CProcessor myProcessor);
	
}
