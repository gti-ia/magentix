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
	OMSProxy OMSservices = new OMSProxy();
	
	//We create the class that will make us the agent proxy sf,  facilitates access to the methods of the SF
	SFProxy SFservices = new SFProxy();
	
	public String result;

	public void execute() {

		ArrayList<String> results = new ArrayList<String>();
		ArrayList<AgentID> agents = new ArrayList<AgentID>();

		try{
		
		//acquired the member role at the organization
		result = OMSservices.AcquireRole(this, "member", "virtual");
		System.out.println("[ClientAgent]Acquire Role member return: "+result+"\n");
		result = OMSservices.AcquireRole(this,"customer", "travelagency");
		System.out.println("[ClientAgent]Acquire Role customer return: "+result+"\n");

		
		//waiting that the agentBroadcast registered service SearchCheapHotel

		//waiting that the agentBroadcast registered service SearchCheapHotel
		do{
			results = SFservices.searchService(this, "SearchCheapHotel");
		}while(results.size()==0);
	
		

		
		
		agents = SFservices.getProcess(this, results.get(0));
		System.out.println("[ClientAgent]agents that offered SearchCheapHotel service: "+ agents.size()+"\n");
		for (AgentID agent : agents)
			System.out
					.println("[ClientAgent] agents who have the service SearchCheapHotel: "
							+ agent.name+"\n");

		String agentProvider = SFservices.getProfile(this,results.get(0));
		System.out.println("[ClientAgent]get Profile return: "+ agentProvider+"\n");
		
		
		ArrayList<String> arg = new ArrayList<String>();
		
		arg.add("FirstParam");
		arg.add("SecondParam");
		arg.add("ThirdParam");
		
		
		//call the service SearchCheapHotel
		SFservices.genericService(this,agents.get(0),agentProvider,"http://localhost:8080/sfservices/THservices/owl/owls/SearchCheapHotelProcess.owl", arg);
		
		
		}catch(Exception e){
			
			logger.error(e.getMessage());
		}
		
		

	}

}
