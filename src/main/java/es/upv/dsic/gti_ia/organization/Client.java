package es.upv.dsic.gti_ia.organization;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.*;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Client extends CAgent{

	public Client(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void setFactories() {
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST); //the template has no use in this example
		CProcessorFactory parentFactory = new CProcessorFactory("parentFactory", template, 1);
		CProcessor parentProcessor = parentFactory.getCProcessor();
		
		//B
		parentProcessor.registerFirstState(new GenericBeginState("B"));
		
		//SRR activate Register Role
		SendState1 SRR = new SendState1("SRR");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("start", "registerRole");
		sendTemplate.setContent("Register Role");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(getAid());
		SRR.setMessageTemplate(sendTemplate);
		parentProcessor.registerState(SRR);
		parentProcessor.addTransition("B", "SRR");
		
		//WRR
		parentProcessor.registerState(new WaitState("WRR",10000));
		parentProcessor.addTransition("SRR", "WRR");
		
		//RWRR
		ReceiveState1 RWRR = new ReceiveState1("RWRR");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("purpose", "waitMessage");
		RWRR.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RWRR);
		parentProcessor.addTransition("WRR", "RWRR");
		parentProcessor.addTransition("RWRR", "WRR");
		
		//RRR1
		ReceiveState1 RRR1 = new ReceiveState1("RRR1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		RRR1.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RRR1);
		parentProcessor.addTransition("WRR", "RRR1");
		
		//RRR2
		ReceiveState1 RRR2 = new ReceiveState1("RRR2");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		RRR2.setAcceptFilter(receiveFilter);
		parentProcessor.registerState(RRR2);
		parentProcessor.addTransition("WRR", "RRR2");
		
		//S
		
		
		//B
		factory.getCProcessor().registerFirstState(new GenericBeginState("B"));
		
		//S
		SendState1 S = new SendState1("S");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setContent("This is a generic request message");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(new AgentID("participant"));
		S.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S);
		factory.getCProcessor().addTransition("B", "S");
		
		//W1
		factory.getCProcessor().registerState(new WaitState("W1",1));
		factory.getCProcessor().addTransition("S", "W1");
		
		//R1
		ReceiveState1 R1 = new ReceiveState1("R1");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		R1.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R1);
		factory.getCProcessor().addTransition("W1", "R1");
		
		//R2
		ReceiveState1 R2 = new ReceiveState1("R2");
		receiveFilter = new ACLMessage(ACLMessage.REFUSE);
		R2.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R2);
		factory.getCProcessor().addTransition("W1", "R2");
		
		//R3
		ReceiveState1 R3 = new ReceiveState1("R3");
		receiveFilter = new ACLMessage(ACLMessage.AGREE);
		R3.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R3);
		factory.getCProcessor().addTransition("W1", "R3");
		
		//RW1
		ReceiveState1 RW1 = new ReceiveState1("RW1");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("Purpose", "WaitMessage");
		RW1.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(RW1);
		factory.getCProcessor().addTransition("W1", "RW1");
		factory.getCProcessor().addTransition("RW1", "W1");
		
		//W2
		factory.getCProcessor().registerState(new WaitState("W2",1));
		factory.getCProcessor().addTransition("R3", "W2");
		
		//R4
		ReceiveState1 R4 = new ReceiveState1("R4");
		receiveFilter = new ACLMessage(ACLMessage.FAILURE);
		R4.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R4);
		factory.getCProcessor().addTransition("W2", "R4");
		
		//R5
		ReceiveState1 R5 = new ReceiveState1("R5");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "done");
		R5.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R5);
		factory.getCProcessor().addTransition("W2", "R5");
		
		//R6
		ReceiveState1 R6 = new ReceiveState1("R6");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("inform", "ref");
		R6.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R6);
		factory.getCProcessor().addTransition("W2", "R6");
		
		//RW2
		ReceiveState1 RW2 = new ReceiveState1("RW2");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("Purpose", "WaitMessage");
		RW2.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(RW2);
		factory.getCProcessor().addTransition("W2", "RW2");
		factory.getCProcessor().addTransition("RW2", "W2");
		
		//final
		factory.getCProcessor().registerState(new GenericFinalState("F"));
		factory.getCProcessor().addTransition("R1", "F");
		factory.getCProcessor().addTransition("R2", "F");
		factory.getCProcessor().addTransition("R4", "F");
		factory.getCProcessor().addTransition("R5", "F");
		factory.getCProcessor().addTransition("R6", "F");
		
		//exception states
		factory.getCProcessor().registerState(new GenericCancelState());
		factory.getCProcessor().registerState(new GenericNotAcceptedMessagesState());
		factory.getCProcessor().registerState(new GenericSendingErrorsState());
		factory.getCProcessor().registerState(new GenericTerminatedFatherState());
		
		//attach factory to agent
		this.addStartingFactory(factory, "C"+this.hashCode()+System.currentTimeMillis());
	}
	
	class ReceiveState1 extends ReceiveState{

		public ReceiveState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
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
	}
	
	class SendState1 extends SendState{

		public SendState1(String n) {
			super(n);
		}

		@Override
		protected ACLMessage run(CProcessor myProcessor, ACLMessage lastReceivedMessage) {
			this.messageTemplate.setConversationId(myProcessor.getConversationID());
			return this.messageTemplate;
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
	}

}
