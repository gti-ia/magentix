package sfTest;

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
		 * Incorrect Service Profile. The deregisterService method is called with a string which not 
		 * represents a Service Profile.
		 * @return
		 * @throws THOMASException 
		 */
		String incorrectParamTest1(){
			try
			{
				return sfProxy.deregisterService("dsic-upv-es"); 
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * A URL of a service specification is provided instead a Service Profile.
		 * @return
		 * @throws THOMASException 
		 */
		String incorrectParamTest2(){
			try
			{
				return sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Correct service profile, but without a service previously registered in the system.
		 * @return
		 * @throws THOMASException 
		 */
		String incorrectParamTest3(){
			try
			{
				return sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the web service SumArray, which is provided by one organization by means of an agent.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest1(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl#SumArrayProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 *  Deregister the web service Product, which is provided by one agent behavior.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest2(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the web service Addition, which is provided by one agent which internally calls to a web service.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest3(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the web service Square, which is directly provided by a web service.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest4(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the demanded web service Division.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest5(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the service Even, which is provided by an organization by means of a web service.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest6(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the service Sign, which is provided by an agent behavior.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest7(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Try to deregister a service already deregistered. In this case, the program tries 
		 * to deregister another time the services SumArray, Product and Division.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest8(){
			try
			{
				String res="";
				res+=sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl#SumArrayProfile");
				res+="\n"+sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
				res+="\n"+sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

				return res;
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}

		/**
		 * Deregister the web service Square, which is directly provided by a web service and also by an agent behavior.
		 * @return
		 * @throws THOMASException 
		 */
		String appropiateParamsTest9(){
			try
			{
				return sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
			}catch(THOMASException e){
				System.err.println(e.getContent());
				return e.getContent();
			}
		}



	}
