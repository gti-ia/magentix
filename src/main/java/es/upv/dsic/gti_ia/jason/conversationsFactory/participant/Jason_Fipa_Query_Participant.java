package es.upv.dsic.gti_ia.jason.conversationsFactory.participant;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FQConversation;

import java.util.ArrayList;
import java.util.List;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;

/**
 * This class represents a template for a Fipa Query If/Ref Protocol from the participant 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_Fipa_Query_Participant {

	protected TransitionSystem Ts; 

	public Jason_Fipa_Query_Participant(String sagName, 
			TransitionSystem ts) {

		Ts = ts;
		
	}
	

	/**
	 * Method executed at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg first message assigned to this conversation
	 */
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "WAIT";
		};
	}

	/**
	 * Method executed when the initiator receives the query
	 * @param myProcessor the CProcessor managing the conversation
	 * @param query request message
	 * @return
	 */
	protected String doReceiveQuery(ConvCProcessor myProcessor,
			ACLMessage query){
		
		String jasonID = query.getHeaderValue("jasonID");
		
		Conversation conv =  myProcessor.getConversation();
		conv.jasonConvID = jasonID;
		conv.initiator = query.getSender();
		
		FQConversation newConv = new FQConversation(conv.jasonConvID,conv.internalConvID,myProcessor.getMyAgent().getName(),
				"",conv.initiator);
		((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);
		
		newConv.query = Literal.parseLiteral(query.getContent());
		String queryKind ="";
		if (query.getPerformative().compareTo(ACLMessage.getPerformative(ACLMessage.QUERY_IF))==0){
			queryKind ="fqip";
			newConv.performative = ACLMessage.QUERY_IF;
		}
		if (query.getPerformative().compareTo(ACLMessage.getPerformative(ACLMessage.QUERY_REF))==0){
			queryKind ="fqrp";
			newConv.performative = ACLMessage.QUERY_REF;
		}		
		List<Literal> allperc = new ArrayList<Literal>();

		String percept = "query("+newConv.initiator.name+","+newConv.query.toString()+","+queryKind+","+newConv.jasonConvID+")[source(self)]";
		
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
		newConv.aquire_semaphore();
		
		String result = null; 
		if (newConv.result.compareTo(ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD))==0){
			result = "NOT_UNDERSTOOD";
		}else 
		if (newConv.result.compareTo(ACLMessage.getPerformative(ACLMessage.REFUSE))==0){
			result = "REFUSE";
		}else 
		if (newConv.result.compareTo(ACLMessage.getPerformative(ACLMessage.FAILURE))==0){
			result = "FAILURE";
		}else 
		if (newConv.result.compareTo(ACLMessage.getPerformative(ACLMessage.AGREE))==0){
			result = "AGREE";
		} 
		
	  return result;
	}

	class RECEIVE_QUERY_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doReceiveQuery((ConvCProcessor)myProcessor, messageReceived);
			
		}
	}

	
	/**
	 * Method executed when the timeout is reached while the initiator was 
	 * waiting for the acceptance of the request or for the confirmation for results
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg timeout message
	 */
	protected void doTimeout(ConvCProcessor myProcessor, ACLMessage msg) {
		FQConversation conv = (FQConversation)myProcessor.getConversation();
		conv.result = '"'+"Timeout"+'"';
	}

	class TIMEOUT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doTimeout((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	/**
	 * Sets the not-understood message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend not-understood message
	 */
	protected void doNotUnderstood(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-query");
		messageToSend.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		
		conv.result = '"'+ACLMessage.getPerformative(ACLMessage.NOT_UNDERSTOOD)+'"';
	}

	class NOT_UNDERSTOOD_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doNotUnderstood((ConvCProcessor)myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Sets the refuse message
	 * @param myProcessor
	 * @param messageToSend
	 */
	protected void doRefuse(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-query");
		messageToSend.setPerformative(ACLMessage.REFUSE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		
		conv.result = '"'+ACLMessage.getPerformative(ACLMessage.REFUSE)+'"';
	}

	class REFUSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doRefuse((ConvCProcessor)myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Sets the failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend failure message
	 */
	protected void doFailure(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-query");
		messageToSend.setPerformative(ACLMessage.FAILURE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		
		conv.result = '"'+ACLMessage.getPerformative(ACLMessage.FAILURE)+'"';
		
	}

	class FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFailure((ConvCProcessor)myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Sets the agree message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend agree message
	 */
	protected void doAgree(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-query");
		messageToSend.setPerformative(ACLMessage.AGREE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		
		conv.result = '"'+ACLMessage.getPerformative(ACLMessage.AGREE)+'"';
	}

	class AGREE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doAgree((ConvCProcessor)myProcessor, messageToSend);
			return "ACTION";
		}
	}
	
	/**
	 * Perform the requested action
	 * @param myProcessor the CProcessor managing the conversation
	 * @return next conversation state
	 */
	protected String doAction(ConvCProcessor myProcessor){
		
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		
		String result = "FAILURE"; 
		if (conv.evaluationResult.compareTo("")!= 0){
			result="INFORM";
		}
		
	  return result;
	}
	
	class ACTION_Method implements ActionStateMethod{
		@Override
		public String run(CProcessor myProcessor) {
			return doAction((ConvCProcessor)myProcessor);
		}
		
	}
	
	/**
	 * Sets the inform message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param response inform message
	 */
	protected void doInform(ConvCProcessor myProcessor, ACLMessage response){
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		response.setProtocol("fipa-query");
		response.setPerformative(ACLMessage.INFORM);
		response.setReceiver(conv.initiator);
		response.setSender(myProcessor.getMyAgent().getAid());
		response.setContent(conv.evaluationResult);
		response.setInReplyTo("query");
		
		conv.result = '"'+ACLMessage.getPerformative(ACLMessage.INFORM)+'"';
	}

	class INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

			doInform((ConvCProcessor)myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend){
		FQConversation conv = (FQConversation) myProcessor.getConversation();
		
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationended("+conv.jasonConvID+","+ conv.result.toLowerCase() +")[source(self)]";
		
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

	}
	
	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			 doFinal((ConvCProcessor)myProcessor, messageToSend);
		}
	}

	/**
	 * Creates a new participant fipa request factory
	 * @param name factory's name
	 * @param filter message filter
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @return a new fipa request participant factory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter,
			int availableConversations, ConvJasonAgent myAgent){
		

		//MessageFilter filter;
		ACLMessage template;
		// Create factory

		if (filter == null) {
			filter = new MessageFilter("protocol = fipa-query");
		}
		//template.setProtocol("REQUEST");
		//template.setPerformative(ACLMessage.REQUEST);
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT State
		processor.registerState(new WaitState("WAIT", 0));
		processor.addTransition("BEGIN", "WAIT");
		
		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
		processor.addTransition("WAIT", "TIMEOUT");
		
		// RECEIVE_QUERY State
		ReceiveState RECEIVE_QUERY = new ReceiveState("RECEIVE_QUERY");
		RECEIVE_QUERY.setMethod(new RECEIVE_QUERY_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.QUERY_IF)+" OR performative = "+ACLMessage.getPerformative(ACLMessage.QUERY_REF));
		RECEIVE_QUERY.setAcceptFilter(filter);
		processor.registerState(RECEIVE_QUERY);
		processor.addTransition("WAIT", "RECEIVE_QUERY");
		
		// NOT_UNDERSTOOD State

		SendState NOT_UNDERSTOOD = new SendState("NOT_UNDERSTOOD");
		NOT_UNDERSTOOD.setMethod(new NOT_UNDERSTOOD_Method());
		template = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		template.setProtocol("fipa-query");
		template.setPerformative(ACLMessage.INFORM);
		NOT_UNDERSTOOD.setMessageTemplate(template);
		processor.registerState(NOT_UNDERSTOOD);
		processor.addTransition("RECEIVE_QUERY", "NOT_UNDERSTOOD");
		
		// REFUSE State

		SendState REFUSE = new SendState("REFUSE");
		REFUSE.setMethod(new REFUSE_Method());
		template = new ACLMessage(ACLMessage.REFUSE);
		template.setProtocol("fipa-query");
		template.setPerformative(ACLMessage.INFORM);
		REFUSE.setMessageTemplate(template);
		processor.registerState(REFUSE);
		processor.addTransition("RECEIVE_QUERY", "REFUSE");
		
		// AGREE State

		SendState AGREE = new SendState("AGREE");
		AGREE.setMethod(new AGREE_Method());
		template = new ACLMessage(ACLMessage.AGREE);
		template.setProtocol("fipa-query");
		template.setPerformative(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);
		processor.registerState(AGREE);
		processor.addTransition("RECEIVE_QUERY", "AGREE");
		
		// ACTION State
		ActionState ACTION = new ActionState("ACTION");
		ACTION.setMethod(new ACTION_Method());
		processor.registerState(ACTION);
		processor.addTransition("AGREE", "ACTION");
		
		// FAILURE State

		SendState FAILURE = new SendState("FAILURE");
		FAILURE.setMethod(new FAILURE_Method());
		template = new ACLMessage(ACLMessage.FAILURE);
		template.setProtocol("fipa-query");
		template.setPerformative(ACLMessage.FAILURE);
		FAILURE.setMessageTemplate(template);
		processor.registerState(FAILURE);
		processor.addTransition("ACTION", "FAILURE");

		processor.addTransition("RECEIVE_QUERY", "FAILURE");
		
		// INFORM State

		SendState INFORM = new SendState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		template = new ACLMessage(ACLMessage.INFORM);
		template.setProtocol("fipa-query");
		INFORM.setMessageTemplate(template);
		processor.registerState(INFORM);
		processor.addTransition("ACTION", "INFORM");
		processor.addTransition("ACTION", "FAILURE");
		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");
		
		FINAL.setMethod(new FINAL_Method());
		processor.registerState(FINAL);
		processor.addTransition("NOT_UNDERSTOOD", "FINAL");
		processor.addTransition("REFUSE", "FINAL");
		processor.addTransition("FAILURE", "FINAL");
		processor.addTransition("INFORM", "FINAL");
		processor.addTransition("TIMEOUT", "FINAL");

		return theFactory;
				
		
	}
}
