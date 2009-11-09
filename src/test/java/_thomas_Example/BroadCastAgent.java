package _thomas_Example;



import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.architecture.*;
import es.upv.dsic.gti_ia.organization.*;
import java.util.ArrayList;


public class BroadCastAgent extends QueueAgent {

	public BroadCastAgent(AgentID aid)throws Exception{
	
 	super(aid);
}
	
	
	public void execute()
    {
		OMSProxy serviciosOMS = new OMSProxy();
		
		String result;
		
		SFProxy sfservice = new SFProxy();
		
		SFAgentDescription sfAgentDescription = new SFAgentDescription("http://localhost:8080/broadcastservices/owl/owls/","http://localhost:8080/broadcastservices/owl/owls/");
		
		result = serviciosOMS.AcquireRole(this, "member","virtual");
		
		System.out.println("Resultado"+result);
		
	    //****************** RegisterUnit *************************
        serviciosOMS.RegisterUnit(this, "news", "congregation", "receivenews", "virtual");
        //*********************************************************
        
        
        
		//****************** RegisterRole ***************
		serviciosOMS.RegisterRole(this, "broadcaster","news" , "external", "member", "public", "member");
		//*********************************************
   
	
		serviciosOMS.AcquireRole(this, "broadcaster", "news");
		
		
        //************ RegisterProfile *****************
        
		sfAgentDescription.setServiceGoal("BroadcastWS");
		if (sfservice.registerProfile(this,sfAgentDescription))
			System.out.println("El register Profile nos ha devuelto: "+  sfAgentDescription.getID());
        
		
        
		if (sfservice.registerProcess(this, sfAgentDescription))
			System.out.println("El register Process nos ha devuelto: "+  sfAgentDescription.getImplementationID());
		
		
			serviciosOMS.RegisterRole(this, "subscriptor","news" , "external", "member", "public", "member");
		
			serviciosOMS.AcquireRole(this,"subscriptor", "news");
	        //************ SearchService *****************
	        ArrayList<String> valores = new ArrayList<String>();
	        
	        valores = sfservice.searchService(this, "BroadcastWS");
	        
	        System.out.println("Valores devueltos: "+ valores.get(0));
	        
	        
	        
	        //************************************************


	        
	        //************ GetProfile *****************
	        
	        ArrayList<AgentID> agentes = new ArrayList<AgentID>();
	        
	        agentes = sfservice.getProcess(this,valores.get(0));

	        for(AgentID agent : agentes)
	        System.out.println("Agentes que tiene ese servicio: "+ agent.name);
		
		
		
		
    }
	
}
