package es.upv.dsic.gti_ia.organization;

/**
 * This class allows us to parse a service response in order to extract relevant
 * information, such as service name, status, and result.
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ResponseParser {

	String serviceName = "";
	String status = "";
	String description = "";
	String specification = "";
	ArrayList<ArrayList<String>> itemsList = new ArrayList<ArrayList<String>>();
	ArrayList<String> elementsList = new ArrayList<String>();
	HashMap<String, String> keyAndValueList = new HashMap<String, String>();

	String getServiceName() {
		return serviceName;
	}

	String getStatus() {
		return status;
	}

	String getDescription() {
		return description;
	}

	String getSpecification() {
		return specification;
	}

	ArrayList<ArrayList<String>> getItemsList() {
		return itemsList;
	}

	ArrayList<String> getElementsList() {
		return elementsList;
	}

	HashMap<String, String> getKeyAndValueList() {
		return keyAndValueList;
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
	 * Parses an XML response given as parameter to extract its data
	 * 
	 * @param response
	 *            {@link String} to parse
	 */
	void parseResponse(String response) {
		Document doc = string2DOM(response);

		if (!doc.hasChildNodes() || !doc.getChildNodes().item(0).hasChildNodes()) {
			// incorrect specification
			System.err.println("incorrect specification");
			return;
		}

		itemsList = new ArrayList<ArrayList<String>>();
		elementsList = new ArrayList<String>();
		keyAndValueList = new HashMap<String, String>();
		NodeList nodeList = doc.getChildNodes().item(0).getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node n = nodeList.item(i);

			if (n.getNodeName().equalsIgnoreCase("serviceName")) {
				serviceName = n.getFirstChild().getNodeValue().trim();
			} else if (n.getNodeName().equalsIgnoreCase("status")) {
				status = n.getFirstChild().getNodeValue().trim();
			} else if (n.getNodeName().equalsIgnoreCase("result")) {
				NodeList resultNodeList = n.getChildNodes();
				for (int j = 0; j < resultNodeList.getLength(); j++) {
					Node resNode = resultNodeList.item(j);
					if (resNode.getNodeName().equalsIgnoreCase("description")) {
						description = resNode.getFirstChild().getNodeValue().trim();
					} else if (resNode.getNodeName().equalsIgnoreCase("item")) {
						NodeList items = resNode.getChildNodes();
						ArrayList<String> itemComponents = new ArrayList<String>();
						for (int it = 0; it < items.getLength(); it++) {
							Node item = items.item(it);
							if (item.getNodeType() == Node.ELEMENT_NODE)
								itemComponents.add(item.getTextContent().trim());
						}
						itemsList.add(itemComponents);
					} else if (resNode.getNodeName().equalsIgnoreCase("specification")) {
						// the content is encapsulated in an XML comment <!--
						// -->
						Node childN = resNode.getFirstChild();
						specification = childN.getNodeValue().trim();
					} else if (resNode.getNodeType() == Node.ELEMENT_NODE) {
						elementsList.add(resNode.getTextContent().trim());
					}

				}
			} else if (n.getNodeName().equalsIgnoreCase("inputs")) {
				NodeList resultNodeList = n.getChildNodes();
				for (int j = 0; j < resultNodeList.getLength(); j++) {
					{
						Node resNode = resultNodeList.item(j);

						if (resNode.getNodeType() == Node.ELEMENT_NODE) {

							if (resNode.getNodeName().equals("NormContent"))
							{
								Node childN = resNode.getFirstChild();
								String s = childN.getNodeValue().trim();
								
								keyAndValueList.put(resNode.getNodeName().trim(), s);
							}
							else
							{
								keyAndValueList.put(resNode.getNodeName().trim(), resNode.getTextContent().trim());
							}
						}

					}
				}
			} else if (n.getNodeName().equalsIgnoreCase("outputs")) {
				NodeList resultNodeList = n.getChildNodes();
				for (int j = 0; j < resultNodeList.getLength(); j++) {
					{
						Node resNode = resultNodeList.item(j);

						if (resNode.getNodeType() == Node.ELEMENT_NODE) {

							keyAndValueList.put(resNode.getNodeName().trim(), resNode.getTextContent().trim());
						}

					}
				}
			}

		}

	}
}
