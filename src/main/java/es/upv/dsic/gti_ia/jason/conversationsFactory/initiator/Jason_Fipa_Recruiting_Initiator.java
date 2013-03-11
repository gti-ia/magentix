package es.upv.dsic.gti_ia.jason.conversationsFactory.initiator;

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
import es.upv.dsic.gti_ia.core.MessageFilter;

import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FRCConversation;

/**
 * This class represents a template for a Fipa Recruiting Protocol from the initiator 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_Fipa_Recruiting_Initiator {

	protected TransitionSystem Ts; 
	
	public Jason_Fipa_Recruiting_Initiator(	TransitionSystem ts) {
		Ts = ts;
	}	
	
	/**
	 * Method executed at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg first message to send
	 */
	protected void doBegin(ConvCProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin((ConvCProcessor) myProcessor, msg);
			return "WAIT_FOR_PARTICIPANT_TO_JOIN";
		};
	}
	
	/**
	 * Method executed when the timeout for the participants to join finishes
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageReceived Message to send
	 */
	private void doReceiveCancelWait(ConvCProcessor myProcessor,
			ACLMessage messageReceived) {
		
	}
	class RECEIVE_CANCEL_WAIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveCancelWait((ConvCProcessor)myProcessor, messageReceived);
			return "SEND_PROXY";
		}
	}
	
	
	/**
	 * Set the proxy message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend proxy message
	 */
	protected void setProxyMessage(ConvCProcessor myProcessor,
			ACLMessage messageToSend){
		FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
		//poner lo siguiente en el header
		messageToSend.setContent(conv.Condition.toString().trim()+","+conv.participantsNumber+","+conv.timeOut+","+conv.jasonConvID);
		messageToSend.setPerformative(ACLMessage.INFORM);
		messageToSend.setReceiver(conv.participant);
		messageToSend.setHeader("factoryname", conv.factoryName);
	}
	
	class SEND_PROXY_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			messageToSend.setProtocol("fipa-recruiting");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			setProxyMessage((ConvCProcessor) myProcessor, messageToSend);
			return "WAIT_FOR_PROXY_ACCEPTANCE";
		}
	}
	
	/**
	 * Method executed when the initiator receives an agree message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg agree message
	 */
	protected void doReceiveAgree(CProcessor myProcessor, ACLMessage msg) {
	}

	class RECEIVE_AGREE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveAgree(myProcessor, messageReceived);
			return "WAIT_FOR_PROXY_RESULT";			
		}
	}
	
	/**
	 * Method executed when the initiator receives a refuse message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg refuse message
	 */
	protected void doReceiveRefuse(ConvCProcessor myProcessor, ACLMessage msg) {
		FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
		conv.conversationResult = "REFUSE";
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationresult(\""+conv.conversationResult+"\","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	}

	class RECEIVE_REFUSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveRefuse((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}
	
	/**
	 * Method executed when the initiator receives a no match message
	 * @param mmyProcessor the CProcessor managing the conversation
	 * @param msg no match message
	 */
	protected void doReceiveFailureNoMatch(ConvCProcessor myProcessor, ACLMessage msg) {
		FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
		conv.conversationResult = "NO AGENT MATCH FOUND!";
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationresult(\""+conv.conversationResult+"\","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		Ts.getAg().getLogger().info("Receive failure not match... "+msg.getSender().getLocalName());
	}

	class RECEIVE_FAILURE_NO_MATCH_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveFailureNoMatch((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}


	
	/**
	 * Method executed when the initiator receives a proxy failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg proxy failure message
	 */
	protected void doReceiveFailureProxy(ConvCProcessor myProcessor, ACLMessage msg) {
		FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
		conv.conversationResult = "PROXY ACTION FAILED!";
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationresult(\""+conv.conversationResult+"\","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		Ts.getAg().getLogger().info("Receive failure proxy... "+msg.getSender().getLocalName());
	}

	class RECEIVE_FAILURE_PROXY_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveFailureProxy((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}

	
	/**
	 * Method executed when the initiator receives an inform message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg inform message
	 */
	protected void doReceiveInform(ConvCProcessor myProcessor, ACLMessage msg) {
		FRCConversation conv =  (FRCConversation) myProcessor.getConversation();
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "receiveinform("+conv.participant.name+","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

		conv.aquire_semaphore();
		
		conv.conversationResult = "PROXY WORKED!";
		allperc = new ArrayList<Literal>();
		percept = "conversationresult(\""+conv.conversationResult+"\","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	}

	class RECEIVE_INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveInform((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL_RECRUITING_INITIATOR";			
		}
	}
	
	
	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinalRecruitingInitiator(ConvCProcessor myProcessor, ACLMessage messageToSend) {
		FRCConversation conv =  (FRCConversation)myProcessor.getConversation();
		messageToSend = myProcessor.getLastSentMessage();
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationended("+conv.participant.name+","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		myProcessor.getMyAgent().removeFactory(conv.factoryName);
	}

	class FINAL_RECRUITING_INITIATOR_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinalRecruitingInitiator((ConvCProcessor) myProcessor, messageToSend);
		}
	}
	

	/**
	 * Creates a new initiator fipa recruiting cfactory
	 * @param name factory's name
	 * @param filter message filter
	 * @param template first message to send
	 * @param availableConversations maximum conversation that can be managed by this CFactory
	 * @param myAgent agent owner of this CFactory
	 * @param timeout for waiting after sending the proxy message
	 * @return a new fipa recruiting initiator factory
	 */
	public ConvCFactory newFactory(String name, MessageFilter filter, ACLMessage template,
			int availableConversations, ConvJasonAgent myAgent, long timeout) {
		
		// Create factory

		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();
		
		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_PARTICIPANT_TO_JOIN state
		WaitState WAIT_FOR_PARTICIPANT_TO_JOIN = new WaitState("WAIT_FOR_PARTICIPANT_TO_JOIN", 500);
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
		
		
		// SEND_PROXY State

		SendState SEND_PROXY = new SendState("SEND_PROXY");

		SEND_PROXY.setMethod(new SEND_PROXY_Method());
		template = new ACLMessage(ACLMessage.UNKNOWN);
		SEND_PROXY.setMessageTemplate(template);
		processor.registerState(SEND_PROXY);
		processor.addTransition(RECEIVE_CANCEL_WAIT, SEND_PROXY);
		
		// WAIT_FOR_PROXY_ACCEPTANCE State

		WaitState WAIT_FOR_PROXY_ACCEPTANCE = new WaitState("WAIT_FOR_PROXY_ACCEPTANCE", timeout);
		processor.registerState(WAIT_FOR_PROXY_ACCEPTANCE);
		processor.addTransition(SEND_PROXY, WAIT_FOR_PROXY_ACCEPTANCE);
		
		// RECEIVE_REFUSE State
		
		ReceiveState RECEIVE_REFUSE = new ReceiveState("RECEIVE_REFUSE");
		RECEIVE_REFUSE.setMethod(new RECEIVE_REFUSE_Method());
		filter = new MessageFilter("performative = REFUSE AND protocol = fipa-recruiting");
		RECEIVE_REFUSE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REFUSE);
		processor.addTransition(WAIT_FOR_PROXY_ACCEPTANCE, RECEIVE_REFUSE);
		
		// RECEIVE_AGREE State
		
		ReceiveState RECEIVE_AGREE = new ReceiveState("RECEIVE_AGREE");
		RECEIVE_AGREE.setMethod(new RECEIVE_AGREE_Method());
		filter = new MessageFilter("performative = AGREE AND protocol = fipa-recruiting");
		RECEIVE_AGREE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_AGREE);
		processor.addTransition(WAIT_FOR_PROXY_ACCEPTANCE, RECEIVE_AGREE);
		
		// WAIT_FOR_PROXY_RESULT State

		WaitState WAIT_FOR_PROXY_RESULT = new WaitState("WAIT_FOR_PROXY_RESULT", timeout);
		processor.registerState(WAIT_FOR_PROXY_RESULT);
		processor.addTransition(RECEIVE_AGREE, WAIT_FOR_PROXY_RESULT);
		
		// RECEIVE_FAILURE_PROXY State
		
		ReceiveState RECEIVE_FAILURE_PROXY = new ReceiveState("RECEIVE_FAILURE_PROXY");
		RECEIVE_FAILURE_PROXY.setMethod(new RECEIVE_FAILURE_PROXY_Method());
		filter = new MessageFilter("performative = FAILURE AND protocol = fipa-recruiting AND reason = proxy");
		RECEIVE_FAILURE_PROXY.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE_PROXY);
		processor.addTransition(WAIT_FOR_PROXY_RESULT, RECEIVE_FAILURE_PROXY);
		
		// RECEIVE_FAILURE_NO_MATCH State
		
		ReceiveState RECEIVE_FAILURE_NO_MATCH = new ReceiveState("RECEIVE_FAILURE_NO_MATCH");
		RECEIVE_FAILURE_NO_MATCH.setMethod(new RECEIVE_FAILURE_NO_MATCH_Method());
		filter = new MessageFilter("performative = FAILURE AND protocol = fipa-recruiting AND reason = no-match");
		RECEIVE_FAILURE_NO_MATCH.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE_NO_MATCH);
		processor.addTransition(WAIT_FOR_PROXY_RESULT, RECEIVE_FAILURE_NO_MATCH);
		
		// RECEIVE_INFORM State
		
		ReceiveState RECEIVE_INFORM = new ReceiveState("RECEIVE_INFORM");
		RECEIVE_INFORM.setMethod(new RECEIVE_INFORM_Method());
		filter = new MessageFilter("performative = INFORM AND protocol = fipa-recruiting");
		RECEIVE_INFORM.setAcceptFilter(filter);
		processor.registerState(RECEIVE_INFORM);
		processor.addTransition(WAIT_FOR_PROXY_RESULT, RECEIVE_INFORM);
		
		// FINAL State

		FinalState FINAL_RECRUITING_INITIATOR = new FinalState("FINAL_RECRUITING_INITIATOR");

		FINAL_RECRUITING_INITIATOR.setMethod(new FINAL_RECRUITING_INITIATOR_Method());
		processor.registerState(FINAL_RECRUITING_INITIATOR);
		
		processor.addTransition(RECEIVE_REFUSE, FINAL_RECRUITING_INITIATOR);
		processor.addTransition(RECEIVE_FAILURE_PROXY, FINAL_RECRUITING_INITIATOR);
		processor.addTransition(RECEIVE_FAILURE_NO_MATCH, FINAL_RECRUITING_INITIATOR);
		processor.addTransition(RECEIVE_INFORM, FINAL_RECRUITING_INITIATOR);
		
		return theFactory;
	}

	 
}
