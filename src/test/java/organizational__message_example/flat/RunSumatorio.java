package organizational__message_example.flat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;


public class RunSumatorio {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(RunSumatorio.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			
			
			Summation sumtAgent = new Summation(new AgentID("agente_sumatorio"));
			Display visAgent = new Display(new AgentID("agente_visor"));
			
			
			for (int i=0; i < 2000;i++)
			{
				Product proAgent = new Product(new AgentID("agente_producto"+i));
				proAgent.start();
			}
	
			

			visAgent.start();
			sumtAgent.start();

		
			

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
