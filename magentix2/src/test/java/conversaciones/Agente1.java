package conversaciones;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.*;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Agente1 extends CAgent{

	public boolean finalizado = false;
	public Agente1(AgentID aid) throws Exception {
		super(aid);		
	}
	
	@Override
	protected void setFactories() {
		ACLMessage template = new ACLMessage(ACLMessage.REQUEST);
		template.setHeader("Purpose", "Hello");
		CProcessorFactory factoriaRes = new CProcessorFactory("Res", template, 5);
		
		//begin
		factoriaRes.getCProcessor().registerFirstState(new GenericBeginState("beginState"));
		
		//wait
		factoriaRes.getCProcessor().registerState(new WaitState("waitState",10));
		factoriaRes.getCProcessor().addTransition("beginState", "waitState");
		
		//receive wait1
		ReceiveState1 receiveWaitState = new ReceiveState1("receiveWaitState1");
		ACLMessage receiveWaitFilter = new ACLMessage(ACLMessage.INFORM);
		receiveWaitFilter.setHeader("purpose", "waitMessage");
		receiveWaitState.setAcceptFilter(receiveWaitFilter);
		factoriaRes.getCProcessor().registerState(receiveWaitState);
		factoriaRes.getCProcessor().addTransition("waitState", "receiveWaitState1");
		
		//receive
		ReceiveState1 receiveState = new ReceiveState1("receiveState");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.REQUEST);
		receiveFilter.setHeader("Purpose", "Hello");
		receiveState.setAcceptFilter(receiveFilter);
		factoriaRes.getCProcessor().registerState(receiveState);
		factoriaRes.getCProcessor().addTransition("waitState", "receiveState");
		
		//send
		SendState1 sendState = new SendState1("sendState");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.AGREE);
		sendTemplate.setHeader("Purpose", "Hello");
		sendTemplate.setContent("Hello! I'm "+this.getName()+" I'm feeling different today");
		sendState.setMessageTemplate(sendTemplate);
		factoriaRes.getCProcessor().registerState(sendState);
		factoriaRes.getCProcessor().addTransition("receiveState", "sendState");
		
		//wait2
		factoriaRes.getCProcessor().registerState(new WaitState("waitState2",10));
		factoriaRes.getCProcessor().addTransition("sendState", "waitState2");
		
		//receive wait2
		ReceiveState1 receiveWaitState2 = new ReceiveState1("receiveWaitState2");
		ACLMessage receiveWaitFilter2 = new ACLMessage(ACLMessage.INFORM);
		receiveWaitFilter2.setHeader("purpose", "waitMessage");
		receiveWaitState2.setAcceptFilter(receiveWaitFilter2);
		factoriaRes.getCProcessor().registerState(receiveWaitState2);
		factoriaRes.getCProcessor().addTransition("waitState2", "receiveWaitState2");
		factoriaRes.getCProcessor().addTransition("receiveWaitState2", "waitState2");
		
		//receive2
		ReceiveState1 receiveState2 = new ReceiveState1("receiveState2");
		ACLMessage receiveFilter2 = new ACLMessage(ACLMessage.REQUEST);
		receiveFilter2.setHeader("Purpose", "AnimicState");
		receiveState2.setAcceptFilter(receiveFilter2);
		factoriaRes.getCProcessor().registerState(receiveState2);
		factoriaRes.getCProcessor().addTransition("waitState2", "receiveState2");
		
		//send2
		SendState1 sendState2 = new SendState1("sendState2");
		ACLMessage sendTemplate2 = new ACLMessage(ACLMessage.AGREE);
		sendTemplate2.setHeader("AnimicState", "Sad");
		//sendTemplate2.setHeader("AnimicState", "Happy");
		sendTemplate2.setContent("I'm feeling Happy");
		sendState2.setMessageTemplate(sendTemplate2);
		factoriaRes.getCProcessor().registerState(sendState2);
		factoriaRes.getCProcessor().addTransition("receiveState2", "sendState2");	
		
		//action
		factoriaRes.getCProcessor().registerState(new ActionState1("actionState"));
		factoriaRes.getCProcessor().addTransition("sendState2", "actionState");
		
		//final
		factoriaRes.getCProcessor().registerState(new GenericFinalState("finalState"));
		factoriaRes.getCProcessor().addTransition("actionState", "finalState");
		
		//exception states
		factoriaRes.getCProcessor().registerState(new GenericCancelState());
		factoriaRes.getCProcessor().registerState(new GenericNotAcceptedMessagesState());
		factoriaRes.getCProcessor().registerState(new GenericSendingErrorsState());
		factoriaRes.getCProcessor().registerState(new GenericTerminatedFatherState());
		
		//attach factory to agent
		this.addFactory(factoriaRes);
	}
	
	class ReceiveState1 extends ReceiveState{

		public ReceiveState1(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(msg.getContent());
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
			System.out.println("Agente: "+myProcessor.getMyAgent().getName()+"SendState conversationID "+lastReceivedMessage.getConversationId()+
					" hashcode "+lastReceivedMessage.hashCode()+" destino "+lastReceivedMessage.getSender());
			this.messageTemplate.setConversationId(myProcessor.getConversationID());
			this.messageTemplate.setReceiver(lastReceivedMessage.getSender());
			this.messageTemplate.setSender(myProcessor.getMyAgent().getAid());
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
			Agente1 aux = (Agente1) myProcessor.getMyAgent();
			aux.finalizado = true;
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
