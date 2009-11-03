package es.upv.dsic.gti_ia.core;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import java.util.ArrayList;
import java.util.Date;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author  Ricard Lopez Fogues
 * @author  Sergio Pajares Ferrando
 * @author  Joan Bellver Faus
 */

public abstract class SingleAgent extends BaseAgent {

	private ArrayList<ACLMessage> messageList;

    /**
     * Creates a new SingleAgent
     * @param aid Agent Id 
     * @param connection Connection the agent will use
     * @throws Exception if agent id already exists on the platform
     */
    public SingleAgent(AgentID aid) throws Exception {
        super(aid);
        messageList = new ArrayList<ACLMessage>();
    }
    
    /**
     * Method to receive a magentix2 AclMessage
     * @return an ACLMessage
     * @throws Exception 
     */
    public final ACLMessage receiveACLMessage() throws Exception{
    	
    	for (int i = 0; i< messageList.size(); i++) {	
    		
    		ACLMessage msg = messageList.get(i);
    		
    		if (msg!=null)
			{
				messageList.remove(msg);
				return msg;
			}
		
			}
    	logger.debug("empty messageList List on receiveACLMessage");
		throw new Exception("empty messageList List on receiveACLMessage");
			
    	
    	
//    	MessageTransfer xfr = new MessageTransfer();
//    	try {
//            xfr = internalQueue.take();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        
//        return MessageTransfertoACLMessage(xfr);
     
    }
    private synchronized void writeQueue(ACLMessage msg)
	{
		messageList.add(msg);
	}

    public void onMessage(ACLMessage msg) {
    	this.writeQueue(msg);
    }
}