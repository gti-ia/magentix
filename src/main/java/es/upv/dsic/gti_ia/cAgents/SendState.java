// CAMBIOS EN ESTA CLASE

// Sustituido el metodo run por registrar objeto SendStateMethod


package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class SendState extends State {

	ACLMessage messageTemplate;
	private SendStateMethod methodToRun;

	public SendState(String n) {
		super(n);
		type = State.SEND;
	}

	
	public void setMessageTemplate(ACLMessage mt){
		messageTemplate = mt;
	}
	
	public ACLMessage getMessageTemplate(){
		return messageTemplate;
	}
	
	public void setMethod(SendStateMethod method) {
		methodToRun = method;
	}

	public SendStateMethod getMethod() {
		return methodToRun;
	}

}
