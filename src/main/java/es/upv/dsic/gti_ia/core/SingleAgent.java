package es.upv.dsic.gti_ia.core;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;
import java.util.Date;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author  Ricard Lopez Fogues
 */

public abstract class SingleAgent extends BaseAgent {

    LinkedBlockingQueue<MessageTransfer> internalQueue;

    /**
     * Creates a new SingleAgent
     * @param aid Agent Id 
     * @param connection Connection the agent will use
     * @throws Exception if agent id already exists on the platform
     */
    public SingleAgent(AgentID aid, Connection connection) throws Exception {
        super(aid, connection);
        internalQueue = new LinkedBlockingQueue<MessageTransfer>();
    }
    
    /**
     * Method to receive a magentix2 AclMessage
     * @return an ACLMessage
     */
    public final ACLMessage receiveACLMessage(){
    	MessageTransfer xfr = new MessageTransfer();
    	try {
            xfr = internalQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        int indice1 = 0;
        int indice2 = 0;
        int aidindice1 = 0;
        int aidindice2 = 0;
        int tam = 0;
        String aidString;
        String body = xfr.getBodyString();
        
        indice2 = body.indexOf('#', indice1);
        ACLMessage msg = new ACLMessage(Integer.parseInt(body.substring(indice1, indice2)));        
                
        for(int i=0; i<3 ; i++){
        	AgentID aid = new AgentID();
        	aidindice1 = 0;
        	aidindice2 = 0;
        	indice1 = indice2 + 1 + tam;
        	indice2 = body.indexOf('#', indice1);
        	tam = Integer.parseInt(body.substring(indice1, indice2));
            aidString = body.substring(indice2 + 1, indice2 + 1 + tam);
            aidindice2 = aidString.indexOf(':');
	        if(aidindice2 - aidindice1 <= 0)
	        	aid.protocol = "";
	        else
	        	aid.protocol = aidString.substring(aidindice1, aidindice2);
	        aidindice1 = aidindice2 + 3;
	        aidindice2 = aidString.indexOf('@', aidindice1);
	        if(aidindice2 - aidindice1 <= 0)
	        	aid.name = "";
	        else
	        	aid.name = aidString.substring(aidindice1, aidindice2);
	        aidindice1 = aidindice2 + 1;
	        aidindice2 = aidString.indexOf(':', aidindice1);
	        if(aidindice2 - aidindice1 <= 0)
	        	aid.host = "";
	        else
	        	aid.host = aidString.substring(aidindice1, aidindice2);
	        aid.port = aidString.substring(aidindice2 + 1);
	                	        	        
	        if(i == 0)
	        	msg.setSender(aid);
	        if(i == 1)
	        	msg.setReceiver(aid);
	        if(i == 2)
	        	msg.setReplyTo(aid);
        }
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));  
        //language
        msg.setLanguage(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //encoding
        msg.setEncoding(body.substring(indice2 + 1, indice2 + 1 +tam)); 
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //ontologyencoding
        msg.setOntology(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //Protocol
        msg.setProtocol(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //Conversation id
        msg.setConversationId(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //Reply with
        msg.setReplyWith(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf("#", indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //In reply to
        msg.setInReplyTo(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //reply by
        if(tam != 0)
        	msg.setReplyByDate(new Date(Integer.parseInt(body.substring(indice2 + 1, indice2 + 1 +tam))));
        
        indice1 = indice2 + 1 + tam;
        indice2 = body.indexOf('#', indice1);
        tam = Integer.parseInt(body.substring(indice1, indice2));        
        //Content
        msg.setContent(body.substring(indice2 + 1, indice2 + 1 +tam));
        
        return msg;
    }

    public void onMessage(Session ssn, MessageTransfer xfr) {
        internalQueue.add(xfr);
    }
}