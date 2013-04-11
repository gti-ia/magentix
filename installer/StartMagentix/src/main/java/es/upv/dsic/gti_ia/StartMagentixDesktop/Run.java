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

   	private static final Object lock = new Object();


    	public static void await() throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}
	
	public static void main(String[] args) {

		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker, default localhost.
		 */
		AgentsConnection.connect();


		try {

			/**
			 * Starting OMS and SF agent's
			 */

	                OMS oms = new OMS(new AgentID("OMS"));
	                SF sf =  new SF(new AgentID("SF"));

	                oms.start();
	                sf.start();

 
			/**
			 * Starting a BridgeAgentInOut SingleAgent
			 */
			///BridgeAgentInOut agentInOut = new BridgeAgentInOut(new AgentID(
			///		"BridgeAgentInOut", "qpid", "localhost", "5000"));
			///agentInOut.start();
			/**
			 * Starting a BridgeAgentOutIn SingleAgent
			 */
			///BridgeAgentOutIn agentOutIn = new BridgeAgentOutIn(new AgentID(
			///		"BridgeAgentOutIn", "qpid", "localhost", "5000"));
			///agentOutIn.start();
			


		
			//Wait until is killed
			await();			
			

		} catch (Exception e) {
			logger.error(e.getMessage());

		}

	}

}
