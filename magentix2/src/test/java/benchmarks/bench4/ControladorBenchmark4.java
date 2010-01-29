package benchmarks.bench4;


import java.util.Vector;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;


public class ControladorBenchmark4 extends SingleAgent {

	ACLMessage msg, msg2;
	int ntotal, nagents = 0, nacabats = 0;
	Vector<String> agents = new Vector<String>();
	long t1, t2;

	public ControladorBenchmark4(AgentID aid, int ntotal) throws Exception {
		super(aid);
		this.ntotal = ntotal;
	}

	public void execute() {
		// Esperem a rebre el Ready de tots els agents emisors
		/*
		 * while(nagents < ntotal){ msg = this.receiveACLMessage();
		 * 
		 * try{ agents.addElement(msg.getSender().name);
		 * }catch(java.lang.NullPointerException e){System.out.println("No s'ha
		 * pogut afegir el Sender");}
		 * 
		 * nagents++; }
		 */

		while (nagents < ntotal) {
			this.receiveACLMessage();
			nagents++;
		}

		t1 = System.currentTimeMillis();
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("Start!");
		msg.setSender(this.getAid());
		AgentID receiver = new AgentID();
		receiver.protocol = "qpid";
		receiver.port = "8080";
		for (int i = 1; i <= ntotal; i++) {
			receiver.host = "host" + i;
			receiver.name = "emisor" + i;
			msg.setReceiver(receiver);
			send(msg);
		}
		// enviem un missatge a cada emisor per a que comencen a emetre
		// missatges
		/*
		 * Iterator<String> iterator=agents.iterator();
		 * 
		 * while(iterator.hasNext()) receiver.name = iterator.next();
		 * msg2.add_receiver(receiver);
		 * 
		 * 
		 * this.send_multicast(msg);
		 */
		// enviem un missatge a cada emisor per a que comencen a emetre
		// missatges

		// esperem a que ens responguen tots amb ok
		while (nacabats < ntotal) {
			receiveACLMessage();
			nacabats++;
		}

		// Mostrem resultat per pantalla
		System.out.println("Prova acabada!");
		t2 = System.currentTimeMillis();
		System.out.println("Bench Time (s): " + (float) (t2 - t1) / 1000);
	}
}
