package TestCore;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.core.Xml;

/**
 * Test class for XML
 * 
 * @author Victor Martinez Morant - vmartinez2@dsic.upv.es
 */

public class TestXML extends TestCase {

	private Method[] m = new Method[2];

	public TestXML(String name) {
		super(name);
	}

	/** Test set up */
	protected void setUp() throws Exception {
		super.setUp();

		/** Preparation to test private method addAttribute */
		Class[] parametersAddAttributte = new Class[2];
		parametersAddAttributte[0] = java.lang.String.class;
		parametersAddAttributte[1] = java.lang.String.class;
		m[0] = Xml.class.getDeclaredMethod("addAttribute",
				parametersAddAttributte);
		m[0].setAccessible(true);

		/** Preparation to test private method addChild */
		Class[] parametersAddChild = new Class[2];
		parametersAddChild[0] = java.lang.String.class;
		parametersAddChild[1] = Xml.class;

		m[1] = Xml.class.getDeclaredMethod("addChild", parametersAddChild);
		m[1].setAccessible(true);
	}

	/**
	 * 
	 * Testing method rootElement(String filename, String rootName)
	 * 
	 * This method is used by the constructor: Xml(String filename, String
	 * rootName)
	 * 
	 */
	public void testRootElement() {
		try {
			new Xml("src/test/java/TestCore/catalog.xml", "catalog");
			assertTrue(true);
		} catch (RuntimeException re) {
			fail(re.getMessage());
		}
	}

	/**
	 * 
	 * Testing method rootElement(String filename, String rootName) when the
	 * root name of the file is not equal to the second argument rootName
	 * 
	 * This method is used by the constructor: Xml(String filename, String
	 * rootName)
	 * 
	 * Expected Exception = Runtime.class
	 * 
	 * Expected message = java.lang.RuntimeException: Could not find root node:
	 * "+ rootName
	 * 
	 */
	public void testWrongRootNameRootElement() {
		try {
			new Xml("src/test/java/TestCore/catalog.xml", "wrongName");
		} catch (RuntimeException re) {
			assertEquals(re.getMessage(),
					"java.lang.RuntimeException: Could not find root node: wrongName");
		}
	}

	/**
	 * 
	 * Testing method ootElement(String filename, String rootName) when it is
	 * impossible to find the xml file
	 * 
	 * This method is used by the constructor: Xml(String filename, String
	 * rootName)
	 * 
	 * Expected Exception = Runtime.class
	 * 
	 */
	public void testXMLNotFoundRootElement() {
		try {
			new Xml("notFoundFile.xml", "catalog");
			fail("Missing exception");
		} catch (RuntimeException re) {
			// success
			assertTrue(true);
		}
	}

	/**
	 * 
	 * Testing method rootElement(InputStream is, String rootName)
	 * 
	 * This method is used by the constructor: Xml(InputStream is, String
	 * rootName)
	 * 
	 */
	public void testISRootElement() {
		try {
			InputStream is = new FileInputStream(
					"src/test/java/TestCore/catalog.xml");
			new Xml(is, "catalog");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException re) {
			fail(re.getMessage());
		}
	}

	/**
	 * 
	 * Testing method rootElement(InputStream is, String rootName) when the root
	 * name of the file is not equal to the second argument rootName
	 * 
	 * This method is used by the constructor: Xml(InputStream is, String
	 * rootName)
	 * 
	 * Expected Exception = Runtime.class
	 * 
	 * Expected message = java.lang.RuntimeException: Could not find root node:
	 * "+ rootName
	 * 
	 */
	public void testISWrongRootNameRootElement() {
		try {
			InputStream is = new FileInputStream(
					"src/test/java/TestCore/catalog.xml");
			new Xml(is, "wrongName");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RuntimeException re) {
			assertEquals(re.getMessage(),
					"java.lang.RuntimeException: Could not find root node: wrongName");
		}
	}

