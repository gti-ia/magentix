package es.upv.dsic.gti_ia.magentix2;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.SingleAgent;


public class AgenteConsumidor extends BaseAgent{
	
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	
	public AgenteConsumidor(AgentID aid, Connection connection) throws Exception {
		super(aid, connection);
	}
	
	public void execute(){
		
	while(true)
	{
		System.out.println("Arranco, soy "+getName());
		ACLMessage msg = this();
		System.out.println("Recibido en Consumidor:"+msg.getContent());
	}
		//System.out.println(msg.getContent() + " Language "+msg.getLanguage());
	}
}
