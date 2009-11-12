package ContractNet_Example;



import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;



/**
 * Run class is an example of an agent that implements the FIPA Contract-Net protocol.
 *
 * 
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author Sergio Pajares - spajares@dsic.upv.es
 */

public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		

        
        try{
        	
        /**
		* Connecting to Qpid Broker, default localhost.
		*/	
        AgentsConnection.connect();
        
        
        for(int i=0;i<20;i++)
        {
        /**
		 * Instantiating a consumer agent
		 */
        Concessionaire concesionario = new Concessionaire(new AgentID("Autos"+i));
        /**
		 * Execute the agents
		 */
        concesionario.start();
        }      
        
    	/**
		 * Instantiating a sender agent
		 */
        Client cliente = new Client(new AgentID("Client"));
        /**
		 * Execute the agents
		 */
        cliente.start();
        }catch(Exception e){}

	}

}
