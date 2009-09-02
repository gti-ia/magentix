package benchmarks.bench3;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.magentix2.SingleAgent;

public class ControladorBenchmark2b extends SingleAgent {
	
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	int ntotal, nagents=0, nacabats = 0;
	long t1,t2;
	
	public ControladorBenchmark2b(AgentID aid, Connection connection) {
		super(aid, connection);
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
		receiver.protocol = "http";
		receiver.port = "8080";
		//enviem un missatge a cada emisor per a que comencen a emetre missatges
		for(int i=0; i < ntotal; i++){
			receiver.host = "host"+i;
			receiver.name = "emisor"+i;
			msg.add_receiver(receiver);
		}
		this.send_multicast(msg);
		
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
