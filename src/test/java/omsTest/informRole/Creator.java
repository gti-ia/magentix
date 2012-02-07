package omsTest.informRole;

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

		ArrayList<String> result = new ArrayList<String>();

		/** Parámetros correctos **/

		//1. Cambiar de padre a las unidades hijas de virtual

		//a) la unidad jerarquia se convierte en la padre de las otras dos.
		
		try {
			result = omsProxy.informRole("miembro2", "plana2");
			
			System.out.println("position "+ result.get(0)+ " visibilitiy: "+ result.get(1)+ " accesibility: "+ result.get(2));
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	

//		result = omsProxy.informRole("creador2", "plana");
////		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("miembro", "plana");
//	
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("creador3", "plana");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("miembro2", "plana");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("miembro2", "equipo");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("creador2", "equipo");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("miembro", "equipo");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("creador3", "equipo");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("subordinado2", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
//		result = omsProxy.informRole("subordinado", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("supervisor", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("supervisor2", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("creador2", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("creador3", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
//		result = omsProxy.informRole("subordinado2", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		/** Parámetros incorrectos **/
		
//		result = omsProxy.informRole("subordinado2", "noexiste");
//		result = omsProxy.informRole("subordinado2", "");
//		result = omsProxy.informRole("subordinado2", null);
//		
//		result = omsProxy.informRole("noexiste", "jerarquia");
//		result = omsProxy.informRole("", "jerarquia");
//		result = omsProxy.informRole(null, "jerarquia");
		
	
		/** Permisos incorrectos **/
		
//		result = omsProxy.informRole("creador3", "jerarquia");
//		
//		for(String s : result)
//		{
//			System.out.println("Result inform: "+ s);	
//		}
		
	}
	
	
	


}
