package es.upv.dsic.gti_ia.jason.conversationsFactory.initiator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
import jason.asSyntax.LiteralImpl;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FSConversation;

/**
 * This class represents a template for a Fipa Subscribe Protocol from the initiator 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_Fipa_Subscribe_Initiator {
	
	protected TransitionSystem Ts;

	public Jason_Fipa_Subscribe_Initiator(String sagName,
			TransitionSystem ts) {
		Ts = ts;
	}

	/**
	 * Method to execute at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend first message to send
	 */
	protected void doBegin(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FSConversation conv =  (FSConversation) myProcessor.getConversation();
		messageToSend.setContent(conv.initialMessage);
		myProcessor.getInternalData().put("InitialMessage", messageToSend);

	}
	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin((ConvCProcessor)myProcessor, msg);
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
			return "SUBSCRIBE";
		}
	}
	
	/**
	 * Method to execute when the initiator receives a refuse message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend Message to send. The "Conversation" object belonging
	 * to the processor must have the list or objects in wich the initiator is interested.
	 */

	protected void doSubscribe(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {

		FSConversation conv = (FSConversation) myProcessor.getConversation();

		conv.aquire_semaphore();
		//Precond: conv.objects c/ valor, conv.Participant c/ valor, 

		Iterator<Entry<String, String>> objIt = conv.objects.entrySet().iterator();
		while (objIt.hasNext()) {
			  Entry<String, String> obj =  objIt.next();
			  String key = obj.getKey();
			  String value = "";
			  messageToSend.getHeaders().put(key, value);
			}
		
		messageToSend.setContent("I want to susbcribe to some objects.");
		messageToSend.setProtocol("fipa-subscribe");
		messageToSend.setPerformative(ACLMessage.getPerformative(ACLMessage.SUBSCRIBE));
		messageToSend.setReceiver(conv.Participant); 
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setHeader("jasonID", conv.jasonConvID);
		messageToSend.setHeader("factoryname", conv.factoryName);
		//Postcond: messageTosend must have as many headers as objects to subscribe
	}


	class SUBSCRIBE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doSubscribe((ConvCProcessor)myProcessor, messageToSend);
			return "WAIT_FOR_ACCEPTANCE";
		}
	}
	
	
	/**
	 * Method to execute when the initiator receives a refuse message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg failure message
	 */
	protected void doRefuse(ConvCProcessor myProcessor, ACLMessage msg){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		conv.firstResult =ACLMessage.getPerformative(ACLMessage.REFUSE);
		conv.finalResult=ACLMessage.getPerformative(ACLMessage.REFUSE);
	}

	class REFUSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doRefuse((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL_SUBSCRIBE_INITIATOR";
		}
	}
	
	/**
	 * Method to execute when the initiator receives an agree message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg agree message
	 */
	protected void doAgree(ConvCProcessor myProcessor, ACLMessage msg){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		conv.firstResult=ACLMessage.getPerformative(ACLMessage.AGREE);
		 //To add a perception
		 List<Literal> allperc = new ArrayList<Literal>();
		 String percept = "subscribeagree("+conv.Participant.name+","+conv.jasonConvID+")[source(self)]";
		 allperc.add(Literal.parseLiteral(percept));
		 ((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	}

	class AGREE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doAgree((ConvCProcessor) myProcessor, messageReceived);
			return "WAIT_FOR_INFORMATION";
		}
	}

	/**
	 * Method to execute when the initiator receives a failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg failure message
	 */
	protected void doFailure(ConvCProcessor myProcessor, ACLMessage msg){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		conv.finalResult=ACLMessage.getPerformative(ACLMessage.FAILURE);
	}

	class FAILURE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doFailure((ConvCProcessor) myProcessor, messageReceived);
			return "FINAL_SUBSCRIBE_INITIATOR";
		}
	}

	/**
	 * Method to execute when the initiator receives an inform message
	 * @param myProcessor
	 * @param msg Message whose header "evaluationresult" has the information
	 * related to the object "object" in the header too. 
	 */
	protected String doReceiveInform(ConvCProcessor myProcessor, ACLMessage msg) {

		FSConversation conv = (FSConversation) myProcessor.getConversation();
		List<Literal> allperc = new ArrayList<Literal>();

		//Precond: msg must have a header with the object as key and the change as value
		
		Literal headerkeyLit ;
		Literal objkey ;
		Iterator<Entry<String, String>> headIt = msg.getHeaders().entrySet().iterator();
		
		while (headIt.hasNext()){
			Entry<String, String> headerkey =  headIt.next();
			headerkeyLit = new LiteralImpl(Literal.parseLiteral(headerkey.getKey()));
			
			String percept;
			Iterator<Entry<String, String>> objIt = conv.objects.entrySet().iterator();
			while (objIt.hasNext()) {
				Entry<String, String> obj = objIt.next();
				objkey = new LiteralImpl(Literal.parseLiteral(obj.getKey()));
				if (objkey.compareTo(headerkeyLit)==0){
					obj.setValue(headerkey.getValue());
					percept = "inform("+msg.getSender().name+","+headerkey.getKey() +","+msg.getHeaderValue(headerkey.getKey() )+","+conv.jasonConvID+")[source(self)]";
					allperc.add(Literal.parseLiteral(percept));
				}
			}
			((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		}
		return  "WAIT_FOR_INFORMATION";
	}

	class INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doReceiveInform((ConvCProcessor)myProcessor, messageReceived);
		}
	}
	
	protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		//To add a perception
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationended("+conv.jasonConvID+","+'"'+ conv.finalResult.toLowerCase()+'"' +")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		messageToSend = myProcessor.getLastSentMessage();
		messageToSend.setProtocol("fipa-subscribe");
		messageToSend.setPerformative(ACLMessage.SUBSCRIBE);
		myProcessor.getMyAgent().removeFactory(conv.factoryName);
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal((ConvCProcessor)myProcessor, messageToSend);
		}
	}	
	
	/**
	 * Method to execute when the initiator cancels the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param msg cancel message
	 */
	protected void doCancel(ConvCProcessor myProcessor, ACLMessage msg){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		msg.setReceiver(conv.Participant);
		msg.setContent("I'd like to cancel the conversation.");
		msg.setProtocol("fipa-subscribe");
		msg.setPerformative(ACLMessage.getPerformative(ACLMessage.CANCEL));
		msg.setSender(myProcessor.getMyAgent().getAid());
		conv.conversationCanceled = true;
	}

	class CANCEL_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doCancel((ConvCProcessor) myProcessor, messageReceived);
			return "WAIT_FOR_CANCEL_NOTIFICATION";
		}
	}
	
	
	/**
	 * Method to execute when the initiator receives an inform message related to the cancel request
	 * @param myProcessor
	 * @param msg Message received. 
	 */
	protected void doInformCancel(ConvCProcessor myProcessor, ACLMessage msg) {
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		conv.finalResult=ACLMessage.getPerformative(ACLMessage.INFORM)+"_CANCEL";
	}

	class INFORM_CANCEL_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doInformCancel((ConvCProcessor)myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	/**
	 * Method to execute when the initiator receives a failure message related to the cancel request
	 * @param myProcessor
	 * @param msg Message received. 
	 */
	protected void doFailureCancel(ConvCProcessor myProcessor, ACLMessage msg) {
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		conv.finalResult=ACLMessage.getPerformative(ACLMessage.INFORM)+"_FAILURE";
	}

	class FAILURE_CANCEL_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doFailureCancel((ConvCProcessor)myProcessor, messageReceived);
			return "FINAL";
		}
	}

	public ConvCFactory newFactory(String name, MessageFilter filter,
			int availableConversations, ConvJasonAgent myAgent, int timeOut){


		//MessageFilter filter;
		ACLMessage template = new ACLMessage();

		// Create factory
		if (filter == null) {
			filter = new MessageFilter("protocol = fipa-subscribe");
		}

		ConvCFactory theFactory = new ConvCFactory(name, filter,
				availableConversations, myAgent);

		// Processor template setup
		ConvCProcessor processor = theFactory.cProcessorTemplate();

		//BEGIN 
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
		
		//SUBSCRIBE
		SendState SUBSCRIBE = new SendState("SUBSCRIBE");
		SUBSCRIBE.setMethod(new SUBSCRIBE_Method());
		template.setProtocol("fipa-subscribe");
		template.setPerformative(ACLMessage.SUBSCRIBE);
		SUBSCRIBE.setMessageTemplate(template);
		processor.registerState(SUBSCRIBE);
		processor.addTransition("RECEIVE_CANCEL_WAIT", "SUBSCRIBE");

		//WAIT
		processor.registerState(new WaitState("WAIT_FOR_ACCEPTANCE", timeOut));
		processor.addTransition("SUBSCRIBE","WAIT_FOR_ACCEPTANCE");

		//RECEIVE_REFUSE
		ReceiveState RECEIVE_REFUSE = new ReceiveState("RECEIVE_REFUSE");
		RECEIVE_REFUSE.setMethod(new REFUSE_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+
				ACLMessage.getPerformative(ACLMessage.REFUSE)+
				" AND in-reply-to = "+ACLMessage.getPerformative(ACLMessage.SUBSCRIBE));
		RECEIVE_REFUSE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REFUSE);
		processor.addTransition("WAIT_FOR_ACCEPTANCE","RECEIVE_REFUSE");

		
		//RECEIVE_AGREE
		ReceiveState RECEIVE_AGREE = new ReceiveState("RECEIVE_AGREE");
		RECEIVE_AGREE.setMethod(new AGREE_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+
				ACLMessage.getPerformative(ACLMessage.AGREE)+
				" AND in-reply-to = "+ACLMessage.getPerformative(ACLMessage.SUBSCRIBE));
		RECEIVE_AGREE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_AGREE);
		processor.addTransition("WAIT_FOR_ACCEPTANCE","RECEIVE_AGREE");
		
		//WAIT
		processor.registerState(new WaitState("WAIT_FOR_INFORMATION", 0));
		processor.addTransition("RECEIVE_AGREE","WAIT_FOR_INFORMATION");
		
		
		//RECEIVE_INFORM
		ReceiveState RECEIVE_INFORM = new ReceiveState("RECEIVE_INFORM");
		RECEIVE_INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+
				ACLMessage.getPerformative(ACLMessage.INFORM));
		RECEIVE_INFORM.setAcceptFilter(filter);
		processor.registerState(RECEIVE_INFORM);
		processor.addTransition("WAIT_FOR_INFORMATION","RECEIVE_INFORM");
		processor.addTransition("RECEIVE_INFORM","WAIT_FOR_INFORMATION");

		
		//RECEIVE_FAILURE
		ReceiveState RECEIVE_FAILURE = new ReceiveState("RECEIVE_FAILURE");
		RECEIVE_FAILURE.setMethod(new FAILURE_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+
				ACLMessage.getPerformative(ACLMessage.FAILURE));
		RECEIVE_FAILURE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE);
		processor.addTransition("WAIT_FOR_INFORMATION","RECEIVE_FAILURE");
		
		//CANCEL
		SendState CANCEL = new SendState("CANCEL_STATE");
		CANCEL.setMethod(new CANCEL_Method());
		CANCEL.setMessageTemplate(template);
		processor.registerState(CANCEL);
		processor.addTransition("WAIT_FOR_INFORMATION", "CANCEL_STATE");
		
		//WAIT
		processor.registerState(new WaitState("WAIT_FOR_CANCEL_NOTIFICATION", 0));
		processor.addTransition("CANCEL_STATE","WAIT_FOR_CANCEL_NOTIFICATION");
		
		
		//RECEIVE_INFORM_CANCEL
		ReceiveState RECEIVE_INFORM_CANCEL = new ReceiveState("RECEIVE_INFORM_CANCEL");
		RECEIVE_INFORM_CANCEL.setMethod(new INFORM_CANCEL_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+
				ACLMessage.getPerformative(ACLMessage.INFORM)+
				" AND in-reply-to = "+ACLMessage.getPerformative(ACLMessage.CANCEL));
		RECEIVE_INFORM_CANCEL.setAcceptFilter(filter);
		processor.registerState(RECEIVE_INFORM_CANCEL);
		processor.addTransition("WAIT_FOR_CANCEL_NOTIFICATION","RECEIVE_INFORM_CANCEL");

		
		//RECEIVE_FAILURE_CANCEL
		ReceiveState RECEIVE_FAILURE_CANCEL = new ReceiveState("RECEIVE_FAILURE_CANCEL");
		RECEIVE_FAILURE_CANCEL.setMethod(new FAILURE_CANCEL_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+
				ACLMessage.getPerformative(ACLMessage.FAILURE)+
				" AND in-reply-to = "+ACLMessage.getPerformative(ACLMessage.CANCEL));
		RECEIVE_FAILURE_CANCEL.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FAILURE_CANCEL);
		processor.addTransition("WAIT_FOR_CANCEL_NOTIFICATION","RECEIVE_FAILURE_CANCEL");
		
		// FINAL
		FinalState FINAL = new FinalState("FINAL");
		FINAL.setMethod(new FINAL_Method());
		
		processor.registerState(FINAL);
		processor.addTransition(RECEIVE_REFUSE, FINAL);
		processor.addTransition(RECEIVE_FAILURE, FINAL);
		processor.addTransition(RECEIVE_INFORM_CANCEL, FINAL);
		processor.addTransition(RECEIVE_FAILURE_CANCEL, FINAL);
		
		return theFactory;
	}
	
}
