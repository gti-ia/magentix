package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class RequestInitiatorFactory extends CProcessorFactory{
	
	public RequestInitiatorFactory(String name, ACLMessage sendMessage) {		
		super(name, new ACLMessage(ACLMessage.INFORM), 1);
		
		//B
		this.getCProcessor().registerFirstState(new GenericBeginState("B"));
		
		//S
		GenericSendState S = new GenericSendState("S");
		S.setMessageTemplate(sendMessage);
		this.getCProcessor().registerState(S);
		this.getCProcessor().addTransition("B", "S");
		
		//W1
		this.getCProcessor().registerState(new WaitState("W1",10000));
		this.getCProcessor().addTransition("S", "W1");
		
		//R1
		GenericReceiveState R1 = new GenericReceiveState("R1");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		R1.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(R1);
		this.getCProcessor().addTransition("W1", "R1");
		
		//R2
		GenericReceiveState R2 = new GenericReceiveState("R2");
		receiveFilter = new ACLMessage(ACLMessage.REFUSE);
		R2.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(R2);
		this.getCProcessor().addTransition("W1", "R2");
		
		//R3
		GenericReceiveState R3 = new GenericReceiveState("R3");
		receiveFilter = new ACLMessage(ACLMessage.AGREE);
		R3.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(R3);
		this.getCProcessor().addTransition("W1", "R3");
		
		//RW1
		GenericReceiveState RW1 = new GenericReceiveState("RW1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("Purpose", "WaitMessage");
		RW1.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(RW1);
		this.getCProcessor().addTransition("W1", "RW1");
		this.getCProcessor().addTransition("RW1", "W1");
		
		//W2
		this.getCProcessor().registerState(new WaitState("W2",10000));
		this.getCProcessor().addTransition("R3", "W2");
		
		//R4
		GenericReceiveState R4 = new GenericReceiveState("R4");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		R4.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(R4);
		this.getCProcessor().addTransition("W2", "R4");
		
		//R5
		GenericReceiveState R5 = new GenericReceiveState("R5");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		R5.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(R5);
		this.getCProcessor().addTransition("W2", "R5");
		
		//R6
		GenericReceiveState R6 = new GenericReceiveState("R6");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "ref");
		R6.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(R6);
		this.getCProcessor().addTransition("W2", "R6");
		
		//RW2
		GenericReceiveState RW2 = new GenericReceiveState("RW2");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("Purpose", "WaitMessage");
		RW2.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(RW2);
		this.getCProcessor().addTransition("W2", "RW2");
		this.getCProcessor().addTransition("RW2", "W2");
		
		//final
		this.getCProcessor().registerState(new GenericFinalState("F"));
		this.getCProcessor().addTransition("R1", "F");
		this.getCProcessor().addTransition("R2", "F");
		this.getCProcessor().addTransition("R4", "F");
		this.getCProcessor().addTransition("R5", "F");
		this.getCProcessor().addTransition("R6", "F");
		
		//exception states
		this.getCProcessor().registerState(new GenericCancelState());
		this.getCProcessor().registerState(new GenericNotAcceptedMessagesState());
		this.getCProcessor().registerState(new GenericSendingErrorsState());
		this.getCProcessor().registerState(new GenericTerminatedFatherState());
	}
	
	public void changeState(State s) throws Exception{
		//if new state's type is different to the previous state's type, rise exception
		if(s.getType() != this.getCProcessor().getState(s.getName()).getType()){
			throw new Exception("Error: type of the new state and type of the previous state do not match");
		}
		else
			this.getCProcessor().registerState(s);
	}

}
