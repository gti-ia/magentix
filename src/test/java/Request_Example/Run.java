package Request_Example;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;





public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


		
		try{
			
	    AgentsConecction.connect();
	        
		Hospital hos = new Hospital(new AgentID("HospitalAgent"));
		hos.start();

		witness tes = new witness(new AgentID("witnesAgent"));
		tes.start();
		
		}catch(Exception e){}

	}

}
