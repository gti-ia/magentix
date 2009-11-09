package SingleAgent_Example;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConecction;

public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DOMConfigurator.configure("loggin.xml");
		AgentsConecction.connect("gtiiaprojects2");
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

		// CopyOfAgenteHola agente3 = new CopyOfAgenteHola(new
		// AgentID("agtente3", "http", "localhost","8080"),con);
		// agente3.start();
	}

	// public static void main(String[] args) {
	// Connection con = new Connection();
	// con.connect("gtiiaprojects.dsic.upv.es", 5672, "test", "guest",
	// "guest",false);
	// try{
	// AgenteHola agente = new AgenteHola(new AgentID("agentehola", "qpid",
	// "localhost","8080"),con);
	// AgenteConsumidor agente2 = new AgenteConsumidor(new
	// AgentID("agenteconsumidor", "qpid", "localhost","8080"),con);
	// agente2.start();
	// agente.start();
	// }catch(Exception e){
	// System.out.println("Error");
	// }
}
