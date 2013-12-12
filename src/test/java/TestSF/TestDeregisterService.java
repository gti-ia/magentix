package TestSF;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.ServiceProfileNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
//import omsTests.DatabaseAccess;

public class TestDeregisterService extends TestCase {

	Process qpid_broker;
	SFProxy sfProxy = null;
	Agent agent = null;
	OMS oms = null;
	SF sf = null;
	DatabaseAccess dbA = null;

	protected void setUp() throws Exception {
		super.setUp();
		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));

		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}

		AgentsConnection.connect();

		oms = new OMS(new AgentID("OMS"));

		sf = new SF(new AgentID("SF"));

		oms.start();
		sf.start();

		agent = new Agent(new AgentID("pruebas"));

		sfProxy = new SFProxy(agent);

		dbA = new DatabaseAccess();

		dbA.removeJenaTables();

	}

	protected void tearDown() throws Exception {
		super.tearDown();

		// ------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		dbA.removeJenaTables();
		// --------------------------------------------//

		sfProxy = null;

		agent.terminate();
		agent = null;

		oms.Shutdown();
		sf.Shutdown();

		oms.await();
		sf.await();

		oms = null;
		sf = null;
		
		qpid_broker.destroy();

	}

	/**
	 * Incorrect Service Profile. The deregisterService method is called with a
	 * string which not represents a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {
		try {
			sfProxy.deregisterService("dsic-upv-es");

			fail();

		} catch (ServiceProfileNotFoundException e) {

			assertNotNull(e);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * A URL of a service specification is provided instead a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest2() {
		try {
			sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls");

			fail();

		} catch (ServiceProfileNotFoundException e) {

			assertNotNull(e);

		} catch (Exception e) {
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
		try {
			sfProxy.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");

			fail();

		} catch (ServiceProfileNotFoundException e) {

			assertNotNull(e);

		} catch (Exception e) {
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
		try {
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile Deregistered",
					result);
		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
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
		try {
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile Deregistered",
					result);
		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
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
		try {
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile Deregistered",
					result);
		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Deregister the demanded web service Division.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest5() {
		try {
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile Deregistered",
					result);
		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
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
		try {
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile Deregistered",
					result);
		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Deregister the service Sign, which is provided by an agent behavior.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest7() {
		try {
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile Deregistered",
					result);
		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Deregister the service Proof, which has only been a provider registered
	 * that has been previously removed from service.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest8() {

		try {

			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Proof.owl");

			sfProxy.removeProvider(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Proof.owl#ProofProfile",
					"ProofAgent");

			String result = sfProxy
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Proof.owl#ProofProfile");

			assertEquals(
					"Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Proof.owl#ProofProfile Deregistered",
					result);

		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {

			fail(e.getMessage());

		}
	}

	/**
	 * Deregister the web service Square, which is directly provided by a
	 * web service and also by an agent behavior.
	 * 
	 * @return
	 */
	/*
	 * public void testAppropiateParamsTest9() { try { sfProxy.registerService(
	 * "http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl"
	 * );
	 * 
	 * String result = sfProxy .deregisterService(
	 * "http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile"
	 * );
	 * 
	 * assertEquals(
	 * "Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile Deregistered"
	 * , result); }catch(THOMASException e) {
	 * 
	 * assertNotNull(e);
	 * 
	 * } catch(Exception e) { fail(e.getMessage()); } }
	 */
}
