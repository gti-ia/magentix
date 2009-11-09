package _thomas_Example;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.organization.CleanBD;



import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;



public class Prueba {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 Connection con;
	     OMS agenteOMS;
	     SF agenteSF;
	     Cliente clientAgent;
	     Concesionario providerAgent;
	     
	     DOMConfigurator.configure("loggin.xml");
	     
	     
	     CleanBD limpiar = new CleanBD();
	     limpiar.clean_database();
	 	
		//creamos conexion
	     AgentsConecction.connect();       // TODO add your handling code here:
        
        
      //creamos el oms 
        try
        {
        	
        agenteOMS = OMS.getOMS();
        agenteOMS.start();
      
  
        agenteSF = SF.getSF();
        agenteSF.start();
	
        /*
        providerAgent = new Concesionario(new AgentID("ProviderAgent", "qpid", "localhost",""),con);
        providerAgent.start();
        
       
        clientAgent = new Cliente(new AgentID("ClientAgent", "qpid", "localhost",""),con);
        clientAgent.start();
       	 */
        BroadCastAgent agent = new BroadCastAgent(new AgentID("BroadCastAgent"));
        agent.start();
    
    	}catch(Exception e){}
       
		
		    
        
        
               
      
       
       
       
        

	/*
	 if (agentes.size() <= 0) {
         System.out.println("No existen ventas de coches.");
     } else {
    	ACLMessage requestMsg = new ACLMessage(ACLMessage.REQUEST);
 		requestMsg.setSender(clientAgent.getAid());
 		requestMsg.setContent("HOLA");
 		requestMsg.setProtocol(InteractionProtocol.FIPA_REQUEST);
 		for(String agente : agentes)
 		{
 			requestMsg.add_receiver(new AgentID(agente,"qpid","localhost",""));
 		}
 		clientAgent.send(requestMsg);
 		
 		MessageTemplate template = new MessageTemplate(InteractionProtocol.FIPA_REQUEST);
 		
 
 		ACLMessage msgResponder =  clientAgent.receiveACLMessageB(template);
 		
 		if (msgResponder!=null)
 			System.out.println("CONTENIDO"+ msgResponder.getContent());
 		
       msgResponder =  clientAgent.receiveACLMessageB(template);
 		
 		if (msgResponder!=null)
 			System.out.println("CONTENIDO"+ msgResponder.getContent());
     	
     }
*/
	 

	 
	 
	 
	 
        

	}
	
}


