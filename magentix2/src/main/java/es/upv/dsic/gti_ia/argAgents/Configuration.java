package es.upv.dsic.gti_ia.argAgents;


import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Configuration parameters, read from configuration.xml
 * @author Jaume Jordan Prunera. GTI-IA. UPV
 */
public class Configuration {
	public String serverName;
	public String databaseName;
	public String userName;
	public String password;
	public String domainCBRSimilarity;
	public float argCBRproponentidweight;
	public float argCBRproponentprefweight;
	public float argCBRopponentidweight;
	public float argCBRopponentprefweight;
	public float argCBRgroupidweight;
	public float argCBRgroupprefweight;
	
	public Configuration(){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance ( );
		Document document = null;

		try
		{
		   DocumentBuilder builder = factory.newDocumentBuilder();
		   File file=new File("configuration.xml");
		   document=builder.parse(file);
		}
		catch (Exception spe)
		{
		   spe.printStackTrace();
		}
		if(document!=null){
			
			Node rootNode = document.getFirstChild();
			
			NodeList sonNodesList = rootNode.getChildNodes();
			for(int i=0;i<sonNodesList.getLength();i++){
				Node sonNode=sonNodesList.item(i);
				if(sonNode.getNodeName().equals("server")){
					NodeList secondsonsList=sonNode.getChildNodes();
					for(int j=0;j<secondsonsList.getLength();j++){
						Node secondSon=secondsonsList.item(j);
						
						if(secondSon.getNodeName().equals("name"))
							serverName=secondSon.getTextContent();
						else if(secondSon.getNodeName().equals("database"))
							databaseName=secondSon.getTextContent();
						else if(secondSon.getNodeName().equals("user"))
							userName=secondSon.getTextContent();
						else if(secondSon.getNodeName().equals("pass"))
							password=secondSon.getTextContent();
							
					}
					
				}
				else if(sonNode.getNodeName().equals("domaincbr")){
					NodeList secondsonsList=sonNode.getChildNodes();
					for(int j=0;j<secondsonsList.getLength();j++){
						Node secondSon=secondsonsList.item(j);
						
						if(secondSon.getNodeName().equals("similarity"))
							domainCBRSimilarity=secondSon.getTextContent();
						
							
					}
				}
				else if(sonNode.getNodeName().equals("argcbr")){
					NodeList secondsonsList=sonNode.getChildNodes();
					for(int j=0;j<secondsonsList.getLength();j++){
						Node secondSon=secondsonsList.item(j);
						
						if(secondSon.getNodeName().equals("proponentidweight"))
							argCBRproponentidweight=Float.parseFloat(secondSon.getTextContent());
						else if(secondSon.getNodeName().equals("proponentprefweight"))
							argCBRproponentprefweight=Float.parseFloat(secondSon.getTextContent());
						else if(secondSon.getNodeName().equals("opponentidweight"))
							argCBRopponentidweight=Float.parseFloat(secondSon.getTextContent());
						else if(secondSon.getNodeName().equals("opponentprefweight"))
							argCBRopponentprefweight=Float.parseFloat(secondSon.getTextContent());
						else if(secondSon.getNodeName().equals("groupidweight"))
							argCBRgroupidweight=Float.parseFloat(secondSon.getTextContent());
						else if(secondSon.getNodeName().equals("groupprefweight"))
							argCBRgroupprefweight=Float.parseFloat(secondSon.getTextContent());
							
					}
				}

				
			}
			
			
			


		}

	}
	
}
