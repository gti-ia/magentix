package conversaciones;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class FipaRequest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect("127.0.0.1");
				
		Initiator initiator = new Initiator(new AgentID("initiator"));
		Participant participant = new Participant(new AgentID("participant"));
		
		initiator.start();
		participant.start();
	}

}
