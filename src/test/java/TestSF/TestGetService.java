package TestSF;

//import omsTests.DatabaseAccess;
import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.ServiceProfileNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

public class TestGetService extends TestCase {

	SFProxy sfProxy = null;
	Agent agent = null;
	OMS oms = null;
	SF sf = null;
	DatabaseAccess dbA = null;

	protected void setUp() throws Exception {
		super.setUp();

		AgentsConnection.connect();


		oms = new OMS(new AgentID("OMS"));

		sf =  new SF(new AgentID("SF"));

		oms.start();
		sf.start();


		agent = new Agent(new AgentID("pruebas"));



		sfProxy = new SFProxy(agent);
		
		dbA = new DatabaseAccess();
		
		dbA.removeJenaTables();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		dbA.removeJenaTables();
		//--------------------------------------------//
		
		sfProxy = null;

		agent.terminate();
		agent = null;

		oms.Shutdown();
		sf.Shutdown();
		
		oms.await();
		sf.await();
		
		oms = null;
		sf = null;
	}


	/**
	 * Incorrect Service Profile. The getService method is called with a string which not represents a Service Profile.
	 * @return
	 */
	public void testIncorrectParamTest1(){

		try
		{
			sfProxy.getService("dsic-upv-es");
			
			fail();

		}catch(ServiceProfileNotFoundException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * A URL of a service specification is provided instead a Service Profile.
	 * @return
	 */
	public void testIncorrectParamTest2(){

		try
		{

			sfProxy.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
			
			fail();

		}catch(ServiceProfileNotFoundException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Correct service profile, but without a service previously registered in the system.
	 * @return
	 */
	public void testIncorrectParamTest3(){

		try
		{
			sfProxy.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");
			
			fail();
		}catch(ServiceProfileNotFoundException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 *  Get the web service Product, which is provided by one agent behavior. The service specification provided by 
	 *  the service should be checked in order to ensure that provider data and also the input and output parameters
	 *   are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest2(){

		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
			
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the web service Addition, which is provided by one agent which internally calls to a web service. The 
	 * service specification provided by the service should be checked in order to ensure that provider data and
	 *  also the input and output parameters are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest3(){
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
			
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the web service Square, which is directly provided by a web service. The service specification provided by 
	 * the service should be checked in order to ensure that grounding data and also the input and output parameters
	 *  are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest4(){
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the demanded web service Division. The service specification provided by the service should be 
	 * checked in order to ensure that the input and output parameters are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest5(){
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Even, which is provided by an organization by means of a web service. The service
	 * specification provided by the service should be checked in order to ensure that provider data and also the input and
	 * output parameters are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest6(){
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Sign, which is provided by an agent behavior. The service specification provided by the service 
	 * should be checked in order to ensure that provider data and also the input and output parameters are properly 
	 * retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest7(){
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Square, which is provided both by a web service and by an agent behavior in this 
	 * case. The service specification provided by the service should be checked in order to ensure that 
	 * provider data, grounding data and also the input and output parameters are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest8(){

		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Division, which is provided both by an organization and by an agent behavior 
	 * in this case. The service specification provided by the service should be checked in order to
	 * ensure that providers data and also the input and output parameters are properly retrieved.
	 * @return
	 */
	public void testAppropiateParamsTest9(){
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			
			String result = sfProxy.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
			
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

}
