package EjemploContractNet;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.core.AgentID;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		// TODO Auto-generated method stub
		
		
		
        Connection con = new Connection();
        con.connect("gtiiaprojects2.dsic.upv.es", 5672, "test", "guest", "guest",false);        // TODO add your handling code here:
        
        try{
        Concesionario concesionario = new Concesionario(new AgentID("Concesionario","qpid","localhost",""),con);
        concesionario.start();
        
        Concesionario concesionario1 = new Concesionario(new AgentID("Concesionario1","qpid","localhost",""),con);
        concesionario1.start();
        
        Concesionario concesionario10 = new Concesionario(new AgentID("Concesionario10","qpid","localhost",""),con);
        concesionario10.start();
        
        Concesionario concesionario11 = new Concesionario(new AgentID("Concesionario11","qpid","localhost",""),con);
        concesionario11.start();
        
        Concesionario concesionario12 = new Concesionario(new AgentID("Concesionario12","qpid","localhost",""),con);
        concesionario12.start();
        
        Concesionario concesionario13 = new Concesionario(new AgentID("Concesionario13","qpid","localhost",""),con);
        concesionario13.start();
        
        Concesionario concesionario14 = new Concesionario(new AgentID("Concesionario14","qpid","localhost",""),con);
        concesionario14.start();
        
        Concesionario concesionario15 = new Concesionario(new AgentID("Concesionario15","qpid","localhost",""),con);
        concesionario15.start();
        
        Concesionario concesionario16 = new Concesionario(new AgentID("Concesionario16","qpid","localhost",""),con);
        concesionario16.start();
        
        Concesionario concesionario17 = new Concesionario(new AgentID("Concesionario17","qpid","localhost",""),con);
        concesionario17.start();
        
        Cliente cliente = new Cliente(new AgentID("cliente","qpid","localhost",""),con);
        cliente.start();
        }catch(Exception e){}

	}

}
