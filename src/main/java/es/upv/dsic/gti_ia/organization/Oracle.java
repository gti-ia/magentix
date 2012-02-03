package es.upv.dsic.gti_ia.organization;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class allows us to parse a profile in order to extract relevant
 * information, such as service inputs, outputs, list of roles for both
 * providers, such as customers.
 * 
 */
public class Oracle {

    /*
     * Attributes
     */
    private Document doc = null;
    private ArrayList<String> inputs;
    private ArrayList<String> outputs;
    private ArrayList<String> providerList;
    private ArrayList<String> clientList;
    private ArrayList<String> clientunitList;
    private ArrayList<String> providerunitList;
    private ArrayList<String> inputTypes = new ArrayList<String>();
    private ArrayList<String> outputTypes = new ArrayList<String>();

    private ArrayList<String> inputProcess = new ArrayList<String>();
    private ArrayList<String> outputProcess = new ArrayList<String>();
    
    private ArrayList<String> wsdlInputParams = new ArrayList<String>();

    private Map<String, String> elements = new LinkedHashMap<String, String>();

    private String wsdl;
    private String qnameService;
    private String qnamePort;
    private String operation;

    private String serviceName;

    private String input_message_WSDL;
    private String output_message_WSDL;

    private String input_Name_WSDL;
    private String output_Name_WSDL;

    private String processLocalName;

    private boolean first = true;

    private boolean open = true;

    private String URLProcess;

    // private boolean webService;
    // private boolean behaviour;

    /*
     * Methods
     */

    /**
     * Returns the Name Service parameter of WSDL file parsed
     * 
     * @return Returns the Name Service parameter of WSDL file parsed
     */
    public String getWSDLNameService() {
    	return qnameService;
    }

    /**
     * Returns the Name Port parameter of WSDL file parsed
     * 
     * @return Returns the Name Port parameter of WSDL file parsed
     */
    public String getNamePort() {
    	return qnamePort;
    }

    /**
     * Returns the Operation parameter of WSDL file parsed
     * 
     * @return Returns the Operation parameter of WSDL file parsed
     */
    public String getOperation() {
    	return operation;
    }

    /**
     * Returns the WSDL parameter of owls file parsed
     * 
     * @return Returns the WSDL parameter of owls file parsed
     */
    public String getWSDL() {
    	return wsdl;
    }

    /**
     * Returns the Elements parameters of owls file parsed
     * 
     * @return Returns the Elements of owls file parsed
     */
    public Map<String, String> getElements() {
    	return elements;
    }

    /**
     * Returns the Output types parameters of owls file parsed
     * 
     * @return Returns the input parameters of owls file parsed
     */
    public ArrayList<String> getOutputsTypes() {
    	return outputTypes;
    }

    /**
     * Returns the input types parameters of owls file parsed
     * 
     * @return Returns the input parameters of owls file parsed
     */
    public ArrayList<String> getInputsTypes() {
    	return inputTypes;
    }

    /**
     * Returns the input parameters of owls file parsed
     * 
     * @return Returns the input parameters of owls file parsed
     */
    public ArrayList<String> getInputs() {
    	return inputs;
    }

    /**
     * Returns the input parameters of owls file parsed
     * 
     * @return Returns the input parameters of owls file parsed
     */
    public ArrayList<String> getProcessOutputs() {
    	return outputProcess;
    }

    /**
     * Returns the input parameters of owls file parsed
     * 
     * @return Returns the input parameters of owls file parsed
     */
    public ArrayList<String> getProcessInputs() {
    	return inputProcess;
    }

    /**
     * Returns the output parameters of owls file parsed
     * 
     * @return Returns the output parameters of owls file parsed
     */
    public ArrayList<String> getOutputs() {
    	return outputs;
    }

    /**
     * Returns the list of roles available to provide the service
     * 
     * @return Returns the list of roles available to provide the service
     */
    public ArrayList<String> getProviderList() {
    	return providerList;
    }

    /**
     * Returns the list of roles required to use the service
     * 
     * @return Returns the list of roles required to use the service
     */
    public ArrayList<String> getClientList() {
    	return clientList;
    }

    /**
     * 
     * Returns the behaviour name to execute on provider
     * 
     * @return Returns the behaviour name to execute on provider
     */
    public String getServiceName() {
    	return serviceName;
    }

    // boolean isWebService() {
    // return webService;
    // }
    //
    // public boolean isBehaviour() {
    // return behaviour;
    // }

    /**
     * Returns providerUnitList is a unit where the role client is defined
     * 
     * @return providerUnitList is a unit where the role client is defined
     */
    public ArrayList<String> getClientUnitList() {
    	return this.clientunitList;
    }

