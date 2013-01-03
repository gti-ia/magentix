package testSFServices;

import omsTests.DatabaseAccess;
import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.ServiceProfileNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

public class TestRemoveProvider extends TestCase {

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


		oms.terminate();
		sf.terminate();
		
		oms = null;
		sf = null;
	}

	/**
	 * Incorrect Service Profile. The removeProvider method is called with a
	 * string which not represents a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {

		try
		{

			String result = sfProxy.removeProvider("dsic-upv-es", "AdditionAgent");
			
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
	 * 
	 * @return
	 */
	public void testIncorrectParamTest2() {

		try
		{
			sfProxy.removeProvider("http://localhost/services/1.1/calculateSunriseTime.owls", "AdditionAgent");
			
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
	 * Correct service profile, but without a service previously registered in
	 * the system.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest3() {

		try
		{
			sfProxy.removeProvider(
					"http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE", "Provider");
			
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
	 * Correct service profile, but wrong provider name.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest4() {

		try
		{
			sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
			"http://localhost/services/Tests/Square.owl#SQUARE_GROUNDING");
			
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
	 * Correct service profile, but wrong grounding.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest5() {

		try
		{
			sfProxy
			.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
			"ProductAgent");
			
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
	 * Remove a provider from a registered service with no more providers or
	 * groundings. Thus, the web service Product is provided by one agent
	 * behavior, and this provider should be removed in this test.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest1() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			
			String result = sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
			"ProductAgent");
			
			assertEquals("Provider or grounding ProductAgent removed", result);

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
	 * Remove a provider from a registered service with more providers.
	 * Concretely, the service Square is registered with two agent providers.
	 * One of them is removed.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest2() {
		try
		{
			
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile", "SquareAgent");
			
			assertEquals("Provider or grounding SquareAgent removed", result);
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
	 * Remove a provider from a registered service with more groundings. The
	 * service Product is provided by a web service and an agent behavior. In
	 * this case, the agent behavior is removed as a provider.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest3() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			
			String result = sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
			"ProductAgent");
			
			assertEquals("Provider or grounding ProductAgent removed", result);
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
	 * Remove a grounding from a registered service with no more providers or
	 * groundings. In this case, the web service Square is directly provided by
	 * a web service, and this web service is removed as a grounding of the
	 * service.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest4() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
			"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding");
			
			assertEquals("Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding removed", result);
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
	 * Remove a grounding from a registered service with more providers. The
	 * service Product is provided by a web service and an agent behavior. In
	 * this case, the web service is removed as a grounding of the web service.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest5() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			
			String result = sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
			"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductGrounding");
			
			assertEquals("Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductGrounding removed", result);
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
	 * Remove a grounding from a registered service with more groundings.In this
	 * case, the web service Square is provided by two different web services.
	 * One of them is removed as a suitable grounding.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest6() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
			"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding");
			
			assertEquals("Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding removed", result);
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
