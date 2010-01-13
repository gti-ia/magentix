package es.upv.dsic.gti_ia.organization;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * This class allows us to parse a profile in order to extract relevant information, such as service inputs, outputs, list of roles for both providers, such as customers.
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

	private String serviceName;
//	private boolean webService;
//	private boolean behaviour;

	/*
	 * Methods
	 */

	/**
	 * Returns the input parameters of owls file parsed
	 * @return Returns the input parameters of owls file parsed
	 */
	public ArrayList<String> getInputs() {
		return inputs;
	}

	/**
	 * Returns the output parameters of owls file parsed
	 * @return Returns the output parameters of owls file parsed
	 */
	public ArrayList<String> getOutputs() {
		return outputs;
	}

	/**
	 * Returns the list of roles available to provide the service
	 * @return Returns the list of roles available to provide the service
	 */
	public ArrayList<String> getProviderList() {
		return providerList;
	}

	/**
	 * Returns the list of roles required to use the service
	 * @return Returns the list of roles required to use the service
	 */
	public ArrayList<String> getClientList() {
		return clientList;
	}

	/**
	 * 
	 * Returns the behaviour name to execute on provider
	 * @return Returns the behaviour name to execute on provider
	 */
	public String getServiceName() {
		return serviceName;
	}

//	boolean isWebService() {
//		return webService;
//	}
//
//	public boolean isBehaviour() {
//		return behaviour;
//	}

	/**
	 * Return providerUnitList is a unit where the role client is defined
	 *  @return providerUnitList is a unit where the role client is defined
	 */
	public ArrayList<String> getClientUnitList() {
		return this.clientunitList;
	}

	/**
	 * Return unitList this parameter is a unit where the role client is defined
	 * @param unitList this parameter is a unit where the role client is defined
	 */
	public void setClientUnitList(ArrayList<String> unitList) {
		this.clientunitList = unitList;
	}

	/**
	 * Return providerUnitList is a unit where the role provider is defined
	 * @return providerUnitList is a unit where the role provider is defined
	 */
	public ArrayList<String> getProviderUnitList() {
		return this.providerunitList;
	}
	/**
	 * Return unitList this parameter is a unit where the role provider is defined
	 * @param unitList this parameter is a unit where the role provider is defined
	 */
	public void setProviderUnitList(ArrayList<String> unitList) {
		this.providerunitList = unitList;
	}
	/**
	 * Method to parse an OWL-S file
	 * 
	 * @param file
	 */
	public Oracle(File file) {
		try {
			doc = parserXML(file);

			visit(doc, 0);
			// Change flags
			//behaviour = true;
			//webService = false;

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	/**
	 * Method to parse an OWL-S url
	 * 
	 * @param url
	 */
	public Oracle(URL url) {
		try {
			doc = parserXML(url);

			visit(doc, 0);

			// Change flags
			//behaviour = false;
			//webService = true;

		} catch (Exception error) {
			error.printStackTrace();
		}
	}

	public void visit(Node node, int level) {
		NodeList nl = node.getChildNodes();

		for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
			// Child node examined
			Node childNode = nl.item(i);

			// It is the service name (aka provider's behaviour to execute)
			if (childNode.getNodeName().equalsIgnoreCase("profile:serviceName")) {

				int acumulador = 0;
				acumulador++;
				// Check node's Value before assign
				String nodeTextContent = childNode.getFirstChild()
						.getNodeValue();

				if (nodeTextContent.contains("#"))
					nodeTextContent = nodeTextContent.substring(nodeTextContent
							.indexOf("#") + 1);

				serviceName = nodeTextContent;
			}

			// It is an input parameter of Web Service
			else if (childNode.getNodeName().equalsIgnoreCase(
					"profile:hasInput")) {

				// Check node's attributes before add to ArrayList
				if (childNode.hasAttributes()) {
					if (inputs == null)
						inputs = new ArrayList<String>();

					// Check node's ID Value before add to ArrayList
					String nodeIDValue = childNode.getAttributes().item(0)
							.getNodeValue();

					if (nodeIDValue.contains("#"))
						nodeIDValue = nodeIDValue.substring(nodeIDValue
								.indexOf("#") + 1);

					inputs.add(nodeIDValue);
				}
			}

			// It is an output parameter of Web Service
			else if (childNode.getNodeName().equalsIgnoreCase(
					"profile:hasOutput")) {

				// Check node's attributes before add to ArrayList
				if (childNode.hasAttributes()) {
					if (outputs == null)
						outputs = new ArrayList<String>();

					// Check node's ID Value before add to ArrayList
					String nodeIDValue = childNode.getAttributes().item(0)
							.getNodeValue();

					if (nodeIDValue.contains("#"))
						nodeIDValue = nodeIDValue.substring(nodeIDValue
								.indexOf("#") + 1);

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

					if (grandsonNode.getNodeName().equalsIgnoreCase(
							"role:role_atom")) {

						// Check node's attributes before add to ArrayList
						if (grandsonNode.hasAttributes()) {
							if (providerList == null)
								providerList = new ArrayList<String>();

							// Check node's ID Value before add to ArrayList
							String nodeIDValue = grandsonNode.getAttributes()
									.item(0).getNodeValue();

							if (nodeIDValue.contains("#"))
								nodeIDValue = nodeIDValue.substring(nodeIDValue
										.indexOf("#") + 1);

							providerList.add(nodeIDValue);
						}

						// Check the necessary Unit to acquire this role (first
						// child)
						NodeList greatgrandsonNodeList = grandsonNode
								.getChildNodes();

						int listLength = greatgrandsonNodeList.getLength();
						for (int k = 0; k < listLength; k++) {
							// Greatgrandson Node examined
							Node greatgrandsonNode = greatgrandsonNodeList
									.item(k);

							if (greatgrandsonNode.getNodeName()
									.equalsIgnoreCase("role:isDefinedIn")) {

								// Check node's attributes before add to
								// ArrayList
								if (greatgrandsonNode.hasAttributes()) {
									if (providerunitList == null)
										providerunitList = new ArrayList<String>();

									// Check node's ID Value before add to
									// ArrayList
									String nodeIDValue = greatgrandsonNode
											.getAttributes().item(0)
											.getNodeValue();

									if (nodeIDValue.contains("#"))
										nodeIDValue = nodeIDValue
												.substring(nodeIDValue
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

					if (grandsonNode.getNodeName().equalsIgnoreCase(
							"role:role_atom")) {

						// Check node's attributes before add to ArrayList
						if (grandsonNode.hasAttributes()) {
							if (clientList == null)
								clientList = new ArrayList<String>();

							// Check node's ID Value before add to ArrayList
							String nodeIDValue = grandsonNode.getAttributes()
									.item(0).getNodeValue();

							if (nodeIDValue.contains("#"))
								nodeIDValue = nodeIDValue.substring(nodeIDValue
										.indexOf("#") + 1);

							clientList.add(nodeIDValue);
						}

						// Check the necessary Unit to acquire this role (first
						// child)
						NodeList greatgrandsonNodeList = grandsonNode
								.getChildNodes();

						int listLength = greatgrandsonNodeList.getLength();
						for (int k = 0; k < listLength; k++) {
							// Greatgrandson Node examined
							Node greatgrandsonNode = greatgrandsonNodeList
									.item(k);

							if (greatgrandsonNode.getNodeName()
									.equalsIgnoreCase("role:isDefinedIn")) {

								// Check node's attributes before add to
								// ArrayList
								if (greatgrandsonNode.hasAttributes()) {
									if (clientunitList == null)
										clientunitList = new ArrayList<String>();

									// Check node's ID Value before add to
									// ArrayList
									String nodeIDValue = greatgrandsonNode
											.getAttributes().item(0)
											.getNodeValue();

									if (nodeIDValue.contains("#"))
										nodeIDValue = nodeIDValue
												.substring(nodeIDValue
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

	public Document parserXML(File file) throws SAXException, IOException,
			ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				file);
	}

	public Document parserXML(URL url) throws SAXException, IOException,
			ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				url.openStream());
	}
}
