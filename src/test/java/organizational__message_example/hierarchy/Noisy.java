package organizational__message_example.hierarchy;

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

		try
		{
			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

			this.initialize_scenario();

			result = omsProxy.acquireRole("manager","externa");
			logger.info("["+this.getName()+"] Result acquire role manager: "+result);

			this.send_request(1,7);
			
			omsProxy.allocateRole("creador", "externa", "agente_creador");

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
			String result = omsProxy.leaveRole("manager", "externa");
			logger.info("["+this.getName()+"] Result leave role manager: "+result);
			
			result = omsProxy.leaveRole("creador", "externa");
			logger.info("["+this.getName()+"] Result leave role manager: "+result);
		
			result = omsProxy.leaveRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result leave role participant: "+result);

			System.out.println("[ "+this.getName()+" ] end execution!");
		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
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

		try
		{
			String result = omsProxy.registerUnit("externa", "flat", "virtual", "creador");
			logger.info("["+this.getName()+"] Result register unit externa: "+ result);
			result = omsProxy.registerRole("manager", "externa", "internal", "private","member");
			logger.info("["+this.getName()+"] Result register role manager: "+ result);

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
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
