package benchmarks.bench3;

import org.apache.qpid.transport.Connection;

import _BaseAgent_Example.SingleAgent;


import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;

public class ReceptorBenchmark3 extends SingleAgent{
	
	public ReceptorBenchmark3(AgentID aid, Connection connection) {
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
