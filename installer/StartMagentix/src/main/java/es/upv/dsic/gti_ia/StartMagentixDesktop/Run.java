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
			 * Instantiating a OMS and FS agent's
			 */

			OMS agenteOMS = OMS.getOMS();
			agenteOMS.start();

			SF agenteSF = SF.getSF();
			agenteSF.start();
			


			

			
			/**
			 * Instantiating a BridgeAgentInOut SingleAgent
			 */
			BridgeAgentInOut agentInOut = new BridgeAgentInOut(new AgentID(
					"BridgeAgentInOut", "qpid", "localhost", "5000"));
			agentInOut.start();
			/**
			 * Instantiating a BridgeAgentOutIn SingleAgent
			 */
			BridgeAgentOutIn agentOutIn = new BridgeAgentOutIn(new AgentID(
					"BridgeAgentOutIn", "qpid", "localhost", "5000"));
			agentOutIn.start();
			
			
	
			
			
			

		} catch (Exception e) {
			logger.error(e.getMessage());

		}

	}

}
