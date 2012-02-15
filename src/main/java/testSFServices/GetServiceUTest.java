package testSFServices;

import persistence.SFinterface;

public class GetServiceUTest {

	
SFinterface sf=new SFinterface();
	
	/**
	 * Incorrect Service Profile. The getService method is called with a string which not represents a Service Profile.
	 * @return
	 */
	String incorrectParamTest1(){
		
		return sf.getService("dsic-upv-es"); 
	}
	
	/**
	 * A URL of a service specification is provided instead a Service Profile.
	 * @return
	 */
	String incorrectParamTest2(){
		
		return sf.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
	}
	
	/**
	 * Correct service profile, but without a service previously registered in the system.
	 * @return
	 */
	String incorrectParamTest3(){
		
		return sf.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");
	}
	
	/**
	 * Get the web service SumArray, which is provided by one organization by means of an agent. The service 
	 * specification provided by the service should be checked in order to ensure that provider data and also
	 * the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest1(){
		
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owls#SumArrayProfile");
	}
	
	/**
	 *  Get the web service Product, which is provided by one agent behavior. The service specification provided by 
	 *  the service should be checked in order to ensure that provider data and also the input and output parameters
	 *   are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest2(){
		
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
	}
	
	/**
	 * Get the web service Addition, which is provided by one agent which internally calls to a web service. The 
	 * service specification provided by the service should be checked in order to ensure that provider data and
	 *  also the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest3(){
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
	}
	
	/**
	 * Get the web service Square, which is directly provided by a web service. The service specification provided by 
	 * the service should be checked in order to ensure that grounding data and also the input and output parameters
	 *  are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest4(){
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
	}
	
	/**
	 * Get the demanded web service Division. The service specification provided by the service should be 
	 * checked in order to ensure that the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest5(){
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
	}
	
	/**
	 * Get the service Even, which is provided by an organization by means of a web service. The service
	 * specification provided by the service should be checked in order to ensure that provider data and also the input and
	 * output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest6(){
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
	}

	/**
	 * Get the service Sign, which is provided by an agent behavior. The service specification provided by the service 
	 * should be checked in order to ensure that provider data and also the input and output parameters are properly 
	 * retrieved.
	 * @return
	 */
	String appropiateParamsTest7(){
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
	}
	
	/**
	 * Get the service Square, which is provided both by a web service and by an agent behavior in this 
	 * case. The service specification provided by the service should be checked in order to ensure that 
	 * provider data, grounding data and also the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest8(){
		
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
	}
	
	/**
	 * Get the service Division, which is provided both by an organization and by an agent behavior 
	 * in this case. The service specification provided by the service should be checked in order to
	 * ensure that providers data and also the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest9(){
		return sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		

		GetServiceUTest getServiceUTest=new GetServiceUTest();
		
//		String res1=getServiceUTest.incorrectParamTest1();
//		System.out.println("incorrectParamTest1:\n"+res1);
//		
//		String res2=getServiceUTest.incorrectParamTest2();
//		System.out.println("incorrectParamTest2:\n"+res2);
//		
//		String res3=getServiceUTest.incorrectParamTest3();
//		System.out.println("incorrectParamTest3:\n"+res3);
//		
//		
		String resApp1=getServiceUTest.appropiateParamsTest1();
		System.out.println("appropiateParamsTest1:\n"+resApp1);
		
		String resApp2=getServiceUTest.appropiateParamsTest2();
		System.out.println("appropiateParamsTest2:\n"+resApp2);
		
		String resApp3=getServiceUTest.appropiateParamsTest3();
		System.out.println("appropiateParamsTest3:\n"+resApp3);
		
		String resApp4=getServiceUTest.appropiateParamsTest4();
		System.out.println("appropiateParamsTest4:\n"+resApp4);
		
		String resApp5=getServiceUTest.appropiateParamsTest5();
		System.out.println("appropiateParamsTest5:\n"+resApp5);
		
		String resApp6=getServiceUTest.appropiateParamsTest6();
		System.out.println("appropiateParamsTest6:\n"+resApp6);
		
		String resApp7=getServiceUTest.appropiateParamsTest7();
		System.out.println("appropiateParamsTest7:\n"+resApp7);
		
		String resApp8=getServiceUTest.appropiateParamsTest8();
		System.out.println("appropiateParamsTest8:\n"+resApp8);
		
		String resApp9=getServiceUTest.appropiateParamsTest9();
		System.out.println("appropiateParamsTest9:\n"+resApp9);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

}
