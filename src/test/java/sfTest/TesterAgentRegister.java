package sfTest;

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


		String res1=incorrectParamTest1();
		System.out.println("incorrectParamTest1:\n"+res1);


		String res2=incorrectParamTest2();
		System.out.println("incorrectParamTest2:\n"+res2);

		String res3=incorrectParamTest3();
		System.out.println("incorrectParamTest3:\n"+res3);


		String resApp1=appropiateParamsTest1();
		System.out.println("appropiateParamsTest1:\n"+resApp1);

		String resApp2=appropiateParamsTest2();
		System.out.println("appropiateParamsTest2:\n"+resApp2);

		String resApp3=appropiateParamsTest3();
		System.out.println("appropiateParamsTest3:\n"+resApp3);

		String resApp4=appropiateParamsTest4();
		System.out.println("appropiateParamsTest4:\n"+resApp4);

		String resApp5=appropiateParamsTest5();
		System.out.println("appropiateParamsTest5:\n"+resApp5);

		String resApp6=appropiateParamsTest6();
		System.out.println("appropiateParamsTest6:\n"+resApp6);

		String resApp7=appropiateParamsTest7();
		System.out.println("appropiateParamsTest7:\n"+resApp7);

		String resApp8=appropiateParamsTest8();
		System.out.println("appropiateParamsTest8:\n"+resApp8);

		String resApp9=appropiateParamsTest9();
		System.out.println("appropiateParamsTest9:\n"+resApp9);

		String resApp10=appropiateParamsTest10();
		System.out.println("appropiateParamsTest10:\n"+resApp10);

		String resApp11=appropiateParamsTest11();
		System.out.println("appropiateParamsTest11:\n"+resApp11);

		String resApp12=appropiateParamsTest12();
		System.out.println("appropiateParamsTest12:\n"+resApp12);



	}

	/**
	 * Incorrect URL. The registerService method is called with a string which not represents a URL
	 * @return
	 * @throws THOMASException 
	 */
	String incorrectParamTest1(){

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
	String incorrectParamTest2(){
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
	String incorrectParamTest3(){
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
	String appropiateParamsTest1(){
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
	String appropiateParamsTest2(){
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
	String appropiateParamsTest3(){
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
	String appropiateParamsTest4(){
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
	String appropiateParamsTest5(){
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
	String appropiateParamsTest6(){
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
	String appropiateParamsTest7(){
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
	String appropiateParamsTest9(){
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
	String appropiateParamsTest10(){
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
	String appropiateParamsTest11(){
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
	String appropiateParamsTest12(){
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
