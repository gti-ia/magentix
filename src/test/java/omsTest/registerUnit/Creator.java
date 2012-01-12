package omsTest.registerUnit;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;


public class Creator extends QueueAgent {
	
	OMSProxy omsProxy = new OMSProxy(this);
	
	public Creator(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {

		String result = "";

		

		//1. Registrar una unidad dentro de virual proporcionando la unidad padre de manera explícita
		//result = omsProxy.registerUnit("Jerarquia", "hierarchy", "virtual", "Creador");
		//result = omsProxy.registerUnit("Equipo", "team", "virtual", "Creador");
		//result = omsProxy.registerUnit("Plana", "flat", "virtual", "Creador");
		
		//2. Registrar una unidad dentro de virtual sin proporcionar la unidad padre.
		//result = omsProxy.registerUnit("JerarquiaSinPadre", "hierarchy", "", "Creador");
		//result = omsProxy.registerUnit("JerarquiaSinPadre1", "hierarchy", null, "Creador");
		
		//result = omsProxy.registerUnit("EquipoSinPadre", "team", "", "Creador");
		//result = omsProxy.registerUnit("EquipoSinPadre1", "team", null, "Creador");
		
		//result = omsProxy.registerUnit("PlanaSinPadre", "Flat", "", "Creador");
		//result = omsProxy.registerUnit("PlanaSinPadre1", "Flat", null, "Creador");
		
		
		//3. Registrar unidades anidadas.
		
//		result = omsProxy.registerUnit("jerarquia", "hierarchy", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("jerarquia2", "hierarchy", "jerarquia","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("jerarquia", "hierarchy", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("equipo", "team", "jerarquia","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("jerarquia", "hierarchy", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("plana", "flat", "jerarquia","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("equipo", "team", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("equipo2", "team", "equipo","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("equipo", "team", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("jerarquia", "hierarchy", "equipo","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("equipo", "team", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("plana", "flat", "equipo","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("plana", "flat", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("plana2", "flat", "plana","Creador");
//		System.out.println("Result register unit: "+ result);

//		result = omsProxy.registerUnit("plana", "flat", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("equipo", "team", "plana","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("plana", "flat", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("jerarquía", "hierarchy", "plana","Creador");
//		System.out.println("Result register unit: "+ result);
		
		
		/** Parametros incorrectos **/
		//1. Registrar una unidad indicando una unidad padre inexistente.
		
//		result = omsProxy.registerUnit("Plana", "flat", "inexistente","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("Equipo", "team", "inexistente","Creador");
//		System.out.println("Result register unit: "+ result);
		
//		result = omsProxy.registerUnit("Jerarquía", "hierarchy", "inexistente","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//2. Registrar una unidad sin indicar el nombre del rol a crear por defecto con posición creator.
		
		result = omsProxy.registerUnit("Plana", "flat", "virtual","");
		System.out.println("Result register unit: "+ result);
		
		//3. Registrar una unidad indicando un tipo de unidad inexistente.
		
//		result = omsProxy.registerUnit("Plana", "insexistente", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("Equipo", "insexistente", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("Jerarquía", "insexistente", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("Plana", null, "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("Equipo", null, "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("Jerarquía", null, "virtual","Creador");
//		System.out.println("Result register unit: "+ result);

		//4. Se registra una unidad sin nombre.
		
//		result = omsProxy.registerUnit("", "Flat", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("", "Team", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit("", "Jerarquía", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit(null, "Flat", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit(null, "Team", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
//		
//		result = omsProxy.registerUnit(null, "Jerarquía", "virtual","Creador");
//		System.out.println("Result register unit: "+ result);
		
		/** Permisos Incorrectos **/
		
		//1. Tratar de añadir una jerarquía a otra ya existente.
		
//		result = omsProxy.registerUnit("Jerarquia2", "hierarchy", "Jerarquia","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//2. Tratar de añadir un equipo a una jerarquía ya existente.
//		result = omsProxy.registerUnit("Equipo2", "team", "Jerarquia","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//3. Tratar de añadir una unidad plana a una jerarquía ya existente.
//		result = omsProxy.registerUnit("Plana2", "flat", "Jerarquia","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//4. Tratar de añadir un equipo a otro existente.
//		result = omsProxy.registerUnit("Jerarquia2", "hierarchy", "Equipo","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//5. Trata de añadir una jerarquía a un equipo ya existente.
//		result = omsProxy.registerUnit("Equipo2", "team", "Equipo","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//6. Trata de añadir una unidad plana a un equipo ya existente.
//		result = omsProxy.registerUnit("Plana2", "flat", "Equipo","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//7. Trata de añadir una unidad plana a una ya existente.
//		result = omsProxy.registerUnit("Jerarquia2", "hierarchy", "Plana","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//8. Trata de añadir una Jerarquía a una unida plana ya existente.
//		result = omsProxy.registerUnit("Equipo2", "team", "Plana","Creador");
//		System.out.println("Result register unit: "+ result);
		
		//9. Trata de añadir un equipo a una unidad plana ya existente.
//		result = omsProxy.registerUnit("Plana2", "flat", "Plana","Creador");
//		System.out.println("Result register unit: "+ result);
		

		
	}
	
	
	


}
