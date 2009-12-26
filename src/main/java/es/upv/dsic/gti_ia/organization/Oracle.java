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

public class Oracle {

	/*
	 * Attributes
	 */
	private Document doc = null;
	private ArrayList<String> inputs;
	private ArrayList<String> outputs;
	private ArrayList<String> providerList;
	private ArrayList<String> clientList;

	/*
	 * Methods
	 */
	
	/**
	 * Returns the input parameters of owls file parsed
	 * @return list
	 */
	public ArrayList<String> getInputs() {
		return inputs;
	}

	/**
	 * Returns the output parameters of owls file parsed
	 * @return list
	 */
	public ArrayList<String> getOutputs() {
		return outputs;
	}

	/**
	 * Returns the list of roles available to provide the service
	 * @return list 
	 */
	public ArrayList<String> getProviderList() {
		return providerList;
	}

	/**
	 * Returns the list of roles required to use the service
	 * @return list
	 */
	public ArrayList<String> getClientList() {
		return clientList;
	}
	
	/**
	 * Method to parse an OWL-S file
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
	 * Method to parse an OWL-S url
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
	
	public void visit(Node node, int level) {
		NodeList nl = node.getChildNodes();

		for (int i = 0, cnt = nl.getLength(); i < cnt; i++) {
			//Child node examined
			Node childNode = nl.item(i);
			
			// It is an input parameter of Web Service
			if (childNode.getNodeName().equalsIgnoreCase("profile:hasInput")) {

				// Check node's attributes before add to ArrayList
				if (childNode.hasAttributes()) {
					if (inputs == null)
						inputs = new ArrayList<String>();
					inputs.add(childNode.getAttributes().item(0).getNodeValue());
				}
			}
			
			// It is an output parameter of Web Service
			else if (childNode.getNodeName().equalsIgnoreCase("profile:hasOutput")) {
			
				// Check node's attributes before add to ArrayList
				if (childNode.hasAttributes()) {
					if (outputs == null)
						outputs = new ArrayList<String>();
					outputs.add(childNode.getAttributes().item(0).getNodeValue());
				}
			}
			
			//It is a role of provider list
			else if(childNode.getNodeName().equalsIgnoreCase("role:role_list") &&
					childNode.getAttributes().item(0).getNodeValue().equalsIgnoreCase("provider_list")) {
			
				//Extract list of grandson
				NodeList grandsonList = childNode.getChildNodes();
				
				for (int j = 0, length = grandsonList.getLength(); j < length; j++) {
					//Grandson Node examined
					Node grandsonNode = grandsonList.item(j);
					
					if (grandsonNode.getNodeName().equalsIgnoreCase("role:role_atom")) {
						
						// Check node's attributes before add to ArrayList
						if (grandsonNode.hasAttributes()) {
							if (providerList == null)
								providerList = new ArrayList<String>();
							providerList.add(grandsonNode.getAttributes().item(0).getNodeValue());
						}
					}
				}
				
			}
			
			//It is a role of client list
			else if(childNode.getNodeName().equalsIgnoreCase("role:role_list") &&
					childNode.getAttributes().item(0).getNodeValue().equalsIgnoreCase("client_list")) {
			
				//Extract list of grandson
				NodeList grandsonList = childNode.getChildNodes();
				
				for (int j = 0, length = grandsonList.getLength(); j < length; j++) {
					//Grandson Node examined
					Node grandsonNode = grandsonList.item(j);
					
					if (grandsonNode.getNodeName().equalsIgnoreCase("role:role_atom")) {
						
						// Check node's attributes before add to ArrayList
						if (grandsonNode.hasAttributes()) {
							if (clientList == null)
								clientList = new ArrayList<String>();
							clientList.add(grandsonNode.getAttributes().item(0).getNodeValue());
						}
					}
				}
				
			}
			//System.out.println("[" + nl.item(i) + "]");

			visit(childNode, level + 1);
		}
	}

	
	public Document parserXML(File file) throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
	}
	
	public Document parserXML(URL url) throws SAXException, IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
	}
}
