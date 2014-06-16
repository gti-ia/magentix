package TestOrganizationalMessage;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;


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
			
			
			
//			OMS oms = new OMS();
//			SF sf = new SF();
//			
//			oms.start();
//			sf.start();
			
			
			/**
			 * Instantiating agents
			 */
			
			Creator iniAgent = new Creator(new AgentID("Creator"));
			Noisy ruiAgent = new Noisy(new AgentID("Noisy"));
			
			Addition sumAgent = new Addition(new AgentID("Addition"));
			Summation sumtAgent = new Summation(new AgentID("Summation"));
			Display visAgent = new Display(new AgentID("Display"));
			Product proAgent = new Product(new AgentID("Product"));
			
			iniAgent.start();
			Monitor m = new Monitor();
			
			m.waiting(5 * 1000);
			ruiAgent.start();
			proAgent.start();
			sumAgent.start();
			visAgent.start();
			sumtAgent.start();



		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
