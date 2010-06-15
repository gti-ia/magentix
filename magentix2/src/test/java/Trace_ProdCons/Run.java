package Trace_ProdCons;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Simple producer/consumer example. This example is based on trace events instead of
 * being based on ACL messages. So, the SenderAgent builds and sends trace events and
 * the ConsumerAgent subscribes to them in order to receive them. When the consumer agent
 * receives a trace event, it displays its content on the screen.
 * 
 * Before ending, the Consumer agent unsubscribes from the corresponding event type.
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
			//TraceManager tm = new TraceManager(new AgentID("qpid://tm@localhost:8080"));
			TraceManager tm = new TraceManager(new AgentID("tm"));
			
			/**
			 * Instantiating the sender agent
			 */
			SenderAgent sender = new SenderAgent(new AgentID("qpid://sender@localhost:8080"));
			//SenderAgent sender = new SenderAgent(new AgentID("sender"));

			/**
			 * Instantiating the consumer agent
			 */
			//ConsumerAgent consumer = new ConsumerAgent(new AgentID("qpid://consumer@localhost:8080"));
			//ConsumerAgent consumer = new ConsumerAgent(new AgentID("consumer"));

			/**
			 * Execute the agents
			 */
			//consumer.start();
			//sender.start();

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
