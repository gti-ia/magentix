package reorganizeTest;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.ReorganizingAgent;

public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating a sender agent
			 */
			ReorganizingAgent reorganizeAgent = new ReorganizingAgent(new AgentID("reorganizer"));
			TestAgent testAgent = new TestAgent(new AgentID("testAgent"));
			
			reorganizeAgent.start();
			testAgent.start();

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}

	}

}
