package es.upv.dsic.gti_ia.cAgents.protocols;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
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
import es.upv.dsic.gti_ia.core.AgentID;;

public abstract class FIPA_RECRUITING_Participant {
	
	public static String BEGIN = "BEGIN";
	public static String RECEIVE_PROXY = "RECEIVE_PROXY";
	public static String REFUSE = "REFUSE";
	public static String AGREE = "AGREE";
	public static String LOCATE_AGENTS = "LOCATE_AGENTS";
	public static String FAILURE_NO_MATCH = "FAILURE_NO_MATCH";
	public static String START_SUB_PROTOCOL = "START_SUB_PROTOCOL";
	public static String FAILURE_PROXY = "FAILURE_PROXY";
	public static String INFORM = "INFORM";
	public static String FINAL_RECRUITING_PARTICIPANT = "FINAL_RECRUITING_PARTICIPANT";
	
	AgentID initiator = new AgentID("");
	
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}
	
	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT_FOR_PROXY_MESSAGE";
		};
	}
	
	protected abstract String doReceiveProxy(CProcessor myProcessor, ACLMessage msg);

	class RECEIVE_PROXY_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("proxyMessage", messageReceived);
			initiator = new AgentID(messageReceived.getSender().name);
			return doReceiveProxy(myProcessor, messageReceived);
		}
	}
	
	protected void doRefuse(CProcessor myProcessor,
			ACLMessage messageToSend){
	}
	
	class REFUSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setPerformative(ACLMessage.REFUSE);
			messageToSend.setReceiver(initiator);
			doRefuse(myProcessor, messageToSend);
			return "FINAL_RECRUITING_PARTICIPANT";
		}
	}
	
	protected void doAgree(CProcessor myProcessor,ACLMessage messageToSend){
	}
	
	class AGREE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setPerformative(ACLMessage.AGREE);
			messageToSend.setReceiver(initiator);
			doRefuse(myProcessor, messageToSend);
			return "LOCATE_AGENTS";
		}
	}
	
	protected abstract ArrayList<AgentID> doLocateAgents(CProcessor myProcessor, ACLMessage proxyMessage);
	
	class LOCATE_AGENTS_Method implements ActionStateMethod{
		@Override
		public String run(CProcessor myProcessor) {
			ACLMessage proxyMessage = (ACLMessage) myProcessor.getInternalData().get("proxyMessage");
			ArrayList<AgentID> locatedAgents = doLocateAgents(myProcessor, proxyMessage);
			if(locatedAgents.size() == 0)
				return "FAILURE_NO_MATCH";
			else{
				myProcessor.getInternalData().put("locatedAgents", locatedAgents);
				return "START_SUB_PROTOCOL";
			}
		}		
	}
	
	protected void doFailureNoMatch(CProcessor myProcessor,ACLMessage messageToSend){
	}
	
	class FAILURE_NO_MATCH_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setPerformative(ACLMessage.FAILURE);
			messageToSend.setHeader("reason", "no-match");
			messageToSend.setReceiver(initiator);
			doRefuse(myProcessor, messageToSend);
			return "FINAL_RECRUITING_PARTICIPANT";
		}
	}
	
	//protected abstract CProcessor startSubProtocol(CProcessor myProcessor);
	protected abstract boolean resultOfSubProtocol(CProcessor myProcessor, ACLMessage subProtocolMessageResult);
		
	class START_SUB_PROTOCOL_Method implements ActionStateMethod{
		@SuppressWarnings("unchecked")
		@Override
		public String run(CProcessor myProcessor) {
			ACLMessage initialMessage = (ACLMessage) myProcessor.getInternalData().get("proxyMessage");
			ArrayList<AgentID> locatedAgents = (ArrayList<AgentID>) myProcessor.getInternalData().get("locatedAgents");
			for(int i=0; i < locatedAgents.size(); i++){
				initialMessage.setReceiver(locatedAgents.get(i));
				initialMessage.setReplyTo(initialMessage.getSender());
				ACLMessage subProtocolMessageResult = myProcessor.createSyncConversation(initialMessage);
				if(!resultOfSubProtocol(myProcessor, subProtocolMessageResult))
					return "FAILURE_PROXY";
			}
			return "INFORM";
		}		
	}
	
	protected void doFailureProxy(CProcessor myProcessor,ACLMessage messageToSend){
	}
	
	class FAILURE_PROXY_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setPerformative(ACLMessage.FAILURE);
			messageToSend.setHeader("reason", "proxy");
			messageToSend.setReceiver(initiator);
			doFailureProxy(myProcessor, messageToSend);
			return "FINAL_RECRUITING_PARTICIPANT";
		}
	}
	
	protected void doInform(CProcessor myProcessor,ACLMessage messageToSend){
	}
	
	class INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setPerformative(ACLMessage.INFORM);
			messageToSend.setReceiver(initiator);
			doInform(myProcessor, messageToSend);
			return "FINAL_RECRUITING_PARTICIPANT";
		}
	}
	
	protected void doFinalRecruitingParticipant(CProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSendedMessage();
	}

	class FINAL_RECRUITING_PARTICIPANT_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinalRecruitingParticipant(myProcessor, messageToSend);
		}
	}
	
	
	public CProcessorFactory newFactory(String name, MessageFilter filter, ACLMessage template,
			int availableConversations, CAgent myAgent, long timeout) {
		
		// Create factory

		if (filter == null) {
			filter = new MessageFilter("protocol = fipa-recruiting"); //falta AND protocol = fipa-request;
		}
		CProcessorFactory theFactory = new CProcessorFactory(name, filter,
				availableConversations, myAgent);
		
		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();
		
		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_PROXY_MESSAGE State

		WaitState WAIT_FOR_PROXY_MESSAGE = new WaitState("WAIT_FOR_PROXY_MESSAGE", timeout);
		processor.registerState(WAIT_FOR_PROXY_MESSAGE);
		processor.addTransition(BEGIN, WAIT_FOR_PROXY_MESSAGE);
		
		// RECEIVE_PROXY State
		
		ReceiveState RECEIVE_PROXY = new ReceiveState("RECEIVE_PROXY");
		RECEIVE_PROXY.setMethod(new RECEIVE_PROXY_Method());
		filter = new MessageFilter("protocol = fipa-recruiting");
		RECEIVE_PROXY.setAcceptFilter(filter);
		processor.registerState(RECEIVE_PROXY);
		processor.addTransition(WAIT_FOR_PROXY_MESSAGE, RECEIVE_PROXY);
		
		// REFUSE State

		SendState REFUSE = new SendState("REFUSE");
		REFUSE.setMethod(new REFUSE_Method());
		template = new ACLMessage(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);
		processor.registerState(REFUSE);
		processor.addTransition(RECEIVE_PROXY, REFUSE);
		
		// AGREE State

		SendState AGREE = new SendState("AGREE");
		AGREE.setMethod(new AGREE_Method());
		template = new ACLMessage(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);
		processor.registerState(AGREE);
		processor.addTransition(RECEIVE_PROXY, AGREE);
		
		// LOCATE_AGENTS State
		
		ActionState LOCATE_AGENTS = new ActionState("LOCATE_AGENTS");
		LOCATE_AGENTS.setMethod(new LOCATE_AGENTS_Method());
		processor.registerState(LOCATE_AGENTS);
		processor.addTransition(AGREE, LOCATE_AGENTS);
		
		// FAILURE_NO_MATCH State

		SendState FAILURE_NO_MATCH = new SendState("FAILURE_NO_MATCH");
		FAILURE_NO_MATCH.setMethod(new FAILURE_NO_MATCH_Method());
		template = new ACLMessage(ACLMessage.FAILURE);
		FAILURE_NO_MATCH.setMessageTemplate(template);
		processor.registerState(FAILURE_NO_MATCH);
		processor.addTransition(LOCATE_AGENTS, FAILURE_NO_MATCH);
		
		// START_SUB_PROTOCOL State
		
		ActionState START_SUB_PROTOCOL = new ActionState("START_SUB_PROTOCOL");
		START_SUB_PROTOCOL.setMethod(new START_SUB_PROTOCOL_Method());
		processor.registerState(START_SUB_PROTOCOL);
		processor.addTransition(LOCATE_AGENTS, START_SUB_PROTOCOL);
		
		// INFORM State

		SendState INFORM = new SendState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		template = new ACLMessage(ACLMessage.INFORM);
		INFORM.setMessageTemplate(template);
		processor.registerState(INFORM);
		processor.addTransition(START_SUB_PROTOCOL, INFORM);
		
		// FAILURE_PROXY State

		SendState FAILURE_PROXY = new SendState("FAILURE_PROXY");
		FAILURE_PROXY.setMethod(new FAILURE_PROXY_Method());
		template = new ACLMessage(ACLMessage.FAILURE);
		FAILURE_PROXY.setMessageTemplate(template);
		processor.registerState(FAILURE_PROXY);
		processor.addTransition(START_SUB_PROTOCOL, FAILURE_PROXY);
		
		// FINAL State

		FinalState FINAL_RECRUITING_PARTICIPANT = new FinalState("FINAL_RECRUITING_PARTICIPANT");

		FINAL_RECRUITING_PARTICIPANT.setMethod(new FINAL_RECRUITING_PARTICIPANT_Method());
		processor.registerState(FINAL_RECRUITING_PARTICIPANT);
		
		processor.addTransition(REFUSE, FINAL_RECRUITING_PARTICIPANT);
		processor.addTransition(FAILURE_PROXY, FINAL_RECRUITING_PARTICIPANT);
		processor.addTransition(FAILURE_NO_MATCH, FINAL_RECRUITING_PARTICIPANT);
		processor.addTransition(INFORM, FINAL_RECRUITING_PARTICIPANT);				
		
		return theFactory;
	}
	
}
