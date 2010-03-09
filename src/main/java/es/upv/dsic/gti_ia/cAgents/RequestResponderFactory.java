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
		this.cProcessorTemplate().registerFirstState(new GenericBeginState("B"));
		
		//W
		this.cProcessorTemplate().registerState(new WaitState("W",1));
		this.cProcessorTemplate().addTransition("B", "W");
		
		//R
		R.setName("R");
		this.cProcessorTemplate().registerState(R);
		this.cProcessorTemplate().addTransition("W", "R");
		
		//RW
		GenericReceiveState RW = new GenericReceiveState("RW");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RW.setAcceptFilter(receiveFilter);
		this.cProcessorTemplate().registerState(RW);
		this.cProcessorTemplate().addTransition("W", "RW");
		this.cProcessorTemplate().addTransition("RW", "W");
		
		//S1
		ReplySendState S1 = new ReplySendState("S1");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		sendTemplate.setContent("Request message not understood");
		S1.setMessageTemplate(sendTemplate);
		this.cProcessorTemplate().registerState(S1);
		this.cProcessorTemplate().addTransition("R", "S1");
		
		//S2
		ReplySendState S2 = new ReplySendState("S2");
		sendTemplate = new ACLMessage(ACLMessage.REFUSE);
		sendTemplate.setContent("Request message refused");
		S2.setMessageTemplate(sendTemplate);
		this.cProcessorTemplate().registerState(S2);
		this.cProcessorTemplate().addTransition("R", "S2");
		
		//S3
		ReplySendState S3 = new ReplySendState("S3");
		sendTemplate = new ACLMessage(ACLMessage.AGREE);
		sendTemplate.setContent("Request message agree");
		S3.setMessageTemplate(sendTemplate);
		this.cProcessorTemplate().registerState(S3);
		this.cProcessorTemplate().addTransition("R", "S3");
		
		//A
		A.setName("A");
		this.cProcessorTemplate().registerState(A);
		this.cProcessorTemplate().addTransition("S3", "A");
		
		//S4
		ReplySendState S4 = new ReplySendState("S4");
		sendTemplate = new ACLMessage(ACLMessage.FAILURE);
		sendTemplate.setContent("Failure performing the action");
		S4.setMessageTemplate(sendTemplate);
		this.cProcessorTemplate().registerState(S4);
		this.cProcessorTemplate().addTransition("A", "S4");
		
		//S5
		ReplySendState S5 = new ReplySendState("S5");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "done");
		sendTemplate.setContent("Action done");
		S5.setMessageTemplate(sendTemplate);
		this.cProcessorTemplate().registerState(S5);
		this.cProcessorTemplate().addTransition("A", "S5");
		
		//S6
		ReplySendState S6 = new ReplySendState("S6");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "ref");
		sendTemplate.setContent("Action ref");
		S6.setMessageTemplate(sendTemplate);
		this.cProcessorTemplate().registerState(S6);
		this.cProcessorTemplate().addTransition("A", "S6");
		
		//final
		this.cProcessorTemplate().registerState(new GenericFinalState("F"));
		this.cProcessorTemplate().addTransition("S1", "F");
		this.cProcessorTemplate().addTransition("S2", "F");
		this.cProcessorTemplate().addTransition("S4", "F");
		this.cProcessorTemplate().addTransition("S5", "F");
		this.cProcessorTemplate().addTransition("S6", "F");
		
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
		else
			this.cProcessorTemplate().registerState(s);
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
