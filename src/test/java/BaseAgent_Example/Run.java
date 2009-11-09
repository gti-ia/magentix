package BaseAgent_Example;

import org.apache.log4j.xml.DOMConfigurator;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;

public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DOMConfigurator.configure("loggin.xml");
		AgentsConecction.connect("gtiiaprojects2.dsic.upv.es");
		try {
			EmisorAgent agente = new EmisorAgent(new AgentID(
					"qpid://agentehola@localhost:8080"));// , "qpid",
															// "localhost","8080"));
			ConsumerAgent agente2 = new ConsumerAgent(new AgentID(
					"agenteconsumidor"));
			agente2.start();
			agente.start();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

}
