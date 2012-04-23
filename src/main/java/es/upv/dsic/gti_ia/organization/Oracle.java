package es.upv.dsic.gti_ia.organization;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
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
 * This class allows us to parse a OWL-S and WSDL files in order to extract
 * relevant information, such as service inputs, outputs, list of roles for both
 * providers, such as customers.
 * 
 */
public class Oracle {

	/*
	 * Attributes
	 */
	private Document doc = null;

	private ArrayList<String> owlsProfileInputs = new ArrayList<String>();
	private ArrayList<String> owlsProfileOutputs = new ArrayList<String>();

	private ArrayList<String> wsdlInputParams = new ArrayList<String>();
	private ArrayList<String> wsdlOutputParams = new ArrayList<String>();

	private ArrayList<String> wsdlInputTypes = new ArrayList<String>();
	private ArrayList<String> wsdlOutputTypes = new ArrayList<String>();

	private ArrayList<Provider> providers = new ArrayList<Provider>();
	private ArrayList<String> providersGroundingWSDL = new ArrayList<String>();

	private ArrayList<String> providerList = new ArrayList<String>();
	private ArrayList<String> clientList = new ArrayList<String>();
	private ArrayList<String> clientunitList = new ArrayList<String>();
	private ArrayList<String> providerunitList = new ArrayList<String>();

	private Map<String, String> elements = new LinkedHashMap<String, String>();

	private String wsdl = "";
	private String qnameService = "";
	private String qnamePort = "";
	private String operation = "";

	private String serviceName = "";

	private String output_message_WSDL = "";

	private String processLocalName = "";

	private boolean open = true;

