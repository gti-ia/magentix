package testSFServices;

import persistence.SFinterface;

public class RegisterUTest {

	/**
	 *  SumArray : This service sums the input numbers and returns the result.
		• Input: array of double
		• Output: double
		• Provider: An organization by means of an agent.
		Product : This service multiplies two input numbers and returns the product.
		• Input: two doubles
		• Output: double
		• Provider: Agent behavior
		Addition : This service adds two input numbers and returns the addition.
		• Input: two doubles
		• Output: double
		• Provider: Agent that calls to a web service
		Square : This service squares an input number and returns the result.
		• Input: one double
		• Output: double
		• Provider: Web service
		Division : This service receives two numbers X and Y as inputs. Then, it calculates X divided by Y.
		• Input: two doubles
		• Output: double
		• Provider: no provider (demanded service)
		Even : This service receives one number and returns true when it is even, otherwise it returns false.
		• Input: one double
		• Output: boolean
		• Provider: An organization by means of a web service
		Sign: This service receives one number and returns the word: ”positive” or ”negative”.
		• Input: one double
		• Output: string
		• Provider: Agent

	 */
	
	SFinterface sf=new SFinterface();
	
	/**
	 * Incorrect URL. The registerService method is called with a string which not represents a URL
	 * @return
	 */
	String incorrectParamTest1(){
		
		return sf.registerService("dsic-upv-es"); 
	}
	
	/**
	 * URL of a web page. The registerService method is called using as input parameter a string which not 
	 * represents a OWL-S specification of a service.
	 * @return
	 */
	String incorrectParamTest2(){
		//TODO mirar previamente si la URL contiene un XML
		return sf.registerService("http://gti-ia.dsic.upv.es");
	}
	
	/**
	 * Correct URL but without file associate.
	 * @return
	 */
	String incorrectParamTest3(){
		
		return sf.registerService("http://localhost/services/1.1/nonExistingService.owl");
	}
	
	/**
	 * Register the web service SumArray, which is provided by one organization by means of an agent.
	 * @return
	 */
	String appropiateParamsTest1(){
		
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl");
	}
	
	/**
	 *  Register the web service Product, which is provided by one agent behavior. 
	 *  The service SumArray must also be registered.
	 * @return
	 */
	String appropiateParamsTest2(){
		
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
	}
	
	/**
	 * Register the web service Addition, which is provided by one agent which internally 
	 * calls to a web service. The services SumArray and Product must also be registered.
	 * @return
	 */
	String appropiateParamsTest3(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
	}
	
	/**
	 * Register the web service Square, which is directly provided by a web service. 
	 * The services SumArray, Product and Addition must also be registered.
	 * @return
	 */
	String appropiateParamsTest4(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
	}
	
	/**
	 * Register the demanded web service Division. The services SumArray, Product, 
	 * Addition and Square must also be registered.
	 * @return
	 */
	String appropiateParamsTest5(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
	}
	
	/**
	 * Register the service Even, which is provided by an organization by means of a web service.
	 * The services SumArray, Product ,Addition, Square and Division must also be registered.
	 * @return
	 */
	String appropiateParamsTest6(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
	}

	/**
	 * Register the service Sign, which is provided by an agent behavior. The services SumArray, 
	 * Product ,Addition, Square, Division and Even must also be registered.
	 * @return
	 */
	String appropiateParamsTest7(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
	}
	
	/**
	 * Try to register a service already registered. In this case, the services SumArray, 
	 * Product ,Addition, Square, Division and Even are already registered. The program tries 
	 * to register another time the services SumArray, Product and Division.
	 * @return
	 */
	String appropiateParamsTest8(){
		
		String res="";
		res+=sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl");
		res+="\n"+sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
		res+="\n"+sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
		
		return res;
	}
	
	/**
	 * Adding a new provider to a registered service. Concretely, a new agent provider will be added to the service
	 * Square. In order to obtain this, the OWL-S service specification will be properly changed.
	 * @return
	 */
	String appropiateParamsTest9(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
	}
	
	/**
	 * Adding a new grounding to a registered service. Concretely, the service Product will be also provided by a
	 * web service. In order to obtain this, the OWL-S service specification will be properly changed.
     * @return
	 */
	String appropiateParamsTest10(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
	}
	
	/**
	 * Adding new providers to a register demanded service, one agent and one organization. A new OWL-S file will
	 * be created to specify the service Division, using the previous one but adding and eliminating the information
	 * required to register the new providers.
     * @return
	 */
	String appropiateParamsTest11(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division2.owl");
	}
	
	/**
	 * Adding new web services which will offer a register demanded service. A new OWL-S file will be created
	 * to specify the service Division, using the original one but adding and eliminating the information required to
	 * register two new groundings.
     * @return
	 */
	String appropiateParamsTest12(){
		return sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division3.owl");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RegisterUTest registerUTest=new RegisterUTest();
		
		String res1=registerUTest.incorrectParamTest1();
		System.out.println("incorrectParamTest1:\n"+res1);
		
		String res2=registerUTest.incorrectParamTest2();
		System.out.println("incorrectParamTest2:\n"+res2);
		
		String res3=registerUTest.incorrectParamTest3();
		System.out.println("incorrectParamTest3:\n"+res3);
		
		
		String resApp1=registerUTest.appropiateParamsTest1();
		System.out.println("appropiateParamsTest1:\n"+resApp1);
		
		String resApp2=registerUTest.appropiateParamsTest2();
		System.out.println("appropiateParamsTest2:\n"+resApp2);
		
		String resApp3=registerUTest.appropiateParamsTest3();
		System.out.println("appropiateParamsTest3:\n"+resApp3);
		
		String resApp4=registerUTest.appropiateParamsTest4();
		System.out.println("appropiateParamsTest4:\n"+resApp4);
		
		String resApp5=registerUTest.appropiateParamsTest5();
		System.out.println("appropiateParamsTest5:\n"+resApp5);
		
		String resApp6=registerUTest.appropiateParamsTest6();
		System.out.println("appropiateParamsTest6:\n"+resApp6);
		
		String resApp7=registerUTest.appropiateParamsTest7();
		System.out.println("appropiateParamsTest7:\n"+resApp7);
		
		String resApp8=registerUTest.appropiateParamsTest8();
		System.out.println("appropiateParamsTest8:\n"+resApp8);
		
		String resApp9=registerUTest.appropiateParamsTest9();
		System.out.println("appropiateParamsTest9:\n"+resApp9);
		
		String resApp10=registerUTest.appropiateParamsTest10();
		System.out.println("appropiateParamsTest10:\n"+resApp10);
		
		String resApp11=registerUTest.appropiateParamsTest11();
		System.out.println("appropiateParamsTest11:\n"+resApp11);
		
		String resApp12=registerUTest.appropiateParamsTest12();
		System.out.println("appropiateParamsTest12:\n"+resApp12);
		
		
		
		
		
		
		
		
		

	}

}
