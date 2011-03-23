package MMS_Example;

import java.util.concurrent.LinkedBlockingQueue;


import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

/**
 * ConsumerAgent class define the structure of a consumer BaseAgent
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class AgentA_C extends SingleAgent {

    LinkedBlockingQueue<MessageTransfer> internalQueue;



    public AgentA_C(AgentID aid) throws Exception {
	super(aid);
    }

    public void execute() {
	logger.info("Executing, I'm " + getName());


	try{
	    AgentID receiver = new AgentID("agentB");

	    /**
	     * Building a ACLMessage
	     */
	    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
	    msg.setReceiver(receiver);
	    msg.setSender(this.getAid());
	    msg.setContent("Hello, I'm " + getName() );

	    send(msg);

	    //Espero la respuesta
	    ACLMessage msgResceived = receiveACLMessage();
	    System.out.println("["+this.getAid().getLocalName()+"] Message received from " + msgResceived.getSender()+ ". The content is: "+ msgResceived.getContent());
	    
	    try{
	    changeIdentity(new AgentID("agentB"));
	    }catch(Exception e)
	    {
		logger.error(e.getMessage());
		
	    }
	    
	   
	    
	    ACLMessage msgResponse = new ACLMessage(ACLMessage.REQUEST);
	    msgResponse.setReceiver(receiver);
	    msgResponse.setSender(this.getAid());
	    msgResponse.setContent("Hello, I'm " + getName() );

	    send(msgResponse);
	    
	    //Espero la respuesta
	    ACLMessage msgResceived2 = receiveACLMessage();
	    System.out.println("["+this.getAid().getLocalName()+"] Message received from " + msgResceived2.getSender()+ ". The content is: "+ msgResceived2.getContent());
	    

	} catch (Exception e) {
	    logger.error(e.getMessage());
	    return;
	}
    }
}
