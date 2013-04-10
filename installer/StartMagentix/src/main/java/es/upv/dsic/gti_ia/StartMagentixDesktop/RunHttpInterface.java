package es.upv.dsic.gti_ia.StartMagentixDesktop;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.BridgeAgentInOut;
import es.upv.dsic.gti_ia.core.BridgeAgentOutIn;
import es.upv.dsic.gti_ia.core.HttpInterface;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.trace.TraceManager;

/**
 *Run class is an example of an agents that connection to thomas organization.
 * 
 * 
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 * 
 */

public class RunHttpInterface {


	public static void main(String[] args) {

		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(RunHttpInterface.class);

		/**
		 * Connecting to Qpid Broker, default localhost.
		 */
		AgentsConnection.connect();


		try {

			
			/**
			 * Instantiating a HttpInterface service
			 */
			HttpInterface httpInterface = new HttpInterface();
			httpInterface.execute();

		} catch (Exception e) {
			logger.error(e.getMessage());

		}

	}

}
