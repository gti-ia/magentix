package benchmark1_rev;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Bench1 {

	static final Lock lock = new ReentrantLock();
	static final Condition notReady  = lock.newCondition();

	public static void main(String[] args) {
		int ntotal = Integer.parseInt(args[0].toString());
		int nmsg = Integer.parseInt(args[1].toString());
		int nmsgpad = Integer.parseInt(args[2].toString());
		int tmsg = Integer.parseInt(args[3].toString());

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Bench1.class);

		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		Emisor emisor;
		Receptor receptor;
		try {
			for(int i = 0; i < ntotal; i++){
				receptor = new Receptor(new AgentID("receptor"+i));
				receptor.deactivateTraceService();
				receptor.start();
			}
			for(int i = 0; i < ntotal; i++){
				emisor = new Emisor(new AgentID("emisor"+i), ntotal, nmsg, nmsgpad, tmsg, i);
				emisor.deactivateTraceService();
				emisor.start();
			}
		} catch (Exception e) {
				logger.error("Error  " + e.getMessage());
		}					
	}
}
