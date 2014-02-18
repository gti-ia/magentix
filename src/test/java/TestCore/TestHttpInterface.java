package TestCore;



import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringEscapeUtils;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.HttpInterface;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class TestHttpInterface extends TestCase {
	Process qpid_broker;

	public TestHttpInterface(String name) {
		super(name);
	}

	/** Test set up */
	protected void setUp() throws Exception {
		super.setUp();

		qpid_broker = Runtime.getRuntime().exec(
				"./installer/magentix2/bin/qpid-broker-0.20/bin/qpid-server");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				qpid_broker.getInputStream()));
		String line = reader.readLine();
		while (!line.contains("Qpid Broker Ready")) {
			line = reader.readLine();
		}

		AgentsConnection.connect();

		HttpInterfaceThread hiThread = new HttpInterfaceThread();
		hiThread.start();
	}

	/**
	 * 
	 * Test1
	 * 
	 * Checking the functionality
	 * 
	 */
	public void test1() {

		String jsonmessage = "{\"agent_name\": \"echoAgent\",\"conversation_id\": \"1\" ,\"content\": \"Hello World\"}";
		EchoAgent worker;
		HttpURLConnection connection = null;
		try {

			worker = new EchoAgent(new AgentID("echoAgent"));
			worker.start();

			/*
			 * Generates the request
			 */
			URL url = new URL("http://localhost:8000");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection
					.setFixedLengthStreamingMode(jsonmessage.getBytes().length);
			connection.setDoOutput(true);

			// send the POST out
			PrintWriter out = new PrintWriter(connection.getOutputStream());
			out.print(jsonmessage);
			out.close();

			InputStream in = connection.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String response = "";
			String responsePart = "";
			while (true) {
				while ((responsePart = reader.readLine()) != null) {
					response += responsePart;
				}
				response = StringEscapeUtils.unescapeJava(response);
				assertEquals("\"{\"jsonObject\":" + jsonmessage + "}\"",response);
				break;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

	}


	/** Ending the test properly */
	protected void tearDown() throws Exception {
		super.tearDown();
		AgentsConnection.disconnect();
		qpid_broker.destroy();

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
		while (true) {
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
				XStream xstream2 = new XStream(
						new JsonHierarchicalStreamDriver() {
							@Override
							public HierarchicalStreamWriter createWriter(
									Writer writer) {
								return new JsonWriter(writer,
										JsonWriter.DROP_ROOT_MODE);
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
}

class HttpInterfaceThread extends Thread {

	public void run() {
		HttpInterface h = new HttpInterface(8000);
		h.execute();
	}
}