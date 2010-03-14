package IntroducingProcessorsAndFactories;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import es.upv.dsic.gti_ia.cAgents.*;

class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	// When a CAgent is created, its first Cprocessor is automatically created
	// and a conversation with the platform begins in which the platform sends
	// a welcome message to the agent. Next, the first processor calls the
	// method Initialize that you have provided.

	protected void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {

		ACLMessage template;

		System.out.println(welcomeMessage.getContent());

		// Create a factory to let Harry tell Hello to Sally

		template = new ACLMessage(ACLMessage.INFORM);

		CProcessorFactory talkWithSallyFactory = new CProcessorFactory(
				"talkWithSallyFactory", template, 1);

		// Create the processor template for this conversation

		// The converstation begins in the predefinited state labeled "BEGIN".

		BeginState BEGIN = (BeginState) talkWithSallyFactory.cProcessorTemplate().getState("BEGIN");

		class BEGIN_Method extends BeginStateMethod {
			protected String run(CProcessor myProcessor, ACLMessage msg) {
				// Rally nothing to do, except to tell the cProcessor to change to
				// the state labeled SEND
				return "SEND";
			};
		}
		BEGIN.setMethod(new BEGIN_Method());

		// Next Harry sends a message to Sally

		SendState SEND = new SendState("SEND");

		class SEND_Method extends SendStateMethod {
			protected String run(CProcessor myProcessor,
					ACLMessage messageToSend) {
				AgentID sallyID = new AgentID("Sally");
				messageToSend.setReceiver(sallyID);
				messageToSend.setContent("Hello Sally. How are you?");
				return "FINAL";
			}
		}
		SEND.setMethod(new SEND_Method());

		template = new ACLMessage(ACLMessage.INFORM);
		SEND.setMessageTemplate(template);

		talkWithSallyFactory.cProcessorTemplate().registerState(SEND);
		talkWithSallyFactory.cProcessorTemplate()
				.addTransition("BEGIN", "SEND");

		// Now the conversation ends. Finalize the processor and notity it to
		// its parent processor

		FinalState FINAL = new FinalState("FINAL");

		class FINAL_Method extends FinalStateMethod {
			protected void run(CProcessor myProcessor, ACLMessage messageToSend) {
				messageToSend.setContent("I just tell Sally 'Hello'");
			}
		}
		FINAL.setMethod(new FINAL_Method());

		talkWithSallyFactory.cProcessorTemplate().registerState(FINAL);
		talkWithSallyFactory.cProcessorTemplate()
				.addTransition("SEND", "FINAL");

		// cProcessorTemplate ready. Activate the factory

		this.addFactory(talkWithSallyFactory);

		// start de conversation

		firstProcessor.createSyncConversation(
				new ACLMessage(ACLMessage.INFORM));

	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}