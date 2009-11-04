package _BridgeAgent_Example;

import org.apache.log4j.xml.DOMConfigurator;
import _BaseAgent_Example.AgenteConsumidor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;
import es.upv.dsic.gti_ia.core.BridgeAgentInOut;
import es.upv.dsic.gti_ia.core.BridgeAgentOutIn;

/**
 * @author Ricard, Sergio
 * 
 */
public class PruebaPasarela2 {

	public static void main(String[] args) {
		// Connection con = new Connection();
		// con.connect("gtiiaprojects2.dsic.upv.es", 5672, "test", "guest",
		// "guest",false);
		// http por que el agente va a enviar un mensaje hacia el exterior
		try {

			DOMConfigurator.configure("loggin.xml");
			AgentsConecction.connect("gtiiaprojects2.dsic.upv.es");
			BridgeAgentInOut agenteInOut = new BridgeAgentInOut(new AgentID(
					"BridgeAgentInOut", "qpid", "localhost", "8080"));
			/*
			 * agenteInOut interactua con agenteOutIn
			 */
			BridgeAgentOutIn agenteOutIn = new BridgeAgentOutIn(new AgentID(
					"BridgeAgentOutIn", "qpid", "localhost", "8080"));

			/*
			 * agente1 interactua con el agenteInOut
			 */
			AgenteSergio agente1 = new AgenteSergio(new AgentID("agentehola",
					"qpid", "localhost", "8080"));
			/*
			 * agente2 interactua con agenteOutIn
			 */
			AgenteConsumidor agente2 = new AgenteConsumidor(new AgentID(
					"agenteconsumidor", "qpid", "localhost", "8080"));

			agenteInOut.start();
			agenteOutIn.start();

			agente2.start();
			agente1.start();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
