package _BridgeAgent_Example;

import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;
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
		receiver.protocol = "http";
		receiver.name = "agenteconsumidor";
		receiver.host = "localhost";
		receiver.port = "5000";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);
		msg.setSender(this.getAid());
		msg.setLanguage("ACL");
		msg.setContent("Hola, soy agente " + getName());
		send(msg);
	}

	public void onMessage(Session ssn, MessageTransfer xfr) {
		System.out.println("Mensaje: " + xfr);
	}
}
