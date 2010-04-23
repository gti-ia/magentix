package HeadersExchangeTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageCreditUnit;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;
import org.apache.qpid.transport.SessionException;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Simple issuer/consumer example. This example is based on trace events instead of
 * being based on ACL messages. So, a SenderAgent builds and sends trace events and
 * a ConsumerAgent subscribe to them in order to receive them. When the consumer agent
 * receives a trace event, it displays its content on the screen
 * 
 * @author Luis Burdalo - lburdalo@dsic.upv.es
 */
public class Run {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);

		// Create connection
        Connection con = new Connection();
        con.connect("localhost", 5672, "test", "guest", "guest",false);

        // Create session
        Session session = con.createSession(0);

        // declare and bind queues
        session.queueDeclare("headers_queue_any", null, null);
        session.queueDeclare("headers_queue_all", null, null);
        // we need to declare the header: name, type, alternate exchange
        session.exchangeDeclare("test.headers", "headers", "amq.direct", null);
        // The matching algorithm is controlled by 'x-match' property
        // 'x-match' can take one of two values,
        // (i) 'all' implies that all the other pairs must match the headers
        // property of a message for that message to be routed (i.e. an AND match)
        // (ii) 'any' implies that the message should be routed if any of the
        // fields in the headers property match one of the fields in the arguments table (i.e. an OR match)
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-match", "any");
        arguments.put("h1", "v1");
        arguments.put("h2", "v2");
        session.exchangeBind("headers_queue_any", "test.headers", "useless", arguments);
        arguments = new HashMap<String, Object>();
        arguments.put("x-match", "all");
        arguments.put("h1", "v1");
        arguments.put("h2", "v2");
        session.exchangeBind("headers_queue_all", "test.headers", "useless", arguments);
        // confirm completion
        session.sync();
        
        System.out.println("Created queues...\n");
        
        //cleanup
        session.close();
        con.close();
        
	}

}
