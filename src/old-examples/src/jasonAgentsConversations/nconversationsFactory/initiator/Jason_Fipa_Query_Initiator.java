package jasonAgentsConversations.nconversationsFactory.initiator;


import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import jasonAgentsConversations.agentNConv.ConvCFactory;
import jasonAgentsConversations.agentNConv.ConvCProcessor;
import jasonAgentsConversations.agentNConv.ConvJasonAgent;
import jasonAgentsConversations.agentNConv.ConvMagentixAgArch;
import jasonAgentsConversations.agentNConv.FQConversation;

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



public class Jason_Fipa_Query_Initiator {


	protected TransitionSystem Ts; 


	
	public Jason_Fipa_Query_Initiator(String sagName,
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
	
	FQConversation conv =  (FQConversation) myProcessor.getConversation();
	messageToSend.setContent(conv.initialMessage);
	myProcessor.getInternalData().put("InitialMessage", messageToSend);
	
	if (messageToSend.getPerformativeInt()==ACLMessage.QUERY_IF){
		conv.performative = ACLMessage.QUERY_IF; 
	}else{
		conv.performative = ACLMessage.QUERY_REF; 
	}
}

class BEGIN_Method implements BeginStateMethod {
	public String run(CProcessor myProcessor, ACLMessage msg) {
		doBegin((ConvCProcessor)myProcessor, msg);
		String result = null; 
		if (msg.getPerformativeInt()==ACLMessage.QUERY_IF){
			result = "QUERY_IF_INITIATOR"; 
		}else{
			result = "QUERY_REF_INITIATOR"; 
		}
		
		return result;
	};
}

/**
 * Sets the query-if message
 * @param myProcessor the CProcessor managing the conversation
 * @param messageToSend request message
 */
protected void doQueryif(ConvCProcessor myProcessor,
		ACLMessage messageToSend) {
	
	FQConversation conv = (FQConversation) myProcessor.getConversation();
	
	conv.aquire_semaphore();
	messageToSend.setContent(conv.query.toString());
	messageToSend.setProtocol("fipa-query");
	messageToSend.setPerformative(ACLMessage.QUERY_IF);
	messageToSend.setReceiver(new AgentID(conv.Participant)); 
	messageToSend.setSender(myProcessor.getMyAgent().getAid());
	messageToSend.setHeader("jasonID", conv.jasonConvID);
	
}


class QUERY_IF_Method implements SendStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageToSend) {
		doQueryif((ConvCProcessor)myProcessor, messageToSend);
		return "WAIT_FOR_ACCEPTANCE";
	}
}

/**
 * Sets the query-ref message
 * @param myProcessor the CProcessor managing the conversation
 * @param messageToSend request message
 */
protected void doQueryref(ConvCProcessor myProcessor,
		ACLMessage messageToSend) {
	
	FQConversation conv = (FQConversation) myProcessor.getConversation();
	
	conv.aquire_semaphore();
	messageToSend.setContent(conv.query.toString());
	messageToSend.setProtocol("fipa-query");
	messageToSend.setPerformative(ACLMessage.QUERY_REF);
	messageToSend.setReceiver(new AgentID(conv.Participant)); 
	messageToSend.setSender(myProcessor.getMyAgent().getAid());
	messageToSend.setHeader("jasonID", conv.jasonConvID);
	
}


class QUERY_REF_Method implements SendStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageToSend) {
		doQueryref((ConvCProcessor)myProcessor, messageToSend);
		return "WAIT_FOR_ACCEPTANCE";
	}
}


/**
 * Method to execute when the initiator receives a not-understood message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg not-understood message
 */
protected void doNotUnderstood(ConvCProcessor myProcessor, ACLMessage msg){
	FQConversation conv = (FQConversation) myProcessor.getConversation();
	conv.result= ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD);
}

class NOT_UNDERSTOOD_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doNotUnderstood((ConvCProcessor) myProcessor, messageReceived);
		return "FINAL_QUERY_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives a refuse message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg failure message
 */
