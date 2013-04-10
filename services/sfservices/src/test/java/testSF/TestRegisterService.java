package testSF;

import persistence.SFinterface;
import junit.framework.TestCase;

public class TestRegisterService extends TestCase {

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
				+ "<serviceName>RegisterService</serviceName>\n"
				+ "<status>Error</status>\n" + "<result>\n" + "<description>"
				+ input + "</description>\n" + "</result>\n" + "</response>";

		return result;
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

		try {
			String result = sf.registerService("dsic-upv-es");

			assertEquals(
					getString("ERROR: dsic-upv-es is not a valid OWL-S document"),
					result);

		} catch (Exception e) {
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

		try {
			String result = sf.registerService("http://gti-ia.dsic.upv.es");

			assertEquals(
					getString("ERROR: http://gti-ia.dsic.upv.es is not a valid OWL-S document"),
					result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Correct URL but without file associate.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest3() {

		try {
			String result = sf
					.registerService("http://localhost/services/1.1/nonExistingService.owl");

			assertEquals(
					getString("ERROR: http://localhost/services/1.1/nonExistingService.owl is not a valid OWL-S document"),
					result);

		} catch (Exception e) {
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

		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);

		} catch (Exception e) {
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
		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
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
		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
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
		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
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
		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
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
		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
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

		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division2.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		try {
			String result = sf
					.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division3.owl");

			String expected = "<response>\n"
					+ "<serviceName>RegisterService</serviceName>\n"
					+ "<status>Ok</status>\n";

			result = result.substring(0, result.indexOf("<result>"));

			assertEquals(expected, result);
		} catch (Exception e) {
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
	 */
	/*
	 * public void testAppropiateParamsTest11() { try{ ArrayList<String> result
	 * = sfProxy.registerService(
	 * "http://localhost:8080/testSFservices/testSFservices/owl/owls/Division2.owl"
	 * );
	 * 
	 * assertEquals(
	 * "0 groundings and 2 providers registered to service profile: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile"
	 * , result.get(0)); }catch(THOMASException e) {
	 * 
	 * fail(e.getMessage());
	 * 
	 * } catch(Exception e) { fail(e.getMessage()); } }
	 *//**
	 * Adding new web services which will offer a register demanded service.
	 * A new OWL-S file will be created to specify the service Division, using
	 * the original one but adding and eliminating the information required to
	 * register two new groundings.
	 * 
	 * @return
	 */
	/*
	 * public void testAppropiateParamsTest12() { try{ ArrayList<String> result
	 * = sfProxy.registerService(
	 * "http://localhost:8080/testSFservices/testSFservices/owl/owls/Division3.owl"
	 * );
	 * 
	 * assertEquals(
	 * "2 groundings and 0 providers registered to service profile: http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile"
	 * , result.get(0)); }catch(THOMASException e) {
	 * 
	 * fail(e.getMessage());
	 * 
	 * } catch(Exception e) { fail(e.getMessage()); } }
	 */

}
