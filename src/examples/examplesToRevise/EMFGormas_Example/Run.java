/**
 * This class has been generated using Gormas2Magentix tool.
 * 
 * @author Mario Rodrigo - mrodrigo@dsic.upv.es
 * 
 */
package EMFGormas_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Run {

	public static void main(String[] args) {

		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker, default localhost.
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Execute the agent
			 */
			GodAgent godAgent = new GodAgent(new AgentID("godAgent"));
			godAgent.start();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	} // End main
} // End of class

