package BaseAgent_Example;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * ConsumerAgent class define the structure of a consumer BaseAgent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class ConsumerAgent extends SingleAgent {

	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public ConsumerAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute(){		
		while(true){
			ACLMessage msg = null;
			try {
				msg = this.receiveACLMessage();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AgentID emisor = msg.getSender();
			//System.out.println("Rebut missatge des de: "+emisor.getName());
			msg.setSender(this.getAid());
			msg.clearAllReceiver();
			msg.setReceiver(emisor);
			//doWait(1000);
			send(msg);
			//System.out.println("Enviat missatge des de: "+getName());
		}
	}

}
