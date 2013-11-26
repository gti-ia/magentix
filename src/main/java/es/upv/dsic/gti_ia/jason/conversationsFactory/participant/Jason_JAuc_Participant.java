package es.upv.dsic.gti_ia.jason.conversationsFactory.participant;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.JAucPartConversation;
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
 * This class represents a template for a Japanese Auction Protocol from the participant 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_JAuc_Participant {

	protected TransitionSystem Ts; 
	
	public Jason_JAuc_Participant( TransitionSystem ts) {
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
			return "WAIT_FOR_NEW_CONV_DATA";
		};
	}

	/**
	 * Method executed when the participant receives the new converstaion internal information
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg bid acceptance message
	 * @return next state of this conversation
	 */
	protected String doReceiveNewConvData(ConvCProcessor myProcessor, ACLMessage msg){
		JAucPartConversation newConv;
		Conversation conv = myProcessor.getConversation();
		if (conv.jasonConvID.compareTo("")==0){
			String jasonConvID = msg.getHeaderValue("jasonID");
			String factName = conv.factoryName;
			//At this point the conversation associated to the CProcessor has no value
			//for the internal conversation ID so it is updated. The factory name is already setted
			conv.jasonConvID = jasonConvID;
			conv.initiator = msg.getSender();
			newConv = new JAucPartConversation(conv.jasonConvID, conv.internalConvID, conv.initiator,"",factName);
			((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);
		}else{
			newConv = (JAucPartConversation) myProcessor.getConversation();
		}
		return "WAIT_FOR_BID_CALL";
	}
	class RECEIVE_NEW_CONV_DATA_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			return doReceiveNewConvData((ConvCProcessor) myProcessor, messageReceived);
		}
	}
	
	
	/**
	 * Method executed when the participant receives a call for bid acceptance
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg bid acceptance message
	 * @return next state of this conversation
	 */
	protected String doReceiveBidceCall(ConvCProcessor myProcessor, ACLMessage msg){
		JAucPartConversation conv;
		conv = (JAucPartConversation) myProcessor.getConversation();
		conv.AuctionLevel = msg.getHeaderValue("auctionlevel");
		String result = "";
		AgentID Sender = myProcessor.getLastReceivedMessage().getSender();
		String request = msg.getContent();
		String bid = msg.getHeaderValue("bid");
		String participants = msg.getHeaderValue("participants");
		String newMsgContent = "callforbid"+"("+Sender.name+","+request+","+participants+","+bid+","+conv.jasonConvID+")[source(self)]";
		List<Literal> allpercep = new ArrayList<Literal>();
		allpercep.add(Literal.parseLiteral(newMsgContent));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allpercep);
		
		conv.aquire_semaphore();
		
		//if (conv.Accept){  
		result = "SEND_ANSWER"; //conv.Accept is true if accepted, false otherwise
		//}else result = "FINAL"; 
		return result;
	}
	class RECEIVE_BID_CALL_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			return doReceiveBidceCall((ConvCProcessor) myProcessor, messageReceived);
		}
	}

	/**
	 * Method executed when the participant sends its answer
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend proposal message
	 */
	protected String doSendAnswer(ConvCProcessor myProcessor,
			ACLMessage messageToSend){
		JAucPartConversation conv = (JAucPartConversation)myProcessor.getConversation();
		String result ="";
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		messageToSend.setHeader("auctionlevel", conv.AuctionLevel);
		if (conv.Accept){
			messageToSend.setContent("agree");
			messageToSend.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			result="WAIT_FOR_BID_CALL2";
		}else{
			messageToSend.setContent("reject");
			messageToSend.setPerformative(ACLMessage.REJECT_PROPOSAL);
			result="FINAL";
		}
		
		messageToSend.setProtocol("japanese-auction");
		return result;
	}
	class SEND_ANSWER_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			
			return doSendAnswer((ConvCProcessor) myProcessor, messageToSend);
		}
	}

	protected void doTimeout(ConvCProcessor myProcessor, ACLMessage msg) {

	}
	class TIMEOUT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doTimeout((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL";
		}
	}

	/**
	 * Method executed when the initiator sends the winner confirmation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg accept message
	 */
	protected void doReceiveWinnerConfirm(ConvCProcessor myProcessor, ACLMessage msg){
		JAucPartConversation conv = (JAucPartConversation)myProcessor.getConversation();
		AgentID Sender = myProcessor.getLastReceivedMessage().getSender();
		String finalbid =  msg.getContent();
		String request = msg.getHeaderValue("request");
		String newMsgContent = "winner"+"(\""+Sender.name+"\","+request+","+finalbid+","+conv.jasonConvID+")[source(self)]";
		List<Literal> allpercep = new ArrayList<Literal>();
		allpercep.add(Literal.parseLiteral(newMsgContent));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allpercep);
	}

	class RECEIVE_WINNER_CONFIRM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveWinnerConfirm((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	
	/**
	 * Class for receiving the end of protocol confirmation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param receivedMessage received message
	 */
	class RECEIVE_END_CONFIRMATION_Method implements ReceiveStateMethod{
		@Override
		public String run(CProcessor myProcessor, ACLMessage receivedMessage) {
			return "FINAL";
		}
		
	}
	
	
	
	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend) {
		JAucPartConversation conv = (JAucPartConversation)myProcessor.getConversation();
		myProcessor.getMyAgent().removeFactory(conv.factoryName);
		messageToSend = myProcessor.getLastSentMessage();
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal((ConvCProcessor) myProcessor, messageToSend);
		}
	}
	

	
	/**
	 * Creates a new contract-net participant CFactory
	 * @param name factory's name
	 * @param filter message filter
	 * @param template first message to send
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this CFactory
	 * @param timeout for waiting after sending the proposal
	 * @return a new fipa contract net participant factory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, ConvJasonAgent myAgent, int timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("protocol = japanese-auction AND purpose = startdata");
		}
		
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_NEW_CONV_DATA State
		WaitState WAIT_FOR_NEW_CONV_DATA = new WaitState("WAIT_FOR_NEW_CONV_DATA", 4000);
		processor.registerState(WAIT_FOR_NEW_CONV_DATA);
		processor.addTransition(BEGIN, WAIT_FOR_NEW_CONV_DATA);
		
		// RECEIVE_NEW_CONV_DATA State
		ReceiveState RECEIVE_NEW_CONV_DATA = new ReceiveState(
				"RECEIVE_NEW_CONV_DATA");
		RECEIVE_NEW_CONV_DATA.setMethod(new RECEIVE_NEW_CONV_DATA_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.INFORM)+" AND purpose = startdata");
		RECEIVE_NEW_CONV_DATA.setAcceptFilter(filter);
		processor.registerState(RECEIVE_NEW_CONV_DATA);
		processor.addTransition(WAIT_FOR_NEW_CONV_DATA,
				RECEIVE_NEW_CONV_DATA);
		
		// WAIT_FOR_BID_CALL State
		WaitState WAIT_FOR_BID_CALL = new WaitState("WAIT_FOR_BID_CALL", timeout);
		processor.registerState(WAIT_FOR_BID_CALL);
		processor.addTransition(RECEIVE_NEW_CONV_DATA, WAIT_FOR_BID_CALL);
		
		// RECEIVE_BID_CALL State

		ReceiveState RECEIVE_BID_CALL = new ReceiveState(
				"RECEIVE_BID_CALL");
		RECEIVE_BID_CALL.setMethod(new RECEIVE_BID_CALL_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.PROPOSE)+" AND ( purpose = start OR purpose = bid )");
		RECEIVE_BID_CALL.setAcceptFilter(filter);
		processor.registerState(RECEIVE_BID_CALL);
		processor.addTransition(WAIT_FOR_BID_CALL,
				RECEIVE_BID_CALL);

		// SEND_ANSWER State

		SendState SEND_ANSWER = new SendState("SEND_ANSWER");

		SEND_ANSWER.setMethod(new SEND_ANSWER_Method());
		template.setProtocol("japanese-auction");
		//template.setPerformative(ACLMessage.ACCEPT_PROPOSAL); //this will is setted in SEND_ANSWER_Method()
		SEND_ANSWER.setMessageTemplate(template);
		processor.registerState(SEND_ANSWER);
		processor.addTransition(RECEIVE_BID_CALL, SEND_ANSWER);
		
		

		// WAIT_FOR_BID_CALL2 State
		WaitState WAIT_FOR_BID_CALL2 = new WaitState("WAIT_FOR_BID_CALL2", timeout);
		processor.registerState(WAIT_FOR_BID_CALL2);
		processor.addTransition(SEND_ANSWER, WAIT_FOR_BID_CALL2);
		processor.addTransition(WAIT_FOR_BID_CALL2,RECEIVE_BID_CALL);
		
		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.INFORM)+" AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
		processor.addTransition(WAIT_FOR_NEW_CONV_DATA, TIMEOUT);
		processor.addTransition(WAIT_FOR_BID_CALL, TIMEOUT);
		processor.addTransition(WAIT_FOR_BID_CALL2, TIMEOUT);


		// RECEIVE_WINNER_CONFIRMATION State

		ReceiveState RECEIVE_WINNER_CONFIRM = new ReceiveState(
				"RECEIVE_WINNER_CONFIRM");
		RECEIVE_WINNER_CONFIRM.setMethod(new RECEIVE_WINNER_CONFIRM_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.INFORM)+" AND purpose = winner");
		RECEIVE_WINNER_CONFIRM.setAcceptFilter(filter);
		processor.registerState(RECEIVE_WINNER_CONFIRM);
		processor.addTransition(WAIT_FOR_BID_CALL2,
				RECEIVE_WINNER_CONFIRM);

		
		// RECEIVE_END_CONFIRMATION State
		ReceiveState RECEIVE_END_CONFIRMATION = new ReceiveState(
		"RECEIVE_END_CONFIRMATION");
		RECEIVE_END_CONFIRMATION.setMethod(new RECEIVE_END_CONFIRMATION_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.INFORM)+" AND purpose = end");
		RECEIVE_END_CONFIRMATION.setAcceptFilter(filter);
		processor.registerState(RECEIVE_END_CONFIRMATION);
		processor.addTransition(WAIT_FOR_BID_CALL2, RECEIVE_END_CONFIRMATION);
		
		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(RECEIVE_WINNER_CONFIRM, FINAL);	
		processor.addTransition(SEND_ANSWER, FINAL);
		//processor.addTransition(RECEIVE_BID_CALL, FINAL);
		processor.addTransition(TIMEOUT, FINAL);
		processor.addTransition(RECEIVE_END_CONFIRMATION, FINAL);
		return theFactory;
	} 
	

}
