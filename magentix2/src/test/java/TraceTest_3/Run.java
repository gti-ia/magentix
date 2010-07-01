package TraceTest_3;

import org.apache.qpid.transport.*;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/*****************************************************************************************
/*                                      TraceTest_3                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent and a SUBSCRIBER agent.
    
    The PUBLISHER agent publishes a tracing service called 'DD_Test_TS' which content is
    just a string. Each second, the publisher generates the trace event. After 10
    seconds, the PUBLISHER unpublishes the tracing service and says 'Bye!'.
    
    The SUBSCRIBER agent subscribes to the tracing service 'DD_Test_TS' and waits for 10
    seconds for trace events to arrive. After this time, the SUBSCRIBER agent
    unsubscribes from the tracing service and says 'Bye!'. Each time a trace event is
    received, the SUBSCRIBER prints its content on the screen.
*****************************************************************************************/
public class Run {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ACLMessage coordination_msg;
		/**
		 * Setting the Logger
		 */
		DOMConfigurator.configure("configuration/loggin.xml");
		Logger logger = Logger.getLogger(Run.class);
		
		/**
		 * Connecting to Qpid Broker
		 */
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating the Trace Manager
			 */
			TraceManager tm = new TraceManager(new AgentID("tm"));
			
			/**
			 * Instantiating the publisher agent
			 */
			Publisher publisher = new Publisher(new AgentID("qpid://publisher@localhost:8080"));

			/**
			 * Instantiating the subscriber agent
			 */
			Subscriber subscriber = new Subscriber(new AgentID("qpid://subscriber@localhost:8080"));

			/**
			 * Execute the agents
			 */
			subscriber.start();
			publisher.start();
			
			System.out.println("WAITING...");
			Thread.sleep(30000);
			System.out.println("STOPPING EVERYTHING!");

			// Create connection
	        org.apache.qpid.transport.Connection con = new org.apache.qpid.transport.Connection();
	        con.connect("localhost", 5672, "test", "guest", "guest",false);
	        // Create session
	        org.apache.qpid.transport.Session session = con.createSession(0);
			
	        MessageTransfer xfr = new MessageTransfer();

			xfr.destination("amq.direct");
			xfr.acceptMode(MessageAcceptMode.EXPLICIT);
			xfr.acquireMode(MessageAcquireMode.PRE_ACQUIRED);
			
			DeliveryProperties deliveryProps = new DeliveryProperties();

			// Serialize message content
			String body;
			// Performative
			body = 16 + "#"; // REQUEST
			// Sender
			body = body + 0 + "#" + "";
			// receiver
			body = body + 0 + "#" + ""; 
			// reply to
			body = body + 0 + "#" + "";
			// language
			body = body + 0 + "#" + "";
			// encoding
			body = body + 0 + "#" + "";
			// ontology
			body = body + 0 + "#" + "";
			// protocol
			body =body + 0 + "#" + "";
			// conversation id
			body = body + 0 + "#" + "";
			// reply with
			body = body + 0 + "#" + "";
			// in reply to
			body = body + 0 + "#" + "";
			// reply by
			body = body + 0 + "#" + "";
			// content
			body = body + "STOP".length() + "#" + "STOP";

			xfr.setBody(body);
			deliveryProps.setRoutingKey(publisher.getAid().name);
			xfr.header(new Header(deliveryProps));
			session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
					xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
			
			xfr.setBody(body);
			deliveryProps.setRoutingKey(subscriber.getAid().name);
			xfr.header(new Header(deliveryProps));
			session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
					xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
