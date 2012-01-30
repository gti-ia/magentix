package organizational__message_example.team;

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

		try {
			
			
			//Lanzamos los agentes OMS y SF
			
			OMS oms = OMS.getOMS();
			SF sf = SF.getSF();
			
			oms.start();
			sf.start();
			

			
			/**
			 * Instantiating agents
			 */
			
			Creator iniAgent = new Creator(new AgentID("agente_creador"));
			Noisy ruiAgent = new Noisy(new AgentID("agente_ruidoso"));
			Product proAgent = new Product(new AgentID("agente_producto"));
			Addition sumAgent = new Addition(new AgentID("agente_suma"));
			Summation sumtAgent = new Summation(new AgentID("agente_sumatorio"));
			Display visAgent = new Display(new AgentID("agente_visor"));
			
			iniAgent.start();
			Thread.sleep(10 * 1000);//Esperamos a que se inicialize la estructura
			ruiAgent.start();
			proAgent.start();
			sumAgent.start();
			visAgent.start();
			sumtAgent.start();
		
			Thread.sleep(60*1000);
			
			

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
