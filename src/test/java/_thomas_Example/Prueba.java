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

	     OMS agenteOMS;
	     SF agenteSF;
	     
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
	
        BroadCastAgent agent = new BroadCastAgent(new AgentID("BroadCastAgent"));
        agent.start();
    
    	}catch(Exception e){}     

	}
	
}


