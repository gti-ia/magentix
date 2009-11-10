package _BridgeAgent_Example;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class ConsumerAgent extends SingleAgent {

	LinkedBlockingQueue<MessageTransfer> internalQueue;

	public ConsumerAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {

		logger.info("Executing, I'm " + getName());
		while (true) {
			
			try {
				ACLMessage msg = receiveACLMessage();
				logger.info("Mensaje received in " + this.getName()
						+ " agent, by receiveACLMessage: " + msg.getContent());
			} catch (Exception e) {
				logger.error(e.getMessage());
			}

	
		}
	
	}


}
