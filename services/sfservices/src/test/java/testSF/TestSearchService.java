package testSF;

import java.util.ArrayList;

import persistence.SFinterface;

import junit.framework.TestCase;

public class TestSearchService extends TestCase {

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
				+ "<serviceName>SearchService</serviceName>\n"
				+ "<status>Error</status>\n" + "<result>\n" + "<description>"
				+ input + "</description>\n" + "</result>\n" + "</response>";

		return result;
	}

	/**
	 * Incorrect inputs, empty outputs and keywords. The searchService method is
	 * called with a string which not represents an input data type.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest1() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("Notype");

		try {
			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(getString("ERROR: incorrect input or output data type"),
					result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Incorrect outputs, empty inputs and keywords. The getService method is
	 * called with a string which not represents an output data type.
	 * 
	 * @return
	 */
	public void testIncorrectParamTest2() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		outputs.add("Notype");

		try {
			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(getString("ERROR: incorrect input or output data type"),
					result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing only one input parameter of type double.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest1() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {

			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing two input parameters of type double.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest2() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");

		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing three input parameters of type double.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest3() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");

		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>0.33333334</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>0.33333334</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>0.33333334</quantity>\n"
							+ "\t</item>\n" + "</result>\n" + "</response>",
					result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing only one output parameter of type double.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest4() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {

			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing two output parameters of type double.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest5() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing only one output parameter of type boolean.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest6() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing only one output parameter of type string.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest7() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		outputs.add("\"http://www.w3.org/2001/XMLSchema#string\"^^xsd:anyURI");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sf.searchService(inputs, outputs, keywords);
			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing only one keyword.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest8() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("product");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing only one keyword.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest9() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("returns the product");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing two words as keywords.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest10() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("product");
		keywords.add("numbers");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing two words as keywords.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest11() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>0.25</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>0.25</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.25</quantity>\n" + "\t</item>\n"
							+ "</result>\n" + "</response>", result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing one input parameters of type double, one
	 * output parameter of type boolean and one keyword.
	 * 
	 * @return
	 */
	public void testAppropiateParamsTest12() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#boolean\"^^xsd:anyURI");
		keywords.add("positive");
		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>0.33333334</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>0.16666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>0.16666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.16666667</quantity>\n"
							+ "\t</item>\n" + "</result>\n" + "</response>",
					result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Search for a service providing two input parameters of type double, one
	 * output parameter of type double and phrase as keyword.
	 */
	public void testAppropiateParamsTest13() {
		ArrayList<String> inputs = new ArrayList<String>();
		ArrayList<String> outputs = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		inputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		outputs.add("\"http://www.w3.org/2001/XMLSchema#double\"^^xsd:anyURI");
		keywords.add("returns the addition");

		try {
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sf.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			String result = sf.searchService(inputs, outputs, keywords);

			assertEquals(
					"<response>\n"
							+ "<serviceName>SearchService</serviceName>\n"
							+ "<status>Ok</status>\n"
							+ "<result>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile</profile>\n"
							+ "\t\t<quantity>1.0</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile</profile>\n"
							+ "\t\t<quantity>0.6666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile</profile>\n"
							+ "\t\t<quantity>0.5</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile</profile>\n"
							+ "\t\t<quantity>0.16666667</quantity>\n"
							+ "\t</item>\n"
							+ "\t<item>\n"
							+ "\t\t<profile>http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile</profile>\n"
							+ "\t\t<quantity>0.16666667</quantity>\n"
							+ "\t</item>\n" + "</result>\n" + "</response>",
					result);

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
