package conversaciones;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.RequestInitiatorFactory;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

public class Initiator extends CAgent{

	public Initiator(AgentID aid) throws Exception {
		super(aid);
	}

	@Override
	protected void setFactories() {
		String CID = "C"+this.hashCode()+System.currentTimeMillis();
		
		ACLMessage sendTemplate = new ACLMessage(ACLMessage.REQUEST);
		sendTemplate.setContent("This is a generic request message");
		sendTemplate.setSender(getAid());
		sendTemplate.setReceiver(new AgentID("participant"));
		sendTemplate.setConversationId(CID);
		RequestInitiatorFactory factory = new RequestInitiatorFactory("Initiator", sendTemplate);
		
		//attach factory to agent
		this.addStartingFactory(factory, CID);
	}
}
