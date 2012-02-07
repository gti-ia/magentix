package omsTest.allocateRole;

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
			result = omsProxy.allocateRole("creador2", "plana2", "pruebas2");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result);
//
//		result = omsProxy.allocateRole("participant", "virtual", "pruebas2");
//		System.out.println(result);

		//2. Registrar roles en una unidad plana.
//		result = omsProxy.allocateRole("participante", "plana", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador", "plana", "pruebas2");
//		System.out.println(result);
		
		//3. Asignar roles en un equipo.
		
//		result = omsProxy.allocateRole("participante", "equipo", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador", "equipo", "pruebas2");
//		System.out.println(result);
		
		//4. Asignar roles en una jerarquia.
//		result = omsProxy.allocateRole("subordinado", "jerarquia", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("supervisor", "jerarquia", "pruebas2");
//		System.out.println(result);
//
//
//		result = omsProxy.allocateRole("creador", "jerarquia", "pruebas2");
//		System.out.println(result);
		
		//5. Asignar un rol del mismo nombre que otro ya desempeñado por el agente destino en una unidad padre.
//		result = omsProxy.allocateRole("subordinado", "jerarquia2", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("supervisor", "jerarquia2", "pruebas2");
//		System.out.println(result);
//
//
//		result = omsProxy.allocateRole("creador", "jerarquia2", "pruebas2");
//		System.out.println(result);
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.allocateRole("", "virtual", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("", "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("", "plana", "pruebas2");
//		System.out.println(result);
//
//
//		result = omsProxy.allocateRole("", "jerarquia", "pruebas2");
//		System.out.println(result);
//
//		
//		result = omsProxy.allocateRole(null, "virtual", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole(null, "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole(null, "plana", "pruebas2");
//		System.out.println(result);
//
//
//		result = omsProxy.allocateRole(null, "jerarquia", "pruebas2");
//		System.out.println(result);
		
		
//		result = omsProxy.allocateRole("inexistente", "virtual", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("inexistente", "equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("inexistente", "plana", "pruebas2");
//		System.out.println(result);
//
//
//		result = omsProxy.allocateRole("inexistente", "jerarquia", "pruebas2");
//		System.out.println(result);
		
//		result = omsProxy.allocateRole("participant", "", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador", "", "pruebas2");
//		System.out.println(result);
//		
//		
//		result = omsProxy.allocateRole("participant", null, "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador",null, "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", null, "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador",null, "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("participant", "inexistente", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador","inexistente", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "inexistente", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador","inexistente", "pruebas2");
//		System.out.println(result);
//		
//		
//		result = omsProxy.allocateRole("participant", "virtual", "");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador","equipo", "");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "plana", "");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador","jerarquia", "");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("participant", "virtual", null);
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador","equipo", null);
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "plana", null);
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador","jerarquia", null);
//		System.out.println(result);
		
		
//		result = omsProxy.allocateRole("participant", "virtual", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador","equipo", "pruebas2");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "plana", "pruebas2");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador","jerarquia", "pruebas2");
//		System.out.println(result);
		
//		result = omsProxy.allocateRole("participant", "virtual", "pruebas");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador","equipo", "pruebas");
//		System.out.println(result);
//		
//		result = omsProxy.allocateRole("creador", "plana", "pruebas");
//		System.out.println(result);
//
//		result = omsProxy.allocateRole("creador","jerarquia", "pruebas");
//		System.out.println(result);
		
		/** Permisos incorrectos **/
		
		//1. Asignar un rol en una unidad plana
//		result = omsProxy.allocateRole("miembro","plana", "pruebas2");
//		System.out.println(result);
		
		//2. Asignar un rol en un equipo.
//		result = omsProxy.allocateRole("miembro","equipo", "pruebas2");
//		System.out.println(result);
		
		//3. Asignar un rol en una jerarquia
//		try {
//			result = omsProxy.allocateRole("subordinado","jerarquia", "pruebas2");
//		} catch (THOMASException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(result);
//	
	}
	
	
	


}
