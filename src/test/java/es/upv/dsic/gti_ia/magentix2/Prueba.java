package es.upv.dsic.gti_ia.magentix2;
import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.fipa.AgentID;

public class Prueba {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection con = new Connection();
        con.connect("localhost", 5672, "test", "guest", "guest",false);
		AgenteHola agente = new AgenteHola(new AgentID("agentehola", "qpid", "localhost","8080"),con);
		AgenteConsumidor agente2 = new AgenteConsumidor(new AgentID("agenteconsumidor", "qpid", "localhost","8080"),con);
		agente2.start();
		agente.start();
	}
}
