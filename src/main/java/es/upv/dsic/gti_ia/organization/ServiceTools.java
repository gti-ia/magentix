package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * This class provides tools in order to facilitate the execution of the
 * services.
 */
public class ServiceTools {

	ResponseParser rp = new ResponseParser();

	/**
	 * Builds a new string with a XML format. This XML is formed by service name
	 * and list of the inputs
	 * 
	 * @param serviceName
	 *            The name of the service
	 * @param inputs
	 *            is a HashMap formed by keys and values
	 * @return string with a XML format.
	 */
	public String buildServiceContent(String serviceName, HashMap<String, String> inputs) {
		String resultXML = "";

		resultXML += "<serviceInput>\n";
		resultXML += "<serviceName>" + serviceName + "</serviceName>\n";
		resultXML += "<inputs>\n";
		for (Entry<String, String> e : inputs.entrySet()) {
			resultXML += "<" + e.getKey() + ">" + e.getValue() + "</" + e.getKey() + ">\n";
		}
		resultXML += "</inputs>\n";
		resultXML += "</serviceInput>\n";
		return resultXML;

	}

	/**
	 * Parses a XML in order to extract the service name and fill the
	 * {@link HashMap} with the service inputs or outputs
	 * 
	 * @param string
	 *            with a XML format, in this XML will be contained the service
	 *            name and service input or outputs
	 * @param inOutputs
	 *            is a new {@link HashMap}. In this {@link HashMap} the service
	 *            inputs or outputs are added
	 * @return service name
	 */
	public String extractServiceContent(String xml, HashMap<String, String> inOutputs) {

		rp.parseResponse(xml);

		String serviceName = rp.getServiceName();

		HashMap<String, String> outputAux = rp.getKeyAndValueList();

		for (Entry<String, String> e : outputAux.entrySet()) {

			inOutputs.put(e.getKey(), e.getValue());
		}

		return serviceName;
	}

	/**
	 * Executes the Web Service of the given WSDL URL with the provided inputs.
	 * Returns the results of the service execution
	 * 
	 * @param serviceWSDLURL
	 *            to execute
	 * @param xmlInputs
	 *            inputs in a XML structure with name of the input and its value
	 *            ex. <inputs><inputX>valueX</inputX></inputs>
	 * @return {@link HashMap} with the output results with name of the output
	 *         and its value
	 */
	public HashMap<String, Object> executeWebService(String serviceWSDLURL, String xmlInputs) {
		HashMap<String, String> inputs = new HashMap<String, String>();
		this.extractServiceContent(xmlInputs, inputs);
		return this.executeWebService(serviceWSDLURL, inputs);
	}

	/**
	 * Executes the Web Service of the given WSDL URL with the provided inputs.
	 * Returns the results of the service execution
	 * 
	 * @param serviceWSDLURL
	 *            to execute
	 * @param inputs
	 *            {@link HashMap} with name of the input and its value
	 * @return {@link HashMap} with the output results with name of the output
	 *         and its value
	 */
	public HashMap<String, Object> executeWebService(String serviceWSDLURL, HashMap<String, String> inputs) {

		Oracle oracle = new Oracle();
		oracle.parseWSDL(serviceWSDLURL);

		ArrayList<String> processInputs = oracle.getWSDLInputs();

		HashMap<String, String> paramsComplete = new HashMap<String, String>();
		Iterator<String> iterProcessInputs = processInputs.iterator();
		while (iterProcessInputs.hasNext()) {
			String in = iterProcessInputs.next();
			// initialize the inputs
			paramsComplete.put(in, "");
		}

		for (Entry<String, String> e : inputs.entrySet()) {

			if (paramsComplete.get(e.getKey()) != null) {
				paramsComplete.put(e.getKey(), e.getValue());
			}
		}

		// construct parameters list with the value of the parameters ordered
		ArrayList<String> params = new ArrayList<String>();
		Iterator<String> iterInputs = processInputs.iterator();
		while (iterInputs.hasNext()) {
			String input = iterInputs.next();
			params.add(paramsComplete.get(input));
		}

		ServiceClient serviceClient = new ServiceClient();
		HashMap<String, Object> results = serviceClient.invoke(serviceWSDLURL, params);

		return results;
	}

}
