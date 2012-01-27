package organizational__message_example.flat;

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



		String result = omsProxy.acquireRole("participant", "virtual");
		
		System.out.println("Result acquire role: "+ result);
		
		this.initialize_scenario();
		
		this.send_request(4, 2);
		
		
		
	}
	
	private void initialize_scenario()
	{
		String result = omsProxy.registerUnit("calculin", "flat", "virtual", "creador");
		
		System.out.println("Result register unit: "+ result);
	
		result = omsProxy.registerRole("manager", "calculin", "external", "public","member");
		System.out.println("Result register role: "+ result);
		result = omsProxy.registerRole("operador", "calculin", "external", "public","member");
		System.out.println("Result register role: "+ result);
	
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
			msg.setContent(n1+" "+ n2);

			//Send the message
			send(msg);
		} catch (THOMASException e) {//Caught the thomas exception
			
			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}


}
