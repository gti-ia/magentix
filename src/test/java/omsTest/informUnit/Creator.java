package omsTest.informUnit;

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
		
//		result = omsProxy.informUnit("plana");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
//		
//		result = omsProxy.informUnit("equipo");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
//		
//		result = omsProxy.informUnit("jerarquia2");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
		/** Parámetros incorrectos **/
		
		
/*		result = omsProxy.informUnit("noexiste");
		
		for(String s : result)
		{
			System.out.println("Result inform: "+ s);	
		}
		
		result = omsProxy.informUnit("");
		
		for(String s : result)
		{
			System.out.println("Result inform: "+ s);	
		}
		
		result = omsProxy.informUnit(null);
		
		for(String s : result)
		{
			System.out.println("Result inform: "+ s);	
		}*/
		
		/** Permisos incorrectos **/
		
		result = omsProxy.informUnit("jerarquia");
		
		for(String s : result)
		{
			System.out.println("Result inform: "+ s);	
		}
		
		
	
		
	}
	
	
	


}
