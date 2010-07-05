package TraceBasic;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.qpid.transport.DeliveryProperties;
import org.apache.qpid.transport.Header;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageTransfer;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/*****************************************************************************************/
/*                                      Trace_Basic                                      */
/*****************************************************************************************/
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       */
/*****************************************************************************************/
/*                                     DESCRIPTION                                       */
/*****************************************************************************************

    Simple battery of requests to make sure the Trace Manager does not let erroneous
    publication/unpublication nor subscription/unsubscription
    
    Initialization:
    
    PUBLISHER:
      - Publishes the tracing service DD_Test_TS1 (OK)
      - Publishes AGAIN the tracing service DD_Test_TS1 (FAIL!)
      - Unpublishes DD_Test_TS2 (FAIL!)
      - Unpublishes DD-Test_TS1 (OK)
      - Publishes 5 tracing services: DD_Test_TS1 to DD_Test_TS5 (OK)
      
    SUBSCRIBER:
    	- Subscribes to DD_Test_TSSS1 from any entity (FAIL!)
    	- Subscribes to DD_Test_TSSS1 from PUBLISHER entity (FAIL!)
    	- Subscribes to DD_Test_TS1 from any entity (OK)
    	- Subscribes AGAIN to DD_Test_TS1 from any entity (FAIL!)
    	- Subscribes to DD_Test_TS1 from PUBLISHER entity (OK)
    	- Subscribes AGAIN to DD_Test_TS1 from PUBLISHER entity (FAIL!)
    	- Subscribes to DD_Test_TS1 from SUBSCRIBER entity (FAIL!)
    	- Unsubscribes from DD_Test_TS2 from any entity (FAIL!)
    	- Unsubscribes from DD_Test_TS1 from SUBSCRIBER (FAIL!)
    	- Unsubscribes from DD_Test_TS1 from PUBLISHER (OK)
    	- Unsubscribes from DD_Test_TS1 from any entity (OK)
    	- Unpublishes DD_Test_TS1 (FAIL!)
      
    Execution:
    
    PUBLISHER:
    	- Generates a trace event of each tracing service every second.
    	- When the SUBSCRIBER requests it (via ACLMessage), it unpublishes DD_Test_TS3
    	- When a STOP message arrives from the main Run thread, it stops generating trace
    	  events and unpublishes all tracing services (DD_Test_TS3 unpublication should fail
    	  since it should be already unpublished).
    	  
    SUBSCRIBER:
    	- Subscribe to DD_Test_TS1 (OK: Receiving events from DD_Test_TS1)
    	- Subscribe to DD_Test_TS2 (OK: Receiving events from DD_Test_TS1 and DD_Test_TS2)
    	- Subscribe to DD_Test_TS3 (OK: Receiving events from DD_Test_TS1,DD_Test_TS2 and
    		DD_Test_TS3)
    	- Send a message to PUBLISHER requesting the unpublication of DD_Test_TS3
    	   (OK: Receiving events from DD_Test_TS1 and DD_Test_TS2)
    	- Unsubscribe from DD_Test_TS1 and DD_Test_TS2 (OK: No more event receiving)

*****************************************************************************************/
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
			publisher.start();
			subscriber.start();
			
			System.out.println("WAITING 15 seconds...");
			Thread.sleep(15000);
			System.out.println("STOPPING EVERYTHING!");

			// Create connection
	        org.apache.qpid.transport.Connection con = new org.apache.qpid.transport.Connection();
	        con.connect("gtiiaprojects2", 5672, "test", "guest", "guest",false);
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
//			
//			xfr.setBody(body);
//			deliveryProps.setRoutingKey(subscriber.getAid().name);
//			xfr.header(new Header(deliveryProps));
//			session.messageTransfer(xfr.getDestination(), xfr.getAcceptMode(),
//					xfr.getAcquireMode(), xfr.getHeader(), xfr.getBodyString());
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
