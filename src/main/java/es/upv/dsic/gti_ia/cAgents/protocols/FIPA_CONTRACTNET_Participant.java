package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;

/**
 * Template for CFactories that manage fipa contract net participant
 * conversation. The user has to create his/her own class extending from this
 * one. And implement the abstract methods. Other methods can be overriden in
 * order to modify the default behaviour
 * 
 * @author ricard
 * 
 */

public abstract class FIPA_CONTRACTNET_Participant {
	/**
	 * Method executed at the beginning of the conversation
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            first message assigned to this conversation
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT_FOR_SOLICIT";
		};
	}

	/**
	 * Method executed when the participant receives a call for proposals
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            call for proposals message
	 * @return next state of this conversation
	 */
	protected abstract String doReceiveSolicit(CProcessor myProcessor,
			ACLMessage msg);

	class RECEIVE_SOLICIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData()
					.put("solicitMessage", messageReceived);
			return doReceiveSolicit(myProcessor, messageReceived);
		}
	}

	/**
	 * Method executed when the participant sends a proposal
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            proposal message
	 */
	protected abstract void doSendProposal(CProcessor myProcessor,
			ACLMessage messageToSend);

	class SEND_PROPOSAL_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendProposal(myProcessor, messageToSend);
			return "WAIT_FOR_ACCEPT";
		}
	}

	/**
	 * Method executed when the participant sends a refuse
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            refuse message
	 */
	protected void doSendRefuse(CProcessor myProcessor, ACLMessage messageToSend) {
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
				"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.REFUSE);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
				.getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_REFUSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendRefuse(myProcessor, messageToSend);
			return "FINAL";
		}
	}

	/**
	 * Method executed when the participant sends a not-understood
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            not-understood message
	 */
	protected void doSendNotUnderstood(CProcessor myProcessor,
			ACLMessage messageToSend) {
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
				"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
				.getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_NOT_UNDERSTOOD_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendNotUnderstood(myProcessor, messageToSend);
			return "FINAL";
		}
	}

	/**
	 * Method executed when the timeout is reached while the initiator was
	 * waiting for proposals
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            timeout message
	 */
	protected void doTimeout(CProcessor myProcessor, ACLMessage msg) {
	}

	class TIMEOUT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doTimeout(myProcessor, messageReceived);
			return "FINAL";
		}
	}

	/**
	 * Method executed when the initiator accepts participant's proposal
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            accept message
	 */
	protected void doReceiveAccept(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_ACCEPT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveAccept(myProcessor, messageReceived);
			return "DO_TASK";
		}
	}

	/**
	 * Method executed when the initiator rejects participant's proposal
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            reject message
	 */
	protected void doReceiveReject(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_REJECT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveReject(myProcessor, messageReceived);
			return "FINAL";
		}
	}

	/**
	 * Perform the proposal's task
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param solicitMessage
	 *            the first message assigned to this conversation containing the
	 *            solicit of the initiator agent
	 * @return next conversation state
	 */
	protected abstract String doTask(CProcessor myProcessor,
			ACLMessage solicitMessage);

	class DO_TASK_Method implements ActionStateMethod {
		public String run(CProcessor myProcessor) {
			ACLMessage solicitMessage = (ACLMessage) myProcessor
					.getInternalData().get("solicitMessage");
			return doTask(myProcessor, solicitMessage);
		}
	}

	/**
	 * Method executed when the task failed
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            fail message
	 */
	protected void doSendFailure(CProcessor myProcessor,
			ACLMessage messageToSend) {
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
				"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.FAILURE);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage()
				.getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendFailure(myProcessor, messageToSend);
			return "FINAL";
		}
	}

	/**
	 * Method executed when the task succeeded
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            inform message
	 */
	protected abstract void doSendInfo(CProcessor myProcessor,
			ACLMessage messageToSend);

	class SEND_INFO_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendInfo(myProcessor, messageToSend);
			return "FINAL";
		}
	}

	/**
	 * Method executed when the conversation ends
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            final message
	 */
	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSentMessage();
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal(myProcessor, messageToSend);
		}
	}

	/**
	 * Creates a new contract-net participant CFactory
	 * 
	 * @param name
	 *            factory's name
	 * @param filter
	 *            message filter
	 * @param template
	 *            first message to send
	 * @param availableConversations
	 *            maximum number of conversation this CFactory can manage
	 *            simultaneously
	 * @param myAgent
	 *            agent owner of this CFactory
	 * @param timeout
	 *            for waiting after sending the proposal
	 * @return a new fipa contract net participant factory
	 */
	public CFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, CAgent myAgent,
			int timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("performative = CFP"); // falta AND
																// protocol
																// =
																// fipa-contract-net;
		}

		if (template == null) {
			template = new ACLMessage(ACLMessage.PROPOSE);
		}
		CFactory theFactory = new CFactory(name, filter,
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

		ReceiveState RECEIVE_SOLICIT = new ReceiveState("RECEIVE_SOLICIT");
		RECEIVE_SOLICIT.setMethod(new RECEIVE_SOLICIT_Method());
		filter = new MessageFilter("performative = CFP");
		RECEIVE_SOLICIT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_SOLICIT);
		processor.addTransition(WAIT_FOR_SOLICIT, RECEIVE_SOLICIT);

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
		//WAIT_FOR_SOLICIT -> SEND_REFUSE debería ser RECEIVE_SOLICIT -> SEND_REFUSE?
		//processor.addTransition(WAIT_FOR_SOLICIT, SEND_REFUSE);
		processor.addTransition(RECEIVE_SOLICIT, SEND_REFUSE);
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

		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter(
				"performative = INFORM AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
		processor.addTransition(WAIT_FOR_ACCEPT, TIMEOUT);

		// RECEIVE_ACCEPT State

		ReceiveState RECEIVE_ACCEPT = new ReceiveState("RECEIVE_ACCEPT");
		RECEIVE_ACCEPT.setMethod(new RECEIVE_ACCEPT_Method());
		filter = new MessageFilter("performative = ACCEPT-PROPOSAL");
		RECEIVE_ACCEPT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_ACCEPT);
		processor.addTransition(WAIT_FOR_ACCEPT, RECEIVE_ACCEPT);

		// RECEIVE_REJECT State

		ReceiveState RECEIVE_REJECT = new ReceiveState("RECEIVE_REJECT");
		RECEIVE_REJECT.setMethod(new RECEIVE_REJECT_Method());
		filter = new MessageFilter("performative = REJECT-PROPOSAL");
		RECEIVE_REJECT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REJECT);
		processor.addTransition(WAIT_FOR_ACCEPT, RECEIVE_REJECT);

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
		//Added by Javier
		processor.addTransition(SEND_REFUSE, FINAL);
		processor.addTransition(SEND_INFO, FINAL);
		processor.addTransition(RECEIVE_REJECT, FINAL);
		processor.addTransition(SEND_NOT_UNDERSTOOD, FINAL);
		processor.addTransition(TIMEOUT, FINAL);

		return theFactory;
	}
}
