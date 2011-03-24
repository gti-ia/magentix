package MMS_Example;

import java.util.concurrent.LinkedBlockingQueue;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * ConsumerAgent class define the structure of a consumer BaseAgent
 * 
 */
public class AgentB extends SingleAgent {

    LinkedBlockingQueue<MessageTransfer> internalQueue;


    public AgentB(AgentID aid) throws Exception {
	super(aid);
    }

    public void execute() {
	logger.info("Executing, I'm " + getName());

	//Espera el mensaje de agentA
	try {

	    ACLMessage msgReceived1 = receiveACLMessage();
	    System.out.println("["+this.getAid().getLocalName()+"] Message received from " + msgReceived1.getSender()+ ". The content is: "+ msgReceived1.getContent());



	    //Contesto al agente que he recibido su mensaje.

	    /**
	     * Building a ACLMessage
	     */
	    ACLMessage msgResponse1 = new ACLMessage(ACLMessage.REQUEST);
	    msgResponse1.setReceiver(msgReceived1.getSender());
	    msgResponse1.setSender(this.getAid());
	    msgResponse1.setLanguage("ACL");
	    msgResponse1.setContent("Hello, I'm " + getName());
	    /**
	     * Sending a ACLMessage
	     */
	    send(msgResponse1);
	    
	    ACLMessage msgReceived2 = receiveACLMessage();
	    System.out.println("["+this.getAid().getLocalName()+"] Message received from " + msgReceived2.getSender()+ ". The content is: "+ msgReceived2.getContent());

	    /**
	     * Building a ACLMessage
	     */
	    ACLMessage msgResponse2 = new ACLMessage(ACLMessage.REQUEST);
	    msgResponse2.setReceiver(msgReceived2.getSender());
	    msgResponse2.setSender(this.getAid());
	    msgResponse2.setLanguage("ACL");
	    msgResponse2.setContent("Hello, I'm " + getName());
	    /**
	     * Sending a ACLMessage
	     */
	    send(msgResponse2);

	} catch (Exception e) {
	    logger.error(e.getMessage());
	    return;
	}
    }

}
