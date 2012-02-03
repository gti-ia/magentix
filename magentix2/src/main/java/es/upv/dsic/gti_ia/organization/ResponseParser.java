package es.upv.dsic.gti_ia.organization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResponseParser {


	String serviceName="";
	String status="";
	String description="";
	ArrayList<ArrayList<String>> itemsList=new ArrayList<ArrayList<String>>();
	ArrayList<String> elementsList=new ArrayList<String>();
	
	
	public String getServiceName() {
		return serviceName;
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public ArrayList<ArrayList<String>> getItemsList() {
		return itemsList;
	}

	public ArrayList<String> getElementsList() {
		return elementsList;
	}

	public String DOM2String(Document doc)
	{
		int coderror=0;
		String msgerror="";

		TransformerFactory transformerFactory =TransformerFactory.newInstance();
		Transformer transformer = null;
		try{
			transformer = transformerFactory.newTransformer();
		}catch (javax.xml.transform.TransformerConfigurationException error){
			coderror=123;
			msgerror=error.getMessage();
			return null;
		}

		Source source = new DOMSource(doc);

		StringWriter writer = new StringWriter();
		Result result = new StreamResult(writer);
		try{
			transformer.transform(source,result);
		}catch (javax.xml.transform.TransformerException error){
			coderror=123;
			msgerror=error.getMessage();
			return null;
		}

		String s = writer.toString();
		return s;
	}

	public static Document string2DOM(String s)
	{
		int coderror=0;
		String msgerror="";

		Document tmpX=null;
		DocumentBuilder builder = null;
		try{
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}catch(javax.xml.parsers.ParserConfigurationException error){
			coderror=10;
			msgerror="Error crando factory String2DOM "+error.getMessage();
			return null;
		}
		try{
			tmpX=builder.parse(new ByteArrayInputStream(s.getBytes()));
		}catch(org.xml.sax.SAXException error){
			coderror=10;
			msgerror="Error parseo SAX String2DOM "+error.getMessage();
			return null;
		}catch(IOException error){
			coderror=10;
			msgerror="Error generando Bytes String2DOM "+error.getMessage();
			return null;
		}
		return tmpX;
	}


	public void parseResponse(String response){
		Document doc=string2DOM(response);

		if(!doc.hasChildNodes() || !doc.getChildNodes().item(0).hasChildNodes()){
			//incorrect specification
			System.err.println("incorrect specification");
			return;
		}

		itemsList=new ArrayList<ArrayList<String>>();
		elementsList=new ArrayList<String>();
		
		NodeList nodeList=doc.getChildNodes().item(0).getChildNodes();
		for(int i=0;i<nodeList.getLength();i++){
			Node n=nodeList.item(i);

			if(n.getNodeName().equalsIgnoreCase("serviceName")){
				serviceName=n.getFirstChild().getNodeValue().trim();
			}
			else if(n.getNodeName().equalsIgnoreCase("status")){
				status=n.getFirstChild().getNodeValue().trim();
			}
			else if(n.getNodeName().equalsIgnoreCase("result")){
				NodeList resultNodeList=n.getChildNodes();
				for(int j=0;j<resultNodeList.getLength();j++){
					Node resNode=resultNodeList.item(j);
					if(resNode.getNodeName().equalsIgnoreCase("description")){
						description=resNode.getFirstChild().getNodeValue().trim();
					}
					else if(resNode.getNodeName().equalsIgnoreCase("item")){
						NodeList items=resNode.getChildNodes();
						ArrayList<String> itemComponents=new ArrayList<String>();
						for(int it=0;it<items.getLength();it++){
							Node item=items.item(it);
							if(item.getNodeType()==Node.ELEMENT_NODE)
								itemComponents.add(item.getTextContent().trim());
						}
						itemsList.add(itemComponents);
					}
					else if(resNode.getNodeType()==Node.ELEMENT_NODE){
						String name=resNode.getNodeName();
						String value=resNode.getNodeValue();
						String text=resNode.getTextContent().trim();
						elementsList.add(resNode.getTextContent().trim());
						short type=resNode.getNodeType();
						System.out.println("name&Value: "+name +" "+ value +" "+text+" "+type);
//						String element=resNode.getFirstChild().getNodeValue().trim();
//						elementsList.add(element);
					}
					

				}
			}

			System.out.println(n.getNodeName());
			System.out.println(n.getFirstChild().getNodeValue());
			System.out.println(n.getChildNodes().getLength());
			System.out.println();
		}
		System.out.println("serviceName: "+serviceName);
		System.out.println("status: "+status);
		System.out.println("description: "+description);

	}





}
