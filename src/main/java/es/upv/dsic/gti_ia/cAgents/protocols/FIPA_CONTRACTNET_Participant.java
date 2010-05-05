package es.upv.dsic.gti_ia.cAgents.protocols;

import java.util.Date;

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
import es.upv.dsic.gti_ia.cAgents.WaitStateMethod;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

public abstract class FIPA_CONTRACTNET_Participant {
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT_FOR_SOLICIT";
		};
	}

	protected abstract String doReceiveSolicit(CProcessor myProcessor, ACLMessage msg);

	class RECEIVE_SOLICIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			return doReceiveSolicit(myProcessor, messageReceived);
		}
	}

	protected abstract void doSendProposal(CProcessor myProcessor,
			ACLMessage messageToSend);

	class SEND_PROPOSAL_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendProposal(myProcessor, messageToSend);
			return "WAIT_FOR_ACCEPT";
		}
	}
	
	protected void doSendRefuse(CProcessor myProcessor,
			ACLMessage messageToSend){
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
		"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.REFUSE);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_REFUSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendRefuse(myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	protected void doSendNotUnderstood(CProcessor myProcessor,
			ACLMessage messageToSend){
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
		"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_NOT_UNDERSTOOD_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendNotUnderstood(myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	protected void doReceiveAccept(CProcessor myProcessor, ACLMessage msg){
	}

	class RECEIVE_ACCEPT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveAccept(myProcessor, messageReceived);
			return "DO_TASK";
		}
	}
	
	protected void doReceiveReject(CProcessor myProcessor, ACLMessage msg){
	}

	class RECEIVE_REJECT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveReject(myProcessor, messageReceived);
			return "FINAL";
		}
	}

	protected abstract String doTask(CProcessor myProcessor, ACLMessage solicitMessage);

	class DO_TASK_Method implements ActionStateMethod {
		public String run(CProcessor myProcessor) {
			ACLMessage solicitMessage = (ACLMessage)myProcessor.getInternalData().get("solicitMessage");
			return doTask(myProcessor, solicitMessage);
		}
	}
	
	protected void doSendFailure(CProcessor myProcessor,
			ACLMessage messageToSend){
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
		"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.FAILURE);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendFailure(myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	protected abstract void doSendInfo(CProcessor myProcessor,
			ACLMessage messageToSend);

	class SEND_INFO_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendInfo(myProcessor, messageToSend);
			return "FINAL";
		}
	}	

	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSendedMessage();
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal(myProcessor, messageToSend);
		}
	}
	
	public CProcessorFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, CAgent myAgent, int timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("performative = CFP"); // falta AND
																	// protocol
																	// =
																	// fipa-contract-net;
		}
		
		if (template == null){
			template = new ACLMessage(ACLMessage.PROPOSE);
		}
		CProcessorFactory theFactory = new CProcessorFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_SOLICIT State
		WaitState WAIT_FOR_SOLICIT = new WaitState("WAIT_FOR_SOLICIT", timeout);
		processor.registerState(WAIT_FOR_SOLICIT);
		processor.addTransition(BEGIN, WAIT_FOR_SOLICIT);
		
		// RECEIVE_SOLICIT State

		ReceiveState RECEIVE_SOLICIT = new ReceiveState(
				"RECEIVE_SOLICIT");
		RECEIVE_SOLICIT.setMethod(new RECEIVE_SOLICIT_Method());
		filter = new MessageFilter("performative = CFP");
		RECEIVE_SOLICIT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_SOLICIT);
		processor.addTransition(WAIT_FOR_SOLICIT,
				RECEIVE_SOLICIT);

		// SEND_PROPOSAL State

		SendState SEND_PROPOSAL = new SendState("SEND_PROPOSAL");

		SEND_PROPOSAL.setMethod(new SEND_PROPOSAL_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.PROPOSE);
		SEND_PROPOSAL.setMessageTemplate(template);
		processor.registerState(SEND_PROPOSAL);
		processor.addTransition(RECEIVE_SOLICIT, SEND_PROPOSAL);
		
		// SEND_REFUSE State

		SendState SEND_REFUSE = new SendState("SEND_REFUSE");

		SEND_REFUSE.setMethod(new SEND_REFUSE_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.REFUSE);
		SEND_REFUSE.setMessageTemplate(template);
		processor.registerState(SEND_REFUSE);
		processor.addTransition(WAIT_FOR_SOLICIT, SEND_REFUSE);
		
		// SEND_NOT_UNDERSTOOD State

		SendState SEND_NOT_UNDERSTOOD = new SendState("SEND_NOT_UNDERSTOOD");

		SEND_NOT_UNDERSTOOD.setMethod(new SEND_NOT_UNDERSTOOD_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		SEND_NOT_UNDERSTOOD.setMessageTemplate(template);
		processor.registerState(SEND_NOT_UNDERSTOOD);
		processor.addTransition(WAIT_FOR_SOLICIT, SEND_NOT_UNDERSTOOD);

		// WAIT_FOR_ACCEPT State
		WaitState WAIT_FOR_ACCEPT = new WaitState("WAIT_FOR_ACCEPT", timeout);
		processor.registerState(WAIT_FOR_ACCEPT);
		processor.addTransition(SEND_PROPOSAL, WAIT_FOR_ACCEPT);

		// RECEIVE_ACCEPT State

		ReceiveState RECEIVE_ACCEPT = new ReceiveState(
				"RECEIVE_ACCEPT");
		RECEIVE_ACCEPT.setMethod(new RECEIVE_ACCEPT_Method());
		filter = new MessageFilter("performative = ACCEPT-PROPOSAL");
		RECEIVE_ACCEPT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_ACCEPT);
		processor.addTransition(WAIT_FOR_ACCEPT,
				RECEIVE_ACCEPT);

		// RECEIVE_REJECT State

		ReceiveState RECEIVE_REJECT = new ReceiveState("RECEIVE_REJECT");
		RECEIVE_REJECT.setMethod(new RECEIVE_REJECT_Method());
		filter = new MessageFilter("performative = REJECT-PROPOSAL");
		RECEIVE_REJECT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REJECT);
		processor.addTransition(WAIT_FOR_ACCEPT,
				RECEIVE_REJECT);

		// DO_TASK State

		ActionState DO_TASK = new ActionState("DO_TASK");
		DO_TASK.setMethod(new DO_TASK_Method());
		processor.registerState(DO_TASK);
		processor.addTransition(RECEIVE_ACCEPT, DO_TASK);
		
		// SEND_INFO State

		SendState SEND_INFO = new SendState("SEND_INFORM");

		SEND_INFO.setMethod(new SEND_INFO_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.INFORM);
		SEND_INFO.setMessageTemplate(template);
		processor.registerState(SEND_INFO);
		processor.addTransition(DO_TASK, SEND_INFO);
		
		// SEND_INFO State

		SendState SEND_FAILURE = new SendState("SEND_FAILURE");

		SEND_FAILURE.setMethod(new SEND_FAILURE_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.FAILURE);
		SEND_FAILURE.setMessageTemplate(template);
		processor.registerState(SEND_FAILURE);
		processor.addTransition(DO_TASK, SEND_FAILURE);		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(SEND_FAILURE, FINAL);			
		processor.addTransition(SEND_INFO, FINAL);
		processor.addTransition(RECEIVE_REJECT, FINAL);
		processor.addTransition(SEND_NOT_UNDERSTOOD, FINAL);
				
		return theFactory;
	}
}
