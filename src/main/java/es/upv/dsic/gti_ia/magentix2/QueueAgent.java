package es.upv.dsic.gti_ia.magentix2;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import es.upv.dsic.gti_ia.fipa.ACLMessage;
import es.upv.dsic.gti_ia.fipa.AgentID;
import es.upv.dsic.gti_ia.proto.MessageTemplate;

import es.upv.dsic.gti_ia.proto.Sincro;;



public class QueueAgent extends BaseAgent{
	
	ArrayList<ACLMessage> messageList = new ArrayList<ACLMessage>();
	//LinkedBlockingQueue<MessageTransfer> internalQueue;
	private AgentID aid = null;
	private Sincro sin;
	public QueueAgent(AgentID aid, Connection connection) {
		super(aid, connection);
		//internalQueue = new LinkedBlockingQueue<MessageTransfer>();
		this.aid = aid;
		this.sin = new Sincro();
	}
	

	
	
	
	public Sincro getSincro()
	{
		return this.sin;
	}

	public final ACLMessage MessageTransfertoACLMessage(MessageTransfer xfr){
    	
        //des-serializamos el mensaje
        //inicializaciones
        int indice1 = 0;
        int indice2 = 0;
        int aidindice1 = 0;
        int aidindice2 = 0;
        int tam = 0;
        String aidString;
        String body = xfr.getBodyString();
        
        System.out.println("BODY: "+ body);
        
        indice2 = body.indexOf('#', indice1);
        ACLMessage msg = new ACLMessage(Integer.parseInt(body.substring(indice1, indice2)));        
        
        
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
        //ontologyencodingACLMessage template
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
	

	public final void onMessage(Session ssn, MessageTransfer xfr) {
		//internalQueue.add(xfr);
        messageList.add(MessageTransfertoACLMessage(xfr));
        this.sin.dar();
        
        
        
      
        
    }
	
	public int getNMensajes()
	{
		return messageList.size();
	}
	
	public final ACLMessage receiveACLMessage(MessageTemplate template){
		ACLMessage msgselect = null;
		//System.out.println("Numero de mensajes:" + messageList.size());
		for(ACLMessage msg : messageList){
			
			//comparamos los campos performative y protocol 
			if(template.getPerformative().equals(msg.getPerformative()))
			{
				
				if (template.getProtocol().equals(msg.getProtocol()))
				{

						msgselect = msg;
						messageList.remove(msg);
							//TODO recuperar quan es igual al template i esborrar de la llista de missatges
							break;
			
				}
			
					
			}
		}
		return msgselect;
	}
	
	
	public final ACLMessage receiveACLMessageT(MessageTemplate template, long timeout){
		ACLMessage msgselect = null;
		int i=0;
		//System.out.println("Numero de mensajes:" + messageList.size());
		do{
		for(ACLMessage msg : messageList){
			
			//comparamos los campos performative y protocol 
			if(template.getPerformative().equals(msg.getPerformative()))
			{
				
				if (template.getProtocol().equals(msg.getProtocol()))
				{

						msgselect = msg;
						messageList.remove(msg);
							//TODO recuperar quan es igual al template i esborrar de la llista de missatges
							break;
			
				}
			
					
			}
		}
		if (msgselect==null)//no hay ningún mensaje
		{
			if (i==0)//solo esperaremos una vez
				try
				{
					this.wait(timeout);
				}catch(InterruptedException e){}
		}
		else
			i=2;
		i++;
		}while(i<2);
		return msgselect;
	}
	
	
	public synchronized final ACLMessage receiveACLMessageB(MessageTemplate template){
		ACLMessage msgselect = null;
		 boolean b = true;
		System.out.println("Numero de mensajes:" + messageList.size());

		do{
		for(ACLMessage msg : messageList){
			
			//comparamos los campos performative y protocol 
			if(template.getPerformative().equals(msg.getPerformative()))
			{
				
				if (template.getProtocol().equals(msg.getProtocol()))
				{

						msgselect = msg;
						messageList.remove(msg);
						System.out.println("Cambio");
						b = false;
							//TODO recuperar quan es igual al template i esborrar de la llista de missatges
							break;
			
				}
			
					
			}
		}
		try
		{
			System.out.println("Espero");
			this.sin.wait();
			System.out.println("Ya no espero");
		}catch(InterruptedException e){}
		}while(b);
		return msgselect;
	}
	
	
	
	
	public final ACLMessage receiveACLMessageI(MessageTemplate template)
	{//comparacion del template para el initiator
		ACLMessage msgselect = null;
		boolean condicion = true;
	//	do{
			//System.out.println("Paso 1");
		for(ACLMessage msg : messageList){
			
			//comparamos los campos  protocol, idcoversación y sender
			if (template.getProtocol().equals(msg.getProtocol()))
				{
				
				if (template.getConversationId().equals(msg.getConversationId()))
				{
					
					if (template.getSender().name.equals(msg.getSender().name) & template.getSender().host.equals(msg.getSender().host) & template.getSender().protocol.equals(msg.getSender().protocol))
					{
				
						msgselect = msg;
						messageList.remove(msg);
						condicion = false;
						break;
						
			
					}
				}
						
				}	
			
		}
		//}while(condicion);
		
		
		return msgselect;
	}

	public void send(ACLMessage msg){
		this.send(msg);
	}
	
}
