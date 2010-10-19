package Thomas_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;




import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.CleanBD;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

/**
 *Run class is an example of an agents that connection to thomas organization.
 * 
 * 
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author Sergio Pajares - spajares@dsic.upv.es
 * 
 */

public class Run {


	public static void main(String[] args) {

		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker, default localhost.
		 */
		AgentsConnection.connect();


		/**
		 * Clean database
		 */

		CleanBD clean = new CleanBD();
		InitializeThomasScenario its = new InitializeThomasScenario();

		clean.clean_database();
		its.initialize_db();

		/**
		 * Connecting to Qpid Broker, default localhost.
		 */
		AgentsConnection.connect();

		try {

			/**
			 * Instantiating a OMS and FS agent's
			 */

			OMS agenteOMS = OMS.getOMS();
			agenteOMS.start();

			SF agenteSF = SF.getSF();
			agenteSF.start();


			/**
			 * Execute the agents
			 */

			AgentPayee payeeAgent = new AgentPayee(new AgentID("agentPayee"));

			AgentProvider providerAgent = new AgentProvider(new AgentID("providerAgent"));

			AgentAnnouncement registerAgent = new AgentAnnouncement(new AgentID("registerAgent"));

			AgentClient clientAgent = new AgentClient(new AgentID("clientAgent"));

			registerAgent.start();
			payeeAgent.start();

			Monitor m = new Monitor();
			m.waiting(15 * 1000);
			providerAgent.start();
			m.waiting(5 * 1000);

			clientAgent.start();

		} catch (Exception e) {
			logger.error(e.getMessage());

		}

	}

}
