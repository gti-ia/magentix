package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class RequestInitiatorFactory extends CProcessorFactory{
	
	public RequestInitiatorFactory(String name, ACLMessage template, ACLMessage sendMessage){		
		super(name, template, 1);
		
		//B
		this.cProcessorTemplate().registerFirstState(new GenericBeginState("B"));
		
		//S
		GenericSendState S = new GenericSendState("S");
		S.setMessageTemplate(sendMessage);
		this.cProcessorTemplate().registerState(S);
		this.cProcessorTemplate().addTransition("B", "S");
		
		//W1
		this.cProcessorTemplate().registerState(new WaitState("W1",10000));
		this.cProcessorTemplate().addTransition("S", "W1");
		
		//R1
		GenericReceiveState R1 = new GenericReceiveState("R1");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		R1.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(R1);
		this.cProcessorTemplate().addTransition("W1", "R1");
		
		//R2
		GenericReceiveState R2 = new GenericReceiveState("R2");
		receiveFilter = new ACLMessage(ACLMessage.REFUSE);
		R2.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(R2);
		this.cProcessorTemplate().addTransition("W1", "R2");
		
		//R3
		GenericReceiveState R3 = new GenericReceiveState("R3");
		receiveFilter = new ACLMessage(ACLMessage.AGREE);
		R3.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(R3);
		this.cProcessorTemplate().addTransition("W1", "R3");
		
		//RW1
		GenericReceiveState RW1 = new GenericReceiveState("RW1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RW1.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(RW1);
		this.cProcessorTemplate().addTransition("W1", "RW1");
		this.cProcessorTemplate().addTransition("RW1", "W1");
		
		//W2
		this.cProcessorTemplate().registerState(new WaitState("W2",10000));
		this.cProcessorTemplate().addTransition("R3", "W2");
		
		//R4
		GenericReceiveState R4 = new GenericReceiveState("R4");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		R4.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(R4);
		this.cProcessorTemplate().addTransition("W2", "R4");
		
		//R5
		GenericReceiveState R5 = new GenericReceiveState("R5");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		R5.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(R5);
		this.cProcessorTemplate().addTransition("W2", "R5");
		
		//R6
		GenericReceiveState R6 = new GenericReceiveState("R6");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "ref");
		R6.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(R6);
		this.cProcessorTemplate().addTransition("W2", "R6");
		
		//RW2
		GenericReceiveState RW2 = new GenericReceiveState("RW2");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RW2.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(RW2);
		this.cProcessorTemplate().addTransition("W2", "RW2");
		this.cProcessorTemplate().addTransition("RW2", "W2");
		
		//final
		this.cProcessorTemplate().registerState(new GenericFinalState("F"));
		this.cProcessorTemplate().addTransition("R1", "F");
		this.cProcessorTemplate().addTransition("R2", "F");
		this.cProcessorTemplate().addTransition("R4", "F");
		this.cProcessorTemplate().addTransition("R5", "F");
		this.cProcessorTemplate().addTransition("R6", "F");
		
		//exception states
		this.cProcessorTemplate().registerState(new GenericCancelState());
		this.cProcessorTemplate().registerState(new GenericNotAcceptedMessagesState());
		this.cProcessorTemplate().registerState(new GenericSendingErrorsState());
		this.cProcessorTemplate().registerState(new GenericTerminatedFatherState());
	}
	
	public void changeState(State s) throws Exception{
		//if new state's type is different to the previous state's type, rise exception
		if(s.getType() != this.cProcessorTemplate().getState(s.getName()).getType()){
			throw new Exception("Error: type of the new state and type of the previous state do not match");
		}
		else{
			this.cProcessorTemplate().states.put(s.getName(), s);
		}
	}

}
