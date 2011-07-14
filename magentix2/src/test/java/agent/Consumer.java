package agent;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Consumer extends SingleAgent{

	boolean gotMsg = false;
	
	public Consumer(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute(){
		System.out.println("Hi! I'm agent "+this.getName()+" and I start my execution");
		ACLMessage msg = null;
		try {
			msg = this.receiveACLMessage();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Hi! I'm agent "+this.getName()+" and I've received the message: "+msg.getContent());
	}
}
