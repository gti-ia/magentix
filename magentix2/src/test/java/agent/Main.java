package agent;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Main {

	public static void main(String[] args) {
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect("localhost", 5672,  "test", "guest", "guest", false);


		try {
			/**
			 * Instantiating a sender agent
			 */
			Sender senderAgent = new Sender(new AgentID("Sender"));

			/**
			 * Instantiating a consumer agent
			 */
			Consumer consumerAgent = new Consumer(new AgentID("Consumer"));

			/**
			 * Execute the agents
			 */
			consumerAgent.start();
			senderAgent.start();

		} catch (Exception e) {
			
		}
	}

}
