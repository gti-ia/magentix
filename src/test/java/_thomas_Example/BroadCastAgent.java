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
		
		String resultado;
		
		SFProxy sfservice = new SFProxy();
		
		SFAgentDescription sfAgentDescription = new SFAgentDescription("http://localhost:8080/broadcastservices/owl/owls/","http://localhost:8080/broadcastservices/owl/owls/");
		
		/**
		suggestedServiceCalls[0]=OMSLocation+"AcquireRoleProcess.owl RoleID= UnitID=virtual AgentID=BroadcasterAgent";	    
		suggestedServiceCalls[1]=SFLocation+"SearchServiceProcess.owl SearchServiceInputServicePurpose=BroadcastWS";
		suggestedServiceCalls[2]=SFLocation+"GetProfileProcess.owl GetProfileInputServiceID=http://localhost:8080/broadcastservices/owl/owls/BroadcastWSProfile.owl#BroadcastWSProfile";
		suggestedServiceCalls[3]=OMSLocation+"RegisterUnitProcess.owl UnitID=news Type=congregation Goal=receivenews ParentUnitID=virtual";
		suggestedServiceCalls[4]=OMSLocation+"RegisterRoleProcess.owl RoleID=broadcaster Accessibility=external Position=member Visibility=public Inheritance=member UnitID=news";
		suggestedServiceCalls[5]=OMSLocation+"AcquireRoleProcess.owl RoleID=broadcaster UnitID=news AgentID=BroadcasterAgent";
		suggestedServiceCalls[6]=SFLocation+"RegisterProfileProcess.owl RegisterProfileInputServiceGoal=BroadcastWS RegisterProfileInputServiceProfile=http://localhost:8080/broadcastservices/owl/owls/BroadcastWSProfile.owl";
		suggestedServiceCalls[7]=SFLocation+"RegisterProcessProcess.owl RegisterProcessInputServiceID=http://localhost:8080/broadcastservices/owl/owls/BroadcastWSProfile.owl#BroadcastWSProfile RegisterProcessInputServiceModel=http://localhost:8080/broadcastservices/owl/owls/BroadcastWSProcess.owl";
		**/
		
		resultado = serviciosOMS.AcquireRole(this, "member","virtual");
		
		System.out.println("Resultado"+resultado);
		
	    //****************** RegisterUnit *************************
        serviciosOMS.RegisterUnit(this, "news", "congregation", "receivenews", "virtual");
        
        //*********************************************************
        
        
        
		//****************** RegisterRole ***************
		 
		//serviciosOMS.RegisterRole(agent, RegisterRoleInputRoleID, UnitID, Accessibility, Position, Visibility, Inheritance);
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
	        System.out.println("Agentes que tiene ese servicio: "+ agent.protocol);
		
		
		
		
    }
	
}
