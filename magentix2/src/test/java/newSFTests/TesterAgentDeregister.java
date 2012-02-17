package newSFTests;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.THOMASException;

public class TesterAgentDeregister extends QueueAgent{
	
	SFProxy sfProxy=new SFProxy(this);
	
	public TesterAgentDeregister(AgentID aid) throws Exception {
		super(aid);
		
	}
	
	
	protected void execute() {
		try{
//		String res1=incorrectParamTest1();
		//		System.out.println("incorrectParamTest1:\n"+res1);
		//		
		//		String res2=incorrectParamTest2();
		//		System.out.println("incorrectParamTest2:\n"+res2);
		//		
		//		String res3=incorrectParamTest3();
		//		System.out.println("incorrectParamTest3:\n"+res3);
		//		
		//		
		//		String resApp1=appropiateParamsTest1();
		//		System.out.println("appropiateParamsTest1:\n"+resApp1);
		//		
		//		String resApp2=appropiateParamsTest2();
		//		System.out.println("appropiateParamsTest2:\n"+resApp2);
		//		
		//		String resApp3=appropiateParamsTest3();
		//		System.out.println("appropiateParamsTest3:\n"+resApp3);
		//		
		//		String resApp4=appropiateParamsTest4();
		//		System.out.println("appropiateParamsTest4:\n"+resApp4);
		//		
		//		String resApp5=appropiateParamsTest5();
		//		System.out.println("appropiateParamsTest5:\n"+resApp5);
		//		
		//		String resApp6=appropiateParamsTest6();
		//		System.out.println("appropiateParamsTest6:\n"+resApp6);
		//		
		//		String resApp7=appropiateParamsTest7();
		//		System.out.println("appropiateParamsTest7:\n"+resApp7);

		//		String resApp8=appropiateParamsTest8();
		//		System.out.println("appropiateParamsTest8:\n"+resApp8);
		//		
		String resApp9=appropiateParamsTest9();
		System.out.println("appropiateParamsTest9:\n"+resApp9);
		
		}catch(THOMASException e){
			System.err.println(e.getContent());
		}
	}
	
	/**
	 * Incorrect Service Profile. The deregisterService method is called with a string which not 
	 * represents a Service Profile.
	 * @return
	 * @throws THOMASException 
	 */
	String incorrectParamTest1() throws THOMASException{

		return sfProxy.deregisterService("dsic-upv-es"); 
	}

	/**
	 * A URL of a service specification is provided instead a Service Profile.
	 * @return
	 * @throws THOMASException 
	 */
	String incorrectParamTest2() throws THOMASException{
		return sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls");
	}

	/**
	 * Correct service profile, but without a service previously registered in the system.
	 * @return
	 * @throws THOMASException 
	 */
	String incorrectParamTest3() throws THOMASException{

		return sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");
	}

	/**
	 * Deregister the web service SumArray, which is provided by one organization by means of an agent.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest1() throws THOMASException{

		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl#SumArrayProfile");
	}

	/**
	 *  Deregister the web service Product, which is provided by one agent behavior.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest2() throws THOMASException{

		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
	}

	/**
	 * Deregister the web service Addition, which is provided by one agent which internally calls to a web service.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest3() throws THOMASException{
		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
	}

	/**
	 * Deregister the web service Square, which is directly provided by a web service.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest4() throws THOMASException{
		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
	}

	/**
	 * Deregister the demanded web service Division.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest5() throws THOMASException{
		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
	}

	/**
	 * Deregister the service Even, which is provided by an organization by means of a web service.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest6() throws THOMASException{
		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
	}

	/**
	 * Deregister the service Sign, which is provided by an agent behavior.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest7() throws THOMASException{
		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
	}

	/**
	 * Try to deregister a service already deregistered. In this case, the program tries 
	 * to deregister another time the services SumArray, Product and Division.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest8() throws THOMASException{

		String res="";
		res+=sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl#SumArrayProfile");
		res+="\n"+sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
		res+="\n"+sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

		return res;
	}

	/**
	 * Deregister the web service Square, which is directly provided by a web service and also by an agent behavior.
	 * @return
	 * @throws THOMASException 
	 */
	String appropiateParamsTest9() throws THOMASException{
		return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
	}
	
	

}
