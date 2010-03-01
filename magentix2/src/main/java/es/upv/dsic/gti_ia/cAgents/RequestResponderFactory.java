package es.upv.dsic.gti_ia.cAgents;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.core.ACLMessage;

public class RequestResponderFactory extends CProcessorFactory{

	public RequestResponderFactory(String name, ACLMessage template,
			int availableConversations, ReceiveState R, ActionState A){
		super(name, template, availableConversations);
		
		ACLMessage receiveFilter;
		
		//B
		this.getCProcessor().registerFirstState(new GenericBeginState("B"));
		
		//W
		this.getCProcessor().registerState(new WaitState("W",1));
		this.getCProcessor().addTransition("B", "W");
		
		//R
		R.setName("R");
		this.getCProcessor().registerState(R);
		this.getCProcessor().addTransition("W", "R");
		
		//RW
		GenericReceiveState RW = new GenericReceiveState("RW");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RW.setAcceptFilter(receiveFilter);
		this.getCProcessor().registerState(RW);
		this.getCProcessor().addTransition("W", "RW");
		this.getCProcessor().addTransition("RW", "W");
		
		//S1
		ReplySendState S1 = new ReplySendState("S1");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		sendTemplate.setProtocol("fipa-request");
		sendTemplate.setContent("Request message not understood");
		S1.setMessageTemplate(sendTemplate);
		this.getCProcessor().registerState(S1);
		this.getCProcessor().addTransition("R", "S1");
		
		//S2
		ReplySendState S2 = new ReplySendState("S2");
		sendTemplate = new ACLMessage(ACLMessage.REFUSE);
		sendTemplate.setProtocol("fipa-request");
		sendTemplate.setContent("Request message refused");
		S2.setMessageTemplate(sendTemplate);
		this.getCProcessor().registerState(S2);
		this.getCProcessor().addTransition("R", "S2");
		
		//S3
		ReplySendState S3 = new ReplySendState("S3");
		sendTemplate = new ACLMessage(ACLMessage.AGREE);
		sendTemplate.setProtocol("fipa-request");
		sendTemplate.setContent("Request message agree");
		S3.setMessageTemplate(sendTemplate);
		this.getCProcessor().registerState(S3);
		this.getCProcessor().addTransition("R", "S3");
		
		//A
		A.setName("A");
		this.getCProcessor().registerState(A);
		this.getCProcessor().addTransition("S3", "A");
		
		//S4
		ReplySendState S4 = new ReplySendState("S4");
		sendTemplate = new ACLMessage(ACLMessage.FAILURE);
		sendTemplate.setProtocol("fipa-request");
		sendTemplate.setContent("Failure performing the action");
		S4.setMessageTemplate(sendTemplate);
		this.getCProcessor().registerState(S4);
		this.getCProcessor().addTransition("A", "S4");
		
		//S5
		ReplySendState S5 = new ReplySendState("S5");
		sendTemplate.setProtocol("fipa-request");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "done");
		sendTemplate.setContent("Action done");
		S5.setMessageTemplate(sendTemplate);
		this.getCProcessor().registerState(S5);
		this.getCProcessor().addTransition("A", "S5");
		
		//S6
		ReplySendState S6 = new ReplySendState("S6");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setProtocol("fipa-request");
		sendTemplate.setHeader("inform", "ref");
		sendTemplate.setContent("Action ref");
		S6.setMessageTemplate(sendTemplate);
		this.getCProcessor().registerState(S6);
		this.getCProcessor().addTransition("A", "S6");
		
		//final
		this.getCProcessor().registerState(new GenericFinalState("F"));
		this.getCProcessor().addTransition("S1", "F");
		this.getCProcessor().addTransition("S2", "F");
		this.getCProcessor().addTransition("S4", "F");
		this.getCProcessor().addTransition("S5", "F");
		this.getCProcessor().addTransition("S6", "F");
		
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
	
	class ReplySendState extends SendState{

		public ReplySendState(String n) {
			super(n);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected String getNext(CProcessor myProcessor,
				ACLMessage lastReceivedMessage) {
			String next = "";
			Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
		        // Get element
		        next = it.next();
		    }
			return next;
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			this.messageTemplate.setReceiver(lastReceivedMessage.getSender());
			this.messageTemplate.setSender(myProcessor.getMyAgent().getAid());
			this.messageTemplate.setConversationId(myProcessor.getConversationID());
			if(myProcessor.internalData.get("outmsg") != null)
				this.messageTemplate.setContent(((ACLMessage) myProcessor.internalData.get("outmsg")).getContent());
			return this.messageTemplate;
		}
		
	}

}
