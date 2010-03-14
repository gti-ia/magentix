package FactoryMakers;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.cAgents.*;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {

		ACLMessage msg;
		ACLMessage template;

		System.out.println(welcomeMessage.getContent());

		template = new ACLMessage(ACLMessage.REQUEST);
		template.setReceiver(new AgentID("Sally"));

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {
			protected String Do_Request(ACLMessage msg) {
				return "INFORM";
			}
			protected void Do_Inform(ACLMessage msg) {
			}
		}

		CProcessorFactory talkWithHarryFactory = new myFIPA_REQUEST()
				.newFactory("?", template, 0, 0);

		this.addFactory(talkWithHarryFactory);

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