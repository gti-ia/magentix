package TestSF;

import java.util.ArrayList;

//import omsTests.DatabaseAccess;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.InvalidServiceURLException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

public class TestRegisterService extends TestCase {

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

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");

		//--------------------------------------------//

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


		oms.terminate();
		sf.terminate();
		
		oms = null;
		sf = null;
	}

	/**
	 * SumArray : This service sums the input numbers and returns the result. •
	 * Input: array of double • Output: double • Provider: An organization by
	 * means of an agent. Product : This service multiplies two input numbers
	 * and returns the product. • Input: two doubles • Output: double •
	 * Provider: Agent behavior Addition : This service adds two input numbers
	 * and returns the addition. • Input: two doubles • Output: double •
	 * Provider: Agent that calls to a web service Square : This service squares
	 * an input number and returns the result. • Input: one double • Output:
	 * double • Provider: Web service Division : This service receives two
	 * numbers X and Y as inputs. Then, it calculates X divided by Y. • Input:
	 * two doubles • Output: double • Provider: no provider (demanded service)
	 * Even : This service receives one number and returns true when it is even,
	 * otherwise it returns false. • Input: one double • Output: boolean •
	 * Provider: An organization by means of a web service Sign: This service
	 * receives one number and returns the word: ”positive” or ”negative”. •
	 * Input: one double • Output: string • Provider: Agent
	 */



	/**
	 * Incorrect URL. The registerService method is called with a string which
	 * not represents a URL
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {

		try
		{
			ArrayList<String> result =  sfProxy.registerService("dsic-upv-es");
			
			fail();

		}catch(InvalidServiceURLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}


	}

	/**
	 * URL of a web page. The registerService method is called using as input
	 * parameter a string which not represents a OWL-S specification of a
	 * service.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest2() {

		try
		{
			ArrayList<String> result =  sfProxy.registerService("http://gti-ia.dsic.upv.es");
			
			fail();

		}catch(InvalidServiceURLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	/**
	 * Correct URL but without file associate.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest3() {


		try
		{
			ArrayList<String> result =  sfProxy.registerService("http://localhost/services/1.1/nonExistingService.owl");
			
			fail();

		}catch(InvalidServiceURLException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}

	}



	/**
	 * Register the web service Product, which is provided by one agent
	 * behavior. The service SumArray must also be registered.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest2() {

		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");


			assertEquals("Service registered: http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile", result.get(0));
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
	 * Register the web service Addition, which is provided by one agent which
	 * internally calls to a web service. The services SumArray and Product must
	 * also be registered.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest3() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");

			assertEquals("Service registered: http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile", result.get(0));
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
	 * Register the web service Square, which is directly provided by a web
	 * service. The services SumArray, Product and Addition must also be
	 * registered.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest4() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			assertEquals("Service registered: http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile", result.get(0));
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
	 * Register the demanded web service Division. The services SumArray,
	 * Product, Addition and Square must also be registered.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest5() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			assertEquals("Service registered: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile", result.get(0));
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
	 * Register the service Even, which is provided by an organization by means
	 * of a web service. The services SumArray, Product ,Addition, Square and
	 * Division must also be registered.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest6() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			assertEquals("Service registered: http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile", result.get(0));
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
	 * Register the service Sign, which is provided by an agent behavior. The
	 * services SumArray, Product ,Addition, Square, Division and Even must also
	 * be registered.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest7() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			assertEquals("Service registered: http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile", result.get(0));
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
	 * Try to register a service already registered. In this case, the services
	 * SumArray, Product ,Addition, Square, Division and Even are already
	 * registered. The program tries to register another time the services
	 * SumArray, Product and Division.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest8() {
		
		String res = "";
		
		try{
			
			res += sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/SumArray.owl");
		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try{
			res += "\n" + sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		try{
			res += "\n" + sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division2.owl");
			
			assertEquals("0 groundings and 2 providers registered to service profile: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile", result.get(0));
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
		
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division3.owl");
			
			assertEquals("2 groundings and 0 providers registered to service profile: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile", result.get(0));
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
	 * Adding new providers to a register demanded service, one agent and one
	 * organization. A new OWL-S file will be created to specify the service
	 * Division, using the previous one but adding and eliminating the
	 * information required to register the new providers.
	 * 
	 * @return
	 *//*
	public void testAppropiateParamsTest11() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division2.owl");
			
			assertEquals("0 groundings and 2 providers registered to service profile: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile", result.get(0));
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}

	*//**
	 * Adding new web services which will offer a register demanded service. A
	 * new OWL-S file will be created to specify the service Division, using the
	 * original one but adding and eliminating the information required to
	 * register two new groundings.
	 * 
	 * @return
	 *//*
	public void testAppropiateParamsTest12() {
		try{
			ArrayList<String> result = sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division3.owl");
			
			assertEquals("2 groundings and 0 providers registered to service profile: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile", result.get(0));
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}
*/

}