	/**
	 * Method to parses an OWL-S file
	 * 
	 * @param file
	 */
	public Oracle(File file) {
		try {
			doc = parserXML(file);

			visit(doc, 0);

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

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Method to parses an OWL-S string
	 * 
	 * @param file
	 */
	public Oracle(String s) {
		try {
			doc = string2DOM(s);

			visitNodeProcess(doc, 0);

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public Oracle() {

	}

	/**
	 * Parses only a WSDL file
	 * 
	 * @param wsdlURL
	 *            to parse
	 */
	public void parseWSDL(String wsdlURL) {
		visitWSDL(wsdlURL);
	}

	/**
	 * Returns the profile input parameters of an OWL-S parsed file
	 * 
	 * @return the profile input parameters of an OWL-S parsed file
	 */
	public ArrayList<String> getOwlsProfileInputs() {
		return owlsProfileInputs;
	}

	/**
	 * Returns the profile output parameters of an OWL-S parsed file
	 * 
	 * @return the profile output parameters of an OWL-S parsed file
	 */
	public ArrayList<String> getOwlsProfileOutputs() {
		return owlsProfileOutputs;
	}

	/**
	 * Returns the WSDL input type parameters of WSDL parsed file
	 * 
	 * @return the WSDL input type parameters of WSDL parsed file
	 */
	public ArrayList<String> getWsdlInputsTypes() {
		return wsdlInputTypes;
	}

	/**
	 * Returns the WSDL output type parameters of WSDL parsed file
	 * 
	 * @return the WSDL output type parameters of WSDL parsed file
	 */
	public ArrayList<String> getWsdlOutputsTypes() {
		return wsdlOutputTypes;
	}

	/**
	 * Returns the WSDL input parameters of WSDL parsed file
	 * 
	 * @return the WSDL input parameters of WSDL parsed file
	 */
	public ArrayList<String> getWSDLInputs() {
		return wsdlInputParams;
	}

	/**
	 * Returns the WSDL output parameters of WSDL parsed file
	 * 
	 * @return the WSDL output parameters of WSDL parsed file
	 */
	public ArrayList<String> getWSDLOutputs() {
		return wsdlOutputParams;
	}

	/**
	 * Returns the list of providers (agents or organizations) of the service
	 * 
	 * @return Returns the list of providers of the service
	 */
	public ArrayList<Provider> getProviders() {
		return providers;
	}

	/**
	 * Returns the list of WSDL grounding providers of the service
	 * 
	 * @return Returns the list of WSDL grounding providers of the service
	 */
	public ArrayList<String> getProvidersGroundingWSDL() {
		return providersGroundingWSDL;
	}

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
	public String getWSDLNamePort() {
		return qnamePort;
	}

	/**
	 * Returns the Operation parameter of WSDL file parsed
	 * 
	 * @return Returns the Operation parameter of WSDL file parsed
	 */
	public String getWSDLOperation() {
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
	 * Sets unitList
	 * 
	 * @param unitList
	 *            this parameter is a unit where the role provider is defined
	 */
	public void setProviderUnitList(ArrayList<String> unitList) {
		this.providerunitList = unitList;
	}

	/**
	 * Returns the process local name
	 * 
	 * @return the process local name
	 */
	public String getProcessLocalName() {
		return processLocalName;
	}

	/**
	 * Parses and extract data from a WSDL file in the given URL
	 * 
	 * @param url
	 *            of the WSDL to extract data
	 */
	private void visitWSDL(String url) {

		open = true;

		// Create an instance of HttpClient.
		HttpClient client = new HttpClient();

		// Create a method instance.
		GetMethod method = new GetMethod(url);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}

			InputStream is = method.getResponseBodyAsStream();

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLStreamReader reader = factory.createXMLStreamReader(is);

			ArrayList<String> parameters = new ArrayList<String>();
			int inputsRead = 0;

			while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				switch (reader.next()) {
				case XMLStreamConstants.START_ELEMENT:

					if (reader.getLocalName().equals("service")) {
						qnameService = reader.getAttributeLocalName(0);
					} else if (reader.getLocalName().equals("portType")) {
						qnamePort = reader.getAttributeValue(0);
					} else if (reader.getLocalName().equals("operation") & reader.getPrefix().equals("wsdl")) {
						operation = reader.getAttributeValue(0);
					} else if (reader.getLocalName().equals("element")) {
						if (reader.getPrefix().equals("xsd")) {
							if (reader.getAttributeValue(0).equalsIgnoreCase(serviceName))
								parameters.add(reader.getAttributeValue(0));

							if (reader.getAttributeCount() > 1) {

								String inOut = reader.getAttributeValue(0);
								String type = reader.getAttributeValue(1).substring(
										reader.getAttributeValue(1).indexOf(":") + 1);
								if (inputsRead <= 1) {
									wsdlInputParams.add(inOut);
									wsdlInputTypes.add(type);
								} else {
									wsdlOutputParams.add(inOut);
									wsdlOutputTypes.add(type);
								}
							} else {
								inputsRead++;
							}

						}
						// Stop reading
						if (reader.getAttributeValue(0).equals(output_message_WSDL))
							open = false;
						if (reader.getAttributeCount() == 2 & open) {
							elements.put(reader.getAttributeValue(0),
									reader.getAttributeValue(1).substring(reader.getAttributeValue(1).indexOf(":") + 1));
						}

					}

					break;

				}
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (XMLStreamException e) {

			e.printStackTrace();
		} catch (HttpException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Parses a node of the process part of an OWL-S document
	 * 
	 * @param node
	 *            to parse
	 * @param level
	 *            of the parser
	 */
	private void visitNodeProcess(Node node, int level) {
		NodeList nl = node.getChildNodes();

		wsdlInputTypes.clear();
		for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
			// Child node examined
			Node childNode = nl.item(i);

			NodeList n = childNode.getChildNodes();
			for (int j = 0; j < n.getLength(); j++) {

				// It is the service name (aka provider's behaviour to execute)
				if (n.item(j).getNodeName().equalsIgnoreCase("process:Input")) {

					NodeList n1 = n.item(j).getChildNodes();

					for (int k = 0; k < n1.getLength(); k++) {
						if (n1.item(k).getNodeName().equalsIgnoreCase("process:parameterType")) {

							String type = n1.item(k).getTextContent()
									.substring(n1.item(k).getTextContent().indexOf("#") + 1);
							wsdlInputTypes.add(type);
						}

					}

				} else if (n.item(j).getNodeName().equalsIgnoreCase("process:Output")) {

					NodeList n1 = n.item(j).getChildNodes();

					for (int k = 0; k < n1.getLength(); k++) {
						if (n1.item(k).getNodeName().equalsIgnoreCase("process:parameterType")) {

							String type = n1.item(k).getTextContent()
									.substring(n1.item(k).getTextContent().indexOf("#") + 1);
							wsdlOutputTypes.add(type);
						}

					}

				}

				else if (n.item(j).getNodeName().equalsIgnoreCase("grounding:WsdlAtomicProcessGrounding")) {
					NodeList n1 = n.item(j).getChildNodes();

					for (int k = 0; k < n1.getLength(); k++) {
						if (n1.item(k).getNodeName().equalsIgnoreCase("grounding:wsdlDocument")) {

							wsdl = n1.item(k).getTextContent().trim();
							if (providersGroundingWSDL == null)
								providersGroundingWSDL = new ArrayList<String>();
							providersGroundingWSDL.add(wsdl);
						}

					}
				}

			}

			// visit the child node
			visit(childNode, level + 1);
		}
	}

	/**
	 * Parses a node of the profile part of an OWL-S document
	 * 
	 * @param node
	 *            to parse
	 * @param level
	 *            of the parser
	 */
	private void visit(Node node, int level) {
		NodeList nl = node.getChildNodes();

		for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {

			// Child node examined
			Node childNode = nl.item(i);

			// It is the service name (aka provider's behaviour to execute)
			if (childNode.getNodeName().equalsIgnoreCase("profile:serviceName")) {

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
					if (owlsProfileInputs == null)
						owlsProfileInputs = new ArrayList<String>();

					// Check node's ID Value before add to ArrayList
					String nodeIDValue = childNode.getAttributes().item(0).getNodeValue();

					if (nodeIDValue.contains("#"))
						nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);

					owlsProfileInputs.add(nodeIDValue);
				}
			}

			// It is an output parameter of Web Service
			else if (childNode.getNodeName().equalsIgnoreCase("profile:hasOutput")) {

				// Check node's attributes before add to ArrayList
				if (childNode.hasAttributes()) {
					if (owlsProfileOutputs == null)
						owlsProfileOutputs = new ArrayList<String>();

					// Check node's ID Value before add to ArrayList
					String nodeIDValue = childNode.getAttributes().item(0).getNodeValue();

					if (nodeIDValue.contains("#"))
						nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);

					owlsProfileOutputs.add(nodeIDValue);
				}
			}

			// It is a provider
			else if (childNode.getNodeName().equalsIgnoreCase("profile:contactInformation")) {

				if (childNode.hasChildNodes()) {
					NodeList childs = childNode.getChildNodes();
					for (int prov = 0; prov < childs.getLength(); prov++) {
						Node provNode = childs.item(prov);
						if (provNode.hasAttributes()) {

							NodeList provChilds = provNode.getChildNodes();
							Provider provider = new Provider();
							for (int provChild = 0; provChild < provChilds.getLength(); provChild++) {
								Node provParamNode = provChilds.item(provChild);
								if (provParamNode.getNodeName().equalsIgnoreCase("provider:entityID")) {
									String entityID = provParamNode.getTextContent();
									provider.setEntityID(entityID);

								} else if (provParamNode.getNodeName().equalsIgnoreCase("provider:entityType")) {
									String entityType = provParamNode.getTextContent();
									provider.setEntityType(entityType);

								} else if (provParamNode.getNodeName().equalsIgnoreCase("provider:language")) {
									String language = provParamNode.getTextContent();
									provider.setLanguage(language);

								} else if (provParamNode.getNodeName().equalsIgnoreCase("provider:performative")) {
									String performative = provParamNode.getTextContent();
									provider.setPerformative(performative);

								}
							}
							if (providers == null)
								providers = new ArrayList<Provider>();
							providers.add(provider);
						}
					}
				}
			}

			// It is a role of provider list
			else if (childNode.getNodeName().equalsIgnoreCase("role:role_list")
					&& childNode.getAttributes().item(0).getNodeValue().equalsIgnoreCase("provider_list")) {

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
							String nodeIDValue = grandsonNode.getAttributes().item(0).getNodeValue();

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

							if (greatgrandsonNode.getNodeName().equalsIgnoreCase("role:isDefinedIn")) {

								// Check node's attributes before add to
								// ArrayList
								if (greatgrandsonNode.hasAttributes()) {
									if (providerunitList == null)
										providerunitList = new ArrayList<String>();

									// Check node's ID Value before add to
									// ArrayList
									String nodeIDValue = greatgrandsonNode.getAttributes().item(0).getNodeValue();

									if (nodeIDValue.contains("#"))
										nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);

									providerunitList.add(nodeIDValue);
								}
							}

						}

					}
				}

			}

