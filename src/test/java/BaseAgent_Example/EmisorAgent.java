package BaseAgent_Example;


import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
/**
 * This class define the structure of a sender agent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class EmisorAgent extends BaseAgent {

	
	public EmisorAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		logger.info("Executing, I'm " + getName());
		AgentID receiver = new AgentID("consumer");
		
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
