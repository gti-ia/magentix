package _Query_Example;




import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;




public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		// TODO Auto-generated method stub
		
		
		try{
	        AgentsConnection.connect("gtiiaprojects2.dsic.upv.es");       // TODO add your handling code here:
        
   
        	
      
        Aeropuerto aeropuerto = new Aeropuerto(new AgentID("aeropuerto1"));
        aeropuerto.start();
        
        Viajante viajante = new Viajante(new AgentID("Viajeroooooooooooooooooooooooooooooooooooo"));
        viajante.start();
        }catch(Exception e){}
	}

}