	/**
	 * 
	 * Testing private method addAttribute()
	 * 
	 * Trying to add null key
	 * 
	 */
	public void testAddAttributeNull() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		Object[] parameters = new Object[2];
		/**
		 * HashMap allows to use null as a key and as a value. Check in XML.java
		 * that key and value are not null
		 */
		parameters[0] = null;
		parameters[1] = null;
		try {
			m[0].invoke(myXml, parameters);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Testing method addChild()
	 * 
	 * Trying to add null key
	 * 
	 */
	public void testAddChildNull() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		Object[] parameters = new Object[2];

		/**
		 * HashMap allows to use null as a key and as a value. Check in XML.java
		 * that key and value are not null
		 */
		parameters[0] = null;
		parameters[1] = null;
		try {
			m[1].invoke(myXml, parameters);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Testing private method addAttribute()
	 * 
	 * Trying to add Empty key
	 * 
	 */
	public void testAddAttributeEmpty() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		Object[] parameters = new Object[2];
		
		parameters[0] = "";
		parameters[1] = "";
		try {
			m[0].invoke(myXml, parameters);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Testing method addChild()
	 * 
	 * Trying to add empty key
	 * 
	 */
	public void testAddChildEmpty() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		Object[] parameters = new Object[2];

		/**
		 * HashMap allows to use null as a key and as a value. Check in XML.java
		 * that key and value are not null
		 */
		parameters[0] = "";
		parameters[1] = null;
		try {
			m[1].invoke(myXml, parameters);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Testing private method addAttribute()
	 * 
	 * Trying to add Empty keys
	 * 
	 */
	public void testAddAttribute() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		Object[] parameters = new Object[2];
		/**
		 * HashMap allows to use null as a key and as a value. Check in XML.java
		 * that key and value are not null
		 */
		parameters[0] = "hello";
		parameters[1] = "world";
		try {
			m[0].invoke(myXml, parameters);

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Testing method addChild()
	 * 
	 * Trying to add empty key
	 * 
	 */
	public void testAddChild() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		Object[] parameters = new Object[2];

		/**
		 * HashMap allows to use null as a key and as a value. Check in XML.java
		 * that key and value are not null
		 */
		parameters[0] = "hello";
		parameters[1] = myXml;
		try {
			m[1].invoke(myXml, parameters);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Testing get method name()
	 * 
	 * 
	 */
	public void testName() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		assertEquals(myXml.name(), "catalog");
	}

	/**
	 * 
	 * Testing get method content()
	 * 
	 * 
	 */
	public void testContent() { // Content of this node and descendants
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		assertEquals(myXml.child("name").child("fullname").content(), "Victor");
	}

	/**
	 * 
	 * Testing method child
	 * 
	 * 1º Test Cases : The node has 1 child
	 * 
	 * 2º Test Cases : The node has 0 or more than one child -> Causes a
	 * RuntimeException
	 * 
	 * 
	 */
	public void testChild() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");

		// 1º Test Cases : The node has 1 child
		try {
			Xml child = myXml.child("name");
			assertEquals("name", child.name());
		} catch (RuntimeException re) {
			fail("RuntimeException");
		}

		// 2º Test Cases : The node has 0 or more than one child -> Causes a
		// RuntimeException
		try {
			myXml.child("book");
		} catch (RuntimeException re) {
			assertEquals(re.getMessage(),
					"Could not find individual child node: book");
		}
	}

	/**
	 * 
	 * Testing method children
	 * 
	 * 1º Test Case : The node has no Children
	 * 
	 * 2º Test Case : The node has any number of children
	 * 
	 * 
	 */
	public void testChildren() {
		
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		List<Xml> myEmptyChildren = new ArrayList<Xml>();
		
		/** 1º Test Case - No children with the same name :S */
		assertEquals(myEmptyChildren, myXml.children("author"));
		assertEquals(myEmptyChildren, myXml.children("description"));
		assertEquals(myEmptyChildren, myXml.children("price"));

		/** 2º Test Case - Right */
		List<Xml> bookChildren = myXml.children("book");
		for (Xml child : bookChildren) {
			assertEquals("book", child.name());
		}
	}

	/**
	 * 
	 * Testing method string(String)
	 * 
	 * 1º Test Cases : The node has the attribute
	 * 
	 * 2º Test Cases : The node has not the attribute -> Causes a
	 * RuntimeException
	 * 
	 * 
	 */
	public void testString() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");

		/** 1º Test Cases : The node has the attribute */
		try {
			List<Xml> bookChildren = myXml.children("book");
			String s = "bk101";
			for (Xml child : bookChildren) {
				assertEquals(s, child.string("id"));
				int i = Integer.parseInt(s.substring(s.length() - 3));
				i++;
				s = s.substring(0, s.length() - 3) + i;
			}
			assertTrue(true);
		} catch (RuntimeException re) {
			fail("RuntimeException");
		}

		/** 2º Test Cases : The node has not the attribute -> Causes a RuntimeException */
		try {
			myXml.string("noAttribute");
		} catch (RuntimeException re) {
			assertEquals(re.getMessage(),
					"Could not find attribute: noAttribute, in node: catalog");
		}
	}

	/**
	 * 
	 * Testing method integer(String)
	 * 
	 */
	public void testInteger() {
		Xml myXml = new Xml("src/test/java/TestCore/catalog.xml", "catalog");
		assertEquals(1, myXml.integer("id"));
	}

	/** Ending the test properly */
	protected void tearDown() throws Exception {
		super.tearDown();

		m = null;
	}
}
