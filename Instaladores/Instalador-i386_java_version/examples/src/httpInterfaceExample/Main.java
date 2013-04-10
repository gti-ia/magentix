package httpInterfaceExample;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();
		
		
		MarketAgent marketAgent = new MarketAgent(new AgentID("MarketAgent"));
		marketAgent.start();
		
	}

}