    /**
     * Returns unitList this parameter is a unit where the role client is
     * defined
     * 
     * @param unitList
     *            this parameter is a unit where the role client is defined
     */
    public void setClientUnitList(ArrayList<String> unitList) {
    	this.clientunitList = unitList;
    }

    /**
     * Returns providerUnitList is a unit where the role provider is defined
     * 
     * @return providerUnitList is a unit where the role provider is defined
     */
    public ArrayList<String> getProviderUnitList() {
    	return this.providerunitList;
    }

    /**
     * Returns unitList this parameter is a unit where the role provider is
     * defined
     * 
     * @param unitList
     *            this parameter is a unit where the role provider is defined
     */
    public void setProviderUnitList(ArrayList<String> unitList) {
    	this.providerunitList = unitList;
    }

    public String getProcessLocalName() {
    	return processLocalName;
    }

    public ArrayList<String> getWSDLInputs() {
    	return wsdlInputParams;
    }
    /**
     * Method to parses an OWL-S file
     * 
     * @param file
     */
    public Oracle(File file) {
		try {
		    doc = parserXML(file);
	
		    visit(doc, 0);
	
		    // Change flags
		    // behaviour = true;
		    // webService = false;
	
		} catch (Exception error) {
		    error.printStackTrace();
		}
    }

    /**
     * Method to parses an OWL-S URL
     * 
     * @param url
     */
    public Oracle(URL url) {
		try {
		    doc = parserXML(url);
	
		    visit(doc, 0);
	
		    // Change flags
		    // behaviour = false;
		    // webService = true;
	
		} catch (Exception error) {
		    error.printStackTrace();
		}
    }

    public Oracle() {

    }

    public void setURLProcess(String _URLProcess) {

		URL process;
		URLProcess = _URLProcess;
		try {
	
		    process = new URL(URLProcess);
	
		    doc = parserXML(process);
	
		    visitNodeProcess(doc, 0);
	
		    visitProcess(process.toString());
	
		    this.visitWSDL_extract(wsdl + "?.wsdl", "input");
		    this.visitWSDL_extract(wsdl + "?.wsdl", "output");
		    this.visitWSDL_extract(wsdl + "?.wsdl", "part");
	
		    this.visitWSDL(wsdl + "?.wsdl");
	
		} catch (Exception error) {
		    error.printStackTrace();
		}
    }

    private void visitProcess(String url) {
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
	
		// Create a method instance.
		GetMethod method = new GetMethod(url);
	
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			new DefaultHttpMethodRetryHandler(3, false));
	
		try {
		    // Execute the method.
		    int statusCode = client.executeMethod(method);
	
		    if (statusCode != HttpStatus.SC_OK) {
		    	System.err.println("Method failed: " + method.getStatusLine());
		    }
	
		    // Read the response body.
	//	    byte[] responseBody = method.getResponseBody();
	
	//	    InputStream is = new ByteArrayInputStream(responseBody);
		    
		    InputStream is = method.getResponseBodyAsStream();
	
		    XMLInputFactory factory = XMLInputFactory.newInstance();
		    XMLStreamReader reader = factory.createXMLStreamReader(is);
	
		    while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				switch (reader.next()) {
				case XMLStreamConstants.START_ELEMENT:
					//System.out.println("LocalName: "+reader.getLocalName()+" prefix: "+ reader.getPrefix() + " text: ");

				    if (reader.getLocalName().equals("hasInput") && reader.getPrefix().equals("process")) {
				    	inputProcess.add(reader.getAttributeValue(0).substring(
				    			reader.getAttributeValue(0).indexOf("#") + 1));
				    } else if (reader.getLocalName().equals("hasOutput") && reader.getPrefix().equals("process")) {
				    	outputProcess.add(reader.getAttributeValue(0).substring(
				    			reader.getAttributeValue(0).indexOf("#") + 1));
				    } else if (reader.getLocalName().equals("owlsProcess")) {
				    	processLocalName = reader.getAttributeValue(0).substring(
				    			reader.getAttributeValue(0).indexOf("#") + 1);
				    }
				break;
				    
				}
		    }
	
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (XMLStreamException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (HttpException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}

    }

    private void visitWSDL_extract(String url, String element) {

		first = true;
		boolean first_input = false;
		boolean mutex = true;
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
	
		// Create a method instance.
		GetMethod method = new GetMethod(url);
	
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			new DefaultHttpMethodRetryHandler(3, false));
	
