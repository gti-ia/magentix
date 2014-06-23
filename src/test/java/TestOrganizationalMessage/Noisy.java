package TestOrganizationalMessage;

import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;


public class Noisy extends CAgent {


	OMSProxy omsProxy = new OMSProxy(this);
	int result=0;
	int expected=2;
	Monitor m = new Monitor();

	public Noisy(AgentID aid) throws Exception {
		super(aid);

	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {

		try
		{
			OMSProxy omsProxy = new OMSProxy(this);

			
			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

			this.initialize_scenario();

			result = omsProxy.acquireRole("Manager","External");
			logger.info("["+this.getName()+"] Result acquire role manager: "+result);

			this.send_request(1,7);
			
			omsProxy.allocateRole("Creator", "External", "Creator");
			
			result = omsProxy.leaveRole("Manager", "External");
			logger.info("["+this.getName()+"] Result leave role manager: "+result);
			
			result = omsProxy.leaveRole("Creator", "External");
			logger.info("["+this.getName()+"] Result leave role manager: "+result);
		
			result = omsProxy.leaveRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result leave role participant: "+result);

			logger.info("["+this.getName()+" ] end execution!");
			
			firstProcessor.ShutdownAgent();

		} catch(THOMASException e) {
			e.printStackTrace();
		} catch(Exception e1) {
			e1.printStackTrace();
		}

	}


	private void initialize_scenario()
	{

		try
		{
			String result = omsProxy.registerUnit("External", "flat", "virtual", "Creator");
			logger.info("["+this.getName()+"] Result register unit externa: "+ result);
			result = omsProxy.registerRole("Manager", "External", "internal", "private","member");
			logger.info("["+this.getName()+"] Result register role manager: "+ result);

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}
	}

	private void send_request(int n1, int n2)
	{
		try {
			ACLMessage msg = omsProxy.buildOrganizationalMessage("Calculator");
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


	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {

		System.out.println("["+firstProcessor.getMyAgent().getName()+"] end execution!");
	}


}
