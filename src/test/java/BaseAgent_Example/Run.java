package BaseAgent_Example;

import org.apache.log4j.Logger;
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
		Logger logger = Logger.getLogger(Run.class);
		try {
			EmisorAgent agente = new EmisorAgent(new AgentID(
					"qpid://emisor@localhost:8080"));
														
			ConsumerAgent agente2 = new ConsumerAgent(new AgentID(
					"consumer"));
			agente2.start();
			agente.start();
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
