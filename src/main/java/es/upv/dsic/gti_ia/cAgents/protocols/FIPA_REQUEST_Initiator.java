package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.cAgents.*;

public abstract class FIPA_REQUEST_Initiator {

	protected abstract void Process_Inform(ACLMessage msg);

	class INFORM_Method implements ReceiveStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {
			Process_Inform(messageReceived);
			return "FINAL";
		}
	}

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage msg) {
			myProcessor.internalData.put("InitialMessage", msg);
			return "SEND";
		};
	}

	public CProcessorFactory newInitiatorFactory(String name,
			ACLMessage template, int availableConversations, long timeout) {

		ACLMessage filter;

		// Create factory

		template.setProtocol("REQUEST");
		template.setPerformative(ACLMessage.REQUEST);
		CProcessorFactory theFactory = new CProcessorFactory(name, template,
				availableConversations);

		// Processor template setup

		CProcessor processor = theFactory.cProcessorTemplate();

		// BEGIN State

		BeginState BEGIN = (BeginState) processor.getState("BEGIN");
		BEGIN.setMethod(new BEGIN_Method());

		// REQUEST State

		SendState REQUEST = new SendState("REQUEST");

		class REQUEST_Method implements SendStateMethod {
			public String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend = (ACLMessage) myProcessor.internalData
						.get("InitialMessage");
				messageToSend.setProtocol("REQUEST");
				messageToSend.setPerformative(ACLMessage.REQUEST);
				return "FINAL";
			}
		}
		REQUEST.setMethod(new REQUEST_Method());
		template = new ACLMessage(ACLMessage.REQUEST);
		template.setProtocol("REQUEST");
		REQUEST.setMessageTemplate(template);
		processor.registerState(REQUEST);
		processor.addTransition("BEGIN", "REQUEST");

		// FIRST_WAIT State

		processor.registerState(new WaitState("FIRST_WAIT", timeout));
		processor.addTransition("REQUEST", "FIRST_WAIT");

		// INFORM State

		ReceiveState INFORM = new ReceiveState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		filter = new ACLMessage(ACLMessage.INFORM);
		INFORM.setAcceptFilter(filter);
		processor.registerState(INFORM);
		processor.addTransition("FIRST_WAIT", "INFORM");

		// FINAL State

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastSendedMessage();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition("INFORM", "FINAL");
		return theFactory;
	}

}
