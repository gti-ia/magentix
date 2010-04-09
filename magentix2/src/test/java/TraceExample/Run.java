package TraceExample;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceManager;

/**
 * Run class is the typical example of the issuer/consumer. The sender BaseAgent
 * builds and sends a ACLMessage to the consumer BaseAgent. When the ACLMessage
 * arrives, the consumer BaseAgent displays the message on screen.
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
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
			 * Instantiating the trace manager agent
			 */
			//TraceManager tmAgent = new TraceManager(new AgentID("qpid://tm@localhost:8080"));
			//tmAgent.start();
			
			/**
			 * Instantiating a sender agent
			 */
			SenderAgent sender = new SenderAgent(new AgentID("qpid://emisor@localhost:8080"));

			/**
			 * Instantiating a consumer agent
			 */
			ConsumerAgent consumer = new ConsumerAgent(new AgentID("qpid://consumer@localhost:8080"));
			
			/**
			 * Instantiating an observer agent
			 */
			//ObserverAgent observer = new ObserverAgent(new AgentID("qpid://observer@localhost:8080"));

			/**
			 * Execute agents
			 */
			//observer.start();
			consumer.start();
			sender.start();
			

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
