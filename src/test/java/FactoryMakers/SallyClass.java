package FactoryMakers;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CParticipantFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {

		ACLMessage template;

		System.out.println(welcomeMessage.getContent());

		template = new ACLMessage(ACLMessage.REQUEST);

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {
			protected String Do_Request(CProcessor myProcessor, ACLMessage msg) {	
				System.out.println(msg.getSender() + " request me " + msg.getContent());
				return "INFORM";
			}
			protected void Do_Inform(CProcessor myProcessor, ACLMessage msg) {
				msg.setContent("May be some day");
				System.out.println(msg.getContent());
			}
		}

		CParticipantFactory talkWith = new myFIPA_REQUEST()
				.newFactory("?", template, 1, 0);

		this.addFactory(talkWith);

	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}