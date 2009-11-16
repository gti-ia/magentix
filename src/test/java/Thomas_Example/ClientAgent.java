package Thomas_Example;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class ClientAgent extends QueueAgent {

	public ClientAgent(AgentID aid) throws Exception {

		super(aid);

	}

	
	//We create the class that will make us the agent proxy oms, facilitates access to the methods of the OMS
	OMSProxy serviciosOMS = new OMSProxy();
	
	//We create the class that will make us the agent proxy sf,  facilitates access to the methods of the SF
	SFProxy sfservice = new SFProxy();
	
	public String result;

	public void execute() {

		ArrayList<String> results = new ArrayList<String>();
		ArrayList<AgentID> agents = new ArrayList<AgentID>();

		try{
		
		//acquired the member role at the organization
		result = serviciosOMS.AcquireRole(this, "member", "virtual");

		result = serviciosOMS.AcquireRole(this,"customer", "travelagency");
		
		
		
		
		//waiting that the agentBroadcast registered service SearchCheapHotel
		do{
			results = sfservice.searchService(this, "SearchCheapHotel");	
		}while(results.get(0).equals("null"));

		
		
		
		agents = sfservice.getProcess(this, results.get(0));

		for (AgentID agent : agents)
			System.out
					.println(this.getAid().name+" agents who have the service SearchCheapHotel: "
							+ agent.name);

		String agentProvider = sfservice.getProfile(this,results.get(0));
		
		
		
		ArrayList<String> arg = new ArrayList<String>();
		
		arg.add("FirstParam");
		arg.add("SecondParam");
		arg.add("ThirdParam");
		
		
		//call the service SearchCheapHotel
		sfservice.genericService(this,agents.get(0),agentProvider,"http://localhost:8080/sfservices/THservices/owl/owls/SearchCheapHotelProcess.owl", arg);
		
		
		}catch(Exception e){
			
			logger.error(e.getMessage());
		}
		
		

	}

}
