package FactoryMakers;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CProcessorFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;

class SallyClass extends CAgent {

	public SallyClass(AgentID aid) throws Exception {
		super(aid);
	}

	protected void Initialize(CProcessor myProcessor, ACLMessage welcomeMessage) {

		System.out.println(myProcessor.getMyAgent().getName()
				+ ": the welcome message is " + welcomeMessage.getContent());

		class myFIPA_REQUEST extends FIPA_REQUEST_Participant {
			protected String Do_Request(CProcessor myProcessor, ACLMessage msg) {
				System.out.println(msg.getSender().name + " request me "
						+ msg.getContent());
				return "INFORM";
			}

			protected void Do_Inform(CProcessor myProcessor, ACLMessage msg) {
				msg.setContent("May be some day");
			}
		}

		CProcessorFactory talk = new myFIPA_REQUEST().newFactory("TALK",
				null, 1);

		this.addFactoryAsParticipant(talk);

	}

	protected void Finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println(finalizeMessage.getContent());
	}
}