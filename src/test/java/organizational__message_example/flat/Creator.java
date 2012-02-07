package organizational__message_example.flat;

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

			logger.info("Result acquire role: "+ result);

			this.initialize_scenario();

			this.send_request(4, 2);

			m.waiting();
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}

	public void conclude()
	{
		m.advise();
	}



	public void finalize()
	{
		try
		{
			String result = omsProxy.deregisterRole("operador", "calculin");
			System.out.println("["+this.getName()+"] Result leave role operador: "+result);

			result = omsProxy.deregisterRole("manager", "calculin");
			System.out.println("["+this.getName()+"] Result leave role manager: "+result);
			
			result = omsProxy.deregisterUnit("calculin");
			System.out.println("["+this.getName()+"] Result deregister unit calculin: "+result);

			result = omsProxy.leaveRole("participant", "virtual");
			System.out.println("["+this.getName()+"] Result leave role participant: "+result);

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
			String result = omsProxy.registerUnit("calculin", "flat", "virtual", "creador");

			logger.info("Result register unit: "+ result);

			result = omsProxy.registerRole("manager", "calculin", "external", "public","member");
			logger.info("Result register role: "+ result);
			result = omsProxy.registerRole("operador", "calculin", "external", "public","member");
			logger.info("Result register role: "+ result);
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

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
