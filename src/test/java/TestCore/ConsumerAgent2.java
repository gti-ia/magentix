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
	private ACLMessage msg2;

	public ConsumerAgent2(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
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
			} catch (Exception e) {
				logger.error(e.getMessage());
				return;
			}


	}
	
	public ACLMessage getMessage(){
		return msg2;
	}
}
