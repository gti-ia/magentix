package testSF;

import persistence.SFinterface;
import junit.framework.TestCase;

public class TestDeregisterService extends TestCase {

	SFinterface sf = new SFinterface();

	DatabaseAccess dbA = null;

	protected void setUp() throws Exception {
		super.setUp();

		dbA = new DatabaseAccess();

		dbA.removeJenaTables();

	}

	protected void tearDown() throws Exception {
		super.tearDown();

		sf = null;

	}

	public String getCorrectString(String input) {
		String result = "<response>\n"
				+ "<serviceName>DeregisterService</serviceName>\n"
				+ "<status>Ok</status>\n" + "<result>\n" + "<description>"
				+ input + "</description>\n" + "</result>\n" + "</response>";

		return result;
	}

	/**
	 * Incorrect Service Profile. The deregisterService method is called with a
	 * string which not represents a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {
		try {
			String result = sf.deregisterService("dsic-upv-es");

			assertEquals(
					"<response>\n"
							+ "<serviceName>DeregisterService</serviceName>\n"
							+ "<status>Error</status>\n"
							+ "<result>\n"
							+ "<description>ERROR: Service dsic-upv-es does not exist</description>\n"
							+ "</result>\n" + "</response>", result);

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
			String result = sf
					.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls");

			assertEquals(
					"<response>\n"
							+ "<serviceName>DeregisterService</serviceName>\n"
							+ "<status>Error</status>\n"
							+ "<result>\n"
							+ "<description>ERROR: Service http://localhost/services/1.1/calculateSunriseTime.owls does not exist</description>\n"
							+ "</result>\n" + "</response>", result);

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
			String result = sf
					.deregisterService("http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");

			assertEquals(
					"<response>\n"
							+ "<serviceName>DeregisterService</serviceName>\n"
							+ "<status>Error</status>\n"
							+ "<result>\n"
							+ "<description>ERROR: Service http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE does not exist</description>\n"
							+ "</result>\n" + "</response>", result);

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
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");

			assertEquals(
					getCorrectString("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile Deregistered"),
					result);
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
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");

			String result = sf
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");

			assertEquals(
					getCorrectString("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile Deregistered"),
					result);
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
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sf
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");

			assertEquals(
					getCorrectString("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile Deregistered"),
					result);
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
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

			assertEquals(
					getCorrectString("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile Deregistered"),
					result);
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
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			String result = sf
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");

			assertEquals(
					getCorrectString("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile Deregistered"),
					result);
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
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sf
					.deregisterService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");

			assertEquals(
					getCorrectString("Service http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile Deregistered"),
					result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/*	*//**
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
