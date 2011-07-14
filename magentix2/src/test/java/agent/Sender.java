package agent;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class Sender extends BaseAgent {

	public Sender(AgentID aid) throws Exception {
		super(aid);
	}
	
	public void execute(){
		System.out.println("Hi! I'm agent "+this.getName()+" and I start my execution");
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setSender(this.getAid());
		msg.addReceiver(new AgentID("Consumer"));
		msg.setContent("Hi! I'm Sender agent and I'm running on Magentix2");
		this.send(msg);
	}
	
}
