package conversaciones;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Initiator extends CAgent{

	public Initiator(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void setFactories() {
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST); //the template has no use in this example
		CProcessorFactory factory = new CProcessorFactory("Initiator", template, 1);
		
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
		factory.getCProcessor().registerState(new WaitState("W1",100000));
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
		
		//W2
		factory.getCProcessor().registerState(new WaitState("W2",100000));
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
