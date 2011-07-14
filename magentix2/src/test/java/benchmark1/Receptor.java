package benchmark1;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Receptor extends SingleAgent{

	public Receptor(AgentID aid) throws Exception {
		super(aid);
	}
	
	public void execute(){
		while(true){
			ACLMessage msg = null;
			try {
				msg = this.receiveACLMessage();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ACLMessage reply = msg.createReply();
			reply.setSender(getAid());
			reply.setPerformative(ACLMessage.INFORM);
			send(reply);
		}
	}
}
