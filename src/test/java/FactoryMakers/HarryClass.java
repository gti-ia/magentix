package FactoryMakers;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.cAgents.*;

class HarryClass extends CAgent {

	public HarryClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {

		ACLMessage msg;
		ACLMessage template;

		System.out.println(welcomeMessage.getContent());

		template = new ACLMessage(ACLMessage.REQUEST);
		template.setReceiver(new AgentID("Sally"));

		class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
			protected void Process_Inform(ACLMessage msg) {
			}
		}

		CProcessorFactory talkWithSallyFactory = new myFIPA_REQUEST()
				.newInitiatorFactory("?", template, 0, 0);

		this.addFactory(talkWithSallyFactory);

		msg = new ACLMessage(ACLMessage.REQUEST);
		template.setReceiver(new AgentID("Sally"));
		template.setHeader("Purpose", "Give me your phone number");
		firstProcessor
				.createSyncConversation(msg);

	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}