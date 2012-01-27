package organizational__message_example.hierarchy;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;


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
		ArrayList<Product> productores = new ArrayList<Product>();

		try {
			
			
			
			OMS oms = OMS.getOMS();
			SF sf = SF.getSF();
			
			oms.start();
			sf.start();
			
			
			/**
			 * Instantiating agents
			 */
			
			Creator iniAgent = new Creator(new AgentID("agente_creador"));
			Noisy ruiAgent = new Noisy(new AgentID("agente_ruidoso"));
			
			Addition sumAgent = new Addition(new AgentID("agente_suma"));
			Summation sumtAgent = new Summation(new AgentID("agente_sumatorio"));
			Exponentiation sumPotAgent = new Exponentiation(new AgentID("agente_sumaPotencias"));
			Display visAgent = new Display(new AgentID("agente_visor"));
			
			iniAgent.start();
			
			Thread.sleep(20 * 1000);//Waiting for system initialization
			
			ruiAgent.start();
			Product proAgent = new Product(new AgentID("agente_producto"));
			proAgent.start();
			sumAgent.start();
			visAgent.start();
			sumtAgent.start();
			sumPotAgent.start();
			
			Thread.sleep(60*1000);
			
			sumPotAgent.conclude();
			sumtAgent.conclude();
			visAgent.conclude();
			sumAgent.conclude();
			proAgent.conclude();
			ruiAgent.conclude();
			Thread.sleep(60*1000);
			iniAgent.finalize();


		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
