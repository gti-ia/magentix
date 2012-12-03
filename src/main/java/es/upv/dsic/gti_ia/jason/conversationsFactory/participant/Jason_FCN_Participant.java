package es.upv.dsic.gti_ia.jason.conversationsFactory.participant;

import java.util.ArrayList;
import java.util.List;
import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

import es.upv.dsic.gti_ia.cAgents.ActionState;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
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


import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FCNConversation;

/**
 * This class represents a template for a Fipa Contract Net Protocol from the participant 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_FCN_Participant {

	protected TransitionSystem Ts; 
	
	public Jason_FCN_Participant(String agName2, TransitionSystem ts2) {

		Ts = ts2;
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
			return "WAIT_FOR_SOLICIT";
		};
	}

	/**
	 * Method executed when the participant receives a call for proposals
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg call for proposals message
	 * @return next state of this conversation
	 */
	protected String doReceiveSolicit(ConvCProcessor myProcessor, ACLMessage msg){
		// accept all the solicits
		String jasonConvID = msg.getHeaderValue("jasonID");
		
		Conversation conv = myProcessor.getConversation();
		//At this point the conversation associated to the CProcessor has no value
		//for the internal conversation ID so it is updated
		conv.jasonConvID = jasonConvID;
		conv.initiator = msg.getSender();
		
		FCNConversation newConv = new FCNConversation(conv.jasonConvID, conv.internalConvID, "", conv.initiator);
		((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);
		
		String result = "";
		AgentID Sender = myProcessor.getLastReceivedMessage().getSender();
		
		
		String newMsgContent = "callforproposal"+"("+Sender.name+","+msg.getContent()+","+conv.jasonConvID+")[source(self)]";
		
		List<Literal> allpercep = new ArrayList<Literal>();
		
		allpercep.add(Literal.parseLiteral(newMsgContent));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allpercep);
		
		newConv.aquire_semaphore();
		
		if (newConv.kindOfAnswer =="propose"){
			result = "SEND_PROPOSAL";
		}else if (newConv.kindOfAnswer=="refuse"){
			result = "SEND_REFUSE";
		}else if (newConv.kindOfAnswer=="notUnderstood"){
			result = "SEND_NOT_UNDERSTOOD";
		}
		return result;
	}

	class RECEIVE_SOLICIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			myProcessor.getInternalData().put("solicitMessage", messageReceived);
			return doReceiveSolicit((ConvCProcessor) myProcessor, messageReceived);
		}
	}

	/**
	 * Method executed when the participant sends a proposal
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend proposal message
	 */
	protected void doSendProposal(ConvCProcessor myProcessor,
			ACLMessage messageToSend){

		FCNConversation conv = (FCNConversation)myProcessor.getConversation();
		
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
		messageToSend.setContent(conv.proposal);
		messageToSend.setPerformative(ACLMessage.PROPOSE);
		messageToSend.setProtocol("fipa-contract-net");
	}

	class SEND_PROPOSAL_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendProposal((ConvCProcessor) myProcessor, messageToSend);
			return "WAIT_FOR_ACCEPT";
		}
	}
	

	/**
	 * Method executed when the participant sends a refuse
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend refuse message
	 */
	protected void doSendRefuse(CProcessor myProcessor,
			ACLMessage messageToSend){
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
		"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.REFUSE);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_REFUSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendRefuse(myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the participant sends a not-understood
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend not-understood message
	 */
	protected void doSendNotUnderstood(CProcessor myProcessor,
			ACLMessage messageToSend){
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
		"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}
	class SEND_NOT_UNDERSTOOD_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendNotUnderstood(myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the initiator accepts participant's proposal
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg accept message
	 */
	protected void doReceiveAccept(CProcessor myProcessor, ACLMessage msg){
	}

	class RECEIVE_ACCEPT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveAccept(myProcessor, messageReceived);
			return "DO_TASK";
		}
	}
	
	/**
	 * Method executed when the initiator rejects participant's proposal
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg reject message
	 */
	protected void doReceiveReject(CProcessor myProcessor, ACLMessage msg){
	}

	class RECEIVE_REJECT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doReceiveReject(myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	/**
	 * Perform the proposal's task
	 * @param myProcessor the CProcessor managing the conversation
	 * @param solicitMessage the first message assigned to this conversation containing the solicit of the initiator agent
	 * @return next conversation state
	 */
	protected String doTask(ConvCProcessor myProcessor, ACLMessage solicitMessage){
		String result = ""; 
		
		FCNConversation conv = (FCNConversation)myProcessor.getConversation();
		
		List<Literal> allpercep = new ArrayList<Literal>();
		allpercep.add(Literal.parseLiteral("timetodotask("+conv.initiator.name+","+conv.jasonConvID+")[source(self)]"));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allpercep);

		conv.aquire_semaphore();
		
		if (conv.taskDone){ 
			result = "SEND_INFORM";}
		else {
			result = "SEND_FAILURE";}
		return result;
	}

	class DO_TASK_Method implements ActionStateMethod {
		public String run(CProcessor myProcessor) {
			ACLMessage solicitMessage = (ACLMessage)myProcessor.getInternalData().get("solicitMessage");
			return doTask((ConvCProcessor) myProcessor, solicitMessage);
		}
	}
	
	/**
	 * Method executed when the task failed
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend fail message
	 */
	protected void doSendFailure(CProcessor myProcessor,
			ACLMessage messageToSend){
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
		"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-contract-net");
		messageToSend.setPerformative(ACLMessage.FAILURE);
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class SEND_FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendFailure(myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the task succeeded
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend inform message
	 */
	protected void doSendInfo(ConvCProcessor myProcessor,
			ACLMessage messageToSend){
		FCNConversation conv = (FCNConversation)myProcessor.getConversation();
		
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
		messageToSend.setContent("I'm "+myProcessor.getMyAgent().getAid().name+": "+conv.infoToSend);
		messageToSend.setPerformative(ACLMessage.INFORM);
		messageToSend.setProtocol("fipa-contract-net");	
	}

	class SEND_INFO_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSendInfo((ConvCProcessor) myProcessor, messageToSend);
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
			filter = new MessageFilter("performative = CFP"); // falta AND
																	// protocol
																	// =
																	// fipa-contract-net;
		}
		
		if (template == null){
			template = new ACLMessage(ACLMessage.PROPOSE);
		}
		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup

		ConvCProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());
		
		// WAIT_FOR_SOLICIT State
		WaitState WAIT_FOR_SOLICIT = new WaitState("WAIT_FOR_SOLICIT", timeout);
		processor.registerState(WAIT_FOR_SOLICIT);
		processor.addTransition(BEGIN, WAIT_FOR_SOLICIT);
		
		// RECEIVE_SOLICIT State

		ReceiveState RECEIVE_SOLICIT = new ReceiveState(
				"RECEIVE_SOLICIT");
		RECEIVE_SOLICIT.setMethod(new RECEIVE_SOLICIT_Method());
		filter = new MessageFilter("performative = CFP");
		RECEIVE_SOLICIT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_SOLICIT);
		processor.addTransition(WAIT_FOR_SOLICIT,
				RECEIVE_SOLICIT);

		// SEND_PROPOSAL State

		SendState SEND_PROPOSAL = new SendState("SEND_PROPOSAL");

		SEND_PROPOSAL.setMethod(new SEND_PROPOSAL_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.PROPOSE);
		SEND_PROPOSAL.setMessageTemplate(template);
		processor.registerState(SEND_PROPOSAL);
		processor.addTransition(RECEIVE_SOLICIT, SEND_PROPOSAL);
		
		// SEND_REFUSE State

		SendState SEND_REFUSE = new SendState("SEND_REFUSE");

		SEND_REFUSE.setMethod(new SEND_REFUSE_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.REFUSE);
		SEND_REFUSE.setMessageTemplate(template);
		processor.registerState(SEND_REFUSE);
		processor.addTransition(RECEIVE_SOLICIT, SEND_REFUSE);
		
		// SEND_NOT_UNDERSTOOD State

		SendState SEND_NOT_UNDERSTOOD = new SendState("SEND_NOT_UNDERSTOOD");

		SEND_NOT_UNDERSTOOD.setMethod(new SEND_NOT_UNDERSTOOD_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		SEND_NOT_UNDERSTOOD.setMessageTemplate(template);
		processor.registerState(SEND_NOT_UNDERSTOOD);
		processor.addTransition(RECEIVE_SOLICIT, SEND_NOT_UNDERSTOOD);

		// WAIT_FOR_ACCEPT State
		WaitState WAIT_FOR_ACCEPT = new WaitState("WAIT_FOR_ACCEPT", timeout);
		processor.registerState(WAIT_FOR_ACCEPT);
		processor.addTransition(SEND_PROPOSAL, WAIT_FOR_ACCEPT);

		// RECEIVE_ACCEPT State

		ReceiveState RECEIVE_ACCEPT = new ReceiveState(
				"RECEIVE_ACCEPT");
		RECEIVE_ACCEPT.setMethod(new RECEIVE_ACCEPT_Method());
		filter = new MessageFilter("performative = ACCEPT-PROPOSAL");
		RECEIVE_ACCEPT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_ACCEPT);
		processor.addTransition(WAIT_FOR_ACCEPT,
				RECEIVE_ACCEPT);

		// RECEIVE_REJECT State

		ReceiveState RECEIVE_REJECT = new ReceiveState("RECEIVE_REJECT");
		RECEIVE_REJECT.setMethod(new RECEIVE_REJECT_Method());
		filter = new MessageFilter("performative = REJECT-PROPOSAL");
		RECEIVE_REJECT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REJECT);
		processor.addTransition(WAIT_FOR_ACCEPT,
				RECEIVE_REJECT);

		// DO_TASK State

		ActionState DO_TASK = new ActionState("DO_TASK");
		DO_TASK.setMethod(new DO_TASK_Method());
		processor.registerState(DO_TASK);
		processor.addTransition(RECEIVE_ACCEPT, DO_TASK);
		
		// SEND_INFO State

		SendState SEND_INFO = new SendState("SEND_INFORM");

		SEND_INFO.setMethod(new SEND_INFO_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.INFORM);
		SEND_INFO.setMessageTemplate(template);
		processor.registerState(SEND_INFO);
		processor.addTransition(DO_TASK, SEND_INFO);
		
		// SEND_INFO State

		SendState SEND_FAILURE = new SendState("SEND_FAILURE");

		SEND_FAILURE.setMethod(new SEND_FAILURE_Method());
		template.setProtocol("fipa-contract-net");
		template.setPerformative(ACLMessage.FAILURE);
		SEND_FAILURE.setMessageTemplate(template);
		processor.registerState(SEND_FAILURE);
		processor.addTransition(DO_TASK, SEND_FAILURE);		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(SEND_FAILURE, FINAL);			
		processor.addTransition(SEND_INFO, FINAL);
		processor.addTransition(RECEIVE_REJECT, FINAL);
		processor.addTransition(SEND_NOT_UNDERSTOOD, FINAL);
		processor.addTransition(SEND_REFUSE, FINAL);
		return theFactory;
	} 
	
	
}