package TestThomas;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;


public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();


		try {
			
			
			/**
			 * Instantiating agents
			 */
			
			InitiatorAgent iniAgent = new InitiatorAgent(new AgentID("InitiatorAgent"));
			Addition addAgent = new Addition(new AgentID("AdditionAgent"));
			James jamAgent = new James(new AgentID("JamesAgent"));
			Product proAgent = new Product(new AgentID("ProductAgent"));
			
			
			/**
			 * Execute the agents
			 */
			iniAgent.start();
			Monitor m = new Monitor();
			
			/**
			 * Waiting the initialization
			 */
			m.waiting(5 * 1000);
			proAgent.start();
			addAgent.start();
			
			m.waiting(15 * 1000);
			jamAgent.start();

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
