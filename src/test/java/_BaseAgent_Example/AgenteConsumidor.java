package _BaseAgent_Example;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;




public class AgenteConsumidor extends BaseAgent{
	
	LinkedBlockingQueue<MessageTransfer> internalQueue;
	
	public AgenteConsumidor(AgentID aid) throws Exception {
		super(aid);
	}
		
	public void execute(){
		
		while(true)
		{
			//waiting messages...
		}
	}
	public void onMessage(ACLMessage msg){
		System.out.println("Mensaje onMessage: " + msg.getContent());	
	}
	
}
