package testSF;

import persistence.SFinterface;
import junit.framework.TestCase;

public class TestGetService extends TestCase {

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

	public String getString(String input) {
		String result = "<response>\n"
				+ "<serviceName>GetService</serviceName>\n"
				+ "<status>Error</status>\n" + "<result>\n" + "<description>"
				+ input + "</description>\n" + "</result>\n" + "</response>";

		return result;
	}

	/**
	 * Incorrect Service Profile. The getService method is called with a string
	 * which not represents a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {

		try {
			String result = sf.getService("dsic-upv-es");

			assertEquals(
					getString("ERROR: Service dsic-upv-es does not exist"),
					result);

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
					.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");

			assertEquals(
					getString("ERROR: Service http://127.0.0.1/services/1.1/calculateSunriseTime.owls does not exist"),
					result);

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
					.getService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE");

			assertEquals(
					getString("ERROR: Service http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE does not exist"),
					result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the web service Product, which is provided by one agent behavior. The
	 * service specification provided by the service should be checked in order
	 * to ensure that provider data and also the input and output parameters are
	 * properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest2() {

		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the web service Addition, which is provided by one agent which
	 * internally calls to a web service. The service specification provided by
	 * the service should be checked in order to ensure that provider data and
	 * also the input and output parameters are properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest3() {
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the web service Square, which is directly provided by a web service.
	 * The service specification provided by the service should be checked in
	 * order to ensure that grounding data and also the input and output
	 * parameters are properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest4() {
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the demanded web service Division. The service specification provided
	 * by the service should be checked in order to ensure that the input and
	 * output parameters are properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest5() {
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Even, which is provided by an organization by means of a
	 * web service. The service specification provided by the service should be
	 * checked in order to ensure that provider data and also the input and
	 * output parameters are properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest6() {
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Sign, which is provided by an agent behavior. The service
	 * specification provided by the service should be checked in order to
	 * ensure that provider data and also the input and output parameters are
	 * properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest7() {
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Square, which is provided both by a web service and by an
	 * agent behavior in this case. The service specification provided by the
	 * service should be checked in order to ensure that provider data,
	 * grounding data and also the input and output parameters are properly
	 * retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest8() {

		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Get the service Division, which is provided both by an organization and
	 * by an agent behavior in this case. The service specification provided by
	 * the service should be checked in order to ensure that providers data and
	 * also the input and output parameters are properly retrieved.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest9() {
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf
					.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

			String expected = "<response>\n"
					+ "<serviceName>GetService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
