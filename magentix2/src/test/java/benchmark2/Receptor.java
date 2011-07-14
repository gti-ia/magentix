package benchmark2;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class Receptor extends SingleAgent{
	ACLMessage msgcont;
	
	public Receptor(AgentID aid) throws Exception {
		super(aid);
		msgcont = new ACLMessage(ACLMessage.UNKNOWN);
		msgcont.addReceiver(new AgentID("controlador"));
		msgcont.setContent("Ready");
		send(msgcont);
	}

	public void execute(){		
		while(true){
			ACLMessage msg = null;
			try {
				msg = this.receiveACLMessage();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			AgentID emisor = msg.getSender();
			//System.out.println("Rebut missatge des de: "+emisor.getName());
			msg.setSender(this.getAid());
			msg.clearAllReceiver();
			msg.setReceiver(emisor);
			//doWait(1000);
			send(msg);
			//System.out.println("Enviat missatge des de: "+getName());
		}
	}
}
