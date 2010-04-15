package MMS_Example;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;


/**
 * EmisorAgent class define the structure of a sender BaseAgent
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class Agent extends BaseAgent {

	private Monitor m;
	
	public Agent(AgentID aid, String keyStorePath, String key, String CertType) throws Exception {
		super(aid,keyStorePath,key,CertType);
	}

	public void init() {
	
		m = new Monitor();
		//this.createCertificate(null);
	}
	public void execute() {
		logger.info("Executing, I'm " + getName());
		m.waiting();
		logger.info("Bye bye, I'm " + getName());
	}
	
	
	public void send(String agentTo, String agentFor)
	{
		AgentID receiver = new AgentID(agentFor);

		/**
		 * Building a ACLMessage
		 */
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);
		msg.setSender(new AgentID(agentTo));
		msg.setLanguage("ACL");
		msg.setContent("Hello, I'm " + getName());
		/**
		 * Sending a ACLMessage
		 */
		send(msg);
	}
	
	
	public void onMessage(ACLMessage msg) {
		/**
		 * When a message arrives, its shows on screen
		 */
		logger.info("Mensaje received in " + this.getName()
				+ " agent, by onMessage: " + msg.getContent());
	}
	
	public void exit(){
		this.m.advise();
	}

}
