package MMS_Example;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.util.Logger;

import MMS_Example.ConsumerAgent;
import MMS_Example.SenderAgent;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 */
public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		//Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.SecureConnect();
		AgentsConnection.connect();
		
		try {
			/**
			 * Instantiating a MMS agent
			 */
			MMS agentMMS = new MMS(new AgentID(
					"MMS"));

			agentMMS.start();
			
			/**
			 * Instantiating a sender agent
			 */
			SenderAgent agente = new SenderAgent(new AgentID("sender"),"/home/joabelfa/wokspace/c++/certificates/clientV2/keystore.jks","key123","SunX509");

			/**
			 * Instantiating a consumer agent
			 */
			ConsumerAgent agente2 = new ConsumerAgent(new AgentID("consumer"),"/home/joabelfa/wokspace/c++/certificates/certificates/keystore.jks","key123","SunX509");
;

			/**
			 * Execute the agents
			 */
			agente2.start();
			agente.start();
			

		} catch (Exception e) {
			//logger.error("Error  " + e.getMessage());
		}
	}

}
