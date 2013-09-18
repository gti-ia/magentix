package TestCore;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.ISO8601;

/**
 * Tests for AgentID class
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestAgentID extends TestCase {
	
	AgentID agent;
	
	public TestAgentID(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		/**
		 * Setting the configuration
		 */
		DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Instantiating the AgentID
		 */
		agent = new AgentID();


	}
	
	
	/**
	 * Testing AgentID empty constructor
	 * 
	 */
	public void testEmptyConstructor()
	{
		//agent is initialize in SetUp() with empty constructor by default
		
		assertEquals(agent.name, "");
		assertEquals(agent.protocol, "");
		assertEquals(agent.host,"");
		assertEquals(agent.port,"");
	}

	/**
	 * Testing AgentID full constructor
	 * 
	 * Constructor with all the atributes
	 * of the class
	 */
	public void testFullConstructor()
	{
		//agent is initialize in SetUp() with empty constructor by default

		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(name, protocol, host, port);
		assertEquals(agent.name, name);
		assertEquals(agent.protocol, protocol);
		assertEquals(agent.host, host);
		assertEquals(agent.port, port);
	}

	/**
	 * Testing AgentID id constructor
	 * 
	 * Constructor with the ID of the agent
	 * in a common name format
	 */
	public void testIDNameConstructor()
	{
		//agent is initialize in SetUp() with empty constructor by default

		String name = "David";
		String protocol = "qpid";	//Default in the constructor
		String host = "localhost";	//Default in the constructor
		String port = "8080";		//Default in the constructor

		agent = new AgentID(name);
		assertEquals(agent.name, name);
		assertEquals(agent.protocol, protocol);
		assertEquals(agent.host, host);
		assertEquals(agent.port, port);
	}

	/**
	 * Testing AgentID id constructor
	 * 
	 * Constructor with the ID of the agent
	 * in an address format
	 */
	public void testIDAddressConstructor()
	{
		//agent is initialize in SetUp() with empty constructor by default

		String id = "FIPA://David@16400:2840";
		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(id);
		assertEquals(agent.name, name);
		assertEquals(agent.protocol, protocol);
		assertEquals(agent.host, host);
		assertEquals(agent.port, port);
	}
	
	/**
	 * Testing AgentID toString()
	 * 
	 * Tested with empty and full cosntructor
	 */
	public void testToString()
	{
		//agent is initialize in SetUp() with empty constructor by default
		assertEquals(agent.toString(), "://@:");

		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(name, protocol, host, port);

		assertEquals(agent.toString(), protocol + "://" + name + "@" + host + ":" + port);
	}

	/**
	 * Testing AgentID name_all()
	 * 
	 * Similar to toString() but returns a string
	 * with a similar format to Jade
	 *
	 * Tested with empty and full cosntructor
	 */
	public void testNameAll()
	{
		//agent is initialize in SetUp() with empty constructor by default
		assertEquals(agent.name_all(), "@:");

		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(name, protocol, host, port);

		assertEquals(agent.name_all(), name + "@" + host + ":" + port);
	}

	/**
	 * Testing AgentID addresses_all()
	 * 
	 * Similar to toString() but returns a string
	 * with a similar format to and URL
	 *
	 * Tested with empty and full cosntructor
	 */
	public void testAddressesAll()
	{
		//agent is initialize in SetUp() with empty constructor by default
		assertEquals(agent.addresses_all(), "://:");

		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(name, protocol, host, port);

		assertEquals(agent.addresses_all(), protocol + "://" + host + ":" + port);
	}

	/**
	 * Testing AgentID addresses_single()
	 * 
	 */
	public void testAddressesSingle()
	{
		//agent is initialize in SetUp() with empty constructor by default
		assertEquals(agent.addresses_single(), ":");

		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(name, protocol, host, port);

		assertEquals(agent.addresses_single(), host + ":" + port);
	}

	/**
	 * Testing AgentID getLocalName()
	 * 
	 *	Tested with empty and full constructor
	 */
	public void testGetLocalName()
	{
		//agent is initialize in SetUp() with empty constructor by default
		assertEquals(agent.getLocalName(), "");

		String name = "David";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";

		agent = new AgentID(name, protocol, host, port);

		assertEquals(agent.getLocalName(), name);
	}

	/**
	 * Testing AgentID getLocalName()
	 * 
	 *Tested when the name given has an "@" 
	 */
	public void testGetLocalNameAddress()
	{
		//agent is initialize in SetUp() with empty constructor by default

		String id = "FIPA://David@16400:2480";
		String name = "David@Fernandez";
		String protocol = "FIPA";
		String host = "16400";
		String port = "2840";
		
		int namePos = name.lastIndexOf('@');		
		String expectedName = name.substring(0, namePos);

		agent = new AgentID(name, protocol, host, port);
		
		assertEquals(agent.getLocalName(), expectedName);
	}

	public void tearDown(){
		agent = null;
	}
}