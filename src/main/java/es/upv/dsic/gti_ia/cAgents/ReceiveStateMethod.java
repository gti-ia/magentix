package es.upv.dsic.gti_ia.cAgents;

public interface ReceiveStateMethod {

	public String run(CProcessor myProcessor,
			ACLMessage receivedMessage);

}
