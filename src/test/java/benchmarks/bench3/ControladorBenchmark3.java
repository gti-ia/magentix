package benchmarks.bench3;

import org.apache.qpid.transport.Connection;
import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.magentix2.SingleAgent;

public class ControladorBenchmark3 extends SingleAgent {
	
	int ntotal, nagents=0, nacabats = 0;
	long t1,t2;
	
	public ControladorBenchmark3(AgentID aid, Connection connection, int ntotal) {
		super(aid, connection);
		this.ntotal = ntotal;
	}
	
	public void execute(){
		//Esperem a rebre el Ready de tots els agents emisors
		while(nagents < ntotal){
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
		//enviem un missatge a cada emisor per a que comencen a emetre missatges
		for(int i=1; i <= ntotal; i++){
			receiver.host = "host"+i;
			receiver.name = "emisor"+i;
			msg.setReceiver(receiver);
			send(msg);
		}
		
		//esperem a que ens responguen tots amb ok
		while(nacabats < ntotal){
			receiveACLMessage();
			nacabats++;
		}
		
		//Mostrem resultat per pantalla
		System.out.println("Prova acabada!");
		t2 = System.currentTimeMillis();
		System.out.println("Bench Time (s): "+ (float) (t2 - t1)/1000);
	}
}
