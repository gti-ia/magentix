package omsTest.informUnitRoles;

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

		//1. Cambiar de padre a las unidades hijas de virtual

		//a) la unidad jerarquia se convierte en la padre de las otras dos.
		
		try {
			result = omsProxy.informUnitRoles("jerarquia");
			
			for(ArrayList<String> s : result)
			{
				System.out.println("rolename: "+ s.get(0)+ " position: "+ s.get(1)+ " visibility: "+ s.get(2)+" accesibility: "+ s.get(3));	
			}
			
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
		

		
		/** Parámetros incorrectos **/
		
//		result = omsProxy.informUnitRoles("noexiste");
////		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
//		
//		result = omsProxy.informUnitRoles("");
////		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
//		
//		result = omsProxy.informUnitRoles(null);
////		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
	
		
	}
	
	
	


}
