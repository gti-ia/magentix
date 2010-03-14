package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.cAgents.*;

public abstract class FIPA_REQUEST_Participant {

	protected abstract String Do_Request(ACLMessage request);

	protected abstract void Do_Inform(ACLMessage response);

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String next = Do_Request(messageReceived);
			return next;
		}
	}

	class INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

			String next = Do_Request(messageToSend);
			return next;
		}
	}

	public CProcessorFactory newFactory(String name, ACLMessage template,
			int availableConversations, long timeout) {

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

		// INFORM State

		SendState INFORM = new SendState("INFORM");
		INFORM.setMethod(new INFORM_Method());
		template = new ACLMessage(ACLMessage.INFORM);
		template.setProtocol("REQUEST");
		INFORM.setMessageTemplate(template);
		processor.registerState(INFORM);
		processor.addTransition("BEGIN", "INFORM");

		// FINAL State
		
		FinalState FINAL = new FinalState("FINAL");
		class FINAL_Method implements FinalStateMethod {
			public void run(CProcessor myProcessor, ACLMessage messageToSend) {
			}
		}
		FINAL.setMethod(new FINAL_Method());
		processor.registerState(FINAL);
		processor.addTransition("INFORM", "FINAL");
		
		// Thath's all
		return theFactory;
	}
}
