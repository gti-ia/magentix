package omsTest.deregisterUnit;

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

		try
		{

		/** Acceso correcto al servicio **/
		
		//1. Eliminar una unidad cuya unidad padre es virtual.
		
//		result = omsProxy.deregisterUnit("Jerarquia");
//		System.out.println("Deregister unit result: "+ result);
//
//		result = omsProxy.deregisterUnit("Equipo");
//		System.out.println("Deregister unit result: "+ result);
//		
//		result = omsProxy.deregisterUnit("Plana");
//		System.out.println("Deregister unit result: "+ result);
		
		//2. Eliminar unidades que ya no tienen como unidad padre a virtual
		
//		result = omsProxy.deregisterUnit("Jerarquia2");
//		System.out.println("Deregister unit result: "+ result);
//
//		result = omsProxy.deregisterUnit("Equipo2");
//		System.out.println("Deregister unit result: "+ result);
//		
//		result = omsProxy.deregisterUnit("Plana2");
//		System.out.println("Deregister unit result: "+ result);
		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.deregisterUnit("virtual");
//		System.out.println("Deregister unit result: "+ result);
		
		/** Permisos incorrectos **/
		result = omsProxy.deregisterUnit("jerarquia");
		System.out.println("Deregister unit result: "+ result);
		
		result = omsProxy.deregisterUnit("equipo");
		System.out.println("Deregister unit result: "+ result);
		
		result = omsProxy.deregisterUnit("plana");
		System.out.println("Deregister unit result: "+ result);
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	


}
