package _ContractNet_Example;



import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		

        
        try{
        //AgentsConecction.connect();	
        AgentsConecction.connect();
        
        
        for(int i=0;i<200;i++)
        {
        Concesionario concesionario = new Concesionario(new AgentID("Concesionario"+i));
        concesionario.start();
        }        Client cliente = new Client(new AgentID("Client"));
        cliente.start();
        }catch(Exception e){}

	}

}
