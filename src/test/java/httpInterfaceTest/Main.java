package httpInterfaceTest;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.HttpInterface;

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
		
		HttpInterface httpInterface = new HttpInterface();
		MarketAgent marketAgent = new MarketAgent(new AgentID("MarketAgent"));
		marketAgent.start();
		httpInterface.execute();
	}

}
