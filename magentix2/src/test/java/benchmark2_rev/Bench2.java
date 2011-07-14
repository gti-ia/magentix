package benchmark2_rev;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Bench2 {

	static final Lock lock = new ReentrantLock();
	static final Condition notReady  = lock.newCondition();

	public static void main(String[] args){
		int ntotal = Integer.parseInt(args[0].toString());
		int nmsg = Integer.parseInt(args[1].toString());
		int nmsgpad = Integer.parseInt(args[2].toString());
		int tmsg = Integer.parseInt(args[3].toString());
		int tipoAgente = Integer.parseInt(args[4].toString()); //1-> controlador
																//2-> emisor
																//3-> receptor
		int numeroInicialAgente = Integer.parseInt(args[5].toString());
		int numeroAgentes = Integer.parseInt(args[6].toString());

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Bench2.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();
		Emisor emisor;
		Controlador controlador;
		Receptor receptor;
		for(int i=numeroInicialAgente; i<numeroInicialAgente + numeroAgentes; i++){
			try{
				if(tipoAgente == 1){
					controlador = new Controlador(new AgentID("controlador"), ntotal);
					controlador.deactivateTraceService();
					controlador.start();
				}
				else if(tipoAgente == 2){
					emisor = new Emisor(new AgentID("emisor"+i), ntotal, nmsg, nmsgpad, tmsg, i);
					emisor.deactivateTraceService();
					emisor.start();
				}
				else{
					receptor = new Receptor(new AgentID("receptor"+i));
					receptor.deactivateTraceService();
					receptor.start();
				}
			}
			catch (Exception e){
				logger.error("Error en creación de agentes " + e.getMessage());
			}
		}
	}
}
