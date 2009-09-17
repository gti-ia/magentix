package EjemploRequest;

import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.fipa.*;

public class Principal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Connection con = new Connection();
        con.connect("localhost", 5672, "test", "guest", "guest",false);
        
       Hospital hos = new Hospital(new AgentID("OMS","qpid","localhost",""),con);
       hos.start();
       
       
       Testigo tes = new Testigo(new AgentID("Testigo","qpid","localhost",""),con);
       
       tes.start();
       
       
        

	}

}
