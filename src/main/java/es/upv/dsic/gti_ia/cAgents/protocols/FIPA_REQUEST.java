package es.upv.dsic.gti_ia.cAgents.protocols;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.*;

public class FIPA_REQUEST {

	public static CProcessorFactory newInitiatorFactory(String name,
			ACLMessage template, int availableConversations, long timeout) {
		
		CProcessor processor;
		ACLMessage filter;

		template.setProtocol("REQUEST");
		template.setPerformative(ACLMessage.REQUEST);
		CProcessorFactory theFactory = new CProcessorFactory(name, template,
				availableConversations);
		
		processor = theFactory.cProcessorTemplate();
		
		BeginState BEGIN = (BeginState) processor.getState("BEGIN");

		class BEGIN_Method extends BeginStateMethod {
			protected String run(CProcessor myProcessor, ACLMessage msg) {
				myProcessor.internalData.put("InitialMessage", msg);
				return "SEND";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		SendState SEND = new SendState("SEND");

		class SEND_Method extends SendStateMethod {
			protected String run(CProcessor myProcessor,
					ACLMessage messageToSend) {
					messageToSend = (ACLMessage) myProcessor.internalData.get("InitialMessage");
					messageToSend.setProtocol("REQUEST");
					messageToSend.setPerformative(ACLMessage.REQUEST);
				return "FINAL";
			}
		}
		SEND.setMethod(new SEND_Method());

		template = new ACLMessage(ACLMessage.REQUEST);
		SEND.setMessageTemplate(template);

		processor.registerState(SEND);
		processor.addTransition("BEGIN", "SEND");

		// FIRST_WAIT
		processor.registerState(new WaitState("FIRST_WAIT", timeout));
		processor.addTransition("SEND", "FIRST_WAIT");
		

		// INFORM
		
		ReceiveState INFORM = new GenericReceiveState("INFORM");
		filter = new ACLMessage(ACLMessage.INFORM);
		
		class INFORM_Method extends ReceiveStateMethod {
			protected String run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastReceivedMessage();
			}
		}


		INFORM.setAcceptFilter(filter);
		
		processor.registerState(INFORM);
		processor.addTransition("FIRST_WAIT", "INFORM");
		
		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method extends FinalStateMethod {
			protected void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend = myProcessor.getLastReceivedMessage();
			}
		}
		FINAL.setMethod(new FINAL_Method());

		processor.registerState(FINAL);
		processor.addTransition("INFORM", "FINAL");		
		return theFactory;
	}

}
