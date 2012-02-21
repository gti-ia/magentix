package sfTest;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class TesterAgentGetService extends QueueAgent{
	
	SFProxy sfProxy=new SFProxy(this);
	
	public TesterAgentGetService(AgentID aid) throws Exception {
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
		
	}
	
	/**
	 * Incorrect Service Profile. The getService method is called with a string which not represents a Service Profile.
	 * @return
	 */
	String incorrectParamTest1(){
		
		try {
			return sfProxy.getService("dsic-upv-es");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	/**
	 * A URL of a service specification is provided instead a Service Profile.
	 * @return
	 */
	String incorrectParamTest2(){
		
		try {
			return sfProxy.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Correct service profile, but without a service previously registered in the system.
	 * @return
	 */
	String incorrectParamTest3(){
		
		try {
			return sfProxy.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the web service SumArray, which is provided by one organization by means of an agent. The service 
	 * specification provided by the service should be checked in order to ensure that provider data and also
	 * the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest1(){
		
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owls#SumArrayProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 *  Get the web service Product, which is provided by one agent behavior. The service specification provided by 
	 *  the service should be checked in order to ensure that provider data and also the input and output parameters
	 *   are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest2(){
		
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the web service Addition, which is provided by one agent which internally calls to a web service. The 
	 * service specification provided by the service should be checked in order to ensure that provider data and
	 *  also the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest3(){
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the web service Square, which is directly provided by a web service. The service specification provided by 
	 * the service should be checked in order to ensure that grounding data and also the input and output parameters
	 *  are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest4(){
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the demanded web service Division. The service specification provided by the service should be 
	 * checked in order to ensure that the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest5(){
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the service Even, which is provided by an organization by means of a web service. The service
	 * specification provided by the service should be checked in order to ensure that provider data and also the input and
	 * output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest6(){
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the service Sign, which is provided by an agent behavior. The service specification provided by the service 
	 * should be checked in order to ensure that provider data and also the input and output parameters are properly 
	 * retrieved.
	 * @return
	 */
	String appropiateParamsTest7(){
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the service Square, which is provided both by a web service and by an agent behavior in this 
	 * case. The service specification provided by the service should be checked in order to ensure that 
	 * provider data, grounding data and also the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest8(){
		
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the service Division, which is provided both by an organization and by an agent behavior 
	 * in this case. The service specification provided by the service should be checked in order to
	 * ensure that providers data and also the input and output parameters are properly retrieved.
	 * @return
	 */
	String appropiateParamsTest9(){
		try {
			return sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
