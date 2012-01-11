package organizational__message_example.team;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Creator extends QueueAgent {
	
	OMSProxy omsProxy = new OMSProxy(this);
	
	public Creator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {



		omsProxy.acquireRole("member", "virtual");
		
		
		this.initialize_scenario();
		
		omsProxy.acquireRole("creador", "calculin");
		
		
		this.send_request(4, 2);
		omsProxy.acquireRole("operador", "calculin");
		Monitor m = new Monitor();
		m.waiting();
		
		
	}
	
	public void onMessage(ACLMessage msg) {

		System.out.println("Me llega un mensaje de: "+msg.getSender() + " para: "+  msg.getReceiver());
			super.onMessage(msg);

	}
	
	private void initialize_scenario()
	{
		omsProxy.registerUnit("calculin", "team", "unidad_calculin", "virtual");
	
		omsProxy.registerRole("creador", "calculin",   "member", "public","member"); 
		omsProxy.registerRole("manager", "calculin",  "member", "public","member");
		omsProxy.registerRole("operador", "calculin",  "member", "public","member");
	
	}
	
	public void send_request(int n1, int n2)
	{
		try {
			//Build the organizational message
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			
			//Refill the organization message with the protocol and the message content
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(n1+" y "+ n2);

			//Send the message
			send(msg);
		} catch (THOMASException e) {//Caught the thomas exception
			
			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}


}
