package TestSF;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.organization.OMS;
import es.upv.dsic.gti_ia.organization.SF;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.exception.InvalidDataTypeException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
//import omsTests.DatabaseAccess;

public class TestSearchService extends TestCase {

	SFProxy sfProxy = null;
	Agent agent = null;
	OMS oms = null;
	SF sf = null;

	DatabaseAccess dbA = null;
	private Process qpid_broker;

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
		AgentsConnection.disconnect();
		qpid_broker.destroy();
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
			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			fail();

		} catch (InvalidDataTypeException e) {

			assertNotNull(e);

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
			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			fail();

		} catch (InvalidDataTypeException e) {

			assertNotNull(e);

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

			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(6, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(1).get(0));
			assertEquals("1.0", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(2).get(0));
			assertEquals("1.0", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(3).get(0));
			assertEquals("0.5", result.get(3).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(4).get(0));
			assertEquals("0.5", result.get(4).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(5).get(0));
			assertEquals("0.5", result.get(5).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(6, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(1).get(0));
			assertEquals("1.0", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(2).get(0));
			assertEquals("1.0", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(3).get(0));
			assertEquals("0.5", result.get(3).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(4).get(0));
			assertEquals("0.5", result.get(4).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(5).get(0));
			assertEquals("0.5", result.get(5).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(6, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("0.6666667", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(1).get(0));
			assertEquals("0.6666667", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(2).get(0));
			assertEquals("0.6666667", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(3).get(0));
			assertEquals("0.33333334", result.get(3).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(4).get(0));
			assertEquals("0.33333334", result.get(4).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(5).get(0));
			assertEquals("0.33333334", result.get(5).get(1));
		} catch (THOMASException e) {

			fail(e.getMessage());

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

			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(4, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(1).get(0));
			assertEquals("1.0", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(2).get(0));
			assertEquals("1.0", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(3).get(0));
			assertEquals("1.0", result.get(3).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(4, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("0.5", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(1).get(0));
			assertEquals("0.5", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(2).get(0));
			assertEquals("0.5", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(3).get(0));
			assertEquals("0.5", result.get(3).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(1, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));
		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);
			assertEquals(1, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));
		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(1, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(1, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(3, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(1).get(0));
			assertEquals("0.5", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(2).get(0));
			assertEquals("0.5", result.get(2).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(6, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(1).get(0));
			assertEquals("0.5", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(2).get(0));
			assertEquals("0.5", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(3).get(0));
			assertEquals("0.25", result.get(3).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(4).get(0));
			assertEquals("0.25", result.get(4).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(5).get(0));
			assertEquals("0.25", result.get(5).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(6, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(0).get(0));
			assertEquals("0.6666667", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(1).get(0));
			assertEquals("0.6666667", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(2).get(0));
			assertEquals("0.33333334", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(3).get(0));
			assertEquals("0.16666667", result.get(3).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(4).get(0));
			assertEquals("0.16666667", result.get(4).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(5).get(0));
			assertEquals("0.16666667", result.get(5).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

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
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl");
			sfProxy.registerService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl");

			ArrayList<ArrayList<String>> result = sfProxy.searchService(inputs,
					outputs, keywords);

			assertEquals(6, result.size());

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Addition.owl#AdditionProfile",
					result.get(0).get(0));
			assertEquals("1.0", result.get(0).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl#ProductProfile",
					result.get(1).get(0));
			assertEquals("0.6666667", result.get(1).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile",
					result.get(2).get(0));
			assertEquals("0.6666667", result.get(2).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Square.owl#SquareProfile",
					result.get(3).get(0));
			assertEquals("0.5", result.get(3).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Even.owl#EvenProfile",
					result.get(4).get(0));
			assertEquals("0.16666667", result.get(4).get(1));

			assertEquals(
					"http://localhost:8080/testSFservices/testSFservices/owl/owls/Sign.owl#SignProfile",
					result.get(5).get(0));
			assertEquals("0.16666667", result.get(5).get(1));

		} catch (THOMASException e) {

			fail(e.getMessage());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
