package omsTest.informMembers;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;


public class Creator extends QueueAgent {
	
	OMSProxy omsProxy = new OMSProxy(this);
	
	public Creator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {

		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		
		

		/** Parámetros correctos **/

		//1. El agente pruebas sólo juega el rol participant en la unidad virtual

		try
		{
		result = omsProxy.informMembers("noexiste", "","");
		
		for (ArrayList<String> s : result)
		{
			System.out.println("AgentName: "+ s.get(0)+ " RoleName: "+ s.get(1));	
		}
		}catch(THOMASException e)
		{
			System.out.println("Error: "+ e.getContent());
		}
		/*
		result = omsProxy.informMembers("equipo", "","");
		
		for (String s : result)
		{
			System.out.println("Equipo: "+ s);	
		}
		
		result = omsProxy.informMembers("plana", "","");
		
		for (String s : result)
		{
			System.out.println("Plana: "+ s);	
		}*/
	
	//-------------------------------------------------------------------------------	

/*		result = omsProxy.informMembers("jerarquia", "supervisor","");
		
		for (String s : result)
		{
			System.out.println("Jerarquia rol supervisor: "+ s);	
		}
		
		result = omsProxy.informMembers("equipo", "manager","");
		
		for (String s : result)
		{
			System.out.println("Equipo rol manager: "+ s);	
		}
		
		result = omsProxy.informMembers("plana", "miembro","");
		
		for (String s : result)
		{
			System.out.println("Plana rol miembro: "+ s);	
		}
		
		result = omsProxy.informMembers("jerarquia", "creador","");
		
		for (String s : result)
		{
			System.out.println("Jerarquia rol creador: "+ s);	
		}*/
		
		//-------------------------------------------------------------------------------
		
/*		result = omsProxy.informMembers("jerarquia", "","subordinate");
		
		for (String s : result)
		{
			System.out.println("Jerarquia position subordinate: "+ s);	
		}
		
		result = omsProxy.informMembers("equipo", "","member");
		
		for (String s : result)
		{
			System.out.println("Equipo position member: "+ s);	
		}
		
		result = omsProxy.informMembers("plana", "","member");
		
		for (String s : result)
		{
			System.out.println("Plana position member: "+ s);	
		}
		
		result = omsProxy.informMembers("equipo", "","creator");
		
		for (String s : result)
		{
			System.out.println("Jerarquia position creator: "+ s);	
		}*/
		
		
		//-------------------------------------------------------------------------------
/*		result = omsProxy.informMembers("jerarquia", "subordinado","subordinate");
		
		
		for (String s : result)
		{
			System.out.println("Jerarquia rol subordinado position subordinate: "+ s);	
		}
	
		result = omsProxy.informMembers("equipo", "manager","member");
		
			
		for (String s : result)
		{
			System.out.println("equipo rol manager position member: "+ s);	
		}
		
		result = omsProxy.informMembers("plana", "miembro","member");
		
	
		for (String s : result)
		{
			System.out.println("plana rol miembro position member: "+ s);	
		}
		
		result = omsProxy.informMembers("plana", "creador","creator");
		
		
		for (String s : result)
		{
			System.out.println("jerarquia rol creador position creator: "+ s);	
		}*/

	
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.informMembers("Noexiste", "subordinado","subordinate");
//		
//		result = omsProxy.informMembers("", "subordinado","subordinate");
//		
//		result = omsProxy.informMembers(null, "subordinado","subordinate");

	}
	
	
	


}
