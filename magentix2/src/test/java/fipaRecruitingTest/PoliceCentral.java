package fipaRecruitingTest;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_RECRUITING_Participant;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.MessageFilter;

public class PoliceCentral extends CAgent{
	ArrayList<AgentID> targetAgents = new ArrayList<AgentID>();
	
	class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
		protected void doInform(CProcessor myProcessor, ACLMessage msg) {
			System.out.println(myProcessor.getMyAgent().getName() + ": "
					+ msg.getSender().name + " informs me "
					+ msg.getContent());
		}
	}
	
	class myFIPARecruitingParticipant extends FIPA_RECRUITING_Participant{

		@Override
		protected ArrayList<AgentID> doLocateAgents(CProcessor myProcessor,
				ACLMessage proxyMessage) {
			ArrayList<AgentID> locatedAgents = new ArrayList<AgentID>();
			String targetName = proxyMessage.getHeaderValue("target");
			for(int i=0; i<targetAgents.size(); i++)
			{
				if(targetAgents.get(i).name.startsWith(targetName)){
					locatedAgents.add(targetAgents.get(i));
				}
			}
			return locatedAgents;
		}

		@Override
		protected String doReceiveProxy(CProcessor myProcessor, ACLMessage msg) {
			return FIPA_RECRUITING_Participant.AGREE;
		}

		@Override
		protected boolean resultOfSubProtocol(CProcessor myProcessor,
				ACLMessage subProtocolMessageResult) {
			System.out.println("El resultado del subProtocolo es: "+subProtocolMessageResult.getContent());
			return true;
		}

		//@Override
		/*protected CProcessor startSubProtocol(CProcessor myProcessor) {
			return null;
		}*/
		
	}

	public PoliceCentral(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {		
	}

	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		//Creem la llista de agents target
		for(int i=0; i<5; i++){
			this.targetAgents.add(new AgentID("ambulance"+i));
		}
		
		for(int i=0; i<5; i++){
			this.targetAgents.add(new AgentID("backup"+i));
		}
		
		// In order to start a conversation the agent creates a message
		// that can be accepted by one of its initiator factories.

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		MessageFilter filter = new MessageFilter("performative = REQUEST");		
		// The agent creates the CFactory that creates processors that initiate
		// REQUEST protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the REQUEST protocol (null) and we do not limit the number of simultaneous
		// processors (value 0)
		
		CFactory subProtocol = new myFIPA_REQUEST().newFactory("TALK", msg,
				1, this, 0);
		
		this.addFactoryAsInitiator(subProtocol);
		
		//ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		MessageFilter filter2 = new MessageFilter("protocol = fipa-recruiting");		
		// The agent creates the CFactory that creates processors that initiate
		// REQUEST protocol conversations. In this
		// example the CFactory gets the name "TALK", we don't add any
		// additional message acceptance criterion other than the required
		// by the REQUEST protocol (null) and we do not limit the number of simultaneous
		// processors (value 0)
		
		CFactory recruiting = new myFIPARecruitingParticipant().newFactory("TALK", filter2, null,
				1, this);
		
		this.addFactoryAsParticipant(recruiting);
		
	}

}
