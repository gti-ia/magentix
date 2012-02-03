package omsTest.informAgentRole;

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

		try{
			result = omsProxy.informAgentRole("pruebas");

			for (ArrayList<String> s : result)
			{

				System.out.println("RoleName: "+ s.get(0)+ " UnitName: "+ s.get(1));

			}
		} catch (THOMASException e) {

			System.out.println("Error:" + e.getContent());
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
