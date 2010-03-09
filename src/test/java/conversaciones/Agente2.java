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
		factory.cProcessorTemplate().registerFirstState(new GenericBeginState("beginState"));
		
		//send
		SendState1 sendState0 = new SendState1("sendState0");
		ACLMessage sendTemplate0 = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate0.setHeader("Purpose", "Hello");
		sendTemplate0.setContent("Hi! I'm "+this.getName());
		sendTemplate0.setReceiver(new AgentID("agenteRes"));
		sendState0.setMessageTemplate(sendTemplate0);
		factory.cProcessorTemplate().registerState(sendState0);
		factory.cProcessorTemplate().addTransition("beginState", "sendState0");
		
		//wait
		factory.cProcessorTemplate().registerState(new WaitState("waitState",1000000000));
		factory.cProcessorTemplate().addTransition("sendState0", "waitState");
		
		//receive
		ReceiveState1 receiveState = new ReceiveState1("receiveState");
		ACLMessage receiveFilter = new ACLMessage(ACLMessage.AGREE);
		receiveFilter.setHeader("Purpose", "Hello");
		receiveState.setAcceptFilter(receiveFilter);
		factory.cProcessorTemplate().registerState(receiveState);
		factory.cProcessorTemplate().addTransition("waitState", "receiveState");
		
		SendState1 sendState = new SendState1("sendState");
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setHeader("Purpose", "AnimicState");
		sendTemplate.setContent("How are you feeling today?");
		sendTemplate.setReceiver(new AgentID("agenteRes"));
		sendState.setMessageTemplate(sendTemplate);
		factory.cProcessorTemplate().registerState(sendState);
		factory.cProcessorTemplate().addTransition("receiveState", "sendState");
		
		//wait
		factory.cProcessorTemplate().registerState(new WaitState("waitState2",1000000000));
		factory.cProcessorTemplate().addTransition("sendState", "waitState2");
		
		//receive happy
		ReceiveState1 receiveStateHappy = new ReceiveState1("receiveStateHappy");
		ACLMessage receiveFilterHappy = new ACLMessage(ACLMessage.AGREE);
		receiveFilterHappy.setHeader("AnimicState", "Happy");
		receiveStateHappy.setAcceptFilter(receiveFilterHappy);
		factory.cProcessorTemplate().registerState(receiveStateHappy);
		factory.cProcessorTemplate().addTransition("waitState2", "receiveStateHappy");
		
		//receive sad
		ReceiveState1 receiveStateSad = new ReceiveState1("receiveStateSad");
		ACLMessage receiveFilterSad = new ACLMessage(ACLMessage.AGREE);
		receiveFilterSad.setHeader("AnimicState", "Sad");
		receiveStateSad.setAcceptFilter(receiveFilterSad);
		factory.cProcessorTemplate().registerState(receiveStateSad);
		factory.cProcessorTemplate().addTransition("waitState2", "receiveStateSad");
		
		//action happy
		factory.cProcessorTemplate().registerState(new ActionStateHappy("actionStateHappy"));
		factory.cProcessorTemplate().addTransition("receiveStateHappy", "actionStateHappy");
		
		//action sad
		factory.cProcessorTemplate().registerState(new ActionStateSad("actionStateSad"));
		factory.cProcessorTemplate().addTransition("receiveStateSad", "actionStateSad");
		
		//exception states
		factory.cProcessorTemplate().registerState(new GenericCancelState());
		factory.cProcessorTemplate().registerState(new GenericNotAcceptedMessagesState());
		factory.cProcessorTemplate().registerState(new GenericSendingErrorsState());
		factory.cProcessorTemplate().registerState(new GenericTerminatedFatherState());
		
		//final
		factory.cProcessorTemplate().registerState(new GenericFinalState("finalState"));
		factory.cProcessorTemplate().addTransition("actionStateHappy", "finalState");
		factory.cProcessorTemplate().addTransition("actionStateSad", "finalState");
		
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
			//send
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
