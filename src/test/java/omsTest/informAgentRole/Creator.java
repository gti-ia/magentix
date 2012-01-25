package omsTest.informAgentRole;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class Creator extends QueueAgent {
	
	OMSProxy omsProxy = new OMSProxy(this);
	
	public Creator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {

		ArrayList<String> result = new ArrayList<String>();
		
		

		/** Parámetros correctos **/

		//1. El agente pruebas sólo juega el rol participant en la unidad virtual


		result = omsProxy.informAgentRole("pruebas2");
		
		for (String s : result)
		{
			System.out.println("Result inform agent role: "+ s);	
		}
		
		//2. El agente pruebas juega los roles participant de virtual y subordinado de Jerarquía.
		
//		result = omsProxy.informAgentRole("pruebas2");
//		
//		for (String s : result)
//		{
//			System.out.println("Result inform agent role: "+ s);	
//		}
		
		//3.  El agente pruebas juega los roles participant de virtual y miembro de Equipo.

//		result = omsProxy.informAgentRole("pruebas2");
//		
//		for (String s : result)
//		{
//			System.out.println("Result inform agent role: "+ s);	
//		}
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.informAgentRole("noexiste");
//		
//		for (String s : result)
//			{
//				System.out.println("Result inform agent role: "+ s);	
//			}
//		
//		result = omsProxy.informAgentRole("");
//		
//		result = omsProxy.informAgentRole(null);
	}
	
	
	


}
