package FactoryMakers;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.cAgents.*;

class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		ACLMessage msg;

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
			protected void Process_Inform(CProcessor myProcessor, ACLMessage msg) {
				System.out.println("Procesando respuesta");
			}
		}

		CInitiatorFactory talkWithSallyFactory = new myFIPA_REQUEST()
				.newInitiatorFactory("?", null, 1, 0);

		this.addFactory(talkWithSallyFactory);

		msg = talkWithSallyFactory.getTemplate();
		msg.setReceiver(new AgentID("Sally"));
		msg.setContent("May you give me your phone number?");

		myProcessor.createSyncConversation(msg);

		myProcessor.ShutdownAgent();
	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}