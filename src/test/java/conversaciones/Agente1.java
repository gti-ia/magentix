package conversaciones;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.*;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
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
		factoriaRes.cProcessorTemplate().registerFirstState(new GenericBeginState("beginState"));
		
		//wait
		factoriaRes.cProcessorTemplate().registerState(new WaitState("waitState",10));
		factoriaRes.cProcessorTemplate().addTransition("beginState", "waitState");
		
		//receive wait1
		ReceiveState1 receiveWaitState = new ReceiveState1("receiveWaitState1");
		ACLMessage receiveWaitFilter = new ACLMessage(ACLMessage.INFORM);
		receiveWaitFilter.setHeader("Purpose", "WaitMessage");
		receiveWaitState.setAcceptFilter(receiveWaitFilter);
		factoriaRes.cProcessorTemplate().registerState(receiveWaitState);
		factoriaRes.cProcessorTemplate().addTransition("waitState", "receiveWaitState1");
		
		//receive
		ReceiveState1 receiveState = new ReceiveState1("receiveState");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.REQUEST);
		receiveFilter.setHeader("Purpose", "Hello");
		receiveState.setAcceptFilter(receiveFilter);
		factoriaRes.cProcessorTemplate().registerState(receiveState);
		factoriaRes.cProcessorTemplate().addTransition("waitState", "receiveState");
		
		//send
		SendState1 sendState = new SendState1("sendState");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.AGREE);
		sendTemplate.setHeader("Purpose", "Hello");
		sendTemplate.setContent("Hello! I'm "+this.getName()+" I'm feeling different today");
		sendState.setMessageTemplate(sendTemplate);
		factoriaRes.cProcessorTemplate().registerState(sendState);
		factoriaRes.cProcessorTemplate().addTransition("receiveState", "sendState");
		
		//sendT
		
		class AnswerMethodClass extends SendStateMethod{

			protected void run(CProcessor myProcessor, ACLMessage messageToSend, String Next) {
				messageToSend.setContent("hello");
				Next = "waitState2";
			}
		}
		
		AnswerMethodClass M = new AnswerMethodClass(); 
		ACLMessage answerTemplate = new ACLMessage(ACLMessage.AGREE);
			
		SendState sendAnswer = new SendState("sendAnswer");
		
		sendAnswer.setMessageTemplate(answerTemplate);
		sendAnswer.setMethod(M);
		factoriaRes.cProcessorTemplate().registerState(sendAnswer);
		factoriaRes.cProcessorTemplate().addTransition("receiveState", "sendAnswer");
		
		//wait2
		factoriaRes.cProcessorTemplate().registerState(new WaitState("waitState2",10));
		factoriaRes.cProcessorTemplate().addTransition("sendState", "waitState2");
		
		//receive wait2
		ReceiveState1 receiveWaitState2 = new ReceiveState1("receiveWaitState2");
		ACLMessage receiveWaitFilter2 = new ACLMessage(ACLMessage.INFORM);
		receiveWaitFilter2.setHeader("Purpose", "WaitMessage");
		receiveWaitState2.setAcceptFilter(receiveWaitFilter2);
		factoriaRes.cProcessorTemplate().registerState(receiveWaitState2);
		factoriaRes.cProcessorTemplate().addTransition("waitState2", "receiveWaitState2");
		factoriaRes.cProcessorTemplate().addTransition("receiveWaitState2", "waitState2");
		
		//receive2
		ReceiveState1 receiveState2 = new ReceiveState1("receiveState2");
		ACLMessage receiveFilter2 = new ACLMessage(ACLMessage.REQUEST);
		receiveFilter2.setHeader("Purpose", "AnimicState");
		receiveState2.setAcceptFilter(receiveFilter2);
		factoriaRes.cProcessorTemplate().registerState(receiveState2);
		factoriaRes.cProcessorTemplate().addTransition("waitState2", "receiveState2");
		
		//send2
		SendState1 sendState2 = new SendState1("sendState2");
		ACLMessage sendTemplate2 = new ACLMessage(ACLMessage.AGREE);
		//sendTemplate2.setHeader("AnimicState", "Sad");
		sendTemplate2.setHeader("AnimicState", "Happy");
		sendTemplate2.setContent("I'm feeling Happy");
		sendState2.setMessageTemplate(sendTemplate2);
		factoriaRes.cProcessorTemplate().registerState(sendState2);
		factoriaRes.cProcessorTemplate().addTransition("receiveState2", "sendState2");	
		
		//action
		factoriaRes.cProcessorTemplate().registerState(new ActionState1("actionState"));
		factoriaRes.cProcessorTemplate().addTransition("sendState2", "actionState");
		
		//final
		factoriaRes.cProcessorTemplate().registerState(new GenericFinalState("finalState"));
		factoriaRes.cProcessorTemplate().addTransition("actionState", "finalState");
		
		//exception states
		factoriaRes.cProcessorTemplate().registerState(new GenericCancelState());
		factoriaRes.cProcessorTemplate().registerState(new GenericNotAcceptedMessagesState());
		factoriaRes.cProcessorTemplate().registerState(new GenericSendingErrorsState());
		factoriaRes.cProcessorTemplate().registerState(new GenericTerminatedFatherState());
		
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
