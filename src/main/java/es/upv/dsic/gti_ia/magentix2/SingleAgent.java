package es.upv.dsic.gti_ia.magentix2;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;
import java.util.Date;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class SingleAgent extends BaseAgent {

    LinkedBlockingQueue<MessageTransfer> internalQueue;

    public SingleAgent(AgentID aid, Connection connection) {
        super(aid, connection);
        internalQueue = new LinkedBlockingQueue<MessageTransfer>();
    }

    public final MessageTransfer receive() {
        MessageTransfer xfr = new MessageTransfer();
        try {
            xfr = internalQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return xfr;
    }
    //Propuesta
    public final ACLMessage receiveACLMessage(){
    	MessageTransfer xfr = new MessageTransfer();
    	try {
            xfr = internalQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //des-serializamos el mensaje
        //inicializaciones
        int indice1 = 0;
        int indice2 = 0;
        int aidindice1 = 0;
        int aidindice2 = 0;
        int tam = 0;
        String aidString;
        String body = xfr.getBodyString();
        
        indice2 = body.indexOf('#', indice1);
        ACLMessage msg = new ACLMessage(Integer.parseInt(body.substring(indice1, indice2)));        
        System.out.println("performative "+ msg.getPerformative());
        
        //deserializamos los diferentes AgentesID (Sender, Receiver, ReplyTo)
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
        msg.setLanguage(body.substring(indice2 + 1, indice2 + 1 +tam));
        
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

    public final void onMessage(Session ssn, MessageTransfer xfr) {
        internalQueue.add(xfr);
    }
}