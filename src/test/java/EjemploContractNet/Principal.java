package EjemploContractNet;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.fipa.AgentID;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		
		// TODO Auto-generated method stub
		
		
		
        Connection con = new Connection();
        con.connect("gtiiaprojects.dsic.upv.es", 5672, "test", "guest", "guest",false);        // TODO add your handling code here:
        
        
        Concesionario concesionario = new Concesionario(new AgentID("Concesionario","qpid","localhost",""),con);
        concesionario.start();
        
        Concesionario concesionario1 = new Concesionario(new AgentID("Concesionario1","qpid","localhost",""),con);
        concesionario1.start();
        
        Cliente cliente = new Cliente(new AgentID("cliente","qpid","localhost",""),con);
        cliente.start();

	}

}
