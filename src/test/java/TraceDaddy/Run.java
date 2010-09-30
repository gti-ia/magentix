package TraceDaddy;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.trace.TraceManager;

public class Run {
	public static void main(String[] args) {
		Boy olderSon, youngerSon;
		Daddy dad;
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
			 * Instantiating the Trace Manager
			 */
			TraceManager tm = new TraceManager(new AgentID("tm"));

			System.out.println("INITIALIZING...");
			
			/**
			 * Instantiating Dad
			 */
			dad = new Daddy(new AgentID("qpid://MrSmith@localhost:8080"));
			
			/**
			 * Instantiating sons
			 */
			olderSon = new Boy(new AgentID("qpid://Timmy@localhost:8080"), 7, dad.getAid());
			youngerSon = new Boy(new AgentID("qpid://Bobby@localhost:8080"), 5, dad.getAid());
			
			/**
			 * Execute the agents
			 */
			dad.start();
			olderSon.start();
			youngerSon.start();
			
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}
}
