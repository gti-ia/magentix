package omsTest.jointUnit;

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

		/** Parámetros correctos **/

		//1. Cambiar de padre a las unidades hijas de virtual

		//a) la unidad jerarquia se convierte en la padre de las otras dos.
		try {
		result = omsProxy.acquireRole("participant", "virtual");
		System.out.println("Result joint unit: "+ result);
			result = omsProxy.jointUnit("plana2", "virtual");
			
			System.out.println("Result joint unit: "+ result);
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		
//		result = omsProxy.jointUnit("plana", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
		
		//b) La unidad equipo se convierte en padre de las dos
//		result = omsProxy.jointUnit("jerarquia", "equipo");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("plana", "equipo");
//		System.out.println("Result joint unit: "+ result);
		
		//c) La unidad plana se convierte en unidad padre de la dos
//		result = omsProxy.jointUnit("equipo", "plana");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("jerarquia", "plana");
//		System.out.println("Result joint unit: "+ result);
		
		//2. Cambiamos de padre a unidades hijas de otras unidades.
		
//		result = omsProxy.jointUnit("equipo2", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("jerarquia2", "equipo");
//		System.out.println("Result joint unit: "+ result);
//		
//
//		result = omsProxy.jointUnit("plana2", "equipo");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("plana", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//		
//
//		result = omsProxy.jointUnit("equipo", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.jointUnit("jerarquia", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("plana", "plana");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("equipo", "equipo");
//		System.out.println("Result joint unit: "+ result);

		
//		result = omsProxy.jointUnit("virtual", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
		
//		result = omsProxy.jointUnit("noexiste", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//
//		result = omsProxy.jointUnit("", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//
//		result = omsProxy.jointUnit(null, "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("equipo", "noexiste");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("equipo", "");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("equipo", null);
//		System.out.println("Result joint unit: "+ result);
		
		/** Permisos incorrectos **/
		
//		result = omsProxy.jointUnit("equipo", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
//		
//		result = omsProxy.jointUnit("plana", "jerarquia");
//		System.out.println("Result joint unit: "+ result);
	}
	
	
	


}
