package TestCore;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * ConsumerAgent class define the structure of a consumer SingleAgent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author David Fernández - dfernandez@dsic.upv.es
 */
public class ConsumerAgent2 extends SingleAgent {

	LinkedBlockingQueue<MessageTransfer> internalQueue;
	ACLMessage msg2;

	public ConsumerAgent2(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		System.out.println("Executing, I'm " + getName());
			/**
			 * This agent has no definite work. Wait infinitely the arrival of
			 * new messages.
			 */

			try {
				/**
				 * receiveACLMessage is a blocking function. its waiting a new
				 * ACLMessage
				 */
				ACLMessage msg = receiveACLMessage();
				msg2=msg;
				System.out.println("Mensaje received in " + this.getName()
						+ " agent, by receiveACLMessage: " + msg.getContent());
				System.out.println(msg.getHeaderValue("Purpose"));
			} catch (Exception e) {
				logger.error(e.getMessage());
				System.out.println(e.getMessage());
				return;
			}


	}
	
	public ACLMessage getMessage(){
		return msg2;
	}
}
