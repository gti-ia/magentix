package TestTrace.TestTraceBasic;

import java.util.concurrent.Semaphore;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/** 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 * @author Jose Alemany Bordera - jalemany1@dsic.upv.es
 * 
 */

public class Coordinator extends BaseAgent {

	static Semaphore contExec;
	private AgentID publisherAid;
	private ACLMessage coordination_msg;
	
	
	public Coordinator(AgentID aid, AgentID publisherAid) throws Exception {
		super(aid);
		contExec = new Semaphore(0);
		this.publisherAid = publisherAid;
	}

	public void execute() {
		try {
			contExec.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		coordination_msg = new ACLMessage(ACLMessage.REQUEST);
		coordination_msg.setSender(this.getAid());
		coordination_msg.setReceiver(publisherAid);
		coordination_msg.setContent("STOP");
		send(coordination_msg);
		
	}
}
