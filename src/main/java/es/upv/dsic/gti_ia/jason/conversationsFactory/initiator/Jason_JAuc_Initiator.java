package es.upv.dsic.gti_ia.jason.conversationsFactory.initiator;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.JAucIniConversation;
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
 * This class represents a template for a Japanese Auction Protocol from the initiator 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_JAuc_Initiator {

	protected TransitionSystem Ts; 
	
	public Jason_JAuc_Initiator(TransitionSystem ts) {
		Ts = ts;
	}	


 	/**
	 * Method executed at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg first message to send in the conversation
	 */
	protected void doBegin(ConvCProcessor myProcessor, ACLMessage msg) {
		JAucIniConversation conv =  (JAucIniConversation) myProcessor.getConversation();
		msg.setContent(conv.initialMessage);
		myProcessor.getInternalData().put("InitialMessage", msg);
	}
	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin( (ConvCProcessor) myProcessor, msg);
			return "WAIT_FOR_PARTICIPANT_TO_JOIN";
		};
	}
	
	
	/**
	 * Method executed when the timeout for the participants to join finishes
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageReceived Message to send
	 */
	class RECEIVE_CANCEL_WAIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return "BID_CALL";
		}
	}
	
	/**
	 * Method executed when the initiator sends the next bid to the remaining participants
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend Message to send
	 */
	protected void doBidCall(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		
		JAucIniConversation conv = (JAucIniConversation)myProcessor.getConversation();
		
		
		messageToSend.setContent(conv.request); //it must have the right value
		messageToSend.setProtocol("japanese-auction");
		if (conv.AuctionLevel==0)
			messageToSend.setHeader("purpose", "start");
		else messageToSend.setHeader("purpose", "bid");
		messageToSend.setHeader("bid", String.valueOf(conv.NextBid));
		
		messageToSend.setPerformative(ACLMessage.PROPOSE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		//Adding receivers. This data can change during the conversation
		conv.updateActiveParticipants();
		List<String> receivers = new ArrayList<String>();
		AgentID rec ;
		if ((conv.PartParticipations!=null)&&(conv.PartParticipations.size()>0))
		{
			Iterator<AgentID> it = conv.ActiveParticipants.iterator();
			while (it.hasNext())
				{
					rec = it.next();
					messageToSend.addReceiver(rec);
					receivers.add("\""+rec.name+"\"");
				}
		}
		
		messageToSend.setHeader("participants",receivers.toString());		
		conv.AuctionLevel++;
		conv.AcceptancesReceivedInCurrLevel.clear();
		messageToSend.setHeader("jasonID", conv.jasonConvID);
		messageToSend.setHeader("auctionlevel",""+conv.AuctionLevel);
	}
	class BID_CALL_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doBidCall((ConvCProcessor)myProcessor, messageToSend);
			return "WAIT_FOR_ACCEPTANCES";
		}
	}

	/**
	 * Method executed when the initiator receives the bidder acceptance
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg Acceptance message
	 */
	protected String doReceiveAcceptance(ConvCProcessor myProcessor, ACLMessage msg){
		JAucIniConversation conv = (JAucIniConversation)myProcessor.getConversation();
		//incrementing participations
		if (conv.getParticipant(msg.getSender().name)!=null)
		{
			int prevVal = conv.getParticipation(msg.getSender().name); 
			conv.setParticipations(msg.getSender().name, prevVal+1);
		}
		String senderlevel = msg.getHeaderValue("auctionlevel");

		if ((senderlevel!=null)&&(senderlevel.compareTo(""+conv.AuctionLevel)==0))
			{
			conv.AcceptancesReceivedInCurrLevel.add(msg.getSender());
			}
		String result = "";
		if (conv.allActiveAcceptancesReceived()) //all active participants have answered
		{
			if (conv.AuctionLevel==conv.MaxIterations){//if the maximum number of iterations has been reached
				{
					result = "FINAL";
				}
			}else{
				conv.NextBid = conv.NextBid+conv.Increment;
				result = "BID_CALL";
			}
		}
		else
			result= "WAIT_FOR_ACCEPTANCES";
		return result;

	}
	class RECEIVE_ACCEPTANCE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doReceiveAcceptance((ConvCProcessor)myProcessor, messageReceived);
		}
	}
	
	/**
	 * Method executed when the timeout is reached while the initiator was waiting for participants answers
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg timeout message
	 */
	protected String doTimeout(ConvCProcessor myProcessor, ACLMessage msg) {
		JAucIniConversation conv = (JAucIniConversation)myProcessor.getConversation();
		String result = "";
			if (conv.AcceptancesReceivedInCurrLevel.size() == 1)
				{
					result = "SEND_WINNER";
				}
			if (conv.AcceptancesReceivedInCurrLevel.size() == 0)
				result = "FINAL";
			if (conv.AcceptancesReceivedInCurrLevel.size() > 1)
			{
				if (conv.AuctionLevel==conv.MaxIterations){//if the maximum number of iterations has been reached
					{
						result = "FINAL";
					}
				}else{
					conv.NextBid = conv.NextBid+conv.Increment;
					result = "BID_CALL";
				}
			}
		return result;
	}
	class TIMEOUT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doTimeout((ConvCProcessor) myProcessor,messageReceived);
			
		}
	}

	
	/**
	 * Method executed when the initiator calls for proposals
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend Message to send
	 */
	protected void doSendWinnerConfirmation(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		JAucIniConversation conv = (JAucIniConversation)myProcessor.getConversation();
		messageToSend.setContent(""+conv.NextBid);
		messageToSend.setProtocol("japanese-auction");
		messageToSend.setPerformative(ACLMessage.INFORM);
		messageToSend.setHeader("purpose", "winner");
		messageToSend.setHeader("request", conv.request);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		int acceptances = conv.AcceptancesReceivedInCurrLevel.size();
		if (acceptances ==1)
		{
			conv.winner = conv.AcceptancesReceivedInCurrLevel.get(0);
			messageToSend.setReceiver(conv.winner);
		}
	}
	class SEND_WINNER_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendWinnerConfirmation((ConvCProcessor) myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the initiator ends the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message of this conversation
	 */
	protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSentMessage();
		
		JAucIniConversation conv = (JAucIniConversation) myProcessor.getConversation();
		
		List<Literal> allperc = new ArrayList<Literal>();
		Iterator<AgentID> it = conv.PartParticipations.keySet().iterator();//it must have the right value
		AgentID currentag=null;
		String participations = "["; String separator = "";
		while (it.hasNext())
			{
				currentag = it.next();
				//adding the combination agent-participtions in the way [agName,agParticipations]
				participations = participations+separator+ "["+"\""+currentag.name+"\""+","+ conv.PartParticipations.get(currentag)+"]";
				separator = ",";
			}
		participations = participations+"]";
		String winner = "";
		if (conv.winner!=null) {winner = "\""+conv.winner.name+"\"";}
		else {winner = "Winner";}
		
		String percept = "conversationended("+participations+","+winner+","+conv.NextBid+","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal((ConvCProcessor) myProcessor, messageToSend);
		}
	}
	
	/**
	 * Creates a new contract net initiator factory
	 * @param name of the factory
	 * @param filter message filter
	 * @param template first message to send
	 * @param availableConversations maximum number of conversation this CFactory can manage simultaneously
	 * @param myAgent agent owner of this Cfactory
	 * @param participants number of participants
	 * @param answ_timeout timeout for waiting for answers
	 * @param join_timeout timeout for waiting for participants to join
	 * @return the a new contract net initiator CFactory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter,
			ACLMessage template, int availableConversations, ConvJasonAgent myAgent,
			int participants, int answ_timeout, int join_timeout) {

		// Create factory

		if (filter == null) {
			filter = new MessageFilter("protocol = japanese-auction");
		}
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());

		
		WaitState WAIT_FOR_PARTICIPANT_TO_JOIN = new WaitState("WAIT_FOR_PARTICIPANT_TO_JOIN", join_timeout);
		processor.registerState(WAIT_FOR_PARTICIPANT_TO_JOIN);
		processor.addTransition(BEGIN, WAIT_FOR_PARTICIPANT_TO_JOIN);
		
		// RECEIVE_CANCEL_WAIT State
		//Header: purpose Value: waitMessage
		ReceiveState RECEIVE_CANCEL_WAIT = new ReceiveState("RECEIVE_CANCEL_WAIT");
		RECEIVE_CANCEL_WAIT.setMethod(new RECEIVE_CANCEL_WAIT_Method());
		filter = new MessageFilter("purpose = waitMessage");  
		RECEIVE_CANCEL_WAIT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_CANCEL_WAIT);
		processor.addTransition(WAIT_FOR_PARTICIPANT_TO_JOIN,
				RECEIVE_CANCEL_WAIT);
		
		// BID_CALL State

		SendState BID_CALL = new SendState("BID_CALL");

		BID_CALL.setMethod(new BID_CALL_Method());
		template.setProtocol("japanese-auction");
		template.setPerformative(ACLMessage.PROPOSE);
		BID_CALL.setMessageTemplate(template);
		processor.registerState(BID_CALL);
		processor.addTransition(RECEIVE_CANCEL_WAIT, BID_CALL);

		// WAIT_FOR_ACCEPTANCES State
		WaitState WAIT_FOR_ACCEPTANCES = new WaitState("WAIT_FOR_ACCEPTANCES", answ_timeout);
		//WAIT_FOR_ACCEPTANCES.setWaitType(WaitState.ABSOLUT);
		processor.registerState(WAIT_FOR_ACCEPTANCES);
		processor.addTransition(BID_CALL, WAIT_FOR_ACCEPTANCES);


		// RECEIVE_ACCEPTANCE State

		ReceiveState RECEIVE_ACCEPTANCE = new ReceiveState("RECEIVE_ACCEPTANCE");
		RECEIVE_ACCEPTANCE.setMethod(new RECEIVE_ACCEPTANCE_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.ACCEPT_PROPOSAL));
		RECEIVE_ACCEPTANCE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_ACCEPTANCE);
		processor.addTransition(WAIT_FOR_ACCEPTANCES, RECEIVE_ACCEPTANCE);
		processor.addTransition(RECEIVE_ACCEPTANCE, WAIT_FOR_ACCEPTANCES);
		processor.addTransition(RECEIVE_ACCEPTANCE, BID_CALL);

		// TIMEOUT State

		ReceiveState TIMEOUT = new ReceiveState("TIMEOUT");
		TIMEOUT.setMethod(new TIMEOUT_Method());
		filter = new MessageFilter("performative = "+ACLMessage.getPerformative(ACLMessage.INFORM)+" AND purpose = waitMessage");
		TIMEOUT.setAcceptFilter(filter);
		processor.registerState(TIMEOUT);
		processor.addTransition(WAIT_FOR_ACCEPTANCES, TIMEOUT);
		processor.addTransition(TIMEOUT, BID_CALL);

		
		// SEND_WINNER State

		SendState SEND_WINNER = new SendState("SEND_WINNER");

		SEND_WINNER.setMethod(new SEND_WINNER_Method());
		template.setProtocol("japanese-auction");
		template.setPerformative(ACLMessage.INFORM);
		SEND_WINNER.setMessageTemplate(template);
		processor.registerState(SEND_WINNER);
		processor.addTransition(TIMEOUT, SEND_WINNER);
		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(TIMEOUT, FINAL);
		processor.addTransition(SEND_WINNER, FINAL);	
		processor.addTransition(RECEIVE_ACCEPTANCE, FINAL);

		return theFactory;
	}
	
	
}
