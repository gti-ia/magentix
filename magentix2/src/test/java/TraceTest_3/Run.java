package TraceTest_3;

import org.apache.qpid.transport.*;

import java.util.Map;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.TraceEvent;
import es.upv.dsic.gti_ia.core.BaseAgent;

/*****************************************************************************************
/*                                      TraceTest_1                                      *
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
			//tm.start();
			
			/**
			 * Instantiating the publisher agent
			 */
			Publisher publisher = new Publisher(new AgentID("qpid://publisher@localhost:8080"));

			/**
			 * Instantiating the subscriber agent
			 */
			//Subscriber subscriber = new Subscriber(new AgentID("qpid://subscriber@localhost:8080"));

			/**
			 * Execute the agents
			 */
			//subscriber.start();
			publisher.start();
			
			System.out.println("WAITING...");
			Thread.sleep(10000);
			System.out.println("STOPPING EVERYTHING!");
//

			// Create connection
	        org.apache.qpid.transport.Connection con = new org.apache.qpid.transport.Connection();
	        con.connect("localhost", 5672, "test", "guest", "guest",false);

	        // Create session
	        org.apache.qpid.transport.Session session = con.createSession(0);
	        DeliveryProperties deliveryProps = new DeliveryProperties();
	        deliveryProps.setRoutingKey(publisher.getAid().toString());
	        session.messageTransfer("amq.direct", MessageAcceptMode.EXPLICIT,MessageAcquireMode.PRE_ACQUIRED,
	        		new Header(deliveryProps), "STOP");
			
//			send(coordination_msg);
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
