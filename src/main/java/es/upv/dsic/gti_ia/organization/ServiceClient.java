package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.namespace.QName;
import javax.xml.rpc.Call;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceFactory;

/**
 * This class provides support to the execution of services using the
 * javax.xml.rpc library
 */

public class ServiceClient {

	private static String qnameService;
	private static String qnamePort;
	private static String operationName;

	// By default it works this namespace
	private static String BODY_NAMESPACE_VALUE = "urn:Foo";

	// Default properties
	private static String ENCODING_STYLE_PROPERTY = "javax.xml.rpc.encodingstyle.namespace.uri";
	private static String NS_XSD = "http://www.w3.org/2001/XMLSchema";
	private static String URI_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";

	// Oracle to parse the WSDL document
	private Oracle oracle;

	/**
	 * Executes the service of the given WSDL URL with the given parameters.
	 * Returns the results of the service execution.
	 * 
	 * @param wsdlURL
	 *            of the service to execute
	 * @param inputParameters
	 *            list of the ordered input parameters of the service to execute
	 * @return a {@link HashMap} with the name of the outputs and their values
	 */
	public HashMap<String, Object> invoke(String wsdlURL, ArrayList<String> inputParameters) {

		try {

			oracle = new Oracle();

			oracle.parseWSDL(wsdlURL);

			qnameService = oracle.getWSDLNameService();

			qnamePort = oracle.getWSDLNamePort();

			operationName = oracle.getWSDLOperation();

			ServiceFactory factory = ServiceFactory.newInstance();

			Service service = factory.createService(new QName(qnameService));

			QName port = new QName(qnamePort);

			Call call = service.createCall(port);
			call.setTargetEndpointAddress(wsdlURL);
			call.setProperty(Call.SOAPACTION_USE_PROPERTY, new Boolean(true));
			call.setProperty(Call.SOAPACTION_URI_PROPERTY, "");
			call.setProperty(ENCODING_STYLE_PROPERTY, URI_ENCODING);
			call.setReturnType(new QName(NS_XSD, oracle.getWsdlOutputsTypes().get(
					oracle.getWsdlOutputsTypes().size() - 1)));
			call.setOperationName(new QName(BODY_NAMESPACE_VALUE, operationName));

			ArrayList<String> inputNames = oracle.getWSDLInputs();
			ArrayList<String> inputTypes = oracle.getWsdlInputsTypes();
			for (int i = 0; i < inputNames.size(); i++) {
				call.addParameter(inputNames.get(i), new QName(NS_XSD, inputTypes.get(i)), ParameterMode.IN);
			}

			// Invoke the service and gets the first output result
			Object firstResult = call.invoke(inputParameters.toArray());

			// Build result
			HashMap<String, Object> results = new HashMap<String, Object>();
			results.put(oracle.getWSDLOutputs().get(0), firstResult);
			for (int i = 1; i < oracle.getWSDLOutputs().size(); i++) {
				results.put(oracle.getWSDLOutputs().get(i), call.getOutputValues().get(i - 1));
			}

			return results;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
