package benchmarks.bench2a;

import org.apache.qpid.transport.Connection;

import s.dsic.gti_ia.fipa.ACLMessage;
import s.dsic.gti_ia.fipa.AgentID;

import _BaseAgent_Example.SingleAgent;



public class ReceptorBenchmark2 extends SingleAgent{
		
	public ReceptorBenchmark2(AgentID aid, Connection connection) {
		super(aid, connection);
	}
	
	public void execute(){		
		while(true)
		{
			ACLMessage msg = receiveACLMessage();	//esperem missatge des de'l emisor
			AgentID sender = msg.getReceiver();		//tornem el missatge al emisor
			msg.setReceiver(msg.getSender());
			msg.setSender(sender);
			send(msg);
		}
	}
}
