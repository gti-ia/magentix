package TestContractNet;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * Run class is an example of an agent that implements the FIPA Contract-Net
 * protocol.
 * 
 * 
 * 
 * @author Joan Bellver - jbellver@dsic.upv.es
 * @author Sergio Pajares - spajares@dsic.upv.es
 */

public class Run {

	static CountDownLatch finished = new CountDownLatch(1);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		try {

			/**
			 * Connecting to Qpid Broker, default localhost.
			 */
			AgentsConnection.connect();

			for (int i = 0; i < 5; i++) {
				/**
				 * Instantiating a consumer agent
				 */
				Concessionaire concesionario = new Concessionaire(new AgentID(
						"Autos" + i), finished);
				/**
				 * Execute the agents
				 */
				concesionario.start();
			}

			/**
			 * Instantiating a sender agent
			 */
			Client cliente = new Client(new AgentID("Client"), finished);
			/**
			 * Execute the agents
			 */
			cliente.start();
		} catch (Exception e) {

			logger.error(e.getMessage());
		}

	}

}