protected void doRefuse(ConvCProcessor myProcessor, ACLMessage msg){
	FQConversation conv = (FQConversation) myProcessor.getConversation();
	conv.result=ACLMessage.getPerformative(ACLMessage.REFUSE);
}

class REFUSE_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doRefuse((ConvCProcessor) myProcessor, messageReceived);
		return "FINAL_QUERY_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives a failure message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg failure message
 */
protected void doFailure(ConvCProcessor myProcessor, ACLMessage msg){
	FQConversation conv = (FQConversation) myProcessor.getConversation();
	
	conv.result=ACLMessage.getPerformative(ACLMessage.FAILURE);
}

class FAILURE_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doFailure((ConvCProcessor) myProcessor, messageReceived);
		return "FINAL_QUERY_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives an agree message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg agree message
 */
protected void doAgree(ConvCProcessor myProcessor, ACLMessage msg){
	FQConversation conv = (FQConversation) myProcessor.getConversation();
	conv.result=ACLMessage.getPerformative(ACLMessage.AGREE);
}

class AGREE_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doAgree((ConvCProcessor) myProcessor, messageReceived);
		return "WAIT_FOR_RESULTS";
	}
}


/**
 * Method to execute when the initiator receives an inform message
 * @param myProcessor
 * @param msg
 */
protected void doInform(ConvCProcessor myProcessor, ACLMessage msg) {

	FQConversation conv = (FQConversation) myProcessor.getConversation();

	List<Literal> allperc = new ArrayList<Literal>();
	conv.evaluationResult=msg.getContent();
	
	String percept = "queryResult("+conv.Participant+","+conv.evaluationResult+","+conv.jasonConvID+")[source(self)]";
	allperc.add(Literal.parseLiteral(percept));
	((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	
	conv.result = ACLMessage.getPerformative(ACLMessage.INFORM);

	conv.aquire_semaphore();
	msg.setProtocol("fipa-query");
	msg.setPerformative(conv.performative);
}

class INFORM_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doInform((ConvCProcessor)myProcessor, messageReceived);
		return "FINAL_QUERY_INITIATOR";
	}
}

/**
 * Method to execute when the initiator ends the conversation
 * @param myProcessor
 * @param messageToSend
 */
protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend){

	FQConversation conv = (FQConversation) myProcessor.getConversation();
	
	List<Literal> allperc = new ArrayList<Literal>();
	String percept = "conversationended("+conv.jasonConvID+","+'"'+ conv.result.toLowerCase()+'"' +")[source(self)]";
	
	allperc.add(Literal.parseLiteral(percept));
	((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	
	messageToSend = myProcessor.getLastSentMessage();
	messageToSend.setProtocol("fipa-query");
	messageToSend.setPerformative(conv.performative);
	
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
	 * @param timeout for waiting after sending the query message and after receiving the "agree" confirmation
	 * @return a new fipa request initiator factory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter, ACLMessage requestMessage,
			int availableConversations, ConvJasonAgent myAgent, long timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("protocol = fipa-query"); 
		}
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);
		
		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());

		// QUERYIF State
		
		SendState QUERYIF = new SendState("QUERY_IF_INITIATOR");

		QUERYIF.setMethod(new QUERY_IF_Method());
		requestMessage = new ACLMessage(ACLMessage.QUERY_IF);
		requestMessage.setProtocol("fipa-request");		
		QUERYIF.setMessageTemplate(requestMessage);
		processor.registerState(QUERYIF);
		processor.addTransition("BEGIN", "QUERY_IF_INITIATOR");
		
		
		// QUERYIF State

		SendState QUERYREF = new SendState("QUERY_REF_INITIATOR");

		QUERYREF.setMethod(new QUERY_REF_Method());
		requestMessage = new ACLMessage(ACLMessage.QUERY_REF);
		requestMessage.setProtocol("fipa-request");		
		QUERYREF.setMessageTemplate(requestMessage);
		processor.registerState(QUERYREF);
		processor.addTransition("BEGIN", "QUERY_REF_INITIATOR");

		// WAIT_FOR_ACCEPTANCE State
		
		processor.registerState(new WaitState("WAIT_FOR_ACCEPTANCE", timeout));
		processor.addTransition("QUERY_IF_INITIATOR", "WAIT_FOR_ACCEPTANCE");
		processor.addTransition("QUERY_REF_INITIATOR", "WAIT_FOR_ACCEPTANCE");
		
		// NOT_UNDERSTOOD State
		
		ReceiveState NOT_UNDERSTOOD = new ReceiveState("NOT_UNDERSTOOD_QUERY_INITIATOR");
		NOT_UNDERSTOOD.setMethod(new NOT_UNDERSTOOD_Method());
		filter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD));
		NOT_UNDERSTOOD.setAcceptFilter(filter);
		processor.registerState(NOT_UNDERSTOOD);
		processor.addTransition("WAIT_FOR_ACCEPTANCE", "NOT_UNDERSTOOD_QUERY_INITIATOR");
		
		// REFUSE State
		
		ReceiveState REFUSE = new ReceiveState("REFUSE_QUERY_INITIATOR");
		REFUSE.setMethod(new REFUSE_Method());
		filter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.REFUSE));
		REFUSE.setAcceptFilter(filter);
		processor.registerState(REFUSE);
		processor.addTransition("WAIT_FOR_ACCEPTANCE", "REFUSE_QUERY_INITIATOR");
		
		// FAILURE State
		
		ReceiveState FAILURE = new ReceiveState("FAILURE_QUERY_INITIATOR");
		FAILURE.setMethod(new FAILURE_Method());
		filter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.FAILURE));
		FAILURE.setAcceptFilter(filter);
		processor.registerState(FAILURE);
		processor.addTransition("WAIT_FOR_ACCEPTANCE", "FAILURE_QUERY_INITIATOR");
		
		// AGREE State
		
		ReceiveState AGREE = new ReceiveState("AGREE_QUERY_INITIATOR");
		AGREE.setMethod(new AGREE_Method());
		filter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.AGREE));
		AGREE.setAcceptFilter(filter);
		processor.registerState(AGREE);
		processor.addTransition("WAIT_FOR_ACCEPTANCE", "AGREE_QUERY_INITIATOR");
		
		// SECOND_WAIT State
		
		processor.registerState(new WaitState("WAIT_FOR_RESULTS", timeout));
		processor.addTransition("AGREE_QUERY_INITIATOR", "WAIT_FOR_RESULTS");
		processor.addTransition("WAIT_FOR_RESULTS", "FAILURE_QUERY_INITIATOR");
		processor.addTransition("WAIT_FOR_RESULTS", "NOT_UNDERSTOOD_QUERY_INITIATOR");
		
		
		// INFORM State
		
		ReceiveState INFORM = new ReceiveState("INFORM_QUERY_INITIATOR");
		INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("performative = " + ACLMessage.getPerformative(ACLMessage.INFORM));
		INFORM.setAcceptFilter(filter);
		processor.registerState(INFORM);
		processor.addTransition("WAIT_FOR_RESULTS", "INFORM_QUERY_INITIATOR");

		// FINAL State

		FinalState FINAL = new FinalState("FINAL_QUERY_INITIATOR");

		FINAL.setMethod(new FINAL_Method());
		
		processor.registerState(FINAL);
		processor.addTransition("REFUSE_QUERY_INITIATOR", "FINAL_QUERY_INITIATOR");
		processor.addTransition("NOT_UNDERSTOOD_QUERY_INITIATOR", "FINAL_QUERY_INITIATOR");
		processor.addTransition("INFORM_QUERY_INITIATOR", "FINAL_QUERY_INITIATOR");
		processor.addTransition("FAILURE_QUERY_INITIATOR", "FINAL_QUERY_INITIATOR");
		
		return theFactory;
	}

	
}