		try {
		    // Execute the method.
		    int statusCode = client.executeMethod(method);
	
		    if (statusCode != HttpStatus.SC_OK) {
		    	System.err.println("Method failed: " + method.getStatusLine());
		    }
	
		    // Read the response body.
	//	    byte[] responseBody = method.getResponseBody();
		    
	//	    InputStream is = new ByteArrayInputStream(responseBody);
		    
		    InputStream is =  method.getResponseBodyAsStream();
		    
		    XMLInputFactory factory = XMLInputFactory.newInstance();
		    XMLStreamReader reader = factory.createXMLStreamReader(is);
	
		    while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				switch (reader.next()) {
				case XMLStreamConstants.START_ELEMENT:
		
				    if (reader.getLocalName().equals("message") & element.equals("part")) {
		
						// Si se encuentra este primero el input va delante.
						if (mutex) {
						    if (reader.getAttributeValue(0).equals(input_Name_WSDL))
							first_input = true;
						    mutex = false;
						}
		
				    }
		
				    if (reader.getLocalName().equals(element) & element.equals("part")) {
		
						if (first_input) {
						    input_message_WSDL = reader.getAttributeValue(1).substring(
							    reader.getAttributeValue(1).indexOf(":") + 1);
						    first_input = false;
						} else {
			
						    output_message_WSDL = reader.getAttributeValue(1).substring(
							    reader.getAttributeValue(1).indexOf(":") + 1);
						    first_input = true;
						}
				    } else if (reader.getLocalName().equals(element) & element.equals("input")) {
		
						if (first) {
						    input_Name_WSDL = reader.getAttributeValue(0).substring(
							    reader.getAttributeValue(0).indexOf(":") + 1);
						    first = false;
						}
				    }
		
				    else if (reader.getLocalName().equals(element) & element.equals("output")) {
		
						if (first) {
						    output_Name_WSDL = reader.getAttributeValue(0).substring(
							    reader.getAttributeValue(0).indexOf(":") + 1);
						    first = false;
						}
				    }
		
				    break;
		
				}
		    }
	
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (XMLStreamException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (HttpException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
    }

    private void visitWSDL(String url) {

		open = true;
	
		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();
	
		// Create a method instance.
		GetMethod method = new GetMethod(url);
	
		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
			new DefaultHttpMethodRetryHandler(3, false));
	
		try {
		    // Execute the method.
		    int statusCode = client.executeMethod(method);
	
		    if (statusCode != HttpStatus.SC_OK) {
		    	System.err.println("Method failed: " + method.getStatusLine());
		    }
	
		    // Read the response body.
	//	    byte[] responseBody = method.getResponseBody();
	
	//	    InputStream is = new ByteArrayInputStream(responseBody);
		    
		    InputStream is = method.getResponseBodyAsStream();
	
		    XMLInputFactory factory = XMLInputFactory.newInstance();
		    XMLStreamReader reader = factory.createXMLStreamReader(is);
	
		    ArrayList<String> parameters=new ArrayList<String>();
		    
		    while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				switch (reader.next()) {
				case XMLStreamConstants.START_ELEMENT:
//					System.out.println(reader.getPrefix()+" "+reader.getLocalName());
				    if (reader.getLocalName().equals("service")) {
						// Sacamos el name="...
						// System.out.println("Type: " +
						// reader.getAttributeLocalName(0));
						qnameService = reader.getAttributeLocalName(0);
				    } 
				    else if (reader.getLocalName().equals("portType")) {
				    	qnamePort = reader.getAttributeValue(0);
				    } 
				    else if (reader.getLocalName().equals("operation")
					    & reader.getPrefix().equals("wsdl")) {
				    	operation = reader.getAttributeValue(0);
				    }
				    else if (reader.getLocalName().equals("element")) {
				    	if(reader.getPrefix().equals("xsd")){
				    		parameters.add(reader.getAttributeValue(0));
//				    		System.out.println("XXX "+reader.getAttributeValue(0));
				    	}
						// Deja de leer
						if (reader.getAttributeValue(0).equals(output_message_WSDL))
						    open = false;
						// if (reader.getPrefix())
						if (reader.getAttributeCount() == 2 & open) {
						    elements.put(reader.getAttributeValue(0), reader.getAttributeValue(1)
							    .substring(reader.getAttributeValue(1).indexOf(":") + 1));
						}
						// elements.add(reader.getAttributeValue(0));
				    }
		
				    break;
		
				}
		    }
		    
		    String serviceName=parameters.remove(0);
		    Iterator<String> iterParams=parameters.iterator();
		    while(iterParams.hasNext()){
		    	String param=iterParams.next();
		    	if(param.contains(serviceName))
		    		break;
		    	wsdlInputParams.add(param);
		    	System.out.println("param: "+param);
		    }
	
		} catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (XMLStreamException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (HttpException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
    }

    private void visitNodeProcess(Node node, int level) {
		NodeList nl = node.getChildNodes();
	
		inputTypes.clear();
		for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
		    // Child node examined
		    Node childNode = nl.item(i);
		    // System.out.println("Node: "+ childNode.getTextContent());
		    NodeList n = childNode.getChildNodes();
		    for (int j = 0; j < n.getLength(); j++) {
	
			// System.out.println("Node Name: "+ childNode.getChildNodes());
			// It is the service name (aka provider's behaviour to execute)
			if (n.item(j).getNodeName().equalsIgnoreCase("process:Input")) {
	
			    NodeList n1 = n.item(j).getChildNodes();
	
			    for (int k = 0; k < n1.getLength(); k++) {
				if (n1.item(k).getNodeName().equalsIgnoreCase("process:parameterType")) {
	
				    String type = n1.item(k).getTextContent()
					    .substring(n1.item(k).getTextContent().indexOf("#") + 1);
				    inputTypes.add(type);
				}
	
			    }
	
			} else if (n.item(j).getNodeName().equalsIgnoreCase("process:Output")) {
	
			    NodeList n1 = n.item(j).getChildNodes();
	
			    for (int k = 0; k < n1.getLength(); k++) {
				if (n1.item(k).getNodeName().equalsIgnoreCase("process:parameterType")) {
	
				    String type = n1.item(k).getTextContent()
					    .substring(n1.item(k).getTextContent().indexOf("#") + 1);
				    outputTypes.add(type);
				}
	
			    }
	
			}
	
			else if (n.item(j).getNodeName()
				.equalsIgnoreCase("grounding:WsdlAtomicProcessGrounding")) {
			    NodeList n1 = n.item(j).getChildNodes();
	
			    for (int k = 0; k < n1.getLength(); k++) {
				if (n1.item(k).getNodeName().equalsIgnoreCase("grounding:wsdlDocument")) {
	
				    wsdl = n1.item(k).getTextContent()
					    .substring(0, n1.item(k).getTextContent().indexOf("?"));
	
				}
	
			    }
			}
	
		    }
	
		    visit(childNode, level + 1);
		}
    }

