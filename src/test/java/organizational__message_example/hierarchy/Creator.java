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


		try
		{
		String result = omsProxy.acquireRole("participant", "virtual");
		logger.info("["+this.getName()+"] Result acquire role participant: "+result);
		
		this.initialize_scenario();
		
		
		this.send_request(4, 2);
		
		
		
		m.waiting();
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void finalize()
	{
		try
		{

		String result = omsProxy.deregisterRole("operador", "calculin");
		logger.info("["+this.getName()+"] Result leave role operador: "+result);
		result = omsProxy.deregisterRole("manager", "calculin");
		logger.info("["+this.getName()+"] Result leave role manager: "+result);
		omsProxy.deregisterRole("creador", "calculin");
		logger.info("["+this.getName()+"] Result leave role creador: "+result);
		result = omsProxy.deregisterUnit("calculin");
		logger.info("["+this.getName()+"] Result deregister unit calculin: "+result);
		result = omsProxy.leaveRole("participant", "virtual");
		logger.info("["+this.getName()+"] Result leave role participant: "+result);
		logger.info("["+this.getName()+" ] end execution!");
		
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}
	private void initialize_scenario()
	{
		try
		{
		String result = omsProxy.registerUnit("calculin", "hierarchy", "virtual", "creador");
		logger.info("["+this.getName()+"] Result register unit calculin: "+result);
		 
		result = omsProxy.registerRole("manager", "calculin",  "external", "public","supervisor");
		logger.info("["+this.getName()+"] Result register role subordinado: "+result);
		result = omsProxy.registerRole("operador", "calculin", "external", "public","subordinate");
		logger.info("["+this.getName()+"] Result register role operador: "+result);
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	
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
