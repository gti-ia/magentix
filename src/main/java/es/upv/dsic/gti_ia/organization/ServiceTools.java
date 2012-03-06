package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * This class provides tools in order to facilitate the execution of the services.
 */
public class ServiceTools {

	ResponseParser rp = new ResponseParser();

	/**
	 * Builds a new string with a XML format. This XML is formed by service name and list of the 
	 * inputs
	 * 
	 * @param serviceName The name of the service
	 * @param inputs is a HashMap formed by keys and values
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
	 * Parses a XML in order to extract the service name and fill the HashMap with the service outputs
	 * 
	 * @param string with a XML format, in this XML will be contained the service name and service outputs
	 * @param outputs is a new HashMap. In this HashMap the service outputs are added
	 * @return service name
	 */
	public String extractServiceContent(String xml, HashMap<String, String> outputs) {

		rp.parseResponse(xml);

		String serviceName = rp.getServiceName();

		HashMap<String, String> outputAux = rp.getKeyAndValueList();

		for (Entry<String, String> e : outputAux.entrySet()) {

			outputs.put(e.getKey(), e.getValue());
		}

		return serviceName;
	}

	public HashMap<String, Object> executeWebService(String serviceWSDLURL, String xmlInputs){
		HashMap<String, String> inputs=new HashMap<String, String>();
		this.extractServiceContent(xmlInputs, inputs);
		return this.executeWebService(serviceWSDLURL, inputs);
	}
	
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
			//	System.out.println("inputParamName: " + e.getKey() + " value: " + e.getValue());
			}
		}

		// construct params list with the value of the parameters ordered...
		ArrayList<String> params = new ArrayList<String>();
		Iterator<String> iterInputs = processInputs.iterator();
		while (iterInputs.hasNext()) {
			String input = iterInputs.next();
			params.add(paramsComplete.get(input));
		}

		ServiceClient serviceClient = new ServiceClient();
		HashMap<String, Object> results= serviceClient.invoke(serviceWSDLURL, params);

		return results;
	}

}
