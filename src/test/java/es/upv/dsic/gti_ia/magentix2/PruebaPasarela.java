package es.upv.dsic.gti_ia.magentix2;
import org.apache.qpid.transport.Connection;

import es.upv.dsic.gti_ia.fipa.AgentID;

public class PruebaPasarela {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection con = new Connection();
        con.connect("rilpefo.dsic.upv.es", 5672, "test", "guest", "guest",false);
        //http por que el agente va a enviar un mensaje hacia el exterior
        BridgeAgentInOut agente2 = new BridgeAgentInOut(new AgentID("agentepasarela", "qpid", "localhost","8080"),con);
        AgenteSergio agente = new AgenteSergio(new AgentID("agentehola", "qpid", "localhost","8080"),con);
		
		agente2.start();
		agente.start();
		
	//	CopyOfAgenteHola agente3 = new CopyOfAgenteHola(new AgentID("agtente3", "http", "localhost","8080"),con);
	//	agente3.start();
	}
}
