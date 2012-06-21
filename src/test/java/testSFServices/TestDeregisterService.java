package testSFServices;

import junit.framework.TestCase;
import omsTests.DatabaseAccess;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.ServiceProfileNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;

public class TestDeregisterService extends TestCase {

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

		
		sfProxy = null;

		agent.terminate();
		agent = null;


		oms.terminate();
		sf.terminate();
		
		oms = null;
		sf = null;
	}

	/**
	 * Incorrect Service Profile. The deregisterService method is called with a
	 * string which not represents a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {
		try
		{
			sfProxy.deregisterService("dsic-upv-es");

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
			sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls");

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
			sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");

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
	 * Deregister the web service Product, which is provided by one agent
	 * behavior.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest2() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			
			String result = sfProxy
			.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile Deregistered", result);
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
	 * Deregister the web service Addition, which is provided by one agent which
	 * internally calls to a web service.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest3() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			
			String result = sfProxy
			.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile Deregistered", result);
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
	 * Deregister the web service Square, which is directly provided by a web
	 * service.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest4() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy
			.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile Deregistered", result);
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
	 * Deregister the demanded web service Division.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest5() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			
			String result =sfProxy
			.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile Deregistered", result);
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
	 * Deregister the service Even, which is provided by an organization by
	 * means of a web service.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest6() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			
			String result = sfProxy
			.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile Deregistered", result);
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
	 * Deregister the service Sign, which is provided by an agent behavior.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest7() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			
			String result = sfProxy.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile Deregistered", result);
		}catch(THOMASException e)
		{

			fail(e.getMessage());

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}


/*	*//**
	 * Deregister the web service Square, which is directly provided by a web
	 * service and also by an agent behavior.
	 * 
	 * @return
	 *//*
	public void testAppropiateParamsTest9() {
		try
		{
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			
			String result = sfProxy
			.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");
			
			assertEquals("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile Deregistered", result);
		}catch(THOMASException e)
		{

			assertNotNull(e);

		}
		catch(Exception e)
		{
			fail(e.getMessage());
		}
	}*/
}
