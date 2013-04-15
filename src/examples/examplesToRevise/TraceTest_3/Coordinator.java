package TraceTest_3;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
//import es.upv.dsic.gti_ia.trace.TraceInteract;

public class Coordinator extends BaseAgent {

	private AgentID publisherAid;
	private AgentID observerAid;
	private ACLMessage coordination_msg;
	
	
	public Coordinator(AgentID aid, AgentID publisherAid, AgentID observerAid) throws Exception {
		super(aid);
		this.publisherAid = publisherAid;
		this.observerAid = observerAid;
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
		
		coordination_msg = new ACLMessage(ACLMessage.REQUEST);
		coordination_msg.setReceiver(observerAid);
		coordination_msg.setContent("STOP");
		send(coordination_msg);
	
	}
	
}
