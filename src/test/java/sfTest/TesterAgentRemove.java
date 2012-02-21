package sfTest;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class TesterAgentRemove extends QueueAgent{
	
	SFProxy sfProxy=new SFProxy(this);
	
	public TesterAgentRemove(AgentID aid) throws Exception {
		super(aid);
		
	}
	
	
	protected void execute() {
		
		String res1=incorrectParamTest1();
		System.out.println("incorrectParamTest1:\n"+res1);
		
		String res2=incorrectParamTest2();
		System.out.println("incorrectParamTest2:\n"+res2);
		
		String res3=incorrectParamTest3();
		System.out.println("incorrectParamTest3:\n"+res3);
		
		String res4=incorrectParamTest4();
		System.out.println("incorrectParamTest4:\n"+res4);
		
		String res5=incorrectParamTest5();
		System.out.println("incorrectParamTest5:\n"+res5);
		
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
		
	}
	
	/**
	 * Incorrect Service Profile. The removeProvider method is called with a string 
	 * which not represents a Service Profile.
	 * @return
	 */
	String incorrectParamTest1(){
		
		try {
			return sfProxy.removeProvider("dsic-upv-es","AdditionAgent");
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
			return sfProxy.removeProvider("http://localhost/services/1.1/calculateSunriseTime.owls","AdditionAgent");
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
			return sfProxy.removeProvider("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE",
					"Provider");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Correct service profile, but wrong provider name.
	 * @return
	 */
	String incorrectParamTest4(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					"http://localhost/services/Tests/Square.owl#SQUARE_GROUNDING");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Correct service profile, but wrong grounding.
	 * @return
	 */
	String incorrectParamTest5(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					"ProductAgent");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Remove a provider from a registered service with no more providers or groundings. Thus, 
	 * the web service Product is provided by one agent behavior, and this provider should be removed in this test.
	 * @return
	 */
	String appropiateParamsTest1(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					"ProductAgent");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**  
	 * Remove a provider from a registered service with more providers. Concretely, 
	 * the service Square is registered with two agent providers. One of them is removed.
	 * @return
	 */
	String appropiateParamsTest2(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					"SquareAgent");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/** 
	 * Remove a provider from a registered service with more groundings. The service Product 
	 * is provided by a web service and an agent behavior. In this case, the agent behavior 
	 * is removed as a provider.
	 * @return
	 */
	String appropiateParamsTest3(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					"ProductAgent");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Remove a grounding from a registered service with no more providers or groundings. 
	 * In this case, the web service Square is directly provided by a web service, and this 
	 * web service is removed as a grounding of the service.
	 * @return
	 */
	String appropiateParamsTest4(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Remove a grounding from a registered service with more providers. The service Product 
	 * is provided by a web service and an agent behavior. In this case, the web service is 
	 * removed as a grounding of the web service.
	 * @return
	 */
	String appropiateParamsTest5(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductGrounding");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/** 
	 * Remove a grounding from a registered service with more groundings.In this case, 
	 * the web service Square is provided by two different web services. One of them is 
	 * removed as a suitable grounding.
	 * @return
	 */
	String appropiateParamsTest6(){
		
		try {
			return sfProxy.removeProvider("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding");
		} catch (THOMASException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	

}
