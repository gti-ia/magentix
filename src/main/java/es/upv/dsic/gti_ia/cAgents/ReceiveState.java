package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.MessageTemplate;

/**
 * 
 * @author Ricard Lopez Fogues
 * 
 */

public class ReceiveState extends State {

	private MessageTemplate acceptFilter;
	private ReceiveStateMethod methodToRun;

	public ReceiveState(String n) {
		super(n);
		type = State.RECEIVE;
	}

	public void setAcceptFilter(MessageTemplate filter) {
		acceptFilter = filter;
	}

	public MessageTemplate getAcceptFilter() {
		return acceptFilter;
	}

	public void setMethod(ReceiveStateMethod method) {
		methodToRun = method;
	}

	public ReceiveStateMethod getMethod() {
		return methodToRun;
	}

}
