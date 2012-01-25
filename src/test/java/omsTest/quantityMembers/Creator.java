package omsTest.quantityMembers;

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

		int result;



		/** Parámetros correctos **/

		//1. El agente pruebas sólo juega el rol participant en la unidad virtual


//				result = omsProxy.quantityMembers("jerarquia", "", "");
//				
//				System.out.println("Resultado jerarquia: "+ result);
//				
//				result = omsProxy.quantityMembers("equipo", "", "");
//				
//				System.out.println("Resultado equipo: "+ result);
//				
//				
//				result = omsProxy.quantityMembers("plana", "", "");
//				
//				System.out.println("Resultado plana: "+ result);

		//-------------------------------------------------------------------------------	
/*				result = omsProxy.quantityMembers("jerarquia", "supervisor", "");

		System.out.println("Resultado jerarquia rol supervisor: "+ result);

		result = omsProxy.quantityMembers("equipo", "manager", "");

		System.out.println("Resultado equipo rol manager: "+ result);


		result = omsProxy.quantityMembers("plana", "miembro", "");

		System.out.println("Resultado plana rol miembro: "+ result);

		result = omsProxy.quantityMembers("jerarquia", "creador", "");

		System.out.println("Resultado jerarquia rol creador: "+ result);*/
		 
		//-------------------------------------------------------------------------------	
/*		result = omsProxy.quantityMembers("jerarquia", "", "subordinate");

		System.out.println("Resultado jerarquia position subordinate: "+ result);

		result = omsProxy.quantityMembers("equipo", "", "member");

		System.out.println("Resultado equipo rol member: "+ result);


		result = omsProxy.quantityMembers("plana", "", "member");

		System.out.println("Resultado plana rol member: "+ result);

		result = omsProxy.quantityMembers("jerarquia", "", "creator");

		System.out.println("Resultado jerarquia rol creador: "+ result);*/
		
		//-------------------------------------------------------------------------------	
//		result = omsProxy.quantityMembers("jerarquia", "subordinado", "subordinate");
//
//		System.out.println("Resultado jerarquia position subordinate: "+ result);
//
//		result = omsProxy.quantityMembers("equipo", "manager", "member");
//
//		System.out.println("Resultado equipo rol member: "+ result);
//
//
//		result = omsProxy.quantityMembers("plana", "miembro", "member");
//
//		System.out.println("Resultado plana rol member: "+ result);
//
//		result = omsProxy.quantityMembers("plana", "creador", "creator");
//
//		System.out.println("Resultado jerarquia rol creador: "+ result);

		/** Parámetros incorrectos **/

		result = omsProxy.quantityMembers("Noexiste", "subordinado","subordinate");

		result = omsProxy.quantityMembers("", "subordinado","subordinate");

		result = omsProxy.quantityMembers(null, "subordinado","subordinate");

	}





}
