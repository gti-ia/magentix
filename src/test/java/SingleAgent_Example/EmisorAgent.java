package SingleAgent_Example;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class EmisorAgent extends BaseAgent {

	public EmisorAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		System.out.println("Arranco, soy " + getName());
		AgentID receiver = new AgentID();
		receiver.protocol = "qpid";
		receiver.name = "agenteconsumidor";
		receiver.host = "localhost";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);
		msg.setSender(this.getAid());
		msg.setLanguage("ACL");
		msg.setContent("Hola, soy agente " + getName());
		send(msg);
		send(msg);
	}

}
