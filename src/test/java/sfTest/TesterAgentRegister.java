package sfTest;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class TesterAgentRegister extends QueueAgent{

	SFProxy sfProxy=new SFProxy(this);

	public TesterAgentRegister(AgentID aid) throws Exception {
		super(aid);

	}


	protected void execute() {


		ArrayList<String> res1=incorrectParamTest1();


		ArrayList<String> res2=incorrectParamTest2();

		ArrayList<String> res3=incorrectParamTest3();


		ArrayList<String> resApp1=appropiateParamsTest1();


		ArrayList<String> resApp2=appropiateParamsTest2();


		ArrayList<String> resApp3=appropiateParamsTest3();
		if (resApp3!=null)
		{
		System.out.println("appropiateParamsTest3 (description):\n"+resApp3.get(0));
		System.out.println("appropiateParamsTest3 (specfication):\n"+resApp3.get(1));
		}

		ArrayList<String> resApp4=appropiateParamsTest4();
		if (resApp4!=null)
		{
		System.out.println("appropiateParamsTest4 (description):\n"+resApp4.get(0));
		System.out.println("appropiateParamsTest4 (specfication):\n"+resApp4.get(1));
		}
		ArrayList<String> resApp5=appropiateParamsTest5();
		if (resApp5!=null)
		{
		System.out.println("appropiateParamsTest5 (description):\n"+resApp5.get(0));
		System.out.println("appropiateParamsTest5 (specfication):\n"+resApp5.get(1));
		}
	
		ArrayList<String> resApp6=appropiateParamsTest6();
		if (resApp6!=null)
		{
		System.out.println("appropiateParamsTest6 (description):\n"+resApp6.get(0));
		System.out.println("appropiateParamsTest6 (specfication):\n"+resApp6.get(1));
		}
		ArrayList<String> resApp7=appropiateParamsTest7();
		if (resApp7!=null)
		{
		System.out.println("appropiateParamsTest7 (description):\n"+resApp7.get(0));
		System.out.println("appropiateParamsTest7 (specfication):\n"+resApp7.get(1));
		}
		String resApp8=appropiateParamsTest8();
		if (resApp8!=null)
		{
		System.out.println("appropiateParamsTest8 (description):\n"+resApp8);
		}
		ArrayList<String> resApp9=appropiateParamsTest9();
		if (resApp9!=null)
		{
		System.out.println("appropiateParamsTest9 (description):\n"+resApp9.get(0));
		System.out.println("appropiateParamsTest9 (specfication):\n"+resApp9.get(1));
		}
		ArrayList<String> resApp10=appropiateParamsTest10();
		if (resApp10!=null)
		{
		System.out.println("appropiateParamsTest10 (description):\n"+resApp10.get(0));
		System.out.println("appropiateParamsTest10 (specfication):\n"+resApp10.get(1));
		}
		ArrayList<String> resApp11=appropiateParamsTest11();
		if (resApp11!=null)
		{
		System.out.println("appropiateParamsTest11 (description):\n"+resApp11.get(0));
		System.out.println("appropiateParamsTest11 (specfication):\n"+resApp11.get(1));
		}
		ArrayList<String> resApp12=appropiateParamsTest12();
		if (resApp12!=null)
		{
		System.out.println("appropiateParamsTest12 (description):\n"+resApp12.get(0));
		System.out.println("appropiateParamsTest12 (specfication):\n"+resApp12.get(1));
		}


	}

	/**
	 * Incorrect URL. The registerService method is called with a string which not represents a URL
	 * @return 
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> incorrectParamTest1(){
		
		try
		{
			return sfProxy.registerService("dsic-upv-es");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * URL of a web page. The registerService method is called using as input parameter a string which not 
	 * represents a OWL-S specification of a service.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> incorrectParamTest2(){
		try
		{
			//TODO mirar previamente si la URL contiene un XML
			return sfProxy.registerService("http://gti-ia.dsic.upv.es");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Correct URL but without file associate.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> incorrectParamTest3(){
		try
		{
			return sfProxy.registerService("http://localhost/services/1.1/nonExistingService.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Register the web service SumArray, which is provided by one organization by means of an agent.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest1(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 *  Register the web service Product, which is provided by one agent behavior. 
	 *  The service SumArray must also be registered.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest2(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Register the web service Addition, which is provided by one agent which internally 
	 * calls to a web service. The services SumArray and Product must also be registered.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest3(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Register the web service Square, which is directly provided by a web service. 
	 * The services SumArray, Product and Addition must also be registered.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest4(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Register the demanded web service Division. The services SumArray, Product, 
	 * Addition and Square must also be registered.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest5(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Register the service Even, which is provided by an organization by means of a web service.
	 * The services SumArray, Product ,Addition, Square and Division must also be registered.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest6(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Register the service Sign, which is provided by an agent behavior. The services SumArray, 
	 * Product ,Addition, Square, Division and Even must also be registered.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest7(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Try to register a service already registered. In this case, the services SumArray, 
	 * Product ,Addition, Square, Division and Even are already registered. The program tries 
	 * to register another time the services SumArray, Product and Division.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest8(){
		try
		{

			String res="";
			res+=sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl");
			res+="\n"+sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			res+="\n"+sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			return res;
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Adding a new provider to a registered service. Concretely, a new agent provider will be added to the service
	 * Square. In order to obtain this, the OWL-S service specification will be properly changed.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest9(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Adding a new grounding to a registered service. Concretely, the service Product will be also provided by a
	 * web service. In order to obtain this, the OWL-S service specification will be properly changed.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest10(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Adding new providers to a register demanded service, one agent and one organization. A new OWL-S file will
	 * be created to specify the service Division, using the previous one but adding and eliminating the information
	 * required to register the new providers.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest11(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division2.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}

	/**
	 * Adding new web services which will offer a register demanded service. A new OWL-S file will be created
	 * to specify the service Division, using the original one but adding and eliminating the information required to
	 * register two new groundings.
	 * @return
	 * @throws THOMASException 
	 */
	ArrayList<String> appropiateParamsTest12(){
		try
		{
			return sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division3.owl");
		}catch(THOMASException e)
		{
			System.err.println(e.getContent());
			return null;
		}
	}



}
