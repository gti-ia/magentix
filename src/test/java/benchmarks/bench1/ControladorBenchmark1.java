package benchmarks.bench1;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class ControladorBenchmark1 extends SingleAgent {

	int ntotal, nagents = 0, nacabats = 0;
	long t1, t2;

	public ControladorBenchmark1(AgentID aid, Connection connection, int ntotal)
			throws Exception {
		super(aid);
		this.ntotal = ntotal;
	}

	public void execute() {
		System.out.println("Soy " + this.getName() + ". Arranco");
		// Esperem a rebre el Ready de tots els agents emisors
		while (nagents < ntotal) {
			try{
				this.receiveACLMessage();
				nagents++;
			}catch(Exception e){System.out.println("Error on receiveACLMessage, ControladorBenchmark1");}
		}

		t1 = System.currentTimeMillis();
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("Start!");
		msg.setSender(this.getAid());
		AgentID receiver = new AgentID();
		receiver.protocol = "qpid";
		receiver.port = "8080";
		// enviem un missatge a cada emisor per a que comencen a emetre
		// missatges
		for (int i = 1; i <= ntotal; i++) {
			receiver.host = "host" + i;
			receiver.name = "emisor" + i;
			msg.setReceiver(receiver);
			send(msg);
		}
		// System.out.println("");
		// System.out.println("Soy "+this.getName()+".Mensajes enviados a
		// receptores");
		// System.out.println("");
		// this.send_multicast(msg);

		// esperem a que ens responguen tots amb ok
		while (nacabats < ntotal) {
		
				this.receiveACLMessage();
				nagents++;
			
			nacabats++;
		}

		// Mostrem resultat per pantalla
		System.out.println("Prova acabada!");
		t2 = System.currentTimeMillis();
		System.out.println("Bench Time (s): " + (float) (t2 - t1) / 1000);
	}
}
