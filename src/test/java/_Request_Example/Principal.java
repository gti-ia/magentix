package _Request_Example;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;





public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub


		try{
	        AgentsConecction.connect("gtiiaprojects2.dsic.upv.es");
	        
		Hospital hos = new Hospital(
				new AgentID("Hospital"));
		hos.start();

		Testigo tes = new Testigo(new AgentID("Testigo"));

		tes.start();
		
		}catch(Exception e){}

	}

}
