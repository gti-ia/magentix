package es.upv.dsic.gti_ia.magentix2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * An XML parser
 * @author ricard
 *
 */

public class Xml
{
	/**
	 * Returns the root element of an XML file
	 * @param filename route to the file
	 * @param rootName name of the root element
	 * @return rootElement of the XML file
	 */
	private static Element rootElement(String filename, String rootName)
	{
		FileInputStream fileInputStream = null;
		try
		{
			fileInputStream = new FileInputStream(filename);		
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(fileInputStream);
			Element rootElement = document.getDocumentElement();
			if(!rootElement.getNodeName().equals(rootName)) 
				throw new RuntimeException("Could not find root node: "+rootName);
			return rootElement;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
		finally
		{
			if(fileInputStream!=null)
			{
				try
				{
					fileInputStream.close();
				}
				catch(Exception exception)
				{
					throw new RuntimeException(exception);
				}
			}
		}
	}
	
	/**
	 * Returns the root element of an XML file
	 * @param is InputStream with an XML file
	 * @param rootName name of the root element
	 * @return rootElement of the XML file
	 */
	private static Element rootElement(InputStream is, String rootName)
	{
		FileInputStream fileInputStream = null;
		try
		{
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document document = builder.parse(is);
			Element rootElement = document.getDocumentElement();
			if(!rootElement.getNodeName().equals(rootName)) 
				throw new RuntimeException("Could not find root node: "+rootName);
			return rootElement;
		}
		catch(Exception exception)
		{
			throw new RuntimeException(exception);
		}
		finally
		{
			if(fileInputStream!=null)
			{
				try
				{
					fileInputStream.close();
				}
				catch(Exception exception)
				{
					throw new RuntimeException(exception);
				}
			}
		}
	}
	
	/**
	 * Creates an XML parser
	 * @param filename route to the XML file
	 * @param rootName
	 */
	public Xml(String filename, String rootName)
	{
		this(rootElement(filename,rootName));
	}
	
	/**
	 * Creates an XML parser
	 * @param is InputStream with an XML file
	 * @param rootName
	 */
	public Xml(InputStream is, String rootName){
		this(rootElement(is, rootName));
	}
	
	/**
	 * Creates an XML parser
	 * @param element XML element
	 */
	private Xml(Element element)
	{
		this.name = element.getNodeName();
		this.content = element.getTextContent();
		NamedNodeMap namedNodeMap = element.getAttributes();
		int n = namedNodeMap.getLength();
		for(int i=0;i<n;i++)
		{
			Node node = namedNodeMap.item(i);
			String name = node.getNodeName();
			addAttribute(name,node.getNodeValue());
		}		
		NodeList nodes = element.getChildNodes();
		n = nodes.getLength();
	    for(int i=0;i<n;i++)
	    {
	    	Node node = nodes.item(i);
	    	int type = node.getNodeType();
	    	if(type==Node.ELEMENT_NODE) addChild(node.getNodeName(),new Xml((Element)node));
	    }
	}
	
	/**
	 * Ads an atribute to the root element
	 * @param name
	 * @param value
	 */
	private void addAttribute(String name, String value)
	{
		nameAttributes.put(name,value);
	}
	
	/**
	 * Ads a child to the root element
	 * @param name
	 * @param child
	 */
	private void addChild(String name, Xml child)
	{
		List<Xml> children = nameChildren.get(name);
		if(children==null)
		{
			children = new ArrayList<Xml>();
			nameChildren.put(name,children);
		}
		children.add(child);
	}
	
	/**
	 * Returns root element's name
	 * @return root element's name
	 */
	public String name()
	{
		return name;
	}
	
	/**
	 * Returns root element's content
	 * @return root element's content
	 */
	public String content()
	{
		return content;
	}
	
	/**
	 * Returns a child of the root element
	 * @param name Child's name
	 * @return Child
	 */
	public Xml child(String name)
	{
		List<Xml> children = children(name);
		if(children.size()!=1) throw new RuntimeException("Could not find individual child node: "+name);
		return children.get(0);
	}
	
	/**
	 * Returns a List of children of the root element
	 * @param name children's name
	 * @return List of children
	 */
	public List<Xml> children(String name)
	{
		List<Xml> children = nameChildren.get(name);
		return children==null ? new ArrayList<Xml>() : children;			
	}
	
	/**
	 * Returns a string value of an attribute
	 * @param name of the attribute
	 * @return attribute's value
	 */
	public String string(String name)
	{
		String value = nameAttributes.get(name);
		if(value==null) throw new RuntimeException("Could not find attribute: "+name+", in node: "+this.name);
		return value;
	}
	
	/**
	* Returns an integer value of an attribute
	 * @param name of the attribute
	 * @return attribute's value
	 */
	public int integer(String name)
	{
		return Integer.parseInt(string(name)); 
	}
	
	private String name;
	private String content;
	private Map<String,String> nameAttributes = new HashMap<String,String>();
	private Map<String,List<Xml>> nameChildren = new HashMap<String,List<Xml>>();
}
