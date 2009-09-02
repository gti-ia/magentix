package es.upv.dsic.gti_ia.magentix2;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;

public class AgenteConsumidor extends SingleAgent{
	
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	
	public AgenteConsumidor(AgentID aid, Connection connection) {
		super(aid, connection);
	}
	
	public void execute(){
		
	while(true)
	{
		System.out.println("Arranco, soy "+getName());
		ACLMessage msg = receiveACLMessage();
		System.out.println("Recibido en Consumidor:"+msg.getContent());
	}
		//System.out.println(msg.getContent() + " Language "+msg.getLanguage());
	}
}
