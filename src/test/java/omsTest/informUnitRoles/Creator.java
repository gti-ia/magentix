package omsTest.informUnitRoles;

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

		ArrayList<String> result = new ArrayList<String>();

		/** Parámetros correctos **/

		//1. Cambiar de padre a las unidades hijas de virtual

		//a) la unidad jerarquia se convierte en la padre de las otras dos.
		
		result = omsProxy.informUnitRoles("jerarquia");
//		
		for(String s : result)
		{
			System.out.println("Result inform: "+ s);	
		}

		
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
