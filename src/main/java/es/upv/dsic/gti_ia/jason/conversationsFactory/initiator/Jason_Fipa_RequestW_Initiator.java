package es.upv.dsic.gti_ia.jason.conversationsFactory.initiator;


import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRWConversation;

import java.util.ArrayList;
import java.util.List;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class Jason_Fipa_RequestW_Initiator {

	protected TransitionSystem Ts; 
	
	public Jason_Fipa_RequestW_Initiator(String sagName,
			TransitionSystem ts) {

		Ts = ts;
		
	}
	

/**
 * Method to execute at the beginning of the conversation
 * @param myProcessor the CProcessor managing the conversation
 * @param msg first message to send
 */

protected void doBegin(ConvCProcessor myProcessor,
		ACLMessage messageToSend) {
	FRWConversation conv =  (FRWConversation) myProcessor.getConversation();
	messageToSend.setContent(conv.initialMessage);
	myProcessor.getInternalData().put("InitialMessage", messageToSend);

}

class BEGIN_Method implements BeginStateMethod {
	public String run(CProcessor myProcessor, ACLMessage msg) {
		doBegin((ConvCProcessor)myProcessor, msg);
		return "WAIT_FOR_CONDITION";
	};
}


/**
 * Method to execute when the initiator receives the confirmation of a fulfilled condition 
 * @param myProcessor the CProcessor managing the conversation
 * @param msg message with the fulfilled condition
 */
protected void doFulfilledCondition(CProcessor myProcessor, ACLMessage msg){
	
}

class FULFILLED_CONDITION_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doFulfilledCondition(myProcessor, messageReceived);
		return "REQUEST_REQUEST_INITIATOR";
	}
}


/**
 * Sets the request message
 * @param myProcessor the CProcessor managing the conversation
 * @param messageToSend request message
 */
protected void doRequest(ConvCProcessor myProcessor,
		ACLMessage messageToSend) {
	
	FRWConversation conv = (FRWConversation) myProcessor.getConversation();
	messageToSend.setContent(conv.frMessage);
	messageToSend.setProtocol("request-when");
	messageToSend.setPerformative(ACLMessage.REQUEST);
	messageToSend.setReceiver(new AgentID(conv.Participant));  
	messageToSend.setSender(myProcessor.getMyAgent().getAid() );
	messageToSend.setHeader("jasonID", conv.jasonConvID);
	
}


class REQUEST_Method implements SendStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageToSend) {
		doRequest((ConvCProcessor)myProcessor, messageToSend);
		return "FIRST_WAIT_REQUEST_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives a not-understood message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg not-understood message
 */
protected void doNotUnderstood(ConvCProcessor myProcessor, ACLMessage msg){
	FRWConversation conv = (FRWConversation) myProcessor.getConversation();
	conv.finalResult = ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD);
}

class NOT_UNDERSTOOD_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doNotUnderstood((ConvCProcessor) myProcessor, messageReceived);
		return "FINAL_REQUEST_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives a failure message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg failure message
 */
protected void doRefuse(ConvCProcessor myProcessor, ACLMessage msg){
	FRWConversation conv = (FRWConversation) myProcessor.getConversation();
	conv.finalResult = ACLMessage.getPerformative(ACLMessage.REFUSE);
	
}

class REFUSE_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doRefuse((ConvCProcessor) myProcessor, messageReceived);
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
/*protected void doSecondWait(CProcessor myProcessor, ACLMessage msg){		
}

class SECOND_WAIT_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doSecondWait(myProcessor, messageReceived);
		return "SECOND_WAIT_REQUEST_INITIATOR";
	}
}*/

/**
 * Method to execute when the initiator receives a failure message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg failure message
 */
protected void doFailure(ConvCProcessor myProcessor, ACLMessage msg){
	FRWConversation conv = (FRWConversation) myProcessor.getConversation();
	conv.finalResult = ACLMessage.getPerformative(ACLMessage.FAILURE);
}

class FAILURE_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doFailure((ConvCProcessor) myProcessor, messageReceived);
		return "FINAL_REQUEST_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives an inform message
 * @param myProcessor
 * @param msg
 */
protected void doInform(ConvCProcessor myProcessor, ACLMessage msg) {

	FRWConversation conv = (FRWConversation) myProcessor.getConversation();

	List<Literal> allperc = new ArrayList<Literal>();
	String percept = "taskdonesuccessfully("+conv.Participant +","+conv.jasonConvID+")[source(self)]";
	allperc.add(Literal.parseLiteral(percept));
	((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	
	conv.aquire_semaphore();
	conv.finalResult = ACLMessage.getPerformative(ACLMessage.INFORM);
	msg.setProtocol("request-when");
	msg.setPerformative(ACLMessage.REQUEST);
}

class INFORM_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doInform((ConvCProcessor)myProcessor, messageReceived);
		return "FINAL_REQUEST_INITIATOR";
	}
}

/**
 * Method to execute when the initiator ends the conversation
 * @param myProcessor
 * @param messageToSend
 */
protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend){

	FRWConversation conv = (FRWConversation) myProcessor.getConversation();

	List<Literal> allperc = new ArrayList<Literal>();
	String percept = "conversationended("+conv.jasonConvID+","+'"'+conv.finalResult+'"'+")[source(self)]";
	allperc.add(Literal.parseLiteral(percept));
	((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	
	messageToSend = myProcessor.getLastSentMessage();
	messageToSend.setProtocol("request-when");
	messageToSend.setPerformative(ACLMessage.REQUEST);
	
}


class FINAL_Method implements FinalStateMethod {
	public void run(CProcessor myProcessor, ACLMessage messageToSend) {
		doFinal((ConvCProcessor)myProcessor, messageToSend);
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
	public ConvCFactory newFactory(String name, MessageFilter filter, ACLMessage requestMessage,
			int availableConversations, ConvJasonAgent myAgent, long timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("performative = REQUEST AND protocol = request-when");
		}
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		
		// WAIT_FOR_CONDITION State

		processor.registerState(new WaitState("WAIT_FOR_CONDITION", timeout));
		processor.addTransition("BEGIN", "WAIT_FOR_CONDITION");

		// FULFILLED_CONDITION State
		ReceiveState FULFILLED_CONDITION = new ReceiveState("FULFILLED_CONDITION");
		FULFILLED_CONDITION.setMethod(new FULFILLED_CONDITION_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.INFORM)+" AND protocol = request-when");
		FULFILLED_CONDITION.setAcceptFilter(filter);
		processor.registerState(FULFILLED_CONDITION);
		processor.addTransition("WAIT_FOR_CONDITION", "FULFILLED_CONDITION");

		// REQUEST State

		SendState REQUEST = new SendState("REQUEST_REQUEST_INITIATOR");

		REQUEST.setMethod(new REQUEST_Method());
		//requestMessage = new ACLMessage(ACLMessage.REQUEST);
		//requestMessage.setProtocol("REQUEST");		
		REQUEST.setMessageTemplate(requestMessage);
		processor.registerState(REQUEST);
		processor.addTransition("FULFILLED_CONDITION", "REQUEST_REQUEST_INITIATOR");

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
		filter = new MessageFilter("performative = FAILURE AND protocol = request-when");
		FAILURE.setAcceptFilter(filter);
		processor.registerState(FAILURE);
		processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "FAILURE_REQUEST_INITIATOR");
		
		// INFORM State

		ReceiveState INFORM = new ReceiveState("INFORM_REQUEST_INITIATOR");
		INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("performative = INFORM AND  protocol = request-when");
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
