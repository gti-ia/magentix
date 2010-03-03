package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

/**
 * 
 * @author Ricard Lopez Fogues
 *
 */

public abstract class SendState extends State{
	public SendState(String n) {
		super(n);
		type = State.SEND;
	}

	protected ACLMessage messageTemplate;
	
	public void setMessageTemplate(ACLMessage mt){
		messageTemplate = mt;
	}
	
	public ACLMessage getMessageTemplate(){
		return messageTemplate;
	}
	
	protected abstract ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage);
	
	protected abstract String getNext(CProcessor myProcessor, ACLMessage lastReceivedMessage);


}
