package testSFServices;

import persistence.SFinterface;

public class DeregisterUTest {

	SFinterface sf=new SFinterface();

	/**
	 * Incorrect Service Profile. The deregisterService method is called with a string which not 
	 * represents a Service Profile.
	 * @return
	 */
	String incorrectParamTest1(){

		return sf.deregisterService("dsic-upv-es"); 
	}

	/**
	 * A URL of a service specification is provided instead a Service Profile.
	 * @return
	 */
	String incorrectParamTest2(){
		return sf.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls");
	}

	/**
	 * Correct service profile, but without a service previously registered in the system.
	 * @return
	 */
	String incorrectParamTest3(){

		return sf.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");
	}

	/**
	 * Deregister the web service SumArray, which is provided by one organization by means of an agent.
	 * @return
	 */
	String appropiateParamsTest1(){

		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl#SumArrayProfile");
	}

	/**
	 *  Deregister the web service Product, which is provided by one agent behavior.
	 * @return
	 */
	String appropiateParamsTest2(){

		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
	}

	/**
	 * Deregister the web service Addition, which is provided by one agent which internally calls to a web service.
	 * @return
	 */
	String appropiateParamsTest3(){
		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
	}

	/**
	 * Deregister the web service Square, which is directly provided by a web service.
	 * @return
	 */
	String appropiateParamsTest4(){
		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
	}

	/**
	 * Deregister the demanded web service Division.
	 * @return
	 */
	String appropiateParamsTest5(){
		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
	}

	/**
	 * Deregister the service Even, which is provided by an organization by means of a web service.
	 * @return
	 */
	String appropiateParamsTest6(){
		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
	}

	/**
	 * Deregister the service Sign, which is provided by an agent behavior.
	 * @return
	 */
	String appropiateParamsTest7(){
		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
	}

	/**
	 * Try to deregister a service already deregistered. In this case, the program tries 
	 * to deregister another time the services SumArray, Product and Division.
	 * @return
	 */
	String appropiateParamsTest8(){

		String res="";
		res+=sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl#SumArrayProfile");
		res+="\n"+sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
		res+="\n"+sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

		return res;
	}

	/**
	 * Deregister the web service Square, which is directly provided by a web service and also by an agent behavior.
	 * @return
	 */
	String appropiateParamsTest9(){
		return sf.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DeregisterUTest deregisterUTest=new DeregisterUTest();

		//		String res1=deregisterUTest.incorrectParamTest1();
		//		System.out.println("incorrectParamTest1:\n"+res1);
		//		
		//		String res2=deregisterUTest.incorrectParamTest2();
		//		System.out.println("incorrectParamTest2:\n"+res2);
		//		
		//		String res3=deregisterUTest.incorrectParamTest3();
		//		System.out.println("incorrectParamTest3:\n"+res3);
		//		
		//		
		//		String resApp1=deregisterUTest.appropiateParamsTest1();
		//		System.out.println("appropiateParamsTest1:\n"+resApp1);
		//		
		//		String resApp2=deregisterUTest.appropiateParamsTest2();
		//		System.out.println("appropiateParamsTest2:\n"+resApp2);
		//		
		//		String resApp3=deregisterUTest.appropiateParamsTest3();
		//		System.out.println("appropiateParamsTest3:\n"+resApp3);
		//		
		//		String resApp4=deregisterUTest.appropiateParamsTest4();
		//		System.out.println("appropiateParamsTest4:\n"+resApp4);
		//		
		//		String resApp5=deregisterUTest.appropiateParamsTest5();
		//		System.out.println("appropiateParamsTest5:\n"+resApp5);
		//		
		//		String resApp6=deregisterUTest.appropiateParamsTest6();
		//		System.out.println("appropiateParamsTest6:\n"+resApp6);
		//		
		//		String resApp7=deregisterUTest.appropiateParamsTest7();
		//		System.out.println("appropiateParamsTest7:\n"+resApp7);

		//		String resApp8=deregisterUTest.appropiateParamsTest8();
		//		System.out.println("appropiateParamsTest8:\n"+resApp8);
		//		
		String resApp9=deregisterUTest.appropiateParamsTest9();
		System.out.println("appropiateParamsTest9:\n"+resApp9);

	}

}
