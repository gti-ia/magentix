package _SingleAgent_Example;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;



public class AgenteConsumidor extends SingleAgent{
	
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	
	public AgenteConsumidor(AgentID aid) throws Exception {
		super(aid);
	}
		
	public void execute(){
		
	while(true)
	{
		//System.out.println("Arranco, soy "+getName());
		try{
		ACLMessage msg = receiveACLMessage();
		System.out.println("Mensaje recibido de receiveACLMessage: " + msg.getContent());	
		}catch (Exception e){}
		
//		System.out.println("Recibido en Consumidor:"+msg.getContent());
	}
		//System.out.println(msg.getContent() + " Language "+msg.getLanguage());
	}

	
}
