package Request_Example;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
* Run class is an example of an agent that implements the FIPA Request protocol.
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
		// TODO Auto-generated method stub


		
		try{
		
			/**
			 * Connecting to Qpid Broker, default localhost.
			 */	
	    AgentsConnection.connect();
	        
	    /**
		 * Instantiating a Hospital agent
		 */
		Hospital hos = new Hospital(new AgentID("HospitalAgent"));
		

		/**
		 * Instantiating a witness agent
		 */
		witness tes = new witness(new AgentID("witnesAgent"));
		
		
		/**
		 * Execute the agents
		 */
		hos.start();
		tes.start();
		
		}catch(Exception e){}

	}

}
