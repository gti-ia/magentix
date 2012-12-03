package es.upv.dsic.gti_ia.jason.conversationsFactory.initiator;


import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRConversation;

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

/**
 * This class represents a template for a Fipa Request Protocol from the initiator 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_Fipa_Request_Initiator {

	protected TransitionSystem Ts; 
	
	public Jason_Fipa_Request_Initiator(String sagName,
			TransitionSystem ts) {

		Ts = ts;
		
	}
	

/**
 * Method to execute at the beginning of the conversation
 * @param myProcessor the CProcessor managing the conversation
 * @param msg first message to send
 */
/*protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
	myProcessor.getInternalData().put("InitialMessage", msg);		
}*/

protected void doBegin(ConvCProcessor myProcessor,
		ACLMessage messageToSend) {
	FRConversation conv =  (FRConversation) myProcessor.getConversation();
	messageToSend.setContent(conv.initialMessage);
	myProcessor.getInternalData().put("InitialMessage", messageToSend);

}

class BEGIN_Method implements BeginStateMethod {
	public String run(CProcessor myProcessor, ACLMessage msg) {
		doBegin((ConvCProcessor)myProcessor, msg);
		return "REQUEST_REQUEST_INITIATOR";
	};
}

/**
 * Sets the request message
 * @param myProcessor the CProcessor managing the conversation
 * @param messageToSend request message
 */
protected void doRequest(ConvCProcessor myProcessor,
		ACLMessage messageToSend) {
	
	FRConversation conv = (FRConversation) myProcessor.getConversation();

	conv.aquire_semaphore();
	
	messageToSend.setContent(conv.frMessage);
	messageToSend.setProtocol("fipa-request");
	messageToSend.setPerformative(ACLMessage.REQUEST);
	messageToSend.setReceiver(new AgentID(conv.Participant));  
	messageToSend.setSender(myProcessor.getMyAgent().getAid() );
	messageToSend.setHeader("jasonID", conv.jasonConvID);
	messageToSend.setHeader("data", conv.frData);
	
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
	FRConversation conv = (FRConversation)myProcessor.getConversation();
	
	conv.FinalResult = '"'+ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD)+'"';

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
	FRConversation conv = (FRConversation)myProcessor.getConversation();
	conv.FinalResult = '"'+ACLMessage.getPerformative(ACLMessage.REFUSE)+'"';

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
protected void doAgree(ConvCProcessor myProcessor, ACLMessage msg){
	FRConversation conv = (FRConversation)myProcessor.getConversation();
	conv.FinalResult = '"'+ACLMessage.getPerformative(ACLMessage.AGREE)+'"';

}

class AGREE_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doAgree((ConvCProcessor) myProcessor, messageReceived);
		return "SECOND_WAIT_REQUEST_INITIATOR";
	}
}

/**
 * Method executed when the timeout is reached while the initiator was 
 * waiting for the acceptance of the request 
 * @param myProcessor the CProcessor managing the conversation
 * @param msg timeout message
 */
protected void doTimeout(ConvCProcessor myProcessor, ACLMessage msg) {
	FRConversation conv = (FRConversation)myProcessor.getConversation();
	conv.FinalResult = '"'+"Timeout"+'"';
}

class TIMEOUT_Method implements ReceiveStateMethod {
	public String run(CProcessor myProcessor, ACLMessage messageReceived) {
		doTimeout((ConvCProcessor) myProcessor, messageReceived);
		return "FINAL_REQUEST_INITIATOR";
	}
}

/**
 * Method to execute when the initiator receives a failure message
 * @param myProcessor the CProcessor managing the conversation
 * @param msg failure message
 */
protected void doFailure(ConvCProcessor myProcessor, ACLMessage msg){
	FRConversation conv = (FRConversation)myProcessor.getConversation();
	conv.FinalResult = '"'+ACLMessage.getPerformative(ACLMessage.FAILURE)+'"';

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

	FRConversation conv = (FRConversation) myProcessor.getConversation();
	conv.TaskResult = msg.getContent();
	List<Literal> allperc = new ArrayList<Literal>();
	String percept = "taskdonesuccessfully("+conv.Participant +","+conv.TaskResult+","+conv.jasonConvID+")[source(self)]";
	allperc.add(Literal.parseLiteral(percept));
	((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

	conv.aquire_semaphore();
	
	msg.setProtocol("fipa-request");
	msg.setPerformative(ACLMessage.REQUEST);
	
	conv.FinalResult = '"'+ACLMessage.getPerformative(ACLMessage.INFORM)+'"';

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
	FRConversation conv = (FRConversation)myProcessor.getConversation();
	
	List<Literal> allperc = new ArrayList<Literal>();
	String percept = "conversationended("+conv.jasonConvID+","+conv.FinalResult+")[source(self)]";
	allperc.add(Literal.parseLiteral(percept));
	((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	
	messageToSend = myProcessor.getLastSentMessage();
	messageToSend.setProtocol("fipa-request");
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
			filter = new MessageFilter("performative = REQUEST"); //falta AND protocol = fipa-request;
		}
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

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
		

		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
		processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "TIMEOUT");
		
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
		
		processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "FAILURE_REQUEST_INITIATOR");
		
		// INFORM State

		ReceiveState INFORM = new ReceiveState("INFORM_REQUEST_INITIATOR");
		INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("performative = INFORM AND in-reply-to = request");
		INFORM.setAcceptFilter(filter);
		processor.registerState(INFORM);
		processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "INFORM_REQUEST_INITIATOR");
		
		processor.addTransition("FIRST_WAIT_REQUEST_INITIATOR", "INFORM_REQUEST_INITIATOR");

		processor.addTransition("SECOND_WAIT_REQUEST_INITIATOR", "TIMEOUT");
		// FINAL State

		FinalState FINAL = new FinalState("FINAL_REQUEST_INITIATOR");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition("TIMEOUT","FINAL_REQUEST_INITIATOR");
		processor.addTransition("INFORM_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		processor.addTransition("FAILURE_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		processor.addTransition("NOT_UNDERSTOOD_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		processor.addTransition("REFUSE_REQUEST_INITIATOR", "FINAL_REQUEST_INITIATOR");
		return theFactory;
	}

	
}
