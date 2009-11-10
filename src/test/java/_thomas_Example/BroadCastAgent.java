package _thomas_Example;



import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFAgentDescription;
import es.upv.dsic.gti_ia.organization.SFProxy;

import java.util.ArrayList;


public class BroadCastAgent extends QueueAgent {

	public BroadCastAgent(AgentID aid)throws Exception{
	
 	super(aid);
}
	
	
	public void execute()
    {
		OMSProxy serviciosOMS = new OMSProxy();
		SFProxy sfservice = new SFProxy();
		
		SFAgentDescription servicio1 = new SFAgentDescription("http://localhost:8080/broadcastservices/owl/owls/","http://localhost:8080/broadcastservices/owl/owls/");
		SFAgentDescription servicio2 = new SFAgentDescription("http://localhost:8080/sfservices/THservices/owl/owls/","http://localhost:8080/sfservices/THservices/owl/owls/");
		
		String result;
		
		
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
        
		servicio1.setServiceGoal("BroadcastWS");
		servicio2.setServiceGoal("SearchCheapHotel");
		
		if (sfservice.registerProfile(this,servicio1))
			System.out.println("El register Profile nos ha devuelto: "+  servicio1.getID());
        
		
        
		if (sfservice.registerProcess(this, servicio1))
			System.out.println("El register Process nos ha devuelto: "+  servicio1.getImplementationID());
		
		
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
		
		
		
	        //intento registrar el searchCheapHotel con un rol q no es el adecuado
	        
	    	if (sfservice.registerProfile(this,servicio2))
				System.out.println("El register Profile nos ha devuelto: "+  servicio1.getID());
		
    }
	
}
