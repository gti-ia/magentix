package es.upv.dsic.gti_ia.cAgents;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public class ReceiveState extends State {

	private MessageFilter acceptFilter;
	private ReceiveStateMethod methodToRun;

	public ReceiveState(String n) {
		super(n);
		type = State.RECEIVE;
	}

	public void setAcceptFilter(MessageFilter filter) {
		acceptFilter = filter;
	}

	public MessageFilter getAcceptFilter() {
		return acceptFilter;
	}

	public void setMethod(ReceiveStateMethod method) {
		methodToRun = method;
	}

	public ReceiveStateMethod getMethod() {
		return methodToRun;
	}

}
