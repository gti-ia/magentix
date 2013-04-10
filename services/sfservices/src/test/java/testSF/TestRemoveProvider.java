package testSF;

import persistence.SFinterface;
import junit.framework.TestCase;

public class TestRemoveProvider extends TestCase {

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
				+ "<serviceName>RemoveProvider</serviceName>\n"
				+ "<status>Error</status>\n" + "<result>\n" + "<description>"
				+ input + "</description>\n" + "</result>\n" + "</response>";

		return result;
	}

	public String getCorrectString(String input) {
		String result = "<response>\n"
				+ "<serviceName>RemoveProvider</serviceName>\n"
				+ "<status>Ok</status>\n" + "<result>\n" + "<description>"
				+ input + "</description>\n" + "</result>\n" + "</response>";

		return result;
	}

	/**
	 * Incorrect Service Profile. The removeProvider method is called with a
	 * string which not represents a Service Profile.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {

		try {

			String result = sf.removeProvider("dsic-upv-es", "AdditionAgent");

			assertEquals(
					getString("ERROR: java.util.NoSuchElementException"),
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
			String result = sf.removeProvider(
					"http://localhost/services/1.1/calculateSunriseTime.owls",
					"AdditionAgent");

			assertEquals(
					getString("ERROR: java.util.NoSuchElementException"),
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
					.removeProvider(
							"http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE",
							"Provider");

			assertEquals(
					getString("ERROR: Provider or grounding http://localhost/services/1.1/calculateSunriseTime.owls#CALCULATESUNRISETIME_PROFILE not found"),
					result);
		} catch (Exception e) {
			fail(e.getMessage());

		}
	}

	/**
	 * Correct service profile, but wrong provider name.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest4() {

		try {
			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
							"http://localhost/services/Tests/Square.owl#SQUARE_GROUNDING");

			assertEquals(
					getString("ERROR: Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile not found"),
					result);
		} catch (Exception e) {
			fail(e.getMessage());

		}
	}

	/**
	 * Correct service profile, but wrong grounding.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest5() {

		try {
			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
							"ProductAgent");

			assertEquals(
					getString("ERROR: Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile not found"),
					result);
		} catch (Exception e) {
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
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
							"ProductAgent");

			assertEquals(
					getCorrectString("Provider or grounding ProductAgent removed"),
					result);

		} catch (Exception e) {
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
		try {

			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
							"SquareAgent");

			assertEquals(
					getCorrectString("Provider or grounding SquareAgent removed"),
					result);
		} catch (Exception e) {
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
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
							"ProductAgent");

			assertEquals(
					getCorrectString("Provider or grounding ProductAgent removed"),
					result);
		} catch (Exception e) {
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
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding");

			assertEquals(
					getCorrectString("Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding removed"),
					result);
		} catch (Exception e) {
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
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductGrounding");

			assertEquals(
					getCorrectString("Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductGrounding removed"),
					result);
		} catch (Exception e) {
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
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String result = sf
					.removeProvider(
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
							"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding");

			assertEquals(
					getCorrectString("Provider or grounding http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareGrounding removed"),
					result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
