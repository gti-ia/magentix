package TestCore;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
 * Tests for ACLMessage class
 * 
 * @author David Fernández - dfernandez@dsic.upv.es
 */

public class TestACLMessage extends TestCase {

	ACLMessage msg;
	Process qpid_broker;

	public TestACLMessage(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		/**
		 * Setting the configuration
		 */
		DOMConfigurator.configure("configuration/loggin.xml");

		/**
		 * Connecting to Qpid Broker
		 */

		try {
			/**
			 * Instantiating the ACLMessage
			 */
			msg = new ACLMessage();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Testing ACLMessage empty constructor
	 * 
	 * More attributes of ALCMessage could be tested but these three are enough
	 */
	public void testEmptyConstructor() {
		// msg is initialize in SetUp() with empty constructor by default

		assertEquals(msg.getPerformativeInt(), ACLMessage.UNKNOWN);
		assertEquals(msg.getSender().toString(), new AgentID().toString());
	}

	/**
	 * Testing ACLMessage constructor with performative
	 * 
	 * More attributes of ALCMessage could be tested but these three are enough
	 */
	public void testPerformativeConstructor() {
		// Set the message with the constructor
		msg = new ACLMessage(ACLMessage.AGREE);

		assertEquals(msg.getPerformativeInt(), ACLMessage.AGREE);
		assertEquals(msg.getSender().toString(), new AgentID().toString());
	}

	/**
	 * Testing ACLMessage setPerformative(Int)
	 * 
	 */
	public void testSetPerformative() {
		// Message with empty constructor by default performative = UNKNOWN
		msg.setPerformative(ACLMessage.PROPAGATE);

		assertEquals(msg.getPerformativeInt(), ACLMessage.PROPAGATE);
		assertFalse(msg.getPerformativeInt() == ACLMessage.UNKNOWN);
	}

	/**
	 * Testing ACLMessage setPerformative(String)
	 * 
	 */
	public void testSetPerformativeString() {
		// Message with empty constructor by default performative = UNKNOWN
		// Test with a not valid performative
		msg.setPerformative("proxy");
		assertTrue(msg.getPerformativeInt() == ACLMessage.UNKNOWN);

		// Test with a valid performative
		msg.setPerformative("PROXY");
		assertEquals(msg.getPerformativeInt(), ACLMessage.PROXY);

	}

	/**
	 * Testing ACLMessage getPerformative()
	 * 
	 */
	public void testGetPerformative() {
		// Message with empty constructor by default performative = UNKNOWN

		assertEquals(msg.getPerformative(), "UNKNOWN");
	}

	/**
	 * Testing ACLMessage getPerformative() Exception
	 * 
	 * If exception is thrown then returns "NOT-UNDERSTOOD"
	 * 
	 */
	public void testGetPerformativeException() {
		msg.setPerformative(30);// Not valid int performative = NOT_UNDERSTOOD

		assertEquals(msg.getPerformative(), "NOT-UNDERSTOOD");
	}

	/**
	 * Testing ACLMessage getPerformativeInt()
	 * 
	 */
	public void testGetPerformativeInt() {
		// Message with empty constructor by default performative = UNKNOWN = -1

		assertEquals(msg.getPerformativeInt(), -1);
	}

	/**
	 * Testing ACLMessage setSender()
	 * 
	 */
	public void testSetSender() {
		// Message with empty constructor by default sender = new AgentID()
		AgentID id = new AgentID("Charles");
		msg.setSender(id);

		assertEquals(msg.getSender().toString(), id.toString());
	}

	/**
	 * Testing ACLMessage getSender()
	 * 
	 */
	public void testGetSender() {
		// Message with empty constructor by default sender = new AgentID()

		assertEquals(msg.getSender().toString(), new AgentID().toString());
	}

	/**
	 * Testing ACLMessage setReceiver()
	 * 
	 * Clears receiver list and add the receiver
	 * 
	 */
	public void testSetReceiver() {
		// Message with empty constructor by default sender = new AgentID()
		AgentID id = new AgentID("Agustin");
		msg.setReceiver(id);

		assertEquals(msg.getTotalReceivers(), 1);
		assertEquals(msg.getReceiver().toString(), id.toString());
	}

	/**
	 * Testing ACLMessage getReceiver()
	 * 
	 * 
	 * Test gerReceiver() with more than one receiver and get the first one
	 */
	public void testGetReceiver() {
		// Message with empty constructor by default receivers = empty
		AgentID id = new AgentID("Agustin");
		AgentID id2 = new AgentID("Thomas");
		msg.setReceiver(id);
		msg.addReceiver(id2);

		assertEquals(msg.getReceiver().toString(), id.toString());
	}

	/**
	 * Testing ACLMessage getReceiver() when receiver is empty
	 * 
	 * getReceiver() retruns null
	 * 
	 */
	public void testGetEmptyReceiver() {
		// Message with empty constructor by default receivers = empty

		assertEquals(msg.getReceiver(), null);
	}

	/**
	 * Testing ACLMessage addReceiver()
	 * 
	 */
	public void testAddReceiverI() {
		// Message with empty constructor by default receivers = empty
		AgentID id = new AgentID("Agustin");
		AgentID id1 = new AgentID("Thomas");
		AgentID id2 = new AgentID("Jose");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		assertEquals(msg.getTotalReceivers(), 3);
	}

	/**
	 * Testing ACLMessage addReceiver() adding an existing Agent
	 * 
	 * When adding an existing agent addReceiver returns -1, if not 1
	 * 
	 */
	public void testAddExistingReceiver() {
		// Message with empty constructor by default receivers = empty
		String agentName = "Agustin";
		String protocol = "FIPA";
		String host = "46019";
		String port = "2840";
		AgentID id = new AgentID(agentName, protocol, host, port);
		int notExists = msg.addReceiver(id);
		int exists = msg.addReceiver(id);

		assertEquals(notExists, 1);
		assertEquals(exists, -1);
	}

	/**
	 * Testing ACLMessage getReceiver(Int)
	 * 
	 * Test get and agent order after been added
	 */
	public void testGetReceiverInt() {
		// Message with empty constructor by default receivers = empty
		AgentID id = new AgentID("Agustin");
		AgentID id1 = new AgentID("Thomas");
		AgentID id2 = new AgentID("Jose");
		msg.setReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		assertEquals(msg.getReceiver(0).toString(), id.toString());
		assertEquals(msg.getReceiver(1).toString(), id1.toString());
		assertEquals(msg.getReceiver(2).toString(), id2.toString());
	}

	/**
	 * Testing ACLMessage getReceiver(Int)
	 * 
	 * Test getReceiver when index is out of the List
	 */
	// @Test(expected = IndexOutOfBoundsException.class)
	public void testGetReceiverException() {
		// Message with empty constructor by default receivers = empty
		AgentID id = new AgentID("Agustin");
		AgentID id1 = new AgentID("Thomas");
		AgentID id2 = new AgentID("Jose");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		boolean expectedException = false;

		try {
			msg.getReceiver(-1);
		} catch (IndexOutOfBoundsException e) {
			expectedException = true;
		}

		assertTrue(expectedException);
	}

	/**
	 * Testing ACLMessage setReplyTo()
	 * 
	 */
	public void testSetReplyTo() {
		// Message with empty constructor by default reply_to = new AgentID();
		AgentID id = new AgentID("Michael");
		msg.setReplyTo(id);

		assertEquals(msg.getReplyTo().toString(), id.toString());
	}

	/**
	 * Testing ACLMessage getReplyTo()
	 * 
	 */
	public void testGetReplyTo() {
		// Message with empty constructor by default reply_to = new AgentID();

		assertEquals(msg.getReplyTo().toString(), new AgentID().toString());
	}

	/**
	 * Testing ACLMessage setContent()
	 * 
	 * 
	 */
	public void testSetContent() {
		// Message with empty constructor by default content = ""
		// byteSequenceContent = null
		String text = "Test text";
		msg.setContent(text);

		assertEquals(msg.getContent(), text);
	}

	/**
	 * Testing ACLMessage getContent()
	 * 
	 * 
	 */
	public void testGetContent() {
		// Message with empty constructor by default content = ""
		// byteSequenceContent = null

		assertEquals(msg.getContent(), "");
	}

	/**
	 * Testing ACLMessage setLanguage()
	 * 
	 * 
	 */
	public void testSetLanguage() {
		// Message with empty constructor by default language = ""
		String lang = "English";
		msg.setLanguage(lang);

		assertEquals(msg.getLanguage(), lang);
	}

	/**
	 * Testing ACLMessage getLanguage()
	 * 
	 * 
	 */
	public void testGetLanguage() {
		// Message with empty constructor by default language = ""

		assertEquals(msg.getLanguage(), "");
	}

	/**
	 * Testing ACLMessage setEncoding()
	 * 
	 * 
	 */
	public void testSetEncoding() {
		// Message with empty constructor by default encoding = ""
		String enc = "utf-8";
		msg.setEncoding(enc);

		assertEquals(msg.getEncoding(), enc);
	}

	/**
	 * Testing ACLMessage getEncoding()
	 * 
	 * 
	 */
	public void testGetEncoding() {
		// Message with empty constructor by default encoding = ""

		assertEquals(msg.getEncoding(), "");
	}

	/**
	 * Testing ACLMessage setOntology()
	 * 
	 */
	public void testSetOntology() {
		// Message with empty constructor by default ontology = ""
		String ont = "House";
		msg.setOntology(ont);

		assertEquals(msg.getOntology(), ont);
	}

	/**
	 * Testing ACLMessage getOntology()
	 * 
	 */
	public void testGetOntology() {
		// Message with empty constructor by default ontology = ""

		assertEquals(msg.getOntology(), "");
	}

	/**
	 * Testing ACLMessage setProtocol()
	 * 
	 */
	public void testSetProtocol() {
		// Message with empty constructor by default protocol = ""
		String prot = "Request";
		msg.setProtocol(prot);

		assertEquals(msg.getProtocol(), prot);
	}

	/**
	 * Testing ACLMessage getProtocol()
	 * 
	 */
	public void testGetProtocol() {
		// Message with empty constructor by default protocol = ""

		assertEquals(msg.getProtocol(), "");
	}

	/**
	 * Testing ACLMessage setConversationId()
	 * 
	 */
	public void testSetConversationId() {
		// Message with empty constructor by default conversation_id = ""
		String convID = "1234";
		msg.setConversationId(convID);

		assertEquals(msg.getConversationId(), convID);
	}

	/**
	 * Testing ACLMessage getConversationId()
	 * 
	 */
	/*
	 * public void testGetConversationId() { //Message with empty constructor by
	 * default conversation_id = ""
	 * 
	 * assertEquals(msg.getConversationId(), ""); }
	 */

	/**
	 * Testing ACLMessage setReplyWith()
	 * 
	 */
	public void testSetReplyWith() {
		// Message with empty constructor by default reply = ""
		String rep = "reply";
		msg.setReplyWith(rep);

		assertEquals(msg.getReplyWith(), rep);
	}

	/**
	 * Testing ACLMessage getReplyWith()
	 * 
	 */
	public void testGetReplyWith() {
		// Message with empty constructor by default reply_with = ""

		assertEquals(msg.getReplyWith(), "");
	}

	/**
	 * Testing ACLMessage setInReplyTo()
	 * 
	 */
	public void testSetInReplyTo() {
		// Message with empty constructor by default in_reply_to = ""
		String repTo = "James";
		msg.setInReplyTo(repTo);

		assertEquals(msg.getInReplyTo(), repTo);
	}

	/**
	 * Testing ACLMessage getInReplyTo()
	 * 
	 */
	public void testGetInReplyTo() {
		// Message with empty constructor by default in_reply_to = ""

		assertEquals(msg.getInReplyTo(), "");
	}

	/**
	 * Testing ACLMessage setReplyByDate()
	 * 
	 */
	public void testSetReplyByDate() {
		// Message with empty constructor by default reply_byInMillisec = 0
		Date date = new Date(1992, 04, 23);
		msg.setReplyByDate(date);

		assertEquals(msg.getReplyByDate(), date);
	}

	/**
	 * Testing ACLMessage getReplyByDate()
	 * 
	 */
	public void testGetReplyByDate() {
		// Message with empty constructor by default reply_byInMillisec = 0

		assertEquals(msg.getReplyByDate(), null);
	}

	/**
	 * Testing ACLMessage getReplyBy()
	 * 
	 * ReplybyDate in string format
	 * 
	 */
	public void testGetReplyBy() {
		// Message with empty constructor by default reply_byInMillisec = 0

		assertEquals(msg.getReplyBy(), "");
		Date date = new Date(1992, 04, 23);
		msg.setReplyByDate(date);
		assertEquals(msg.getReplyBy(), ISO8601.toString(date));

	}

	/**
	 * Testing ACLMessage clearAllReceiver()
	 * 
	 */
	public void testClearAllReceiver() {
		// Message with empty constructor by default receivers = empty
		AgentID id = new AgentID("Agustin");
		AgentID id1 = new AgentID("Thomas");
		AgentID id2 = new AgentID("Jose");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		assertEquals(msg.getTotalReceivers(), 3);

		msg.clearAllReceiver();

		assertEquals(msg.getTotalReceivers(), 0);
	}

	/**
	 * Testing ACLMessage getReceiverList()
	 * 
	 */
	public void testGetReceiverList() {
		// Message with empty constructor by default receivers = empty
		AgentID id = new AgentID("Agustin");
		AgentID id1 = new AgentID("Thomas");
		AgentID id2 = new AgentID("Jose");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		ArrayList<AgentID> agents = new ArrayList<AgentID>();
		agents.add(id);
		agents.add(id1);
		agents.add(id2);

		assertEquals(msg.getReceiverList(), agents);
	}

	/**
	 * Testing ACLMessage getTtoalReceivers()
	 * 
	 */
	public void testGetTotalReceivers() {
		// Message with empty constructor by default receivers = empty

		assertEquals(msg.getTotalReceivers(), 0);

		AgentID id = new AgentID("Ana");
		AgentID id1 = new AgentID("Clara");
		AgentID id2 = new AgentID("Julia");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		assertEquals(msg.getTotalReceivers(), 3);
	}

	/**
	 * Testing ACLMessage clone()
	 * 
	 */
	public void testClone() {
		// Message with empty constructor by default receivers = empty

		AgentID id = new AgentID("Ana");
		AgentID id1 = new AgentID("Clara");
		AgentID id2 = new AgentID("Julia");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);

		ACLMessage msg2 = msg.clone();

		assertEquals(msg.toString(), msg2.toString());// Change this when equals
														// method is implemented
	}

	/**
	 * Testing ACLMessage createReply(()
	 * 
	 */
	public void testCreateReply() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setReplyWith("reply");
		msg.setConversationId("16");

		ACLMessage replyMsg = msg.createReply();

		assertEquals(replyMsg.getReceiver().toString(), msg.getSender()
				.toString());
		assertEquals(replyMsg.getLanguage(), msg.getLanguage());
		assertEquals(replyMsg.getOntology(), msg.getOntology());
		assertEquals(replyMsg.getProtocol(), msg.getProtocol());
		assertEquals(replyMsg.getSender(), null);
		assertEquals(replyMsg.getInReplyTo(), msg.getReplyWith());
		assertEquals(replyMsg.getConversationId(), msg.getConversationId());
		assertEquals(replyMsg.getReplyByDate(), null);
		assertEquals(replyMsg.getContent(), "");
		assertEquals(replyMsg.getEncoding(), "");
		assertEquals(replyMsg.getExchangeHeaders(),
				new HashMap<String, String>());
	}

