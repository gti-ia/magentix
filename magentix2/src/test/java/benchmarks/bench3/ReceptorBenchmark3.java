package benchmarks.bench3;


import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;


public class ReceptorBenchmark3 extends SingleAgent {

	public ReceptorBenchmark3(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		while (true) {
			ACLMessage msg = receiveACLMessage(); // esperem missatge des de'l
													// emisor
			AgentID sender = msg.getReceiver(); // tornem el missatge al emisor
			msg.setReceiver(msg.getSender());
			msg.setSender(sender);
			send(msg);
		}
	}
}