    private void visit(Node node, int level) {
		NodeList nl = node.getChildNodes();
	
		for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
	
		    // Child node examined
		    Node childNode = nl.item(i);
	
		    // It is the service name (aka provider's behaviour to execute)
		    if (childNode.getNodeName().equalsIgnoreCase("profile:serviceName")) {
	
			int acumulador = 0;
			acumulador++;
			// Check node's Value before assign
			String nodeTextContent = childNode.getFirstChild().getNodeValue();
	
			if (nodeTextContent.contains("#"))
			    nodeTextContent = nodeTextContent.substring(nodeTextContent.indexOf("#") + 1);
	
			serviceName = nodeTextContent;
		    }
	
		    // It is an input parameter of Web Service
		    else if (childNode.getNodeName().equalsIgnoreCase("profile:hasInput")) {
	
			// Check node's attributes before add to ArrayList
			if (childNode.hasAttributes()) {
			    if (inputs == null)
				inputs = new ArrayList<String>();
	
			    // Check node's ID Value before add to ArrayList
			    String nodeIDValue = childNode.getAttributes().item(0).getNodeValue();
	
			    if (nodeIDValue.contains("#"))
				nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);
	
			    inputs.add(nodeIDValue);
			}
		    }
	
		    // It is an output parameter of Web Service
		    else if (childNode.getNodeName().equalsIgnoreCase("profile:hasOutput")) {
	
			// Check node's attributes before add to ArrayList
			if (childNode.hasAttributes()) {
			    if (outputs == null)
				outputs = new ArrayList<String>();
	
			    // Check node's ID Value before add to ArrayList
			    String nodeIDValue = childNode.getAttributes().item(0).getNodeValue();
	
			    if (nodeIDValue.contains("#"))
				nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);
	
			    outputs.add(nodeIDValue);
			}
		    }
	
		    // It is a role of provider list
		    else if (childNode.getNodeName().equalsIgnoreCase("role:role_list")
			    && childNode.getAttributes().item(0).getNodeValue()
				    .equalsIgnoreCase("provider_list")) {
	
			// Extract list of grandson
			NodeList grandsonList = childNode.getChildNodes();
	
			for (int j = 0, length = grandsonList.getLength(); j < length; j++) {
			    // Grandson Node examined
			    Node grandsonNode = grandsonList.item(j);
	
			    if (grandsonNode.getNodeName().equalsIgnoreCase("role:role_atom")) {
	
				// Check node's attributes before add to ArrayList
				if (grandsonNode.hasAttributes()) {
				    if (providerList == null)
					providerList = new ArrayList<String>();
	
				    // Check node's ID Value before add to ArrayList
				    String nodeIDValue = grandsonNode.getAttributes().item(0)
					    .getNodeValue();
	
				    if (nodeIDValue.contains("#"))
					nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);
	
				    providerList.add(nodeIDValue);
				}
	
				// Check the necessary Unit to acquire this role (first
				// child)
				NodeList greatgrandsonNodeList = grandsonNode.getChildNodes();
	
				int listLength = greatgrandsonNodeList.getLength();
				for (int k = 0; k < listLength; k++) {
				    // Greatgrandson Node examined
				    Node greatgrandsonNode = greatgrandsonNodeList.item(k);
	
				    if (greatgrandsonNode.getNodeName()
					    .equalsIgnoreCase("role:isDefinedIn")) {
	
					// Check node's attributes before add to
					// ArrayList
					if (greatgrandsonNode.hasAttributes()) {
					    if (providerunitList == null)
						providerunitList = new ArrayList<String>();
	
					    // Check node's ID Value before add to
					    // ArrayList
					    String nodeIDValue = greatgrandsonNode.getAttributes().item(0)
						    .getNodeValue();
	
					    if (nodeIDValue.contains("#"))
						nodeIDValue = nodeIDValue.substring(nodeIDValue
							.indexOf("#") + 1);
	
					    providerunitList.add(nodeIDValue);
					}
				    }
	
				}
	
			    }
			}
	
		    }
	
		    // It is a role of client list
		    else if (childNode.getNodeName().equalsIgnoreCase("role:role_list")
			    && childNode.getAttributes().item(0).getNodeValue()
				    .equalsIgnoreCase("client_list")) {
	
			// Extract list of grandson
			NodeList grandsonList = childNode.getChildNodes();
	
			for (int j = 0, length = grandsonList.getLength(); j < length; j++) {
			    // Grandson Node examined
			    Node grandsonNode = grandsonList.item(j);
	
			    if (grandsonNode.getNodeName().equalsIgnoreCase("role:role_atom")) {
	
				// Check node's attributes before add to ArrayList
				if (grandsonNode.hasAttributes()) {
				    if (clientList == null)
					clientList = new ArrayList<String>();
	
				    // Check node's ID Value before add to ArrayList
				    String nodeIDValue = grandsonNode.getAttributes().item(0)
					    .getNodeValue();
	
				    if (nodeIDValue.contains("#"))
					nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);
	
				    clientList.add(nodeIDValue);
				}
	
				// Check the necessary Unit to acquire this role (first
				// child)
				NodeList greatgrandsonNodeList = grandsonNode.getChildNodes();
	
				int listLength = greatgrandsonNodeList.getLength();
				for (int k = 0; k < listLength; k++) {
				    // Greatgrandson Node examined
				    Node greatgrandsonNode = greatgrandsonNodeList.item(k);
	
				    if (greatgrandsonNode.getNodeName()
					    .equalsIgnoreCase("role:isDefinedIn")) {
	
					// Check node's attributes before add to
					// ArrayList
					if (greatgrandsonNode.hasAttributes()) {
					    if (clientunitList == null)
						clientunitList = new ArrayList<String>();
	
					    // Check node's ID Value before add to
					    // ArrayList
					    String nodeIDValue = greatgrandsonNode.getAttributes().item(0)
						    .getNodeValue();
	
					    if (nodeIDValue.contains("#"))
						nodeIDValue = nodeIDValue.substring(nodeIDValue
							.indexOf("#") + 1);
	
					    clientunitList.add(nodeIDValue);
					}
				    }
	
				}
			    }
			}
	
		    }
		    // System.out.println("[" + nl.item(i) + "]");
	
		    visit(childNode, level + 1);
		}
    }

    /**
     * Converts a file to org.w3c.dom.Document.
     * 
     * @param file
     * @return org.w3c.dom.Document
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private Document parserXML(File file) throws SAXException, IOException,
	    ParserConfigurationException {
    	return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    }

    /**
     * Converts a URL to org.w3c.dom.Document
     * 
     * @param url
     * @return org.w3c.dom.Document
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private Document parserXML(URL url) throws SAXException, IOException,
	    ParserConfigurationException {
    	return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
    }
}
