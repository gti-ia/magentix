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

public class Agente2 extends CAgent{

	public boolean finalizado = false;
	public Agente2(AgentID aid) throws Exception {
		super(aid);
	}
	
	@Override
	protected void setFactories() {

		ACLMessage template = new ACLMessage(ACLMessage.AGREE);
		template.setHeader("Purpose", "Hello");
		CProcessorFactory factory = new CProcessorFactory("Ini", template, 1);
		
		//begin
		factory.getCProcessor().registerFirstState(new GenericBeginState("beginState"));
		
		//send
		SendState1 sendState0 = new SendState1("sendState0");
		ACLMessage sendTemplate0 = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate0.setHeader("Purpose", "Hello");
		sendTemplate0.setContent("Hi! I'm "+this.getName());
		sendTemplate0.setReceiver(new AgentID("agenteRes"));
		sendState0.setMessageTemplate(sendTemplate0);
		factory.getCProcessor().registerState(sendState0);
		factory.getCProcessor().addTransition("beginState", "sendState0");
		
		//wait
		factory.getCProcessor().registerState(new WaitState("waitState",10));
		factory.getCProcessor().addTransition("sendState0", "waitState");
		
		//receive
		ReceiveState1 receiveState = new ReceiveState1("receiveState");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.AGREE);
		receiveFilter.setHeader("Purpose", "Hello");
		receiveState.setAcceptFilter(receiveFilter);
		factory.getCProcessor().registerState(receiveState);
		factory.getCProcessor().addTransition("waitState", "receiveState");
		
		//send
		SendState1 sendState = new SendState1("sendState");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("Purpose", "AnimicState");
		sendTemplate.setContent("How are you feeling today?");
		sendTemplate.setReceiver(new AgentID("agenteRes"));
		sendState.setMessageTemplate(sendTemplate);
		factory.getCProcessor().registerState(sendState);
		factory.getCProcessor().addTransition("receiveState", "sendState");
		
		//wait
		factory.getCProcessor().registerState(new WaitState("waitState2",10));
		factory.getCProcessor().addTransition("sendState", "waitState2");
		
		//receive happy
		ReceiveState1 receiveStateHappy = new ReceiveState1("receiveStateHappy");
		ACLMessage receiveFilterHappy = new ACLMessage(ACLMessage.AGREE);
		receiveFilterHappy.setHeader("AnimicState", "Happy");
		receiveStateHappy.setAcceptFilter(receiveFilterHappy);
		factory.getCProcessor().registerState(receiveStateHappy);
		factory.getCProcessor().addTransition("waitState2", "receiveStateHappy");
		
		//receive sad
		ReceiveState1 receiveStateSad = new ReceiveState1("receiveStateSad");
		ACLMessage receiveFilterSad = new ACLMessage(ACLMessage.AGREE);
		receiveFilterSad.setHeader("AnimicState", "Sad");
		receiveStateSad.setAcceptFilter(receiveFilterSad);
		factory.getCProcessor().registerState(receiveStateSad);
		factory.getCProcessor().addTransition("waitState2", "receiveStateSad");
		
		//action happy
		factory.getCProcessor().registerState(new ActionStateHappy("actionStateHappy"));
		factory.getCProcessor().addTransition("receiveStateHappy", "actionStateHappy");
		
		//action sad
		factory.getCProcessor().registerState(new ActionStateSad("actionStateSad"));
		factory.getCProcessor().addTransition("receiveStateSad", "actionStateSad");
		
		//exception states
		factory.getCProcessor().registerState(new GenericCancelState());
		factory.getCProcessor().registerState(new GenericNotAcceptedMessagesState());
		factory.getCProcessor().registerState(new GenericSendingErrorsState());
		factory.getCProcessor().registerState(new GenericTerminatedFatherState());
		
		//final
		factory.getCProcessor().registerState(new GenericFinalState("finalState"));
		factory.getCProcessor().addTransition("actionStateHappy", "finalState");
		factory.getCProcessor().addTransition("actionStateSad", "finalState");
		
		this.addStartingFactory(factory, "C"+this.hashCode()+System.currentTimeMillis());		
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
			System.out.println("Contenido messageTemplate "+this.messageTemplate.getContent());
			this.messageTemplate.setConversationId(myProcessor.getConversationID());
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
	
	class ActionStateHappy extends ActionState{

		public ActionStateHappy(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor) {
			Agente2 aux = (Agente2) myProcessor.getMyAgent();
			aux.finalizado = true;
			
			System.out.println("The other agent is Happy! :)");		
			
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
	
	class ActionStateSad extends ActionState{

		public ActionStateSad(String n) {
			super(n);
		}

		@Override
		protected String run(CProcessor myProcessor) {
			Agente2 aux = (Agente2) myProcessor.getMyAgent();
			aux.finalizado = true;
			
			System.out.println("The other agent is Sad! :(");		
			
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
