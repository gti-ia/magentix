package organizational__message_example.hierarchy;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Creator extends QueueAgent {
	
	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();
	
	public Creator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {



		omsProxy.acquireRole("member", "virtual");
		
		
		this.initialize_scenario();
		
		omsProxy.acquireRole("creador", "calculin");
		
		this.send_request(4, 2);
		
		
		
		m.waiting();
		
		
	}
	
	public void finalize()
	{
		omsProxy.leaveRole("creador", "calculin");
		omsProxy.deregisterRole("operador", "calculin");
		omsProxy.deregisterRole("manager", "calculin");
		omsProxy.deregisterRole("creador", "calculin");
		
		omsProxy.deregisterUnit("calculin");
		
		omsProxy.leaveRole("member", "virtual");
		
		logger.info("[ "+this.getName()+" ] end execution!");
	}
	private void initialize_scenario()
	{
		omsProxy.registerUnit("calculin", "hierarchy", "unidad_calculin", "virtual");
		
		omsProxy.registerRole("creador", "calculin", "supervisor", "public","member"); 
		omsProxy.registerRole("manager", "calculin",  "supervisor", "public","member");
		omsProxy.registerRole("operador", "calculin", "subordinate", "public","member");
	
	}
	
	public void conclude()
	{
		m.advise();
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
