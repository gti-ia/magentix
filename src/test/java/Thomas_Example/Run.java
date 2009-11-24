package Thomas_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



import es.upv.dsic.gti_ia.organization.CleanBD;



import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

/**
*Run class is an example of an agents that connection to thomas organization.
*
* 
* 
* @author Joan Bellver - jbellver@dsic.upv.es
* @author Sergio Pajares - spajares@dsic.upv.es
*
*/



public class Run {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {

	     DOMConfigurator.configure("configuration/loggin.xml");
	     Logger logger = Logger.getLogger(Run.class);

	     
	     /**
	      * Clean database 
	      */
	     CleanBD clean = new CleanBD();
	     clean.clean_database();
	 	
	
	     
	 	/**
			 * Connecting to Qpid Broker, default localhost.
			 */	
	     AgentsConnection.connect();       
        
        
      
        try
        {
        
        /**
    	* Instantiating a OMS and FS agent's
    	*/
        OMS agenteOMS = OMS.getOMS();
        agenteOMS.start();
      
  
        SF agenteSF = SF.getSF();
        agenteSF.start();
	
        
        /**
		 * Instantiating a BroadCast agent
		 */
        BroadCastAgent broadCastagent = new BroadCastAgent(new AgentID("BroadCastAgent"));
        
        /**
		 * Instantiating a ClientAgent agent
		 */
        ClientAgent clientAgent = new ClientAgent(new AgentID("ClientAgent"));
        
        /**
		 * Execute the agents
		 */
        
        broadCastagent.start();
        clientAgent.start();
    
    	}catch(Exception e){
    		logger.error(e.getMessage());
    		
    	}     

	}
	
}


