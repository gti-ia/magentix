package TraceProdConsObserver;

import java.util.Random;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * EmisorAgent class define the structure of a sender BaseAgent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class SenderAgent extends BaseAgent {

	public SenderAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		int i;
		
		logger.info("[SENDER]: Executing, I'm " + getName());
		AgentID receiver = new AgentID("consumer");

		
		for (i=0; i < 10; i++){
			/**
			 * Building a ACLMessage
			 */
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setReceiver(receiver);
			msg.setSender(this.getAid());
			msg.setLanguage("ACL");
			msg.setContent("Hello, I'm " + getName());
			/**
			 * Sending a ACLMessage
			 */
			send(msg);
		}
	}

}
