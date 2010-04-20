package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
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

public abstract class FIPA_REQUEST_Initiator {
	
	protected void doBegin(CProcessor myProcessor, ACLMessage msg) {
		myProcessor.getInternalData().put("InitialMessage", msg);		
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			doBegin(myProcessor, msg);
			return "REQUEST";
		};
	}
	
	protected void doRequest(CProcessor myProcessor,
			ACLMessage messageToSend) {
		ACLMessage aux = (ACLMessage) myProcessor.getInternalData().get(
				"InitialMessage");
		messageToSend.copyFromAsTemplate(aux);
		messageToSend.setProtocol("fipa-request");
		messageToSend.setPerformative(ACLMessage.REQUEST);
		messageToSend.setSender(myProcessor.getMyAgent().getAid());
	}

	class REQUEST_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			doRequest(myProcessor, messageToSend);
			return "FIRST_WAIT";
		}
	}
	
	protected void doFirstWait(CProcessor myProcessor, ACLMessage msg){	
	}

	class FIRST_WAIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doFirstWait(myProcessor, messageReceived);
			return "FIRST_WAIT";
		}
	}
	
	protected void doNotUnderstood(CProcessor myProcessor, ACLMessage msg){
	}

	class NOT_UNDERSTOOD_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doNotUnderstood(myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	protected void doRefuse(CProcessor myProcessor, ACLMessage msg){
	}

	class REFUSE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doRefuse(myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	protected void doAgree(CProcessor myProcessor, ACLMessage msg){
	}

	class AGREE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doAgree(myProcessor, messageReceived);
			return "SECOND_WAIT";
		}
	}
	
	protected void doSecondWait(CProcessor myProcessor, ACLMessage msg){		
	}

	class SECOND_WAIT_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doSecondWait(myProcessor, messageReceived);
			return "SECOND_WAIT";
		}
	}
	
	protected void doFailure(CProcessor myProcessor, ACLMessage msg){
	}

	class FAILURE_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doFailure(myProcessor, messageReceived);
			return "FINAL";
		}
	}
	
	protected abstract void doInform(CProcessor myProcessor, ACLMessage msg); //Method to implement

	class INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			doInform(myProcessor, messageReceived);
			return "FINAL";
		}
	}

	protected void doFinal(CProcessor myProcessor, ACLMessage messageToSend) {
		messageToSend = myProcessor.getLastSendedMessage();
	}

	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			doFinal(myProcessor, messageToSend);
		}
	}

	public CProcessorFactory newFactory(String name, ACLMessage template,
			int availableConversations, CAgent myAgent, long timeout) {

		MessageFilter filter;

		// Create factory

		if (template == null) {
			template = new ACLMessage();
		}
		template.setProtocol("fipa-request");
		template.setPerformative(ACLMessage.REQUEST);
		CProcessorFactory theFactory = new CProcessorFactory(name, template,
				availableConversations, myAgent);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());

		// REQUEST State

		SendState REQUEST = new SendState("REQUEST");

		REQUEST.setMethod(new REQUEST_Method());
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setProtocol("REQUEST");
		REQUEST.setMessageTemplate(template);
		processor.registerState(REQUEST);
		processor.addTransition("BEGIN", "REQUEST");

		// FIRST_WAIT State

		processor.registerState(new WaitState("FIRST_WAIT", timeout));
		processor.addTransition("REQUEST", "FIRST_WAIT");
		
		// RECEIVE_FIRST_WAIT State
		
		ReceiveState RECEIVE_FIRST_WAIT = new ReceiveState("RECEIVE_FIRST_WAIT");
		RECEIVE_FIRST_WAIT.setMethod(new FIRST_WAIT_Method());
		filter = new MessageFilter("performative = INFORM AND purpose = waitMessage");
		RECEIVE_FIRST_WAIT.setAcceptFilter(filter);
		processor.registerState(RECEIVE_FIRST_WAIT);
		processor.addTransition("RECEIVE_FIRST_WAIT", "FIRST_WAIT");
		
		// NOT_UNDERSTOOD State
		
		ReceiveState NOT_UNDERSTOOD = new ReceiveState("NOT_UNDERSTOOD");
		NOT_UNDERSTOOD.setMethod(new NOT_UNDERSTOOD_Method());
		filter = new MessageFilter("performative = NOT_UNDERSTOOD");
		NOT_UNDERSTOOD.setAcceptFilter(filter);
		processor.registerState(NOT_UNDERSTOOD);
		processor.addTransition("FIRST_WAIT", "NOT_UNDERSTOOD");
		
		// REFUSE State
		
		ReceiveState REFUSE = new ReceiveState("REFUSE");
		REFUSE.setMethod(new REFUSE_Method());
		filter = new MessageFilter("performative = REFUSE");
		REFUSE.setAcceptFilter(filter);
		processor.registerState(REFUSE);
		processor.addTransition("FIRST_WAIT", "REFUSE");
		
		// AGREE State
		
		ReceiveState AGREE = new ReceiveState("AGREE");
		AGREE.setMethod(new AGREE_Method());
		filter = new MessageFilter("performative = AGREE");
		AGREE.setAcceptFilter(filter);
		processor.registerState(AGREE);
		processor.addTransition("FIRST_WAIT", "AGREE");
		
		// SECOND_WAIT State

		processor.registerState(new WaitState("SECOND_WAIT", timeout));
		processor.addTransition("AGREE", "SECOND_WAIT");
		
		// FAILURE State

		ReceiveState FAILURE = new ReceiveState("FAILURE");
		FAILURE.setMethod(new FAILURE_Method());
		filter = new MessageFilter("performative = FAILURE");
		FAILURE.setAcceptFilter(filter);
		processor.registerState(FAILURE);
		processor.addTransition("SECOND_WAIT", "FAILURE");
		
		// INFORM State

		ReceiveState INFORM = new ReceiveState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		filter = new MessageFilter("performative = INFORM");
		INFORM.setAcceptFilter(filter);
		processor.registerState(INFORM);
		processor.addTransition("SECOND_WAIT", "INFORM");

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition("INFORM", "FINAL");
		processor.addTransition("FAILURE", "FINAL");
		processor.addTransition("NOT_UNDERSTOOD", "FINAL");
		processor.addTransition("REFUSE", "FINAL");
		return theFactory;
	}

}
