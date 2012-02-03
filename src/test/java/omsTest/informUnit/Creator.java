package omsTest.informUnit;

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

		try
		{
			result = omsProxy.informUnit("plana");

			System.out.println("type "+ result.get(0).get(0)+ " parentName: "+ result.get(0).get(1));
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		//		result = omsProxy.informUnit("jerarquia");
		//		
		//		for(String s : result)
		//		{
		//			System.out.println("Result inform: "+ s);	
		//		}




	}





}
