package _thomas_Example;

import org.apache.log4j.xml.DOMConfigurator;


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

	     DOMConfigurator.configure("configuration/loggin.xml");
	     
	     
	     CleanBD limpiar = new CleanBD();
	     limpiar.clean_database();
	 	
	
	     
	     //AgentsConecction.connect("gtiiaprojects2.dsic.upv.es");
	     AgentsConecction.connect();       
        
        
      
        try
        {
        
        //We launch the agents OMS and SF
        OMS agenteOMS = OMS.getOMS();
        agenteOMS.start();
      
  
        SF agenteSF = SF.getSF();
        agenteSF.start();
	
        
        //We launch our agent
        BroadCastAgent agent = new BroadCastAgent(new AgentID("BroadCastAgent"));
        agent.start();

        ClientAgent agentClient = new ClientAgent(new AgentID("ClientAgent"));
        agentClient.start();
    
    	}catch(Exception e){}     

	}
	
}


