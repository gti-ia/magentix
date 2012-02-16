package organizational__message_example.hierarchy;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
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
			
			omsProxy.acquireRole("manager", "calculin");
			
			m.waiting(); //Waiting the shut down message of the agent summation
			
			omsProxy.jointUnit("externa", "calculin");

			boolean searching = true;
			do{
				int quantity = omsProxy.quantityMembers("calculin", "", "supervisor");
			
				if (quantity > 1)
					m.waiting(3 * 1000);
				else
					searching = false;
			}while(searching);
			
			omsProxy.leaveRole("manager", "calculin");
			
			ArrayList<ArrayList<String>> members = omsProxy.informMembers("calculin", "", "subordinate");
			
			for(ArrayList<String> member : members)
			{
				
				omsProxy.deallocateRole(member.get(1), "calculin", member.get(0));
			}
			
			result = omsProxy.deregisterRole("operador", "calculin");
			logger.info("["+this.getName()+"] Result leave role operador: "+result);

			result = omsProxy.deregisterRole("manager", "calculin");
			logger.info("["+this.getName()+"] Result leave role manager: "+result);
			
		//	ArrayList<ArrayList<String>> agentRole;
			
			do
			{
				m.waiting(3 * 1000);
				//agentRole = omsProxy.informAgentRole("agente_ruidoso");
				members = omsProxy.informMembers("externa", "manager", "");
			}while(members.contains("agente_ruidoso"));

			
			result = omsProxy.deregisterUnit("externa");
			logger.info("["+this.getName()+"] Result deregister unit calculin: "+result);
			
			result = omsProxy.deregisterUnit("calculin");
			logger.info("["+this.getName()+"] Result deregister unit calculin: "+result);

			result = omsProxy.leaveRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result leave role participant: "+result);
			System.out.println("["+this.getName()+" ] end execution!");
			
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}

	
	private void initialize_scenario()
	{
		try
		{
			omsProxy.registerUnit("calculin", "hierarchy", "virtual", "creador");
			

			omsProxy.registerRole("manager", "calculin",  "internal", "private","supervisor");
			
			
			omsProxy.registerRole("operador", "calculin", "external", "public","subordinate");
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_visor");
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_sumatorio");
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_sumaPotencias");
			
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
			msg.setContent(n1+" y "+ n2);

			//Send the message
			send(msg);
		} catch (THOMASException e) {//Caught the thomas exception

			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}
	
	public void onMessage(ACLMessage msg)
	{
		
		
		
		if (msg.getContent().equals("shut down")) //Messages that come from the organization
		{
		
			m.advise(); //When a new message arrives, it advise the main thread

		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}

}
