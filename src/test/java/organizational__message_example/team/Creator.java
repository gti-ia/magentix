package organizational__message_example.team;

import java.util.ArrayList;

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
			omsProxy.acquireRole("participant", "virtual");


			this.initialize_scenario();



			

			this.send_request(4, 2);
			
			omsProxy.acquireRole("manager", "calculin");
			
			m.waiting(); //Waiting the shut down message of the agent summation
			
			System.out.println("Joint unit: " + omsProxy.jointUnit("externa", "calculin"));

			boolean searching = true;
			do{
				int quantity = omsProxy.quantityMembers("calculin", "", "member");
			
				if (quantity > 2)
					m.waiting(3 * 1000);
				else
					searching = false;
			}while(searching);
			
			omsProxy.leaveRole("manager", "calculin");
			
			ArrayList<ArrayList<String>> members = omsProxy.informMembers("calculin", "", "member");
			
			for(ArrayList<String> member : members)
			{
				
				omsProxy.deallocateRole(member.get(1), "calculin", member.get(0));
			}
			
			omsProxy.deregisterRole("operador", "calculin");
			

			omsProxy.deregisterRole("manager", "calculin");
			
			
	
			
			do
			{
				m.waiting(3 * 1000);
	
				members = omsProxy.informMembers("externa", "manager", "");
			}while(members.contains("agente_ruidoso"));

			
	

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}


	}

	public void conclude()
	{
		m.advise();
	}

	public void onMessage(ACLMessage msg) {



		if (msg.getContent().equals("shut down")) //Messages that come from the organization
		{
		
			m.advise(); //When a new message arrives, it advise the main thread

		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF"))
			super.onMessage(msg);

	}

	public void finalize()
	{
		try
		{

			omsProxy.deregisterUnit("externa");
			
			
			omsProxy.deregisterUnit("calculin");
			

			omsProxy.leaveRole("participant", "virtual");
			

			System.out.println("[ "+this.getName()+" ] end execution!");

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}

	private void initialize_scenario()
	{

		try
		{
			omsProxy.registerUnit("calculin", "team", "virtual", "creador");

			omsProxy.registerRole("manager", "calculin",  "internal", "private","member");
			omsProxy.registerRole("operador", "calculin",  "external", "public","member");
			
			omsProxy.allocateRole("manager", "calculin", "agente_visor");
			
			
			omsProxy.allocateRole("manager", "calculin", "agente_sumatorio");
			

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


}
