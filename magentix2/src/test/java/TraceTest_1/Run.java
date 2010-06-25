package TraceTest_1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TracingService;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.core.TraceEvent;

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
			 * Instantiating the coordinator agent
			 */
			Coordinator coordinator = new Coordinator(new AgentID("qpid://coordinator@localhost:8080"));
			
			/**
			 * Instantiating the publisher agent
			 */
			Publisher publisher = new Publisher(new AgentID("qpid://publisher@localhost:8080"));

			/**
			 * Instantiating the subscriber agent
			 */
			Subscriber subscriber = new Subscriber(new AgentID("qpid://subscriber@localhost:8080"));

			/**
			 * Execute the agents
			 */
			coordinator.start();
			subscriber.start();
			publisher.start();
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
