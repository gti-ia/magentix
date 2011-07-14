package fipaRecruitingTest;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Participant;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class TargetAgent extends CAgent{

	class myFIPA_REQUEST extends FIPA_REQUEST_Participant {

		@Override
		protected String doAction(CProcessor myProcessor) {
			System.out.println("Message received! "+getAid().name+" on my way");
			return "INFORM";
		}

		@Override
		protected void doInform(CProcessor myProcessor, ACLMessage response) {
			System.out.println(myProcessor.getMyAgent().getName()
					+ ": I'll arrive in 15 minutes ");			
		}

		@Override
		protected String doReceiveRequest(CProcessor myProcessor,
				ACLMessage request) {
			System.out.println("Allways accept requests");
			return "AGREE";
		}
		
	}
	
	public TargetAgent(AgentID aid) throws Exception {
		super(aid);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {	
		MessageFilter filter = new MessageFilter("performative = REQUEST");
		CFactory talk = new myFIPA_REQUEST().newFactory("TALK", filter,
				0, firstProcessor.getMyAgent());

		// Finally the factory is setup to answer to incoming messages that
		// can start the participation of the agent in a new conversation
		this.addFactoryAsParticipant(talk);
	}
	

}
