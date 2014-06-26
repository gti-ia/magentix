package TestCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.HttpInterface;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class TestHttpInterface {
	Process qpid_broker;
	HttpInterfaceThread hiThread;

	/** Test set up */
	@Before
	public void setUp() throws Exception {

		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));
		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}

		AgentsConnection.connect();

		hiThread = new HttpInterfaceThread();
		hiThread.start();
	}

	/**
	 * 
	 * Test
	 * 
	 * Checking the functionality
	 * 
	 * Expected Behavour
	 * 
	 */
	@Test(timeout = 8000)
	public void testFunctionality() {

		String jsonmessage = "{\"agent_name\": \"echoAgent\",\"conversation_id\": \"1\" ,\"content\": \"Hello World\"}";
		EchoAgent worker;
		HttpURLConnection connection = null;
		try {

			worker = new EchoAgent(new AgentID("echoAgent"));
			worker.start();

			connection = sendRequest(connection, jsonmessage);

			String response = getResponse(connection);

			assertEquals("\"{\"jsonObject\":" + jsonmessage + "}\"", response);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	/**
	 * 
	 * Test Agent Name Empty
	 * 
	 * Send the post request with the agent_name empty
	 * 
	 * MalformedURLException Expected
	 * 
	 */
	@Test(timeout = 8000)
	public void testAgentNameEmpty() {

		String jsonmessage = "{\"agent_name\": \"\",\"conversation_id\": \"1\" ,\"content\": \"Hello World\"}";
		EchoAgent worker;
		HttpURLConnection connection = null;
		try {

			worker = new EchoAgent(new AgentID("echoAgent"));
			worker.start();

			connection = sendRequest(connection, jsonmessage);

			assertEquals(
					"Wrong request format. Read Magentix manual for more information.",
					getResponse(connection));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	/**
	 * 
	 * Test Agent Conversation ID Empty
	 * 
	 * Send the post request with the conversation_id empty
	 * 
	 * MalformedURLException Expected
	 * 
	 */
	@Test(timeout = 8000)
	public void testAgentConversationIDEmpty() {

		String jsonmessage = "{\"agent_name\": \"echoAgent\",\"conversation_id\": \"\" ,\"content\": \"Hello World\"}";
		EchoAgent worker;
		HttpURLConnection connection = null;
		try {

			worker = new EchoAgent(new AgentID("echoAgent"));
			worker.start();

			connection = sendRequest(connection, jsonmessage);

			assertEquals(
					"Wrong request format. Read Magentix manual for more information.",
					getResponse(connection));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	/**
	 * 
	 * Test Agent Wrong Name
	 * 
	 */
	@Test(timeout = 8000)
	public void testAgentWrongName() {

		String jsonmessage = "{\"agent_name\": \"wrongAgent\",\"conversation_id\": \"1\" ,\"content\": \"Hello World\"}";
		EchoAgent worker;
		HttpURLConnection connection = null;
		try {

			worker = new EchoAgent(new AgentID("echoAgent"));
			worker.start();

			connection = sendRequest(connection, jsonmessage);

			
			assertEquals(
					"There is not any agent with that name.",
					getResponse(connection));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			fail();
			e1.printStackTrace();
			
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}

	/**
	 * 
	 * Test Agent Simultaneous requests handled by different Agents
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	@Test(timeout = 8000)
	public void testSimultaneousRequestsDifferentWorkerAgent() {
		int requestsAmount = 7;
		String[] jsonmessage = new String[requestsAmount];
		for (int i = 0; i < requestsAmount; i++) {
			jsonmessage[i] = "{\"agent_name\": \"echoAgent" + i
					+ "\",\"conversation_id\": \"" + i
					+ "\" ,\"content\": \"Message" + i + "\"}";
		}
		EchoAgent worker;
		HttpURLConnection[] connection = new HttpURLConnection[requestsAmount];
		try {
			for (int i = 0; i < requestsAmount; i++) {
				worker = new EchoAgent(new AgentID("echoAgent" + i));
				worker.start();
			}

			for (int i = 0; i < requestsAmount; i++) {
				connection[i] = sendRequest(connection[i], jsonmessage[i]);
			}
			System.out.println("Responses: ");
			String[] response = new String[requestsAmount];
			for (int i = 0; i < requestsAmount; i++) {
				response[i] = getResponse(connection[i]);
			}
			for (int i = 0; i < requestsAmount; i++) {
				assertEquals("\"{\"jsonObject\":" + jsonmessage[i] + "}\"",
						response[i]);
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			fail();
		} finally {
			if (connection != null) {
				for (int i = 0; i < requestsAmount; i++) {
					connection[i].disconnect();
				}
			}
		}

	}
	
	/** Ending the test properly */
	@After
	public void tearDown() throws Exception {
		hiThread.shutdown();
		AgentsConnection.disconnect();
		qpid_broker.destroy();

	}

	/*
	 * 
	 * 
	 * 
	 * Auxiliar Methods
	 */
	private HttpURLConnection sendRequest(HttpURLConnection connection,
			String jsonmessage) throws IOException {
		/*
		 * Generates the request
		 */
		URL url = new URL("http://localhost:8000");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setFixedLengthStreamingMode(jsonmessage.getBytes().length);
		connection.setDoOutput(true);
		connection.setDoInput(true);

		/*
		 * Send the POST
		 */
		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.print(jsonmessage);
		out.close();
		return connection;
	}

	public String getResponse(HttpURLConnection connection) {
		InputStream in = null;
		try {
			int statusCode = connection.getResponseCode();
			if (statusCode <= 200 && statusCode < 300) {
				in = connection.getInputStream();
			} else {
				in = connection.getErrorStream();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String response = "";
		String responsePart = "";

		try {
			while ((responsePart = reader.readLine()) != null) {
				response += responsePart;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response = StringEscapeUtils.unescapeJava(response);
		return response;
	}
}

class EchoAgent extends SingleAgent {

	public EchoAgent(AgentID aid) throws Exception {
		super(aid);
	}

	public void execute() {
		/**
		 * Wait for messages, forever
		 */

		try {
			ACLMessage msg = receiveACLMessage(); // receive the message
													// with the query from
													// the web page
			String resContent = msg.getContent();

			// We prepare the response message
			ACLMessage response = new ACLMessage(ACLMessage.INFORM);
			response.setSender(getAid());
			response.setReceiver(msg.getSender());
			response.setConversationId(msg.getConversationId());

			// Transform the product Java objet into a JSON object
			XStream xstream2 = new XStream(new JsonHierarchicalStreamDriver() {
				@Override
				public HierarchicalStreamWriter createWriter(Writer writer) {
					return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
				}
			});
			String result = xstream2.toXML(resContent);
			response.setContent(result);

			// send the response message
			this.send(response);
		} catch (Exception e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
			return;
		}
	}
}

class HttpInterfaceThread extends Thread {
	HttpInterface h;

	public void run() {
		h = new HttpInterface(8000);
		h.execute();
	}

	public void shutdown() {
		h.shutdown();
	}
}