	/**
	 * Testing ACLMessage copyFromAsTemplate(()
	 * 
	 */
	public void testCopyFromAsTemplate() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		AgentID id1 = new AgentID("Clara");
		AgentID id2 = new AgentID("Julia");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.addReceiver(id2);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date
		msg.setHeader("Topic", "Bandages");

		ACLMessage templateMsg = new ACLMessage();
		templateMsg.copyFromAsTemplate(msg);

		assertEquals(templateMsg.getPerformative(), msg.getPerformative());
		assertEquals(templateMsg.getSender().toString(), msg.getSender()
				.toString());
		assertEquals(templateMsg.getReceiverList(), msg.getReceiverList());
		assertEquals(templateMsg.getReplyTo(), msg.getReplyTo());
		assertEquals(templateMsg.getContent(), msg.getContent());
		assertEquals(templateMsg.getLanguage(), msg.getLanguage());
		assertEquals(templateMsg.getEncoding(), msg.getEncoding());
		assertEquals(templateMsg.getOntology(), msg.getOntology());
		assertEquals(templateMsg.getProtocol(), msg.getProtocol());
		assertEquals(templateMsg.getConversationId(), msg.getConversationId());
		assertEquals(templateMsg.getReplyWith(), msg.getReplyWith());
		assertEquals(templateMsg.getInReplyTo(), msg.getInReplyTo());
		assertEquals(templateMsg.getReplyByDate(), msg.getReplyByDate());
		assertEquals(templateMsg.getExchangeHeaders(), msg.getExchangeHeaders());
	}

	/**
	 * Testing ACLMessage setHeader()
	 * 
	 */
	public void testSetHeader() {
		msg.setHeader("Topic", "PC");

		assertEquals(msg.getHeaderValue("Topic"), "PC");
	}

	/**
	 * Testing ACLMessage getReplyByDate()
	 * 
	 * Test getHeader() with a not existing key
	 */
	public void testGetHeader() {
		// Message with empty constructor by default headers = empty

		assertEquals(msg.getHeaderValue("Rogue"), "");
	}

	/**
	 * Testing ACLMessage getHeaders()
	 * 
	 */
	public void testGetHeaders() {
		msg.setHeader("Topic", "PC");
		msg.setHeader("Error", "NotFound");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Topic", "PC");
		headers.put("Error", "NotFound");

		assertEquals(msg.getHeaders(), headers);
	}

	/**
	 * Testing ACLMessage headersAreEqual()
	 * 
	 * 
	 * The execution flow returns true Test created throw reflection due to it
	 * is a private method
	 */
	public void testHeadersAreEqualTrue() {
		msg.setHeader("Topic", "PC");
		msg.setHeader("ERROR", "NotFound");
		msg.setHeader("Urgency", "High");

		ACLMessage msg2 = new ACLMessage();

		msg2.setHeader("Topic", "PC");
		msg2.setHeader("ERROR", "Void");
		msg2.setHeader("Urgency", "High");

		Class[] parameterTypes = new Class[1];
		parameterTypes[0] = ACLMessage.class;

		Method m = null;
		try {
			m = msg.getClass().getDeclaredMethod("headersAreEqual",
					parameterTypes);
			m.setAccessible(true);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}

		Object[] parameters = new Object[1];
		parameters[0] = msg2;

		boolean result = false;
		try {
			result = (Boolean) m.invoke(msg, parameters);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		assertTrue(result);
	}

	/**
	 * Testing ACLMessage headersAreEqual()
	 * 
	 * The execution flow returns false
	 * 
	 */
	public void testHeadersAreEqualFalse() {
		msg.setHeader("Topic", "PC");
		msg.setHeader("Color", "Red");
		msg.setHeader("Urgency", "High");

		ACLMessage msg2 = new ACLMessage();

		msg2.setHeader("Topic", "PC");
		msg2.setHeader("Color", "Blue");
		msg2.setHeader("Urgency", "High");

		Class[] parameterTypes = new Class[1];
		parameterTypes[0] = ACLMessage.class;

		Method m = null;
		try {
			m = msg.getClass().getDeclaredMethod("headersAreEqual",
					parameterTypes);
			m.setAccessible(true);
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		}

		Object[] parameters = new Object[1];
		parameters[0] = msg2;

		boolean result = true;
		try {
			result = (Boolean) m.invoke(msg, parameters);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}

		assertFalse(result);
	}

	/**
	 * Testing ACLMessage setContentObject()
	 * 
	 * Needs a serializable parameter
	 * 
	 */
	public void testSetContentObject() {
		int num = 2;
		try {
			msg.setContentObject(num);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		int num2 = (Integer) msg.getContentObject();
		assertEquals(num2, num);
	}

	/**
	 * Testing ACLMessage setContentObject()
	 * 
	 * Test serialializable exception
	 * 
	 */
	public void testSetContentObjectException() {
		boolean thrownException = false;
		// ArraList implements serializable
		// but Socket does not
		ArrayList<Socket> data = new ArrayList<Socket>();
		data.add(new Socket());

		try {
			msg.setContentObject(data);
		} catch (IOException e) {
			thrownException = true;
		}

		assertTrue(thrownException);
	}

	/**
	 * Testing ACLMessage getContentObject()
	 * 
	 * Test method when content !=null & !content.equals("")
	 */
	public void testGetContentObject() {
		// When content !=null & !content.equals("")
		String content = "hi everyone";
		msg.setContent(content);
		String content2 = (String) msg.getContentObject();
		assertEquals(content2, content);
	}

	/**
	 * Testing ACLMessage getContentObject()
	 * 
	 * Test method when content = null && ByteSequenceContent is not empty
	 */
	public void testGetContentObjectByteSequence() {
		int num = 2;
		try {
			ByteArrayOutputStream c = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(c);
			oos.writeObject(num);
			oos.flush();
			byte[] byteArray = c.toByteArray();
			msg.setByteSequenceContent(byteArray);
			Integer content2 = (Integer) msg.getContentObject();
			assertEquals(content2.intValue(), num);
		} catch (IOException e) {
			fail();
		}

	}

	/**
	 * Testing ACLMessage getContentObject()
	 * 
	 * Test method when content = null && ByteSequenceContent is empty
	 */
	public void testGetContentObjectByteSequenceEmpty() {
		byte[] byteArray = new byte[0];
		msg.setByteSequenceContent(byteArray);
		Object content2 = msg.getContentObject();
		assertEquals(content2, null);
	}

	/**
	 * Testing ACLMessage getContentObject()
	 * 
	 * Test method when content = null && ByteSequenceContent = null
	 */
	public void testGetContentObjectNull() {
		msg.setContent(null);
		Object content2 = msg.getContentObject();
		assertEquals(content2, null);
	}

	/**
	 * Testing ACLMessage setByteSequenceContent()
	 * 
	 * @throws IOException
	 * 
	 */
	public void testSetByteSequenceContent() throws IOException {
		try {
			int num = 2;
			ByteArrayOutputStream c = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(c);
			oos.writeObject(num);
			oos.flush();
			byte[] byteArray = c.toByteArray();
			msg.setByteSequenceContent(byteArray);

			assertEquals(msg.getByteSequenceContent(), byteArray);
		} catch (IOException e) {
			fail();
		}
	}

	/**
	 * Testing ACLMessage getByteSequenceContent()
	 * 
	 * Get tested when content == null & byteSequenceContent != null
	 * 
	 * @throws IOException
	 */
	public void testGetByteSequence() {
		try {
			String textContent = "Text content";
			ByteArrayOutputStream c = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(c);
			oos.writeObject(textContent);
			oos.flush();
			byte[] byteArray = c.toByteArray();
			msg.setByteSequenceContent(byteArray);

			assertEquals(msg.getByteSequenceContent(), byteArray);
		} catch (IOException e) {
			fail();
		}
	}

	/**
	 * Testing ACLMessage getByteSequenceContent()
	 * 
	 * Get tested when content == null & byteSequenceContent == null also tested
	 * when only content != null
	 * 
	 * @throws IOException
	 */
	public void testGetByteSequenceContent() {
		// ACLMessage empty constructor content = ""

		// Test when content == null & byteSequenceContent == null
		msg.setContent(null);
		assertEquals(msg.getByteSequenceContent(), null);

		// Test when only content != null
		String textContent = "Text content";
		msg.setContent(textContent);

		byte[] msgBytes = msg.getByteSequenceContent();
		byte[] bytes = new StringBuffer(textContent).toString().getBytes();

		boolean equals = true;
		if (msgBytes.length != bytes.length)// check different length
			equals = false;
		for (int i = 0; i < bytes.length; i++) {// check different content
			if (bytes[i] != msgBytes[i])
				equals = false;
		}

		assertTrue(equals);
	}

	/**
	 * Testing ACLMessage getPerformative(String)
	 * 
	 * Returns the integer of the Performative or -1 if it is not a valid
	 * performative
	 */
	public void testGetPerformativeFromString() {
		// Valid performative
		int perf = ACLMessage.getPerformative("AGREE");
		assertEquals(perf, ACLMessage.AGREE);

		// Invalid performative
		perf = ACLMessage.getPerformative("ENO");
		assertEquals(perf, -1);

	}

	/**
	 * Testing ACLMessage getPerformative(Int)
	 * 
	 * Returns the String of the Performative or perfomative "NOT UNDERSTOOD" if
	 * it is not a valid performative
	 */
	public void testGetPerformativeFromInt() {
		// Valid performative
		String perf = ACLMessage.getPerformative(11);
		assertEquals(perf, "PROPOSE");

		// Invalid performative
		perf = ACLMessage.getPerformative(36);
		assertEquals(perf, "NOT-UNDERSTOOD");
	}

	/**
	 * Testing ACLMessage toString()
	 * 
	 */
	public void testToString() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		msg.addReceiver(id);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date
		msg.setHeader("Topic", "Bandages");

		String msgString = msg.toString();

		assertTrue(msgString.contains(msg.getPerformativeInt() + ""));
		assertTrue(msgString.contains(msg.getSender().toString()));
		assertTrue(msgString.contains(msg.getReceiver().toString()));
		assertTrue(msgString.contains(msg.getReplyTo().toString()));
		assertTrue(msgString.contains(msg.getContent().toString()));
		assertTrue(msgString.contains(msg.getLanguage().toString()));
		assertTrue(msgString.contains(msg.getEncoding().toString()));
		assertTrue(msgString.contains(msg.getOntology().toString()));
		assertTrue(msgString.contains(msg.getProtocol().toString()));
		assertTrue(msgString.contains(msg.getConversationId().toString()));
		assertTrue(msgString.contains(msg.getReplyWith().toString()));
		assertTrue(msgString.contains(msg.getInReplyTo().toString()));
		assertTrue(msgString.contains(msg.getReplyBy()));
	}

	/**
	 * Testing ACLMessage toString()
	 * 
	 * When parameters are null
	 */
	public void testToStringNull() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(null);
		// Receiver is empty
		msg.setReplyTo(null);
		msg.setContent(null);
		msg.setLanguage(null);
		msg.setEncoding(null);
		msg.setOntology(null);
		msg.setProtocol(null);
		msg.setConversationId(null);
		msg.setReplyWith(null);
		msg.setInReplyTo(null);
		msg.setReplyByDate(null);// Current date

		String msgString = msg.toString();
		String[] msgArray = msgString.split("#"); // Msg separated by # to count
													// the correnct number of
													// terms in the msg
		assertEquals(msgArray.length, 13); // It should be 13
	}

	/**
	 * Testing ACLMessage fromString()
	 * 
	 */
	public void testfromString() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		msg.addReceiver(id);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date
		// msg.setHeader("Topic", "Bandages");

		String msgString = msg.toString();

		ACLMessage msg2 = ACLMessage.fromString(msgString);

		assertEquals(msgString, msg2.toString());
	}

	/**
	 * Testing ACLMessage fromString()
	 * 
	 * FromString tested when ACLMessage atributes are empty, in this case
	 * toString must be equal and messages
	 * 
	 * 
	 * 
	 */
	public void testfromStringEmptyMsg() {

		// Message with empty constructor by default performative = UNKNOWN
		String msgString = msg.toString();

		ACLMessage msgFrom = ACLMessage.fromString(msgString);
		msgFrom.setReplyByDate(msg.getReplyByDate());

		assertEquals(msgString, msgFrom.toString());
		assertTrue(msg.equals(msgFrom));
	}

	/**
	 * Testing ACLMessage fromString()
	 * 
	 * FromString tested when ACLMessage atributes are null, in this case
	 * toString are equals but messages are different
	 * 
	 */
	public void testfromStringNullMsg() {

		// Message with empty constructor by default performative = UNKNOWN
		String msgString = msg.toString();
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(null);
		// Receiver is empty
		msg.setReplyTo(null);
		msg.setContent(null);
		msg.setLanguage(null);
		msg.setEncoding(null);
		msg.setOntology(null);
		msg.setProtocol(null);
		msg.setConversationId(null);
		msg.setReplyWith(null);
		msg.setInReplyTo(null);
		msg.setReplyByDate(null);// Current date

		ACLMessage msg2 = ACLMessage.fromString(msgString);
		msg2.setReplyByDate(msg.getReplyByDate());

		assertEquals(msgString, msg2.toString());
		assertFalse(msg.equals(msg2));
	}

	/**
	 * Testing ACLMessage getExchangeHeader()
	 * 
	 */
	public void testGetExchangeHeader() {
		// Message with empty constructor by default exchangeHeaders = empty

		msg.putExchangeHeader("Topic", "PC");

		assertEquals(msg.getExchangeHeader("Topic"), "PC");
	}

	/**
	 * Testing ACLMessage putExchangeHeader()
	 * 
	 */
	public void testPutExchangeHeader() {
		// Message with empty constructor by default exchangeHeaders = empty

		msg.putExchangeHeader("Rogue", "Legacy");

		assertEquals(msg.getExchangeHeader("Rogue"), "Legacy");
	}

	/**
	 * Testing ACLMessage getExchangeHeaders()
	 * 
	 */
	public void testGetExchangeHeaders() {

		msg.putExchangeHeader("Topic", "PC");
		msg.putExchangeHeader("Error", "NotFound");

		Map<String, String> exchangeHeaders = new HashMap<String, String>();
		exchangeHeaders.put("Topic", "PC");
		exchangeHeaders.put("Error", "NotFound");

		assertEquals(msg.getExchangeHeaders(), exchangeHeaders);
	}

	/**
	 * Testing ACLMessage equals()
	 * 
	 */
	public void testEquals() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		AgentID id1 = new AgentID("Clara");
		msg.addReceiver(id);
		msg.addReceiver(id1);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date
		msg.setHeader("Topic", "Bandages");

		String msgString = msg.toString();

		ACLMessage msg2 = new ACLMessage();

		msg2.setPerformative(ACLMessage.AGREE);
		msg2.setSender(new AgentID("David"));
		msg2.addReceiver(id1);
		msg2.addReceiver(id);
		msg2.setReplyTo(new AgentID("Peter"));
		msg2.setContent("Hi");
		msg2.setLanguage("English");
		msg2.setEncoding("utf-8");
		msg2.setOntology("Hospital");
		msg2.setProtocol("Request");
		msg2.setConversationId("16");
		msg2.setReplyWith("reply");
		msg2.setInReplyTo("Alan");
		msg2.setReplyByDate(msg.getReplyByDate());// Current date
		msg2.setHeader("Topic", "Bandages");

		assertTrue(msg.equals(msg2));
	}

	/**
	 * Testing ACLMessage equals()
	 * 
	 * Tested when messages are not equals
	 */
	public void testNotEquals() {
		ACLMessage msg2 = new ACLMessage();

		msg.setPerformative(ACLMessage.AGREE);
		msg2.setPerformative(ACLMessage.CANCEL);
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setSender(new AgentID("David"));
		msg2.setSender(new AgentID("Angel"));
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		AgentID id = new AgentID("Ana");
		AgentID id1 = new AgentID("Clara");
		msg.addReceiver(id);
		msg2.addReceiver(id1);
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setReplyTo(new AgentID("Peter"));
		msg2.setReplyTo(new AgentID("Ricard"));
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setContent("Hi");
		msg2.setContent("Bye bye");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setLanguage("English");
		msg2.setLanguage("Spanish");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setEncoding("utf-8");
		msg2.setEncoding("ascii");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setOntology("Hospital");
		msg2.setOntology("Food");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setProtocol("Request");
		msg2.setProtocol("LookForProposals");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setConversationId("16");
		msg2.setConversationId("19");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setReplyWith("UPV");
		msg2.setReplyWith("UV");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setInReplyTo("Alan");
		msg2.setInReplyTo("Alfonso");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setReplyByDate(null);
		msg2.setReplyByDate(new Date());
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.setHeader("Topic", "Bandages");
		msg2.setHeader("Topic", "Animals");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg.putExchangeHeader("Apples", "2");
		msg2.putExchangeHeader("Meat", "9");
		assertFalse(msg.equals(msg2));
		msg = new ACLMessage();
		msg2 = new ACLMessage();

		msg2.setPerformative(ACLMessage.AGREE);
		msg2.setSender(new AgentID("David"));
		msg2.addReceiver(id1);
		msg2.addReceiver(id);
		msg2.setReplyTo(new AgentID("Peter"));
		msg2.setContent("Hi");
		msg2.setLanguage("English");
		msg2.setEncoding("utf-8");
		msg2.setOntology("Hospital");
		msg2.setProtocol("Request");
		msg2.setConversationId("16");
		msg2.setReplyWith("reply");
		msg2.setInReplyTo("Alan");
		msg2.setReplyByDate(null);// Current date
		msg2.setHeader("Topic", "Sun");

		assertFalse(msg.equals(msg2));
	}

	/**
	 * Testing ACLMessage equals()
	 * 
	 * Equals tested when Object parameter is null
	 */
	public void testEqualsNull() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		msg.addReceiver(id);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date

		assertFalse(msg.equals(null));
	}

	/**
	 * Testing ACLMessage equals()
	 * 
	 * Equals tested when Object parameter is null
	 */
	public void testEqualsSameObject() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		msg.addReceiver(id);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date

		assertTrue(msg.equals(msg));
	}

	/**
	 * Testing ACLMessage equals()
	 * 
	 * Equals tested when Object parameter is not an instance of ACLMessage
	 */
	public void testEqualsNotIntanceOfACLMessage() {
		msg.setPerformative(ACLMessage.AGREE);
		msg.setSender(new AgentID("David"));
		AgentID id = new AgentID("Ana");
		msg.addReceiver(id);
		msg.setReplyTo(new AgentID("Peter"));
		msg.setContent("Hi");
		msg.setLanguage("English");
		msg.setEncoding("utf-8");
		msg.setOntology("Hospital");
		msg.setProtocol("Request");
		msg.setConversationId("16");
		msg.setReplyWith("reply");
		msg.setInReplyTo("Alan");
		msg.setReplyByDate(null);// Current date

		int num = 2;

		assertFalse(msg.equals(num));
	}

	public void tearDown() throws Exception {
		super.tearDown();
		msg = null;
	}
}