package Thomas_example;


/**
 * In this class the agent initiator is represented. 
 * Functions:
 *  -	Acquire role participant inside the unit virtual.
 * 	-	Initialize the organization. This organization is formed by two units (calculator and school) 
 * 		and two roles (operator and student)
 */
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class InitiatorAgent extends CAgent {

	OMSProxy omsProxy = new OMSProxy(this);
	

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
			
			String result = omsProxy.registerUnit("calculator", "team", "virtual", "creator");
			logger.info("["+this.getName()+"] Result register unit calculator: "+result);

			result = omsProxy.registerUnit("school", "flat", "virtual", "creator");
			logger.info("["+this.getName()+"] Result register unit school: "+result);
			
			result = omsProxy.registerRole("operation", "calculator",  "external", "public","member");
			logger.info("["+this.getName()+"] Result register role operation: "+result);
			
			result = omsProxy.registerRole("student", "school", "external", "public","member");
			logger.info("["+this.getName()+"] Result register role student: "+result);

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
