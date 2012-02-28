package omsTest.acquireRole;

import java.util.HashMap;
import java.util.Map.Entry;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.ServiceTools;
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
		
		try{
			
		
		result = omsProxy.acquireRole("participant", "virtual");
		System.out.println(result);
			
		result = omsProxy.acquireRole("miembro2", "plana2");
		System.out.println(result);
		
		}catch(THOMASException e)
		{
			System.out.println("Error: "+ e.getContent());
			System.out.println("Error: "+ e.getMessage());
		}
//		result = omsProxy.acquireRole("creador", "plana2");
//		System.out.println(result);
		
		
//		result = omsProxy.leaveRole("miembro2", "plana2");
//		System.out.println(result);

		
		//2. Se quiere adquirir el rol de la unidad hija, el agente existe solamente en la unidad padre.
		
		/*result = omsProxy.acquireRole("creador2", "plana");
		System.out.println(result);*/
		
//		result = omsProxy.acquireRole("miembro", "equipo");
//		System.out.println(result);

/*		result = omsProxy.acquireRole("creador3", "equipo");
		System.out.println(result);*/

//		result = omsProxy.acquireRole("subordinado2", "jerarquia");
//		System.out.println(result);
//		
//		result = omsProxy.acquireRole("supervisor", "jerarquia");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador3", "jerarquia");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("miembro2", "plana2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("miembro", "equipo2");
//		System.out.println(result);
		
		
//		result = omsProxy.acquireRole("subordinado2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("supervisor", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("subordinado", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("subordinado", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("supervisor2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador3", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador2", "equipo2");
//		System.out.println(result);
		
/*		result = omsProxy.acquireRole("subordinado", "jerarquia2");
		System.out.println(result);*/
		
//		result = omsProxy.acquireRole("supervisor2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador3", "jerarquia2");
//		System.out.println(result);
//		result = omsProxy.acquireRole("creador2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador3", "jerarquia2");
//		System.out.println(result);
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.acquireRole("participant", "noexiste");
//		System.out.println(result);
//		
//		result = omsProxy.acquireRole("participant", "");
//		System.out.println(result);
//		
//		result = omsProxy.acquireRole("participant", null);
//		System.out.println(result);
//		
//		
//		result = omsProxy.acquireRole("noexiste", "virtual");
//		System.out.println(result);
//		
//		result = omsProxy.acquireRole("", "virtual");
//		System.out.println(result);
//		
//		result = omsProxy.acquireRole(null, "virtual");
//		System.out.println(result);
//
		/** Permisos incorrectos **/
		
//		result = omsProxy.acquireRole("miembro2", "plana2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador2", "equipo2");
//		System.out.println(result);
	
		

//		result = omsProxy.acquireRole("creador3", "equipo2");
//		System.out.println(result);
		
		
//		result = omsProxy.acquireRole("subordinado2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("subordinado", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("subordinado", "jerarquia2");
//		System.out.println(result);

//		result = omsProxy.acquireRole("creador2", "jerarquia2");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("creador", "jerarquia");
//		System.out.println(result);
		
//		result = omsProxy.acquireRole("supervisor2", "jerarquia2");
//		System.out.println(result);
//		result = omsProxy.registerRole("Creador2", "virtual","internal", "private", "creator");
//		System.out.println(result);
		//2. Registrar roles en una unidad plana
//		result = omsProxy.registerRole("Miembro", "plana","external", "public", "member");
//		System.out.println(result);
//
//		result = omsProxy.registerRole("Creador2", "plana","internal", "private", "creator");
//		System.out.println(result);
		
		//3. Registrar roles en un equipo.
//		result = omsProxy.registerRole("Miembro", "Equipo","external", "public", "member");
//		System.out.println(result);
//
//		result = omsProxy.registerRole("Creador2", "Equipo","internal", "private", "creator");
//		System.out.println(result);
		
		//3. Registrar roles en una jerarquia.
//		result = omsProxy.registerRole("Miembro", "Jerarquia","external", "public", "subordinate");
//		System.out.println(result);
//
//		result = omsProxy.registerRole("Creador2", "Jerarquia","internal", "private", "creator");
//		System.out.println(result);
		
		/** Paso de parámetros incorrectos **/
		//1. No se introduce el role.
//		result = omsProxy.registerRole(null, "virtual", "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole(null, "equipo", "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole(null, "plana", "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole(null, "jerarquia", "external", "subordinate", "member");
//		System.out.println(result);
		
		//2. No se introduce la unidad.
//		result = omsProxy.registerRole("miembro", null, "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", null, "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", null, "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", null, "external", "subordinate", "member");
//		System.out.println(result);
		
		//3. Se proporciona un nombre de unidad inexistente.
//		result = omsProxy.registerRole("miembro", "inexistente", "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "inexistente", "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "inexistente", "external", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "inexistente", "external", "subordinate", "member");
//		System.out.println(result);
		
		//4. El tipo de accesibilidad no es válido.
//		result = omsProxy.registerRole("miembro", "virtual",null, "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "equipo", null, "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "plana", null, "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "jerarquia", null, "public", "subordinate");
//		System.out.println(result);
		
		//5. El tipo de accesibilidad no es válido.
//		result = omsProxy.registerRole("miembro", "virtual","inexistente", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "equipo", "inexistente", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "plana", "inexistente", "public", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "jerarquia", "inexistente", "public", "subordinate");
//		System.out.println(result);
		
		//6. El tipo de visibilidad no es válido.
//		result = omsProxy.registerRole("miembro", "virtual","external","inexistente", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "equipo", "external", "inexistente", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "plana", "external", "inexistente", "member");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "jerarquia", "external", "inexistente", "subordinate");
//		System.out.println(result);
		
		//7. El tipo de position no es válido.
//		result = omsProxy.registerRole("miembro", "virtual","external","public", "inexistente");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "equipo", "external", "public", "inexistente");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "plana", "external", "public", "inexistente");
//		System.out.println(result);
//		result = omsProxy.registerRole("miembro", "subordinado", "external", "public", "inexistente");
//		System.out.println(result);
		
		/** Permisos incorrectos **/
		
//		result = omsProxy.registerRole("subordinado2", "jerarquia","external","public", "subordinate");
//		System.out.println(result);
		
//        result = omsProxy.registerRole("Miembro","Virtual","External","Public","Supervisor");
//        System.out.println(result);
//        result = omsProxy.registerRole("Miembro","Equipo","External","Public","Supervisor");
//        System.out.println(result);
//        result = omsProxy.registerRole("Miembro","Plana","External","Public","Subordinate");
//        System.out.println(result);
//        result = omsProxy.registerRole("Subordinado","Jerarquía","External","Public","member");
//        System.out.println(result);
	}
	
	
	

	
	


}
