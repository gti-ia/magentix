package TraceBasic;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
//import es.upv.dsic.gti_ia.trace.TraceInteract;

public class Coordinator extends BaseAgent {

	private AgentID publisherAid;
	private ACLMessage coordination_msg;
	
	
	public Coordinator(AgentID aid, AgentID publisherAid) throws Exception {
		super(aid);
		this.publisherAid = publisherAid;
	}

	public void execute() {
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		coordination_msg = new ACLMessage(ACLMessage.REQUEST);
		coordination_msg.setReceiver(publisherAid);
		coordination_msg.setContent("STOP");
		send(coordination_msg);
		
		
	
	}
	
}
