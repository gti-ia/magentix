package es.upv.dsic.gti_ia.magentix2;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;



public class AgenteHola extends BaseAgent {
	
	public AgenteHola(AgentID aid) throws Exception {
		super(aid);
	}
	
	public void execute(){
		System.out.println("Arranco, soy "+getName());
		AgentID receiver = new AgentID();
		receiver.protocol = "qpid";
		receiver.name = "agenteconsumidor";
		receiver.host = "localhost";
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setReceiver(receiver);
		msg.setSender(this.getAid());
		msg.setLanguage("ACL");
		msg.setContent("Hola, soy agente "+ getName());
		send(msg);
	}
	
	public void onMessage(ACLMessage msg){
		System.out.println("Mensaje: " + msg.getContent());	
	}
}
