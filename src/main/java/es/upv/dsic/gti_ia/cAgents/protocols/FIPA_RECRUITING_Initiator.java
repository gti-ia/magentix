package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

public abstract class FIPA_RECRUITING_Initiator {
	
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "SEND_PROXY";
		};
	}

	protected abstract void setProxyMessage(CProcessor myProcessor,
			ACLMessage messageToSend);
	
	class SEND_PROXY_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			setProxyMessage(myProcessor, messageToSend);
			return "WAIT_FOR_PROXY_ACCEPTANCE";
		}
	}
	
	protected void doReceiveRefuse(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_REFUSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveRefuse(myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}
	
	protected void doReceiveAgree(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_AGREE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveAgree(myProcessor, messageReceived);
			return "WAIT_FOR_PROXY_RESULT";			
		}
	}
	
	protected void doReceiveFailureProxy(CProcessor myProcessor, ACLMessage msg) {
		System.out.println("Proxy action failed");
	}

	class RECEIVE_FAILURE_PROXY_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveFailureProxy(myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}
	
	protected void doReceiveFailureNoMatch(CProcessor myProcessor, ACLMessage msg) {
		System.out.println("No agent match found");
	}

	class RECEIVE_FAILURE_NO_MATCH_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveFailureNoMatch(myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}
	
	protected void doReceiveInform(CProcessor myProcessor, ACLMessage msg) {
		System.out.println("Proxy worked");
	}

	class RECEIVE_INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveInform(myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}
	
	protected void doFinalRecruitingInitiator(CProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSendedMessage();
	}

	class FINAL_RECRUITING_INITIATOR_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinalRecruitingInitiator(myProcessor, messageToSend);
		}
	}
	
	public CProcessorFactory newFactory(String name, MessageFilter filter, ACLMessage template,
			int availableConversations, CAgent myAgent, long timeout) {
		
		// Create factory

		/*if (filter == null) {
			filter = new MessageFilter("performative = REQUEST"); //falta AND protocol = fipa-request;
		}*/
		CProcessorFactory theFactory = new CProcessorFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();
		
		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// SEND_PROXY State

		SendState SEND_PROXY = new SendState("SEND_PROXY");

		SEND_PROXY.setMethod(new SEND_PROXY_Method());
		template = new ACLMessage(ACLMessage.UNKNOWN);
		SEND_PROXY.setMessageTemplate(template);
		processor.registerState(SEND_PROXY);
		processor.addTransition(BEGIN, SEND_PROXY);
		
		// WAIT_FOR_PROXY_ACCEPTANCE State

		WaitState WAIT_FOR_PROXY_ACCEPTANCE = new WaitState("WAIT_FOR_PROXY_ACCEPTANCE", timeout);
		processor.registerState(WAIT_FOR_PROXY_ACCEPTANCE);
		processor.addTransition(SEND_PROXY, WAIT_FOR_PROXY_ACCEPTANCE);
		
		// RECEIVE_REFUSE State
		
		ReceiveState RECEIVE_REFUSE = new ReceiveState("RECEIVE_REFUSE");
		RECEIVE_REFUSE.setMethod(new RECEIVE_REFUSE_Method());
		filter = new MessageFilter("performative = REFUSE AND protocol = fipa-recruiting");
		RECEIVE_REFUSE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REFUSE);
		processor.addTransition(WAIT_FOR_PROXY_ACCEPTANCE, RECEIVE_REFUSE);
		
		// RECEIVE_AGREE State
		
		ReceiveState RECEIVE_AGREE = new ReceiveState("RECEIVE_AGREE");
		RECEIVE_AGREE.setMethod(new RECEIVE_AGREE_Method());
		filter = new MessageFilter("performative = AGREE AND protocol = fipa-recruiting");
		RECEIVE_AGREE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_AGREE);
		processor.addTransition(WAIT_FOR_PROXY_ACCEPTANCE, RECEIVE_AGREE);
		
		// WAIT_FOR_PROXY_RESULT State

		WaitState WAIT_FOR_PROXY_RESULT = new WaitState("WAIT_FOR_PROXY_RESULT", timeout);
		processor.registerState(WAIT_FOR_PROXY_RESULT);
		processor.addTransition(RECEIVE_AGREE, WAIT_FOR_PROXY_RESULT);
		
		// RECEIVE_FAILURE_PROXY State
		
		ReceiveState RECEIVE_FAILURE_PROXY = new ReceiveState("RECEIVE_FAILURE_PROXY");
		RECEIVE_FAILURE_PROXY.setMethod(new RECEIVE_FAILURE_PROXY_Method());
		filter = new MessageFilter("performative = FAILURE AND protocol = fipa-recruiting AND reason = proxy");
		RECEIVE_FAILURE_PROXY.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE_PROXY);
		processor.addTransition(WAIT_FOR_PROXY_RESULT, RECEIVE_FAILURE_PROXY);
		
		// RECEIVE_FAILURE_NO_MATCH State
		
		ReceiveState RECEIVE_FAILURE_NO_MATCH = new ReceiveState("RECEIVE_FAILURE_NO_MATCH");
		RECEIVE_FAILURE_NO_MATCH.setMethod(new RECEIVE_FAILURE_NO_MATCH_Method());
		filter = new MessageFilter("performative = FAILURE AND protocol = fipa-recruiting AND reason = no-match");
		RECEIVE_FAILURE_NO_MATCH.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE_NO_MATCH);
		processor.addTransition(WAIT_FOR_PROXY_RESULT, RECEIVE_FAILURE_NO_MATCH);
		
		// RECEIVE_INFORM State
		
		ReceiveState RECEIVE_INFORM = new ReceiveState("RECEIVE_INFORM");
		RECEIVE_INFORM.setMethod(new RECEIVE_INFORM_Method());
		filter = new MessageFilter("performative = INFORM AND protocol = fipa-recruiting");
		RECEIVE_INFORM.setAcceptFilter(filter);
		processor.registerState(RECEIVE_INFORM);
		processor.addTransition(WAIT_FOR_PROXY_RESULT, RECEIVE_INFORM);
		
		// FINAL State

		FinalState FINAL_RECRUITING_INITIATOR = new FinalState("FINAL_RECRUITING_INITIATOR");

		FINAL_RECRUITING_INITIATOR.setMethod(new FINAL_RECRUITING_INITIATOR_Method());
		processor.registerState(FINAL_RECRUITING_INITIATOR);
		
		processor.addTransition(RECEIVE_REFUSE, FINAL_RECRUITING_INITIATOR);
		processor.addTransition(RECEIVE_FAILURE_PROXY, FINAL_RECRUITING_INITIATOR);
		processor.addTransition(RECEIVE_FAILURE_NO_MATCH, FINAL_RECRUITING_INITIATOR);
		processor.addTransition(RECEIVE_INFORM, FINAL_RECRUITING_INITIATOR);
		
		return theFactory;
	}
}