package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */
public abstract class ActionState extends State {

	public ActionState(String n) {
		super(n);
		type = State.ACTION;
	}
	
	protected abstract String run(CProcessor myProcessor);
}
