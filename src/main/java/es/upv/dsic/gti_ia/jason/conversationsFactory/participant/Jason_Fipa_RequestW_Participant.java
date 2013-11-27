package es.upv.dsic.gti_ia.jason.conversationsFactory.participant;


import jason.asSemantics.TransitionSystem;
import jason.asSyntax.Literal;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCFactory;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvCProcessor;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Protocol_Template;

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
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;
import es.upv.dsic.gti_ia.cAgents.ActionStateMethod;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;


public class Jason_Fipa_RequestW_Participant {

	//private String AgName;
	//private AgentID AgID;
	//private String JasonConversationID;
	protected TransitionSystem Ts; 
	
	public int TimeOut ;
	
	/*public String Initiator ;*/
	public String TaskResult = "" ;
	public String TaskDesition = "";
	public String RequestResult = "";
	public String Task = "" ;
	
	public Jason_Fipa_RequestW_Participant(String sagName, 
			TransitionSystem ts, AgentID aid, int iTO) {
		//JasonConversationID = sagentConversationID;
		Ts = ts;
		TimeOut = iTO;
		
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
	 * Method executed when the initiator receives the request
	 * @param myProcessor the CProcessor managing the conversation
	 * @param request request message
	 * @return
	 */
	protected String doReceiveRequest(ConvCProcessor myProcessor,
			ACLMessage request){
		
		String jasonID = request.getHeaderValue("jasonID");
		//Conversation conv = new Conversation(myProcessor, jasonID,myProcessor.getConversationID());
		//ConvJasonAgent myag = ((ConvMagentixAgArch)Ts.getUserAgArch()).getJasonAgent();
		//myag.insertConversation(conv);
		
		Conversation conv = myProcessor.getConversation();
		conv.jasonConvID = jasonID;
		conv.initiator = request.getSender();
		
		
		List<Literal> allperc = new ArrayList<Literal>();
		Task = request.getContent();
		String percept = "request("+request.getSender().name+","+Task+","+jasonID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
//		try {
//			Protocol_Semaphore.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		//myag.getConversationByintID(myProcessor.getConversationID()).aquire_semaphore();
		
		conv.aquire_semaphore();
		
		String result = null; 
		if (RequestResult==Protocol_Template.AGREE_STEP){
			result = "AGREE";
		}else 
		if (RequestResult==Protocol_Template.REFUSE_STEP){
			result = "REFUSE";
		}else 
		if (RequestResult==Protocol_Template.NOT_UNDERSTOOD_STEP){
			result = "NOT_UNDERSTOOD";
		} 

	  return result;
	}

	class RECEIVE_REQUEST_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			return doReceiveRequest((ConvCProcessor)myProcessor, messageReceived);
			
		}
	}

	/**
	 * Sets the not-understood message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend not-understood message
	 */
	protected void doNotUnderstood(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		messageToSend.setProtocol("request-when");
		messageToSend.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
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
		messageToSend.setProtocol("request-when");
		messageToSend.setPerformative(ACLMessage.REFUSE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
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
		messageToSend.setProtocol("request-when");
		messageToSend.setPerformative(ACLMessage.AGREE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
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
		//ConvJasonAgent myag = ((ConvMagentixAgArch)Ts.getUserAgArch()).getJasonAgent();
		
		//Conversation conv = myag.getConversationByintID(myProcessor.getConversationID());
		
		Conversation conv = myProcessor.getConversation();
		
		List<Literal> allperc = new ArrayList<Literal>();
		String percept = "timetodotask("+conv.initiator.getLocalName()+","+Task+","+conv.jasonConvID+")[source(self)]";
		allperc.add(Literal.parseLiteral(percept));
		((ConvMagentixAgArch)Ts.getUserAgArch()).setPerception(allperc);
		
//		try {
//			Protocol_Semaphore.acquire();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		conv.aquire_semaphore();
		
		String result = null; 
		if (TaskDesition==Protocol_Template.INFORM_STEP){
			result = "INFORM";
		}else 
		if (TaskDesition==Protocol_Template.FAILURE_STEP){
			result = "FAILURE";
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
	 * Sets the failure message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend failure message
	 */
	protected void doFailure(ConvCProcessor myProcessor,
			ACLMessage messageToSend) {
		messageToSend.setProtocol("request-when");
		messageToSend.setPerformative(ACLMessage.FAILURE);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
		messageToSend.setReceiver(myProcessor.getLastReceivedMessage().getSender());
	}

	class FAILURE_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFailure((ConvCProcessor)myProcessor, messageToSend);
			return "FINAL";
		}
	}
	
	/**
	 * Sets the inform message
	 * @param myProcessor the CProcessor managing the conversation
	 * @param response inform message
	 */
	protected void doInform(ConvCProcessor myProcessor, ACLMessage response){
		response.setProtocol("request-when");
		response.setPerformative(ACLMessage.INFORM);
		response.setContent(TaskResult);
	}

	class INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

			doInform((ConvCProcessor)myProcessor, messageToSend);
			messageToSend.addReceiver(myProcessor.getLastReceivedMessage()
					.getSender());
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			return "FINAL";
		}
	}
	
	/**
	 * Method executed when the conversation ends
	 * @param myProcessor the CProcessor managing the conversation
	 * @param messageToSend final message
	 */
	protected void doFinal(ConvCProcessor myProcessor, ACLMessage messageToSend){
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
			filter = new MessageFilter("performative = REQUEST AND protocol = request-when");
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
		
		// RECEIVE_REQUEST State
		ReceiveState RECEIVE_REQUEST = new ReceiveState("RECEIVE_REQUEST");
		RECEIVE_REQUEST.setMethod(new RECEIVE_REQUEST_Method());
		filter = new MessageFilter("performative = REQUEST");
		RECEIVE_REQUEST.setAcceptFilter(filter);
		processor.registerState(RECEIVE_REQUEST);
		processor.addTransition("WAIT", "RECEIVE_REQUEST");
		
		// NOT_UNDERSTOOD State

		SendState NOT_UNDERSTOOD = new SendState("NOT_UNDERSTOOD");
		NOT_UNDERSTOOD.setMethod(new NOT_UNDERSTOOD_Method());
		template = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
		template.setProtocol("request-when");
		template.setPerformative(ACLMessage.INFORM);
		NOT_UNDERSTOOD.setMessageTemplate(template);
		processor.registerState(NOT_UNDERSTOOD);
		processor.addTransition("RECEIVE_REQUEST", "NOT_UNDERSTOOD");
		
		// REFUSE State

		SendState REFUSE = new SendState("REFUSE");
		REFUSE.setMethod(new REFUSE_Method());
		template = new ACLMessage(ACLMessage.REFUSE);
		template.setProtocol("request-when");
		template.setPerformative(ACLMessage.INFORM);
		REFUSE.setMessageTemplate(template);
		processor.registerState(REFUSE);
		processor.addTransition("RECEIVE_REQUEST", "REFUSE");
		
		// AGREE State

		SendState AGREE = new SendState("AGREE");
		AGREE.setMethod(new AGREE_Method());
		template = new ACLMessage(ACLMessage.AGREE);
		template.setProtocol("request-when");
		template.setPerformative(ACLMessage.AGREE);
		AGREE.setMessageTemplate(template);
		processor.registerState(AGREE);
		processor.addTransition("RECEIVE_REQUEST", "AGREE");
		
		// ACTION State
		ActionState ACTION = new ActionState("ACTION");
		ACTION.setMethod(new ACTION_Method());
		processor.registerState(ACTION);
		processor.addTransition("AGREE", "ACTION");
		
		// FAILURE State

		SendState FAILURE = new SendState("FAILURE");
		FAILURE.setMethod(new FAILURE_Method());
		template = new ACLMessage(ACLMessage.FAILURE);
		template.setProtocol("request-when");
		FAILURE.setMessageTemplate(template);
		processor.registerState(FAILURE);
		processor.addTransition("ACTION", "FAILURE");
		
		// INFORM State

		SendState INFORM = new SendState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		template = new ACLMessage(ACLMessage.INFORM);
		template.setProtocol("request-when");
		INFORM.setMessageTemplate(template);
		processor.registerState(INFORM);
		processor.addTransition("ACTION", "INFORM");
		

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");
		
		FINAL.setMethod(new FINAL_Method());
		processor.registerState(FINAL);
		processor.addTransition("NOT_UNDERSTOOD", "FINAL");
		processor.addTransition("REFUSE", "FINAL");
		processor.addTransition("FAILURE", "FINAL");
		processor.addTransition("INFORM", "FINAL");

		// Thath's all
		return theFactory;
				
		
	}
}
