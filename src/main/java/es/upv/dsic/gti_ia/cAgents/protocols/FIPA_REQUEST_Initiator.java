package es.upv.dsic.gti_ia.cAgents.protocols;

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
 * Template for CFactories that manage fipa request initiator conversation.
 * The user has to create his/her own class extending from this one. And implement
 * the abstract methods. Other methods can be overriden in order to modify the default
 * behaviour
 * @author ricard
 *
 */

public abstract class FIPA_REQUEST_Initiator {
	
	/**
	 * Method to execute at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg first message to send
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);		
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "REQUEST_REQUEST_INITIATOR";
		};
	}
	
	/**
	 * Sets the request message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend request message
	 */
	protected void doRequest(CProcessor myProcessor,
			ACLMessage messageToSend) {
		/*ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
				"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-request");
		messageToSend.setPerformative(ACLMessage.REQUEST);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());*/
	}

	class REQUEST_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doRequest(myProcessor, messageToSend);
			return "FIRST_WAIT_REQUEST_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the initiator receives a not-understood message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg not-understood message
	 */
	protected void doNotUnderstood(CProcessor myProcessor, ACLMessage msg){
	}

	class NOT_UNDERSTOOD_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doNotUnderstood(myProcessor, messageReceived);
			return "FINAL_REQUEST_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the initiator receives a failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg failure message
	 */
	protected void doRefuse(CProcessor myProcessor, ACLMessage msg){
	}

	class REFUSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doRefuse(myProcessor, messageReceived);
			return "FINAL_REQUEST_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the initiator receives an agree message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg agree message
	 */
	protected void doAgree(CProcessor myProcessor, ACLMessage msg){
	}

	class AGREE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doAgree(myProcessor, messageReceived);
			return "SECOND_WAIT_REQUEST_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the timeout is reached
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg timeout message
	 */
	protected void doSecondWait(CProcessor myProcessor, ACLMessage msg){		
	}

	class SECOND_WAIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doSecondWait(myProcessor, messageReceived);
			return "SECOND_WAIT_REQUEST_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the initiator receives a failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg failure message
	 */
	protected void doFailure(CProcessor myProcessor, ACLMessage msg){
	}

	class FAILURE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doFailure(myProcessor, messageReceived);
			return "FINAL_REQUEST_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the initiator receives an inform message
	 * @param myProcessor
	 * @param msg
	 */
	protected abstract void doInform(CProcessor myProcessor, ACLMessage msg); //Method to implement

	class INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doInform(myProcessor, messageReceived);
			return "FINAL_REQUEST_INITIATOR";
		}
	}

	/**
	 * Method to execute when the initiator ends the conversation
	 * @param myProcessor
	 * @param messageToSend
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
	 * Creates a new initiator fipa request cfactory
	 * @param name factory's name
	 * @param filter message filter
	 * @param requestMessage first message to send
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @param timeout for waiting after sending the request message
	 * @return a new fipa request initiator factory
	 */
	public CFactory newFactory(String name, MessageFilter filter, ACLMessage requestMessage,
			int availableConversations, CAgent myAgent, long timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("performative = REQUEST"); //falta AND protocol = fipa-request;
		}
		CFactory theFactory = new CFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());

		// REQUEST State

		SendState REQUEST = new SendState("REQUEST_REQUEST_INITIATOR");

		REQUEST.setMethod(new REQUEST_Method());
		//requestMessage = new ACLMessage(ACLMessage.REQUEST);
		//requestMessage.setProtocol("REQUEST");		
		REQUEST.setMessageTemplate(requestMessage);
		processor.registerState(REQUEST);
		processor.addTransition("BEGIN", "REQUEST_REQUEST_INITIATOR");

		// FIRST_WAIT State

		processor.registerState(new WaitState("FIRST_WAIT_REQUEST_INITIATOR", timeout));
		processor.addTransition("REQUEST_REQUEST_INITIATOR", "FIRST_WAIT_REQUEST_INITIATOR");
		
		// NOT_UNDERSTOOD State
		
		ReceiveState NOT_UNDERSTOOD = new ReceiveState("NOT_UNDERSTOOD_REQUEST_INITIATOR");
		NOT_UNDERSTOOD.setMethod(new NOT_UNDERSTOOD_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD));
		NOT_UNDERSTOOD.setAcceptFilter(filter);
		processor.registerState(NOT_UNDERSTOOD);
		processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "NOT_UNDERSTOOD_REQUEST_INITIATOR");
		
		// REFUSE State
		
		ReceiveState REFUSE = new ReceiveState("REFUSE_REQUEST_INITIATOR");
		REFUSE.setMethod(new REFUSE_Method());
		filter = new MessageFilter("performative = REFUSE");
		REFUSE.setAcceptFilter(filter);
		processor.registerState(REFUSE);
		processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "REFUSE_REQUEST_INITIATOR");
		
		// AGREE State
		
		ReceiveState AGREE = new ReceiveState("AGREE_REQUEST_INITIATOR");
		AGREE.setMethod(new AGREE_Method());
		filter = new MessageFilter("performative = AGREE");
		AGREE.setAcceptFilter(filter);
		processor.registerState(AGREE);
		processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "AGREE_REQUEST_INITIATOR");
		
		// SECOND_WAIT State

		processor.registerState(new WaitState("SECOND_WAIT_REQUEST_INITIATOR", timeout));
		processor.addTransition("AGREE_REQUEST_INITIATOR", "SECOND_WAIT_REQUEST_INITIATOR");
		
		// FAILURE State

		ReceiveState FAILURE = new ReceiveState("FAILURE_REQUEST_INITIATOR");
		FAILURE.setMethod(new FAILURE_Method());
		filter = new MessageFilter("performative = FAILURE");
		FAILURE.setAcceptFilter(filter);
		processor.registerState(FAILURE);
		processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "FAILURE_REQUEST_INITIATOR");
		
		// INFORM State

		ReceiveState INFORM = new ReceiveState("INFORM_REQUEST_INITIATOR");
		INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("performative = INFORM");
		INFORM.setAcceptFilter(filter);
		processor.registerState(INFORM);
		processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "INFORM_REQUEST_INITIATOR");

		// FINAL State

		FinalState FINAL = new FinalState("FINAL_REQUEST_INITIATOR");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition("INFORM_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		processor.addTransition("FAILURE_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		processor.addTransition("NOT_UNDERSTOOD_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		processor.addTransition("REFUSE_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		return theFactory;
	}

}
