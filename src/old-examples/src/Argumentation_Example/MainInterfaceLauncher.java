package Argumentation_Example;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class MainInterfaceLauncher {

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
		
		ArgInterfaceAgent interfaceAgent = new ArgInterfaceAgent(new AgentID("ArgInterfaceAgent"));
		interfaceAgent.start();
		
	}

}
