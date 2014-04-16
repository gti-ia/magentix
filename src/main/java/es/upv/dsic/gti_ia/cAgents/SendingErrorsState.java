package es.upv.dsic.gti_ia.cAgents;


/**
 * 
 * @author Ricard Lopez Fogues
 * @author Javier Jorge Cano - jjorge@dsic.upv.es
 */

public class SendingErrorsState extends State {

	private SendingErrorsStateMethod methodToRun;

	public SendingErrorsState() {
		super("SENDING_ERRORS_STATE");
		type = State.SENDING_ERRORS;
	}

	public void setMethod(SendingErrorsStateMethod method) {
		methodToRun = method;
	}

	public SendingErrorsStateMethod getMethod() {
		return methodToRun;
	}

}
