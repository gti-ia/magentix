package MMS_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import es.upv.dsic.gti_ia.core.AgentID;


/**
 * Run class is the typical example of the issuer/consumer. The sender BaseAgent
 * builds and sends a ACLMessage to the consumer BaseAgent. When the ACLMessage
 * arrives, the consumer BaseAgent displays the message on screen. This test is 
 * in secure mode.
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class Run_Example1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run_Example1.class);

		/**
		 * Connecting to Qpid Broker
		 */


		try {
			/**
			 * Instantiating a sender agent
			 */
			SenderAgent agente = new SenderAgent(new AgentID(
					"qpid://emisor@localhost:8080"));

			/**
			 * Instantiating a consumer agent
			 */
			ConsumerAgent agente2 = new ConsumerAgent(new AgentID("consumer"));

			/**
			 * Execute the agents
			 */
			agente2.start();
			agente.start();

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
