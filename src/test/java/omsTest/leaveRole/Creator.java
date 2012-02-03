package omsTest.leaveRole;

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

		String result = "";

		

		/** Acceso correcto al servicio **/
		
		//1. Eliminar una unidad cuya unidad padre es virtual.
		
		try {
			result = omsProxy.leaveRole("participante","jerarquia2");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Resultado leave role: "+ result);
//		
//		result = omsProxy.leaveRole("participante","plana2");
//		
//		System.out.println("Resultado leave role: "+ result);
//		
//		result = omsProxy.leaveRole("participante","equipo2");
//		
//		System.out.println("Resultado leave role: "+ result);
		
		/** Parametros incorrectos **/
//		result = omsProxy.leaveRole("participante",null);
		
	//	System.out.println("Resultado leave role: "+ result);
		
//		result = omsProxy.leaveRole("noexiste","virtual");
//		System.out.println("Resultado leave role: "+ result);
//		
//		result = omsProxy.leaveRole("","virtual");
//		System.out.println("Resultado leave role: "+ result);
//		
//		result = omsProxy.leaveRole(null,"virtual");
//		System.out.println("Resultado leave role: "+ result);
		
		/** Permisos incorrectos **/
		
//		result = omsProxy.leaveRole("rolNojugado", "plana");
//		System.out.println("Resultado leave role: "+ result);
		
//		result = omsProxy.leaveRole("participante", "jerarquia2");
//		System.out.println("Resultado leave role: "+ result);
//		
//		result = omsProxy.leaveRole("participante", "equipo2");
//		System.out.println("Resultado leave role: "+ result);
//		
//		result = omsProxy.leaveRole("participante", "plana2");
//		System.out.println("Resultado leave role: "+ result);
	}
	
	
	


}
