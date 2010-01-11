package conversaciones;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Participant extends CAgent{

	public Participant(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void setFactories() {
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST);
		CProcessorFactory factory = new CProcessorFactory("Participant", template, 1);
		
		//B
		factory.getCProcessor().registerFirstState(new GenericBeginState("B"));
		
		//W
		factory.getCProcessor().registerState(new WaitState("W",100000));
		factory.getCProcessor().addTransition("B", "W");
		
		//R
		ReceiveState1 R = new ReceiveState1("R");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.REQUEST);
		R.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(R);
		factory.getCProcessor().addTransition("W", "R");
		
		//RW
		ReceiveState1 RW = new ReceiveState1("RW");
		receiveFilter = new ACLMessage(ACLMessage.INFORM);
		receiveFilter.setHeader("Purpose", "WaitMessage");
		RW.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(RW);
		factory.getCProcessor().addTransition("W", "RW");
		factory.getCProcessor().addTransition("RW", "W");
		
		//S1
		SendState1 S1 = new SendState1("S1");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		sendTemplate.setContent("Request message not understood");
		sendTemplate.setSender(getAid());
		S1.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S1);
		factory.getCProcessor().addTransition("R", "S1");
		
		//S2
		SendState1 S2 = new SendState1("S2");
		sendTemplate = new ACLMessage(ACLMessage.REFUSE);
		sendTemplate.setContent("Request message refused");
		sendTemplate.setSender(getAid());
		S2.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S2);
		factory.getCProcessor().addTransition("R", "S2");
		
		//S3
		SendState1 S3 = new SendState1("S3");
		sendTemplate = new ACLMessage(ACLMessage.AGREE);
		sendTemplate.setContent("Request message agree");
		sendTemplate.setSender(getAid());
		S3.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S3);
		factory.getCProcessor().addTransition("R", "S3");
		
		//A
		factory.getCProcessor().registerState(new ActionState1("A"));
		factory.getCProcessor().addTransition("S3", "A");
		
		//S4
		SendState1 S4 = new SendState1("S4");
		sendTemplate = new ACLMessage(ACLMessage.FAILURE);
		sendTemplate.setContent("Failure performing the action");
		sendTemplate.setSender(getAid());
		S4.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S4);
		factory.getCProcessor().addTransition("A", "S4");
		
		//S5
		SendState1 S5 = new SendState1("S5");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "done");
		sendTemplate.setContent("Action done");
		sendTemplate.setSender(getAid());
		S5.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S5);
		factory.getCProcessor().addTransition("A", "S5");
		
		//S6
		SendState1 S6 = new SendState1("S6");
		sendTemplate = new ACLMessage(ACLMessage.INFORM);
		sendTemplate.setHeader("inform", "ref");
		sendTemplate.setContent("Action ref");
		sendTemplate.setSender(getAid());
		S6.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(S6);
		factory.getCProcessor().addTransition("A", "S6");
		
		//final
		factory.getCProcessor().registerState(new GenericFinalState("F"));
		factory.getCProcessor().addTransition("S1", "F");
		factory.getCProcessor().addTransition("S2", "F");
		factory.getCProcessor().addTransition("S4", "F");
		factory.getCProcessor().addTransition("S5", "F");
		factory.getCProcessor().addTransition("S6", "F");
		
		//exception states
		factory.getCProcessor().registerState(new GenericCancelState());
		factory.getCProcessor().registerState(new GenericNotAcceptedMessagesState());
		factory.getCProcessor().registerState(new GenericSendingErrorsState());
		factory.getCProcessor().registerState(new GenericTerminatedFatherState());
		
		//attach factory to agent
		this.addFactory(factory);
		
	}
	
	class ReceiveState1 extends ReceiveState{

		public ReceiveState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			String next = "S3";
			/*Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
		        // Get element
		        next = it.next();		        
		    }*/
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
			this.messageTemplate.setReceiver(lastReceivedMessage.getSender());
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
	
	class ActionState1 extends ActionState{

		public ActionState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor) {
			System.out.println("Performing an action...");		
			
			String next = "S5";
			/*Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(this.getName());
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
		        // Get element
		        next = it.next();
		    }*/
			return next;
		}
	}

}
