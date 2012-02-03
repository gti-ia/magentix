package omsTest.deallocateRole;

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
		
		//1. Eliminar asignaciones de roles en la unidad virtual.
		
//		result = omsProxy.deallocateRole("miembro", "virtual", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("participant", "virtual", "pruebas2");
//		System.out.println(result);
		
		//2. Eliminar asignaciones de roles en una unidad plana(flat).

//		result = omsProxy.deallocateRole("creador", "plana", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("participante", "plana", "pruebas2");
//		System.out.println(result);
		
		//3. Quitar asignaciones de roles en un equipo.
		
//		result = omsProxy.deallocateRole("creador", "equipo", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("miembro", "equipo", "pruebas2");
//		System.out.println(result);
		
		//4. Eliminar asignaciones de roles en una jerarquía.
//		result = omsProxy.deallocateRole("subordinado", "jerarquia", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("supervisor", "jerarquia", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "jerarquia", "pruebas2");
//		System.out.println(result);
		
		//5. Eliminar la asignación de un rol del mismo nombre que otro ya desempeñado por el agente destino en una 
		//unidad padre.
//		result = omsProxy.deallocateRole("subordinado", "jerarquia2", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("supervisor", "jerarquia2", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "jerarquia2", "pruebas2");
//		System.out.println(result);
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.deallocateRole("", "virtual", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("", "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("", "plana", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("", "jerarquia", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole(null, "virtual", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole(null, "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole(null, "plana", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole(null, "jerarquia", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("inexistente", "virtual", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("inexistente", "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("inexistente", "plana", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("inexistente", "jerarquia", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("", "virtual", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("", "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("", "plana", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("", "jerarquia", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("participant", "", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", "", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("participant", null, "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", null, "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", null, "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", null, "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("participant", "inexsitente", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", "inexsitente", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "inexsitente", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "inexsitente", "pruebas2");
//		System.out.println(result);
//		
//		
//		result = omsProxy.deallocateRole("participant", "virtual", "");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", "equipo", "");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "plana", "");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "jerarquia", "");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("participant", "virtual", null);
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", "equipo", null);
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "plana", null);
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "jerarquia", null);
//		System.out.println(result);
		
//		result = omsProxy.deallocateRole("participant", "virtual", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "plana", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "jerarquia", "pruebas2");
//		System.out.println(result);
		
//		result = omsProxy.deallocateRole("participant", "virtual", "pruebas");
//		System.out.println(result);
//
//		result = omsProxy.deallocateRole("creador", "equipo", "pruebas");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "plana", "pruebas");
//		System.out.println(result);
//		
//		result = omsProxy.deallocateRole("creador", "jerarquia", "pruebas");
//		System.out.println(result);
		
		/** Permisos incorrectos **/
		
//		result = omsProxy.deallocateRole("miembro", "plana", "pruebas2");
//		System.out.println(result);
		
//		result = omsProxy.deallocateRole("miembro", "equipo", "pruebas2");
//		System.out.println(result);
		
		try {
			result = omsProxy.deallocateRole("subordinado", "jerarquia", "pruebas2");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
	}
	
	
	


}
