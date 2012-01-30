package organizational__message_example.team;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Noisy extends QueueAgent {


	OMSProxy omsProxy = new OMSProxy(this);
	int result=0;
	int expected=2;
	Monitor m = new Monitor();

	public Noisy(AgentID aid) throws Exception {
		super(aid);

	}

	public void execute() {


		omsProxy.acquireRole("participant", "virtual");


		this.initialize_scenario();

		omsProxy.acquireRole("manager","externa");


		this.send_request(1,7);

		m.waiting(); 

		this.send_result(result+"");

	}


	public void conclude()
	{
		m.advise();
	}
	
	public void finalize()
	{
	
		String result = omsProxy.leaveRole("manager", "externa");
		System.out.println("["+this.getName()+"] Result leave role manager: "+result);
		
		result = omsProxy.leaveRole("creador", "externa");
		System.out.println("["+this.getName()+"] Result leave role creador: "+result);
		
		result = omsProxy.deregisterRole("manager", "externa");
		System.out.println("["+this.getName()+"] Result deregister role manager: "+result);
		
		result = omsProxy.deregisterRole("creador", "externa");
		System.out.println("["+this.getName()+"] Result deregister role creador: "+result);
		
		result = omsProxy.deregisterUnit("externa");
		System.out.println("["+this.getName()+"] Result deregister unit externa: "+result);
		
		result = omsProxy.leaveRole("participant", "virtual");
		System.out.println("["+this.getName()+"] Result leave role participant: "+result);
		
		logger.info("["+this.getName()+" ] end execution!");
	}
	
	private void add_and_advise(ACLMessage msg)
	{
		result+=Integer.parseInt(msg.getContent());
		expected--;


		if (expected == 0)
		{

			m.advise();
		}
	}

	public void onMessage(ACLMessage msg)
	{



		if (msg.getSender().name.equals("agente_suma") || msg.getSender().name.equals("agente_producto"))
		{
			if (!msg.getContent().contains("OK"))
			{
				this.add_and_advise(msg);
			}
		}
		if (msg.getSender().name.equals("OMS") || msg.getSender().name.equals("SF")) 
			super.onMessage(msg);

	}

	private void initialize_scenario()
	{

		omsProxy.registerUnit("externa", "flat", "virtual", "creador");
		omsProxy.registerRole("manager", "externa",  "external", "public","member");
		 
	
	}

	private void send_request(int n1, int n2)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(InteractionProtocol.FIPA_REQUEST);
			msg.setProtocol(InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage("ACL");
			msg.setContent(n1+" "+ n2);
			
			System.out.println("[ "+this.getName()+" ] Sending a message!");
			send(msg);
		} catch (THOMASException e) {
			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}

	private void send_result(String content)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("calculin");
			msg.setPerformative(ACLMessage.INFORM);
			msg.setLanguage("ACL");
			msg.setContent(content);


			send(msg);
		} catch (THOMASException e) {
			System.out.println("[ "+this.getName()+" ] "+ e.getContent());

		}
	}


}
