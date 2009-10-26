package es.upv.dsic.gti_ia.magentix2;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;



public class Prueba_Log4j {

		
//		 Define una variable estática que tiene una referencia
//		 a una instancia de Logger llamada "MyApp".
		static Logger logger = Logger.getLogger(Prueba_Log4j.class);
		public static void main(String[] args) {
//		Configuracion XML
		DOMConfigurator.configure("loggin.xml");
		logger.info("Entrando en la aplicación.");
//		Bar bar = new Bar();
		logger.error("Ocurrió una excepcion");
//		bar.doIt();
//		Action.hola();
		logger.info("Saliendo de la aplicación.");
		}
	

}
