package SingleAgent_Example;

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
 */
public class ConsumerAgent extends SingleAgent {

	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public ConsumerAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		logger.info("Executing, I'm " + getName());
		while (true) {
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
				logger.info("Mensaje received in " + this.getName()
						+ " agent, by receiveACLMessage: " + msg.getContent());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

		}

	}
}
