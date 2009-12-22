package conversaciones;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Conversaciones{

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect("127.0.0.1");
				
		Agente2 agenteIni1 = new Agente2(new AgentID("agenteIni1"));
		Agente2 agenteIni2 = new Agente2(new AgentID("agenteIni2"));
		Agente2 agenteIni3 = new Agente2(new AgentID("agenteIni3"));
		Agente2 agenteIni4 = new Agente2(new AgentID("agenteIni4"));
		Agente2 agenteIni5 = new Agente2(new AgentID("agenteIni5"));
		Agente1 agenteRes = new Agente1(new AgentID("agenteRes"));
				
		agenteIni1.start();
		agenteIni2.start();
		agenteIni3.start();
		agenteIni4.start();
		agenteIni5.start();
		agenteRes.start();
	}
}