			// It is a role of client list
			else if (childNode.getNodeName().equalsIgnoreCase("role:role_list")
					&& childNode.getAttributes().item(0).getNodeValue().equalsIgnoreCase("client_list")) {

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
							String nodeIDValue = grandsonNode.getAttributes().item(0).getNodeValue();

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

							if (greatgrandsonNode.getNodeName().equalsIgnoreCase("role:isDefinedIn")) {

								// Check node's attributes before add to
								// ArrayList
								if (greatgrandsonNode.hasAttributes()) {
									if (clientunitList == null)
										clientunitList = new ArrayList<String>();

									// Check node's ID Value before add to
									// ArrayList
									String nodeIDValue = greatgrandsonNode.getAttributes().item(0).getNodeValue();

									if (nodeIDValue.contains("#"))
										nodeIDValue = nodeIDValue.substring(nodeIDValue.indexOf("#") + 1);

									clientunitList.add(nodeIDValue);
								}
							}

						}
					}
				}

			}

			// visit the child node
			visit(childNode, level + 1);
		}
	}

	/**
	 * Converts an {@link String} to a {@link Document} to parse
	 * 
	 * @param s
	 *            String to convert
	 * @return {@link Document} that can be parsed
	 */
	private Document string2DOM(String s) {

		Document tmpX = null;
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (javax.xml.parsers.ParserConfigurationException error) {
			error.printStackTrace();
			return null;
		}
		try {
			tmpX = builder.parse(new ByteArrayInputStream(s.getBytes()));
		} catch (org.xml.sax.SAXException error) {
			error.printStackTrace();
			return null;
		} catch (IOException error) {
			error.printStackTrace();
			return null;
		}
		return tmpX;

	}

	/**
	 * Converts a file to {@link org.w3c.dom.Document}
	 * 
	 * @param file
	 * @return {@link Document}
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private Document parserXML(File file) throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	}

	/**
	 * Converts a URL to {@link Document}
	 * 
	 * @param url
	 * @return {@link Document}
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private Document parserXML(URL url) throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
	}
}
