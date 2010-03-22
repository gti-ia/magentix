package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class FIPA_REQUEST_Participant {

	protected abstract String Do_Request(CProcessor myProcessor, ACLMessage request);

	protected abstract void Do_Inform(CProcessor myProcessor, ACLMessage response);

	class BEGIN_Method implements BeginStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String next = Do_Request(myProcessor, messageReceived);
			return next;
		}
	}

	class INFORM_Method implements SendStateMethod {
		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

			Do_Inform(myProcessor, messageToSend);
			messageToSend.addReceiver(myProcessor.getLastReceivedMessage().getSender());
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			return "FINAL";
		}
	}

	public CProcessorFactory newFactory(String name, ACLMessage template,
			int availableConversations, CAgent myAgent) {

		// Create factory
		
		if (template == null) {
			template = new ACLMessage();
		}
		template.setProtocol("REQUEST");
		template.setPerformative(ACLMessage.REQUEST);
		CProcessorFactory theFactory = new CProcessorFactory(name, template,
				availableConversations, myAgent);

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
		template.setPerformative(ACLMessage.INFORM);
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
