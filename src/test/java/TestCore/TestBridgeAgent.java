package TestCore;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.BridgeAgentInOut;
import es.upv.dsic.gti_ia.core.BridgeAgentOutIn;
import es.upv.dsic.gti_ia.core.BridgeAgentOutIn.httpToACL;

public class TestBridgeAgent {
	Process qpid_broker;
	private Method method;
	BridgeAgentInOut baio = null;
	BridgeAgentOutIn baoi = null;
	ConsumerAgent consumer = null;
	SenderAgent sender = null;
	SmallHttpServer shs = null;
    CountDownLatch cdl = null;

	/** Test set up */
	@Before
	public void setUp() throws Exception {
		qpid_broker = qpidManager.UnixQpidManager.startQpid(
				Runtime.getRuntime(), qpid_broker);

		AgentsConnection.connect();

		/** Preparation to test private method Generate_All */
		Class parametersAddAttributte = ACLMessage.class;

		method = BridgeAgentInOut.class.getDeclaredMethod("Generate_All",
				parametersAddAttributte);
		method.setAccessible(true);

		/**
		 * Preparation to test private method Generate_Header Class[] params =
		 * new Class[2]; params[0] = InetAddress.class; params[0] =
		 * Integer.class;
		 * 
		 * method[1] = BridgeAgentInOut.class.getDeclaredMethod("Generate_All",
		 * parametersAddAttributte); method[1].setAccessible(true);
		 */

		try {

			baio = new BridgeAgentInOut(new AgentID("baio"));
			baoi = new BridgeAgentOutIn(new AgentID("baoi"), 8082);
			consumer = new ConsumerAgent(new AgentID("receiver"));
			sender = new SenderAgent(new AgentID("sender"));
			cdl = new CountDownLatch(1);
			shs = new SmallHttpServer(cdl);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Test Http Message Transformations First, take and Http message and change
	 * it to a ACLMessage. Then, turn back the transformation and check whether
	 * the new message is equal to the previous.
	 */ 
	 @Test
	public void testFormatHttp() {

		String message = "POST http://foo.com:80/acc HTTP/1.1\r\n"
				+ "Cache-Control: no-cache\r\n"
				+ "Host: foo.com:80\r\n"
				+ "Mime-Version: 1.0\r\n"
				+ "Content-Type: multipart-mixed;boundary=\"251D738450A171593A1583EB\"\r\n"
				+ "Content-Length: 1518\r\n"
				+ "Connection: close\r\n"
				+ "\r\n"
				+ "This is not part of the MIME multipart encoded message.\r\n"
				+ "--251D738450A171593A1583EB\r\n"
				+ "Content-Type: application/fipa.mts.env.rep.xml.std\r\n"
				+ "\r\n"
				+ "<?xml version=\"1.0\"?>\r\n"
				+ "<envelope>"
				+ "<params index=\"1\">"
				+ "<to>"
				+ "<agent-identifier>"
				+ "<name>receiver@foo.com</name>"
				+ "<addresses>"
				+ "<url>http://localhost:8081</url>"
				+ "</addresses>"
				+ "</agent-identifier>"
				+ "</to>"
				+ "<from>"
				+ "<agent-identifier>"
				+ "<name>sender</name>"
				+ "<addresses>"
				+ "<url>http://localhost:8082</url>"
				+ "</addresses>"
				+ "</agent-identifier>"
				+ "</from>"
				+ "<acl-representation>fipa.acl.rep.string.std</acl-representation>"
				+ "<payload-encoding>US-ASCII</payload-encoding>"
				+ "<date>20000508T042651481</date>"
				+ "<received >"
				+ "<received-by value=\"http://foo.com/acc\"/>"
				+ "<received-date value=\"20000508T042651481\"/>"
				+ "<received-id value=\"123456789\"/>"
				+ "</received>"
				+ "</params>"
				+ "</envelope>\r\n"
				+ "--251D738450A171593A1583EB\r\n"
				+ "Content-Type: application/fipa.acl.rep.string.std; charset=US-ASCII\r\n"
				+ "\r\n" + "(inform\r\n" + ":sender" + "(agent-identifier"
				+ ":name sender@bar.com"
				+ ":addresses (sequence http://bar.com:80/acc))\r\n"
				+ ":receiver" + "(agent-identifier" + ":name receiver@foo.com"
				+ ":addresses (sequence http://foo.com:80/acc )) )\r\n"
				+ ":content-length 14" + ":reply-with task1-003"
				+ ":language fipa-sl0" + ":ontology planning-ontology-1"
				+ ":content" + "\"((done task1))\"" + "\r\n"
				+ ":protocol http\r\n" + ":conversation-id 1\r\n)"
				+ "--251D738450A171593A1583EB--";

		String expected = "";
		try {
			expected = "POST qpid://localhost:8081 HTTP/1.1\r\n"
					+ "Cache-Control: no-cache\r\n"
					+ "Mime-Version: 1.0\r\n"
					+ "Host: 127.0.0.1:8081\r\n"
					+ "Content-Type: multipart/mixed ; boundary=\"a36869921a26b9d812878a42b8fc2cd\"\r\n"
					+ "Content-Length: 800\r\n"
					+ "Connection: Keep-Alive\r\n"
					+ "\r\n"
					+ "This is not part of the MIME multipart encoded message.\r\n"
					+ "--a36869921a26b9d812878a42b8fc2cd\r\n"
					+ "Content-Type: application/xml\r\n" + "\r\n"
					+ "<?xml version=\"1.0\"?>\n" + "<envelope>"
					+ "<params index=\"1\">" + "<to>" + "<agent-identifier>"
					+ "<name>receiver</name>" + "<addresses>"
					+ "<url>qpid://localhost:8081</url>" + "</addresses>"
					+ "</agent-identifier>" + "</to>" + "<from>"
					+ "<agent-identifier>"
					+ "<name>sender@localhost:8082</name>" + "<addresses>"
					+ "<url>http://"
					+ InetAddress.getLocalHost().getCanonicalHostName()
					+ ":8082</url>"
					+ "</addresses>"
					+ "</agent-identifier>"
					+ "</from>"
					+ "<acl-representation>fipa.acl.rep.string.std</acl-representation>"
					+ "<payload-length>321</payload-length>"
					+ "<date></date>"
					+ "<intended-receiver><agent-identifier><name>receiver</name><addresses><url>qpid://localhost:8081</url></addresses></agent-identifier></intended-receiver>"
					+ "</params>"
					+ "</envelope>\r\n"
					+ "--a36869921a26b9d812878a42b8fc2cd\r\n"
					+ "Content-Type: application/text\r\n"
					+ "\r\n"
					+ "(INFORM\r\n"
					+ " :sender "
					+ "( agent-identifier"
					+ " :name \"sender@"
					+ InetAddress.getLocalHost().getHostName()
					+ ":"
					+ BridgeAgentOutIn.getHttp_port()
					+ "\""
					+ " :addresses (sequence http://"
					+ InetAddress.getLocalHost().getCanonicalHostName()
					+ ":"
					+ BridgeAgentOutIn.getHttp_port()
					+ " ))\r\n"
					+ " :receiver (set ( agent-identifier :name \"receiver\""
					+ " :addresses (sequence qpid://localhost:8081 )) )\r\n"
					+ " :content "
					+ "\"((done task1))\""
					+ "\r\n"
					+ " :protocol \"http\"\r\n"
					+ " :conversation-id 1\r\n )\r\n"
					+ "--a36869921a26b9d812878a42b8fc2cd--\r\n\r\n\r\n";
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(message.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httpToACL hta = baoi.new httpToACL(is);
		Object aclMsg = hta.createACLMessage();
		try {
			String received = (String) method.invoke(baio, aclMsg);
			received = received.substring(0, received.indexOf("<date>") + 6)
					+ received.substring(received.indexOf("</date>"));
			System.out.println(received);
			assertEquals(expected, received);
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
	 * Test Sending from OUT to IN
	 * 
	 * @throws UnknownHostException
	 * 
	 */
	 @Test(timeout = 5000)
	public void testSendOutIn() throws UnknownHostException {
		String message = "This is not part of the MIME multipart encoded message.\r\n"
				+ "--a36869921a26b9d812878a42b8fc2cd\r\n"
				+ "Content-Type: application/xml\r\n"
				+ "\r\n"
				+ "<?xml version=\"1.0\"?>\n"
				+ "<envelope>"
				+ "<params index=\"1\">"
				+ "<to>"
				+ "<agent-identifier>"
				+ "<name>receiver@localhost</name>"
				+ "<addresses>"
				+ "<url>http://"
				+ InetAddress.getLocalHost().getCanonicalHostName()
				+ ":8082</url>"
				+ "</addresses>"
				+ "</agent-identifier>"
				+ "</to>"
				+ "<from>"
				+ "<agent-identifier>"
				+ "<name>sender@localhost</name>"
				+ "<addresses>"
				+ "<url>http://"
				+ InetAddress.getLocalHost().getCanonicalHostName()
				+ ":8082</url>"
				+ "</addresses>"
				+ "</agent-identifier>"
				+ "</from>"
				+ "<acl-representation>fipa.acl.rep.string.std</acl-representation>"
				+ "<payload-length>321</payload-length>"
				+ "<date></date>"
				+ "<intended-receiver><agent-identifier><name>receiver</name><addresses><url>qpid://localhost:8081</url></addresses></agent-identifier></intended-receiver>"
				+ "</params>"
				+ "</envelope>\r\n"
				+ "--a36869921a26b9d812878a42b8fc2cd\r\n"
				+ "Content-Type: application/text\r\n"
				+ "\r\n"
				+ "(INFORM\r\n"
				+ " :sender "
				+ "( agent-identifier"
				+ " :name \"sender@"
				+ InetAddress.getLocalHost().getHostName()
				+ ":"
				+ BridgeAgentOutIn.getHttp_port()
				+ "\""
				+ " :addresses (sequence http://"
				+ InetAddress.getLocalHost().getCanonicalHostName()
				+ ":"
				+ BridgeAgentOutIn.getHttp_port()
				+ " ))\r\n"
				+ " :receiver (set ( agent-identifier :name \"receiver\""
				+ " :addresses (sequence qpid://localhost:8081 )) )\r\n"
				+ " :content "
				+ "\"((done task\""
				+ "\r\n"
				+ " :protocol \"http\"\r\n"
				+ " :conversation-id 1\r\n )\r\n"
				+ "--a36869921a26b9d812878a42b8fc2cd--\r\n\r\n\r\n";

		HttpURLConnection connection = null;
		baoi.start();
		consumer.start();

		try {
			connection = sendRequest(connection, message);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (consumer.getMessage() == null) {
			// System.out.println("Busco:");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		assertEquals("((done task", consumer.getMessage().getContent());

	}

	/**
	 * 
	 * Test Sending from OUT to IN
	 * 
	 * @throws UnknownHostException
	 * 
	 */ 
	 @Test(timeout = 50000)
	public void testSendOutInLarge() throws UnknownHostException {
		String bigContent = readBigMsg();
		String message = "This is not part of the MIME multipart encoded message.\r\n"
				+ "--a36869921a26b9d812878a42b8fc2cd\r\n"
				+ "Content-Type: application/xml\r\n"
				+ "\r\n"
				+ "<?xml version=\"1.0\"?>\n"
				+ "<envelope>"
				+ "<params index=\"1\">"
				+ "<to>"
				+ "<agent-identifier>"
				+ "<name>receiver@localhost</name>"
				+ "<addresses>"
				+ "<url>http://"
				+ InetAddress.getLocalHost().getCanonicalHostName()
				+ ":8082</url>"
				+ "</addresses>"
				+ "</agent-identifier>"
				+ "</to>"
				+ "<from>"
				+ "<agent-identifier>"
				+ "<name>sender@localhost</name>"
				+ "<addresses>"
				+ "<url>http://"
				+ InetAddress.getLocalHost().getCanonicalHostName()
				+ ":8082</url>"
				+ "</addresses>"
				+ "</agent-identifier>"
				+ "</from>"
				+ "<acl-representation>fipa.acl.rep.string.std</acl-representation>"
				+ "<payload-length>321</payload-length>"
				+ "<date></date>"
				+ "<intended-receiver><agent-identifier><name>receiver</name><addresses><url>qpid://localhost:8081</url></addresses></agent-identifier></intended-receiver>"
				+ "</params>"
				+ "</envelope>\r\n"
				+ "--a36869921a26b9d812878a42b8fc2cd\r\n"
				+ "Content-Type: application/text\r\n"
				+ "\r\n"
				+ "(INFORM\r\n"
				+ " :sender "
				+ "( agent-identifier"
				+ " :name \"sender@"
				+ InetAddress.getLocalHost().getHostName()
				+ ":"
				+ BridgeAgentOutIn.getHttp_port()
				+ "\""
				+ " :addresses (sequence http://"
				+ InetAddress.getLocalHost().getCanonicalHostName()
				+ ":"
				+ BridgeAgentOutIn.getHttp_port()
				+ " ))\r\n"
				+ " :receiver (set ( agent-identifier :name \"receiver\""
				+ " :addresses (sequence qpid://localhost:8081 )) )\r\n"
				+ " :content "
				+ "\""
				+ bigContent
				+ "\""
				+ "\r\n"
				+ " :protocol \"http\"\r\n"
				+ " :conversation-id 1\r\n )\r\n"
				+ "--a36869921a26b9d812878a42b8fc2cd--\r\n\r\n\r\n";

		HttpURLConnection connection = null;
		baoi.start();
		consumer.start();

		try {
			connection = sendRequest(connection, message);

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (consumer.getMessage() == null) {
			// System.out.println("Busco:");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		assertEquals(bigContent, consumer.getMessage().getContent());

	}

	/**
	 * 
	 * Test Sending from IN to OUT
	 * 
	 * @throws UnknownHostException
	 * 
	 */
	@Test(timeout = 10000)
	public void testSendInOut() throws UnknownHostException {
		baio.start();
		shs.start();
		sender.start();
		try {
			cdl.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(shs.getContentMsg(), "Hello, I'm sender");
	}

	/*
	 * Auxiliar Methods
	 */
	private HttpURLConnection sendRequest(HttpURLConnection connection,
			String message) throws IOException {
		/*
		 * Generates the request
		 */
		URL url = new URL("http://localhost:8082/acc");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		// connection.setUseCaches(false);
		connection
				.setRequestProperty("Content-Type",
						"multipart-mixed ;boundary=\"a36869921a26b9d812878a42b8fc2cd\"");
		connection.setFixedLengthStreamingMode(message.getBytes().length);
		// connection.setChunkedStreamingMode(1000000);
		connection.setDoOutput(true);
		connection.setDoInput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(message);
		wr.flush();
		wr.close();
		return connection;
	}
	public String readBigMsg() {
		String result = "";
		BufferedReader br = null;
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(
					"src/test/java/TestCore/bigMsg.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				result += sCurrentLine;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/** Ending the test properly */
	@After
	public void tearDown() throws Exception {
		shs.finalize();
		baoi.exit();
		baio.exit();
		
		AgentsConnection.disconnect();

		qpidManager.UnixQpidManager.stopQpid(qpid_broker);

	}

}