package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CInitiatorFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.core.ACLMessage;

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
			myProcessor.getInternalData().put("InitialMessage", msg);
			return "REQUEST";
		};
	}

	public CInitiatorFactory newInitiatorFactory(String name,
			ACLMessage template, int availableConversations, long timeout) {

		ACLMessage filter;

		// Create factory

		if (template == null) {
			template = new ACLMessage();
		}
		template.setProtocol("REQUEST");
		template.setPerformative(ACLMessage.REQUEST);
		CInitiatorFactory theFactory = new CInitiatorFactory(name, template,
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
				ACLMessage aux = (ACLMessage) myProcessor.getInternalData()
						.get("InitialMessage");
				messageToSend.copyFromAsTemplate(aux);
				messageToSend.setProtocol("REQUEST");
				messageToSend.setPerformative(ACLMessage.REQUEST);
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				System.out.println("ENVIANDO");
				return "FIRST_WAIT";
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
