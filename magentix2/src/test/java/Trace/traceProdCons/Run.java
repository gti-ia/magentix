package traceProdCons;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceManager;

/**
 * Simple issuer/consumer example. This example is based on trace events instead of
 * being based on ACL messages. So, a SenderAgent builds and sends trace events and
 * a ConsumerAgent subscribe to them in order to receive them. When the consumer agent
 * receives a trace event, it displays its content on the screen
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
			TraceManager tm = new TraceManager(new AgentID("qpid://tm@localhost:8080"));
			
			/**
			 * Instantiating the sender agent
			 */
			SenderAgent sender = new SenderAgent(new AgentID("qpid://sender@localhost:8080"));

			/**
			 * Instantiating the consumer agent
			 */
			ConsumerAgent consumer = new ConsumerAgent(new AgentID("consumer"));

			/**
			 * Execute the agents
			 */
			consumer.start();
			sender.start();

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
