package TraceBasic;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Simple battery of requests to make sure the Trace Manager does not let erroneous
 * publication/unpublication nor subscription/unsubscription
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
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
			tm.start();
			
			/**
			 * Instantiating the sender agent
			 */
			Publisher publisher = new Publisher(new AgentID("qpid://sender@localhost:8080"));

			/**
			 * Instantiating the consumer agent
			 */
			Subscriber subscriber = new Subscriber(new AgentID("qpid://consumer@localhost:8080"));

			/**
			 * Execute the agents
			 */
			subscriber.start();
			publisher.start();

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
