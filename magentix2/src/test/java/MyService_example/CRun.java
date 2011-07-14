package MyService_example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.CleanBD;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;

/**
 * Run class is an example of an agents that connection to THOMAS organization.
 * 
 * 
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author Sergio Pajares - spajares@dsic.upv.es
 * 
 */

public class CRun {

	public static void main(String[] args) {

		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(CRun.class);

		/**
		 * Connecting to Qpid Broker, default localhost.
		 */
		AgentsConnection.connect();

		CleanBD clean = new CleanBD();

		clean.clean_database();

		try {

			OMS agenteOMS = OMS.getOMS();
		    agenteOMS.start();

		    SF agenteSF = SF.getSF();
		    agenteSF.start();
			
			CAgentProvider cAgentProvider = new CAgentProvider(new AgentID(
					"CAgentProvider"));
			CAgentClient cAgentClient = new CAgentClient(new AgentID(
					"CAgentClient"));
			
			cAgentProvider.start();
			Monitor m = new Monitor();
			m.waiting(15 * 1000);

			cAgentClient.start();

		} catch (Exception e) {
			logger.error(e.getMessage());

		}

	}

}
