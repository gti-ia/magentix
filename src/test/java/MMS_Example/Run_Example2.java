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
public class Run_Example2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run_Example2.class);

		/**
		 * Connecting to Qpid Broker
		 */


		try {

			/**
			 * Instantiating a consumer agent
			 */
			ConsumerAgent agent = new ConsumerAgent(new AgentID("consumer_bob"));

			/**
			 * Execute the agents
			 */
			agent.start();
	

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
