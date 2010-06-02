package BridgeAgent_Example;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * EmisorAgent class define the structure of a sender BaseAgent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class SenderAgentJADE extends BaseAgent {

	public SenderAgentJADE(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		logger.info("Executing, I'm " + getName());
		//AgentID receiver = new AgentID("consumer");
		
		AgentID receiver = new AgentID();
		receiver.name = "JUAN@laplace:1099/JADE";
		//receiver.name="JuanAngel@pepe";
		receiver.host = "laplace.dsic.upv.es";
		receiver.port = "7778";
		receiver.protocol = "http";

		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);
		msg.setSender(this.getAid());
		msg.setLanguage("ACL");
		msg.setContent("HOLAAAAA, I'm " + getName());
		/**
		 * Sending a ACLMessage
		 */
		send(msg);
		
		while(true)
		{
			
		}
	
	}
	
	public void onMessage(ACLMessage msg) {
		/**
		 * When a message arrives, its shows on screen
		 */
		logger.info("Mensaje received in " + this.getName()
				+ " agent, by onMessage: " + msg.getContent());
	}

}
