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
			return "WAIT_FOR_BID_CALL";
		};
	}

	/**
	 * Method executed when the participant receives a call for bid acceptance
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg bid acceptance message
	 * @return next state of this conversation
	 */
	protected String doReceiveBidceCall(ConvCProcessor myProcessor, ACLMessage msg){
		
		JAucPartConversation newConv;
		
		Conversation conv = myProcessor.getConversation();
		if (conv.jasonConvID.compareTo("")==0){
			String jasonConvID = msg.getHeaderValue("jasonID");
			//At this point the conversation associated to the CProcessor has no value
			//for the internal conversation ID so it is updated
			conv.jasonConvID = jasonConvID;
			conv.initiator = msg.getSender();
			newConv = new JAucPartConversation(conv.jasonConvID, conv.internalConvID, conv.initiator,"");
			((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);
		}else{
			newConv = (JAucPartConversation) myProcessor.getConversation();
		}
		newConv.AuctionLevel = msg.getHeaderValue("auctionlevel");
		String result = "";
		AgentID Sender = myProcessor.getLastReceivedMessage().getSender();
		String request = msg.getContent();
		String bid = msg.getHeaderValue("bid");
		String participants = msg.getHeaderValue("participants");
		String newMsgContent = "callforbid"+"("+Sender.name+","+request+","+participants+","+bid+","+conv.jasonConvID+")[source(self)]";
		List<Literal> allpercep = new ArrayList<Literal>();
		
		allpercep.add(Literal.parseLiteral(newMsgContent));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allpercep);
		
		newConv.aquire_semaphore();
		
		if (newConv.Accept){  //it must be setted
			result = "SEND_AGREE";
		}else result = "FINAL";

		return result;
	}
	class RECEIVE_BID_CALL_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			return doReceiveBidceCall((ConvCProcessor) myProcessor, messageReceived);
		}
	}

	/**
	 * Method executed when the participant sends a proposal
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend proposal message
	 */
	protected void doSendAgree(ConvCProcessor myProcessor,
			ACLMessage messageToSend){

		JAucPartConversation conv = (JAucPartConversation)myProcessor.getConversation();
		
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		messageToSend.setContent("agree");
		messageToSend.setHeader("auctionlevel", conv.AuctionLevel);
		messageToSend.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		messageToSend.setProtocol("japanese-auction");
	}
	class SEND_AGREE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendAgree((ConvCProcessor) myProcessor, messageToSend);
			return "WAIT_FOR_BID_CALL2";
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
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
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
			filter = new MessageFilter("protocol = japanese-auction AND purpose = start");
		}
		
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_BID_CALL State
		WaitState WAIT_FOR_BID_CALL = new WaitState("WAIT_FOR_BID_CALL", timeout);
		processor.registerState(WAIT_FOR_BID_CALL);
		processor.addTransition(BEGIN, WAIT_FOR_BID_CALL);
		
		// RECEIVE_BID_CALL State

		ReceiveState RECEIVE_BID_CALL = new ReceiveState(
				"RECEIVE_BID_CALL");
		RECEIVE_BID_CALL.setMethod(new RECEIVE_BID_CALL_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.PROPOSE)+" AND ( purpose = start OR purpose = bid )");
		RECEIVE_BID_CALL.setAcceptFilter(filter);
		processor.registerState(RECEIVE_BID_CALL);
		processor.addTransition(WAIT_FOR_BID_CALL,
				RECEIVE_BID_CALL);

		// SEND_AGREE State

		SendState SEND_AGREE = new SendState("SEND_AGREE");

		SEND_AGREE.setMethod(new SEND_AGREE_Method());
		template.setProtocol("japanese-auction");
		template.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
		SEND_AGREE.setMessageTemplate(template);
		processor.registerState(SEND_AGREE);
		processor.addTransition(RECEIVE_BID_CALL, SEND_AGREE);
		
		

		// WAIT_FOR_BID_CALL2 State
		WaitState WAIT_FOR_BID_CALL2 = new WaitState("WAIT_FOR_BID_CALL2", timeout);
		processor.registerState(WAIT_FOR_BID_CALL2);
		processor.addTransition(SEND_AGREE, WAIT_FOR_BID_CALL2);
		processor.addTransition(WAIT_FOR_BID_CALL2,RECEIVE_BID_CALL);
		
		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
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

		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(RECEIVE_WINNER_CONFIRM, FINAL);			
		processor.addTransition(RECEIVE_BID_CALL, FINAL);
		processor.addTransition(TIMEOUT, FINAL);
		return theFactory;
	} 
	

}
