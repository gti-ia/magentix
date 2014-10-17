package TestCAgents.Agents;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendingErrorsState;
import es.upv.dsic.gti_ia.cAgents.SendingErrorsStateMethod;
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

			doSolicitProposals(myProcessor, messageToSend);

			return "SOLICIT_PROPOSALS";
		}
	}

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
			ACLMessage template, int availableConversations, CAgent myAgent) {

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

		SendingErrorsState SENDING_ERRORS = (SendingErrorsState) processor
				.getState("SENDING_ERRORS_STATE");

		SENDING_ERRORS.setMethod(new SendingErrorsStateMethod() {

			@Override
			public String run(CProcessor myProcessor, ACLMessage errorMessage) {

				return "FINAL";
			}
		});

		// SOLICIT_PROPOSALS State

		SendState SOLICIT_PROPOSALS = new SendState("SOLICIT_PROPOSALS");

		SOLICIT_PROPOSALS.setMethod(new SOLICIT_PROPOSALS_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.CFP);
		SOLICIT_PROPOSALS.setMessageTemplate(template);
		processor.registerState(SOLICIT_PROPOSALS);
		processor.addTransition(BEGIN, SOLICIT_PROPOSALS);
		processor.addTransition(SOLICIT_PROPOSALS, SOLICIT_PROPOSALS);
		//processor.addTransition(SOLICIT_PROPOSALS, SOLICIT_PROPOSALS);
		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);

		processor.addTransition(SOLICIT_PROPOSALS, FINAL);

		return theFactory;
	}
}
