package organizational__message_example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import Thomas_Example.CleanDB;
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
			
			
			//Lanzamos el agente OMS
			
			OMS oms = OMS.getOMS();
			SF sf = SF.getSF();
			
			oms.start();
			sf.start();
			
			CleanDB clean = new CleanDB();

			clean.initialize_db();
			
			/**
			 * Instantiating a sender agent
			 */
			

//			/**
//			 * Instantiating a consumer agent
//			 */
			Agente_Creador iniAgent = new Agente_Creador(new AgentID("agente_creador"));
			Agente_Ruidoso ruiAgent = new Agente_Ruidoso(new AgentID("agente_ruidoso"));
			Agente_Producto proAgent = new Agente_Producto(new AgentID("agente_producto"));
			Agente_Suma sumAgent = new Agente_Suma(new AgentID("agente_suma"));
			Agente_Sumatorio sumtAgent = new Agente_Sumatorio(new AgentID("agente_sumatorio"));
			Agente_Visor visAgent = new Agente_Visor(new AgentID("agente_visor"));
			
			iniAgent.start();
			Thread.sleep(10 * 1000);//Esperamos a que se inicialize la estructura
			ruiAgent.start();
			proAgent.start();
			sumAgent.start();
			visAgent.start();
			sumtAgent.start();
//			
			

		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
