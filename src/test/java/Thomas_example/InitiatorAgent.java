package Thomas_example;

import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class InitiatorAgent extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	Monitor m = new Monitor();

	public InitiatorAgent(AgentID aid) throws Exception {
		super(aid);
	}

	protected void execution(CProcessor firstProcessor, ACLMessage welcomeMessage) {


		try
		{
			String result = omsProxy.acquireRole("participant", "virtual");
			logger.info("["+this.getName()+"] Result acquire role participant: "+result);

			this.initialize_scenario();

		}catch(THOMASException e)
		{
			e.printStackTrace();
		}

	}



	private void initialize_scenario()
	{
		try
		{
			omsProxy.registerUnit("calculator", "team", "virtual", "creator");

			omsProxy.registerUnit("school", "team", "virtual", "creator");

			omsProxy.registerRole("operation", "calculator",  "external", "public","member");

			omsProxy.registerRole("student", "school", "external", "public","member");


		}catch(THOMASException e)
		{
			System.out.println("["+this.getName()+"] "+ e.getContent());
		}

	}



	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		System.out.println("["+firstProcessor.getMyAgent().getName()+"] end execution!");	 

	}

}
