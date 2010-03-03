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
		Participant participant2 = new Participant(new AgentID("participant2"));
		Participant participant3 = new Participant(new AgentID("participant3"));
		Participant participant4 = new Participant(new AgentID("participant4"));
		
		initiator.start();
		participant.start();
		participant2.start();
		participant3.start();
		participant4.start();
	}

}
