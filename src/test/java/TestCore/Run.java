package TestCore;

import org.apache.log4j.xml.DOMConfigurator;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Run class is the typical example of the issuer/consumer. The sender
 * SingleAgent builds and sends a ACLMessage to the consumer SingleAgent. When
 * the ACLMessage arrives, the consumer SingleAgent displays the message on
 * screen.
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author David Fernández - dfernandez@dsic.upv.es
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
		//Logger logger = Logger.getLogger(Run.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating a sender agent
			 */
			SenderAgent2 agente = new SenderAgent2(new AgentID(
					"qpid://emisor@localhost:8080"));

			/**
			 * Instantiating a consumer agent
			 */
			ConsumerAgent2 agente2 = new ConsumerAgent2(new AgentID("qpid://consumer@localhost:8080"));

			/**
			 * Execute the agents
			 */
			agente2.start();
			agente.start();

		} catch (Exception e) {
			//logger.error("Error  " + e.getMessage());
			System.out.println(e.getMessage());
		}
	}

}
