package es.upv.dsic.gti_ia.jason.conversationsFactory.participant;

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
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.FSConversation;

/**
 * This class represents a template for a Fipa Subscribe Protocol from the participant 
 * perspective for being used in the Conversations Factory from Jason agents.
 * @author Bexy Alfonso Espinosa
 */

public class Jason_Fipa_Subscribe_Participant {

	protected TransitionSystem Ts;

	public Jason_Fipa_Subscribe_Participant(String sagName,
			TransitionSystem ts) {
		Ts = ts;
	}


	/**
	 * Method executed at the beginning of the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend first message assigned to this conversation
	 */
	protected void doBegin(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		myProcessor.getInternalData().put("InitialMessage", messageToSend);

	}
	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin((ConvCProcessor)myProcessor, msg);
			return "WAIT_SUBSCRIBE_INITIATOR";
		};
	}


	/**
	 * Method executed when the initiator receives the subscribe request
	 * @param myProcessor the CProcessor managing the conversation
	 * @param subscribe subscribe message. It must have as many headers as objects 
	 * that the sender is interested in
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected String doReceiveSubscribe(ConvCProcessor myProcessor,
			ACLMessage subscribe){
		String jasonID = subscribe.getHeaderValue("jasonID");
		Conversation conv =  myProcessor.getConversation();
		String factName = conv.factoryName;
		conv.jasonConvID = jasonID;
		conv.initiator = subscribe.getSender();
		FSConversation newConv = new FSConversation(conv.jasonConvID,conv.internalConvID,myProcessor.getMyAgent().getAid(),
				"",conv.initiator,factName);
		((ConvCFactory)myProcessor.getMyFactory()).UpdateConv(newConv, myProcessor);

		//Precond: subscribe message must have as many headers as objects 
		//that the initiator is interested in
		newConv.objects = subscribe.getHeaders();
		newConv.objects.remove("factoryname");
		newConv.objects.remove("jasonID"); 
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "subscriberequest("+newConv.initiator.name+",[";
		Iterator objIt = newConv.objects.entrySet().iterator();
		while (objIt.hasNext()) {
			Entry<String, String> obj = (Entry<String, String>) objIt.next();
			String key = obj.getKey();
			percept = percept + key+",";
		}
		percept = percept.substring(0, percept.length()-1);
		percept = percept+"],"+newConv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);

		newConv.aquire_semaphore();

		//Precond: newConv.firstResult must have Refuse or agree values
		String result = null; 
		if (newConv.firstResult.compareTo(ACLMessage.getPerformative(ACLMessage.REFUSE))==0){
			result = "REFUSE";
		}else 
			if (newConv.firstResult.compareTo(ACLMessage.getPerformative(ACLMessage.AGREE))==0){
				result = "AGREE";
			}
		return result;
	}

	class RECEIVE_SUBSCRIBE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doReceiveSubscribe((ConvCProcessor)myProcessor, messageReceived);
		}
	}

	/**
	 * Sets the refuse message
	 * @param myProcessor
	 * @param messageToSend
	 */
	protected void doRefuse(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-subscribe");
		messageToSend.setPerformative(ACLMessage.REFUSE);
		messageToSend.setInReplyTo(ACLMessage.getPerformative(ACLMessage.SUBSCRIBE));
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);
	}

	class REFUSE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doRefuse((ConvCProcessor)myProcessor, messageToSend);
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
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-subscribe");
		messageToSend.setPerformative(ACLMessage.AGREE);
		messageToSend.setInReplyTo(ACLMessage.getPerformative(ACLMessage.SUBSCRIBE));
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);

		//Precond: not necessarily but there sould be at least an object in 
		//conv.objects with result values
		Iterator<Entry<String, String>> objIt = conv.objects.entrySet().iterator();
		List<Literal> allperc = new ArrayList<Literal>();
		String percept;
		while (objIt.hasNext()) {
			Entry<String, String> obj =  objIt.next();
			String key = obj.getKey();
			percept = "subscribe("+conv.initiator.name+","+key+","+conv.jasonConvID+")[source(self)]";
			allperc.add(Literal.parseLiteral(percept));
		}
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
	}

	class AGREE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doAgree((ConvCProcessor)myProcessor, messageToSend);
			return "WAIT_FOR_CANCEL";
		}
	}

	/**
	 * Sets the inform message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param response inform message. The values of conv.objects will be checked
	 * searching the first object with result values... 
	 */


	protected String doInform(ConvCProcessor myProcessor, ACLMessage messagereceived){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		ACLMessage informmsg = new ACLMessage();
		informmsg.setSender(myProcessor.getMyAgent().getAid());
		informmsg.setReceiver(conv.initiator);
		informmsg.setProtocol("fipa-subscribe");
		informmsg.setPerformative(ACLMessage.INFORM);
		informmsg.setContent("Informing changes.");
		informmsg.setConversationId(myProcessor.getConversationID());
		Literal headerkeyLit ;
		Literal objkey ;
		Iterator<Entry<String, String>> headIt = messagereceived.getHeaders().entrySet().iterator();
		while (headIt.hasNext()){
			Entry<String, String> headerkey =  headIt.next();
			headerkeyLit = new LiteralImpl(Literal.parseLiteral(headerkey.getKey()));

			//Precond: message must have a header with the object as key and the changes as value 
			Iterator<Entry<String, String>> objIt = conv.objects.entrySet().iterator();
			while (objIt.hasNext()) {
				Entry<String, String> obj =  objIt.next();
				objkey = new LiteralImpl(Literal.parseLiteral(obj.getKey()));
				if (objkey.compareTo(headerkeyLit)==0){
					obj.setValue(headerkey.getValue());
					informmsg.setHeader(headerkey.getKey(), headerkey.getValue());
				}
			}

		}
		myProcessor.getMyAgent().send(informmsg);


		return "WAIT_FOR_CANCEL";
	}

	class INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messagereceived) {
			return doInform((ConvCProcessor) myProcessor, messagereceived);
		}
	}


	/**
	 * Sets the failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend failure message
	 */
	protected void doFailure(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		messageToSend.setProtocol("fipa-subscribe");
		messageToSend.setPerformative(ACLMessage.FAILURE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(conv.initiator);

	}

	class FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFailure((ConvCProcessor)myProcessor, messageToSend);
			return "FINAL";
		}
	}


	/**
	 * Cancels the conversation
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messagereceived cancel message
	 */
	protected String doCancel(ConvCProcessor myProcessor,ACLMessage messagereceived ){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		//To add a perception
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "conversationcanceledbyinitiator("+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		conv.aquire_semaphore();
		//Precond: conv.conversationCanceled must say if the conversation was right canceled 
		String result ;
		if (conv.conversationCanceled){result = "CANCEL_INFORM";}
		else {result = "CANCEL_FAILURE";}
		return result;
	}

	class CANCEL_Method implements ReceiveStateMethod {
		@Override
		public String run(CProcessor myProcessor, ACLMessage messagereceived) {
			return doCancel((ConvCProcessor) myProcessor, messagereceived);
		}
	}

	/**
	 * Sets the inform message in response the cancel request
	 * @param myProcessor the CProcessor managing the conversation
	 * @param response inform message. 
	 */
	protected void doCancelInform(ConvCProcessor myProcessor, ACLMessage response){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		response.setProtocol("fipa-subscribe");
		response.setPerformative(ACLMessage.INFORM);
		response.setInReplyTo(ACLMessage.getPerformative(ACLMessage.CANCEL));
		response.setReceiver(conv.initiator);
		response.setSender(myProcessor.getMyAgent().getAid());
		response.setContent("Cancel done!.");
	}

	class CANCEL_INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doCancelInform((ConvCProcessor)myProcessor, messageToSend);
			return  "FINAL";
		}
	}

	/**
	 * Sets the failure message in response to the cancel request
	 * @param myProcessor the CProcessor managing the conversation
	 * @param response failure message. 
	 */
	protected void doCancelFailure(ConvCProcessor myProcessor, ACLMessage response){
		FSConversation conv = (FSConversation) myProcessor.getConversation();
		response.setProtocol("fipa-subscribe");
		response.setPerformative(ACLMessage.FAILURE);
		response.setInReplyTo(ACLMessage.getPerformative(ACLMessage.CANCEL));
		response.setReceiver(conv.initiator);
		response.setSender(myProcessor.getMyAgent().getAid());
		response.setContent("Cancel failed!.");
	}

	class CANCEL_FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doCancelFailure((ConvCProcessor)myProcessor, messageToSend);
			return  "FINAL";
		}
	}

	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend) {
		FSConversation conv = (FSConversation)myProcessor.getConversation();
		myProcessor.getMyAgent().removeFactory(conv.factoryName);
		messageToSend = myProcessor.getLastSentMessage();
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal((ConvCProcessor) myProcessor, messageToSend);
		}
	}

	public ConvCFactory newFactory(String name, MessageFilter filter,
			int availableConversations, ConvJasonAgent myAgent){

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

		//WAIT_SUBSCRIBE_INITIATOR
		processor.registerState(new WaitState("WAIT_SUBSCRIBE_INITIATOR", 30000));
		processor.addTransition("BEGIN","WAIT_SUBSCRIBE_INITIATOR");

		//RECEIVE_SUBSCRIBE
		ReceiveState RECEIVE_SUBSCRIBE = new ReceiveState("RECEIVE_SUBSCRIBE");
		RECEIVE_SUBSCRIBE.setMethod(new RECEIVE_SUBSCRIBE_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+ACLMessage.getPerformative(ACLMessage.SUBSCRIBE));
		RECEIVE_SUBSCRIBE.setAcceptFilter(filter);
		processor.registerState(RECEIVE_SUBSCRIBE);
		processor.addTransition("WAIT_SUBSCRIBE_INITIATOR","RECEIVE_SUBSCRIBE");

		//REFUSE
		SendState REFUSE = new SendState("REFUSE");
		REFUSE.setMethod(new REFUSE_Method());
		template.setProtocol("fipa-subscribe");
		template.setPerformative(ACLMessage.REFUSE);
		REFUSE.setMessageTemplate(template);
		processor.registerState(REFUSE);
		processor.addTransition("RECEIVE_SUBSCRIBE","REFUSE");

		//AGREE
		SendState AGREE = new SendState("AGREE");
		AGREE.setMethod(new AGREE_Method());
		template.setProtocol("fipa-subscribe");
		template.setPerformative(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);
		processor.registerState(AGREE);
		processor.addTransition("RECEIVE_SUBSCRIBE","AGREE");


		//INFORM
		ReceiveState INFORM = new ReceiveState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+ACLMessage.getPerformative(ACLMessage.INFORM));
		INFORM.setAcceptFilter(filter);
		processor.registerState(INFORM);
		//processor.addTransition("AGREE","INFORM");

		//FAILURE
		SendState FAILURE = new SendState("FAILURE");
		FAILURE.setMethod(new FAILURE_Method());
		template.setProtocol("fipa-subscribe");
		template.setPerformative(ACLMessage.FAILURE);
		FAILURE.setMessageTemplate(template);
		processor.registerState(FAILURE);


		//WAIT_FOR_CHANGES
		processor.registerState(new WaitState("WAIT_FOR_CANCEL", 0));
		processor.addTransition("AGREE","WAIT_FOR_CANCEL");
		processor.addTransition("WAIT_FOR_CANCEL","INFORM");
		processor.addTransition("INFORM","WAIT_FOR_CANCEL");
		processor.addTransition("WAIT_FOR_CANCEL","FAILURE");


		//RECEIVE_CANCEL
		ReceiveState RECEIVE_CANCEL = new ReceiveState("CANCEL_STATE");
		RECEIVE_CANCEL.setMethod(new CANCEL_Method());
		filter = new MessageFilter("protocol = fipa-subscribe AND performative = "+ACLMessage.getPerformative(ACLMessage.CANCEL));
		RECEIVE_CANCEL.setAcceptFilter(filter);
		processor.registerState(RECEIVE_CANCEL);
		processor.addTransition("WAIT_FOR_CANCEL","CANCEL_STATE");

		//CANCEL_INFORM
		SendState CANCEL_INFORM = new SendState("CANCEL_INFORM");
		CANCEL_INFORM.setMethod(new CANCEL_INFORM_Method());
		template.setProtocol("fipa-subscribe");
		template.setPerformative(ACLMessage.INFORM);
		CANCEL_INFORM.setMessageTemplate(template);
		processor.registerState(CANCEL_INFORM);
		processor.addTransition("CANCEL_STATE","CANCEL_INFORM");

		//CANCEL_FAILURE
		SendState CANCEL_FAILURE = new SendState("CANCEL_FAILURE");
		CANCEL_FAILURE.setMethod(new CANCEL_FAILURE_Method());
		template.setProtocol("fipa-subscribe");
		template.setPerformative(ACLMessage.FAILURE);
		CANCEL_FAILURE.setMessageTemplate(template);
		processor.registerState(CANCEL_FAILURE);
		processor.addTransition("CANCEL_STATE","CANCEL_FAILURE");

		FinalState FINAL = new FinalState("FINAL");
		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition(REFUSE,FINAL);
		processor.addTransition(FAILURE,FINAL);
		processor.addTransition(CANCEL_INFORM,FINAL);
		processor.addTransition(CANCEL_FAILURE,FINAL);

		return theFactory;


	}

}
