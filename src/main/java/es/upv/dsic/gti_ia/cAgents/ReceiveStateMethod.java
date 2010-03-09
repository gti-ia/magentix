package es.upv.dsic.gti_ia.cAgents;

import es.upv.dsic.gti_ia.core.ACLMessage;

public abstract class ReceiveStateMethod {

	protected abstract String run(CProcessor myProcessor,
			ACLMessage receivedMessage);

}
