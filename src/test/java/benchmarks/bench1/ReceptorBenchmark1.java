package benchmarks.bench1;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class ReceptorBenchmark1 extends SingleAgent {
	public int nemisores;

	public ReceptorBenchmark1(AgentID aid, Connection connection)
			throws Exception {
		super(aid);
	}

	public void execute() {
		System.out.println("");
		System.out.println("Soy " + this.getName() + ". Arranco");
		System.out.println("");
		int ntotal = 0;
		while (true) {
			ACLMessage msg = receiveACLMessage(); // esperem missatge des de'l
													// emisor
			AgentID sender = msg.getReceiver(); // tornem el missatge al emisor
			msg.setReceiver(msg.getSender());
			msg.setSender(sender);
			// System.out.println("");
			// System.out.println("Soy "+this.getName()+". He recibido y paso a
			// responder");
			// System.out.println("");
			send(msg);
			ntotal++;
		}

	}
}
