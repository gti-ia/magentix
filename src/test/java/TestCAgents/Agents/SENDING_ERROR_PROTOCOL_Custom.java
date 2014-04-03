package TestCAgents.Agents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
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
 * Template for CFactories that manage fipa contract net initiator conversation.
 * The user has to create his/her own class extending from this one. And
 * implement the abstract methods. Other methods can be overriden in order to
 * modify the default behaviour
 * 
 * @author ricard
 * 
 */

public abstract class SENDING_ERROR_PROTOCOL_Custom {

	/**
	 * Method executed at the beginning of the conversation
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            first message to send in the conversation
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "SOLICIT_PROPOSALS";
		};
	}

	/**
	 * Method executed when the initiator calls for proposals
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            Message to send
	 */
	protected void doSolicitProposals(CProcessor myProcessor,
			ACLMessage messageToSend) {
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.CFP);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SOLICIT_PROPOSALS_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			int participants = (Integer) myProcessor.getInternalData().get(
					"participants");

			doSolicitProposals(myProcessor, messageToSend);
			System.out.println(participants);
			if(participants-- >= 0 )
				return "SOLICIT_PROPOSALS";
			return "WAIT_FOR_PROPOSALS";
		}
	}

	/**
	 * Method executed when the initiator receives a not-understood message
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            not-understood message
	 */
	protected void doReceiveNotUnderstood(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_NOT_UNDERSTOOD_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			int participants = (Integer) myProcessor.getInternalData().get(
					"participants");
			int receivedResponses = (Integer) myProcessor.getInternalData()
					.get("receivedResponses");
			receivedResponses++;
			doReceiveNotUnderstood(myProcessor, messageReceived);
			if (receivedResponses >= participants)
				return "EVALUATE_PROPOSALS";
			else
				return "WAIT_FOR_PROPOSALS";
		}
	}

	/**
	 * Method executed when the initiator receives a refuse message
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            refuse message
	 */
	protected void doReceiveRefuse(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_REFUSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			int participants = (Integer) myProcessor.getInternalData().get(
					"participants");
			int receivedResponses = (Integer) myProcessor.getInternalData()
					.get("receivedResponses");
			doReceiveRefuse(myProcessor, messageReceived);
			receivedResponses++;
			doReceiveRefuse(myProcessor, messageReceived);
			if (receivedResponses >= participants)
				return "EVALUATE_PROPOSALS";
			else
				return "WAIT_FOR_PROPOSALS";
		}
	}

	/**
	 * Method executed when the initiator receives a proposal
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            proposal message
	 */
	@SuppressWarnings("unchecked")
	protected void doReceiveProposal(CProcessor myProcessor, ACLMessage msg) {
		ArrayList<ACLMessage> proposes = (ArrayList<ACLMessage>) myProcessor
				.getInternalData().get("proposes");
		proposes.add(msg);
	}

	class RECEIVE_PROPOSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			int participants = (Integer) myProcessor.getInternalData().get(
					"participants");
			int receivedResponses = (Integer) myProcessor.getInternalData()
					.get("receivedResponses");
			doReceiveProposal(myProcessor, messageReceived);
			receivedResponses++;
			myProcessor.getInternalData().put("receivedResponses",
					receivedResponses);
			if (receivedResponses >= participants)
				return "EVALUATE_PROPOSALS";
			else
				return "WAIT_FOR_PROPOSALS";
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
			return "EVALUATE_PROPOSALS";
		}
	}

	/**
	 * Evaluate proposals. Each accepted proposal has to be added to the
	 * acceptances list. Each rejected proposal has to be added to the rejected
	 * list
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param proposes
	 *            Proposals
	 * @param acceptances
	 *            Accepted proposals
	 * @param rejections
	 *            Rejected proposals
	 */
	protected abstract void doEvaluateProposals(CProcessor myProcessor,
			ArrayList<ACLMessage> proposes, ArrayList<ACLMessage> acceptances,
			ArrayList<ACLMessage> rejections);

	class EVALUATE_PROPOSALS_Method implements ActionStateMethod {
		@SuppressWarnings("unchecked")
		public String run(CProcessor myProcessor) {
			ArrayList<ACLMessage> proposes = (ArrayList<ACLMessage>) myProcessor
					.getInternalData().get("proposes");
			ArrayList<ACLMessage> acceptances = new ArrayList<ACLMessage>();
			ArrayList<ACLMessage> rejections = new ArrayList<ACLMessage>();
			doEvaluateProposals(myProcessor, proposes, acceptances, rejections);
			// We create dinamically the send states
			SendState send;
			boolean accept = false;
			boolean reject = false;
			int i;
			myProcessor.getInternalData().put("acceptedProposals",
					acceptances.size());
			if (acceptances.size() > 0)
				accept = true;
			if (rejections.size() > 0)
				reject = true;

			for (i = 0; i < acceptances.size(); i++) {
				send = new SendState("SEND_ACCEPTANCE_" + i);
				send.setMethod(new SEND_Method("SEND_ACCEPTANCE_" + i));
				send.setMessageTemplate(acceptances.get(i));
				myProcessor.registerState(send);
				if (i == 0)
					myProcessor.addTransition("EVALUATE_PROPOSALS",
							"SEND_ACCEPTANCE_" + i);
				else
					myProcessor.addTransition("SEND_ACCEPTANCE_" + (i - 1),
							"SEND_ACCEPTANCE_" + i);
			}
			if (!reject && accept)
				myProcessor.addTransition("SEND_ACCEPTANCE_" + (i - 1),
						"WAIT_FOR_RESULTS");

			int j;
			for (j = 0; j < rejections.size(); j++) {
				send = new SendState("SEND_REJECTION_" + j);
				send.setMethod(new SEND_Method("SEND_REJECTION_" + j));
				send.setMessageTemplate(rejections.get(j));
				myProcessor.registerState(send);
				if (j == 0)
					if (accept) {
						System.out.println("SEND_ACCEPTANCE_" + (i - 1));
						myProcessor.addTransition("SEND_ACCEPTANCE_" + (i - 1),
								"SEND_REJECTION_" + j);
					} else
						myProcessor.addTransition("EVALUATE_PROPOSALS",
								"SEND_REJECTION_" + j);
				else
					myProcessor.addTransition("SEND_REJECTION_" + (j - 1),
							"SEND_REJECTION_" + j);
			}

			if (reject)
				if (!accept)
					myProcessor.addTransition("SEND_REJECTION_" + (j - 1),
							"FINAL");
				else
					myProcessor.addTransition("SEND_REJECTION_" + (j - 1),
							"WAIT_FOR_RESULTS");

			if (accept)
				return "SEND_ACCEPTANCE_0";
			else if (reject)
				return "SEND_REJECTION_0";
			else
				return "FINAL";
		}
	}

	class SEND_Method implements SendStateMethod {
		private String stateName;

		public SEND_Method(String stateName) {
			this.stateName = stateName;
		}

		@Override
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			String next = "";
			Set<String> transitions = new HashSet<String>();
			transitions = myProcessor.getTransitionTable().getTransitions(
					this.stateName);
			Iterator<String> it = transitions.iterator();
			if (it.hasNext()) {
				// Get element
				next = it.next();
			}
			return next;
		}

	}

	/**
	 * Method executed when the initiator receives a failure
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            the failure message
	 */
	protected void doReceiveFailure(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_FAILURE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveFailure(myProcessor, messageReceived);
			int acceptedProposals = (Integer) myProcessor.getInternalData()
					.get("acceptedProposals");
			int receivedResults = (Integer) myProcessor.getInternalData().get(
					"receivedResults");
			receivedResults++;
			myProcessor.getInternalData().put("receivedResults",
					receivedResults);
			if (receivedResults >= acceptedProposals)
				return "FINAL";
			else
				return "WAIT_FOR_RESULTS";
		}
	}

	/**
	 * Method executed when the initiator receives a inform
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param msg
	 *            inform message
	 */
	protected abstract void doReceiveInform(CProcessor myProcessor,
			ACLMessage msg); // Method
	// to
	// implement

	class RECEIVE_INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveInform(myProcessor, messageReceived);
			int acceptedProposals = (Integer) myProcessor.getInternalData()
					.get("acceptedProposals");
			int receivedResults = (Integer) myProcessor.getInternalData().get(
					"receivedResults");
			receivedResults++;
			myProcessor.getInternalData().put("receivedResults",
					receivedResults);
			if (receivedResults >= acceptedProposals)
				return "FINAL";
			else
				return "WAIT_FOR_RESULTS";
		}
	}

	/**
	 * Method executed when the initiator ends the conversation
	 * 
	 * @param myProcessor
	 *            the CProcessor managing the conversation
	 * @param messageToSend
	 *            final message of this conversation
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
	 * Creates a new contract net initiator factory
	 * 
	 * @param name
	 *            of the factory
	 * @param filter
	 *            message filter
	 * @param template
	 *            first message to send
	 * @param availableConversations
	 *            maximum number of conversation this CFactory can manage
	 *            simultaneously
	 * @param myAgent
	 *            agent owner of this Cfactory
	 * @param participants
	 *            number of participants
	 * @param deadline
	 *            for waiting for proposals
	 * @param timeout
	 *            for waiting for inform
	 * @return the a new contract net initiator CFactory
	 */
	public CFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, CAgent myAgent,
			int participants, long deadline, int timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("performative = CFP"); // falta AND
			// protocol
			// =
			// fipa-contract-net;
		}
		CFactory theFactory = new CFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());

		// SOLICIT_PROPOSALS State

		SendState SOLICIT_PROPOSALS = new SendState("SOLICIT_PROPOSALS");

		SOLICIT_PROPOSALS.setMethod(new SOLICIT_PROPOSALS_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.CFP);
		SOLICIT_PROPOSALS.setMessageTemplate(template);
		processor.registerState(SOLICIT_PROPOSALS);
		processor.addTransition(BEGIN, SOLICIT_PROPOSALS);
		processor.addTransition(SOLICIT_PROPOSALS, SOLICIT_PROPOSALS);

		// WAIT_FOR_PROPOSALS State
		WaitState WAIT_FOR_PROPOSALS = new WaitState("WAIT_FOR_PROPOSALS",
				deadline);
		WAIT_FOR_PROPOSALS.setWaitType(WaitState.ABSOLUT);
		processor.registerState(WAIT_FOR_PROPOSALS);
		processor.addTransition(SOLICIT_PROPOSALS, WAIT_FOR_PROPOSALS);

		// RECEIVE_NOT_UNDERSTOOD State

		ReceiveState RECEIVE_NOT_UNDERSTOOD = new ReceiveState(
				"RECEIVE_NOT_UNDERSTOOD");
		RECEIVE_NOT_UNDERSTOOD.setMethod(new RECEIVE_NOT_UNDERSTOOD_Method());
		filter = new MessageFilter("performative = "
				+ ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD)); // Bexy
																			// 11/8/11
		RECEIVE_NOT_UNDERSTOOD.setAcceptFilter(filter);
		processor.registerState(RECEIVE_NOT_UNDERSTOOD);
		processor.addTransition(WAIT_FOR_PROPOSALS, RECEIVE_NOT_UNDERSTOOD);
		processor.addTransition(RECEIVE_NOT_UNDERSTOOD, WAIT_FOR_PROPOSALS);

		// RECEIVE_REFUSE State

		ReceiveState RECEIVE_REFUSE = new ReceiveState("RECEIVE_REFUSE");
		RECEIVE_REFUSE.setMethod(new RECEIVE_REFUSE_Method());
		filter = new MessageFilter("performative = REFUSE");
		RECEIVE_REFUSE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REFUSE);
		processor.addTransition(WAIT_FOR_PROPOSALS, RECEIVE_REFUSE);
		processor.addTransition(RECEIVE_REFUSE, WAIT_FOR_PROPOSALS);

		// RECEIVE_PROPOSE State

		ReceiveState RECEIVE_PROPOSE = new ReceiveState("RECEIVE_PROPOSE");
		RECEIVE_PROPOSE.setMethod(new RECEIVE_PROPOSE_Method());
		filter = new MessageFilter("performative = PROPOSE");
		RECEIVE_PROPOSE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_PROPOSE);
		processor.addTransition(WAIT_FOR_PROPOSALS, RECEIVE_PROPOSE);
		processor.addTransition(RECEIVE_PROPOSE, WAIT_FOR_PROPOSALS);

		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter(
				"performative = INFORM AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
		processor.addTransition(WAIT_FOR_PROPOSALS, TIMEOUT);

		// EVALUATE_PROPOSALS State

		ActionState EVALUATE_PROPOSALS = new ActionState("EVALUATE_PROPOSALS");
		EVALUATE_PROPOSALS.setMethod(new EVALUATE_PROPOSALS_Method());
		processor.registerState(EVALUATE_PROPOSALS);
		processor.addTransition(RECEIVE_PROPOSE, EVALUATE_PROPOSALS);
		processor.addTransition(RECEIVE_NOT_UNDERSTOOD, EVALUATE_PROPOSALS);
		processor.addTransition(RECEIVE_REFUSE, EVALUATE_PROPOSALS);
		processor.addTransition(TIMEOUT, EVALUATE_PROPOSALS);

		// We don't need to register send rejections/acceptances. It is done
		// dynamically by the EVALUATE_PROPOSALS State

		// WAIT_FOR_RESULTS State

		WaitState WAIT_FOR_RESULTS = new WaitState("WAIT_FOR_RESULTS", timeout);
		processor.registerState(WAIT_FOR_RESULTS);
		// It is not necessary to add transitions to this state, it is done
		// dynamically by EVALUATE_PROPOSALS State

		// RECEIVE_FAILURE State

		ReceiveState RECEIVE_FAILURE = new ReceiveState("RECEIVE_FAILURE");
		RECEIVE_FAILURE.setMethod(new RECEIVE_FAILURE_Method());
		filter = new MessageFilter("performative = FAILURE");
		RECEIVE_FAILURE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE);
		processor.addTransition(WAIT_FOR_RESULTS, RECEIVE_FAILURE);
		processor.addTransition(RECEIVE_FAILURE, WAIT_FOR_RESULTS);

		// RECEIVE_INFORM State

		ReceiveState RECEIVE_INFORM = new ReceiveState("RECEIVE_INFORM");
		RECEIVE_INFORM.setMethod(new RECEIVE_INFORM_Method());
		filter = new MessageFilter("performative = INFORM");
		RECEIVE_INFORM.setAcceptFilter(filter);
		processor.registerState(RECEIVE_INFORM);
		processor.addTransition(WAIT_FOR_RESULTS, RECEIVE_INFORM);
		processor.addTransition(RECEIVE_INFORM, WAIT_FOR_RESULTS);

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(EVALUATE_PROPOSALS, FINAL);
		processor.addTransition(RECEIVE_INFORM, FINAL);
		processor.addTransition(RECEIVE_FAILURE, FINAL);

		// We add the internal data
		processor.getInternalData().put("participants", participants);
		processor.getInternalData().put("receivedResponses", 0);
		processor.getInternalData().put("acceptedProposals", 0);
		processor.getInternalData().put("receivedResults", 0);
		processor.getInternalData()
				.put("proposes", new ArrayList<ACLMessage>());

		return theFactory;
	}
}
