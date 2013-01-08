package TraceBasic;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


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
		//Cuando se utilize modo seguro deberemos comentar esta linea, ya que las conexiones se harán por agente, no por usuario.
		AgentsConnection.connect();

		try {
			/**
			 * Instantiating the Trace Manager
			 */
			TraceManager tm = new TraceManager(new AgentID("TM"));

			/**
			 * Instantiating the publisher agent
			 */
			Publisher publisher = new Publisher(new AgentID("qpid://publisher@localhost:8080"));

			/**
			 * Instantiating the subscriber agent
			 */
			Subscriber subscriber = new Subscriber(new AgentID("qpid://subscriber@localhost:8080"));
			
			/**
			 * Instantiating the coordinator agent
			 */

			Coordinator coordinator = new Coordinator(new AgentID("qpid://coordinator@localhost:8080"), publisher.getAid());
			/**
			 * Execute the agents
			 */
			publisher.start();
			subscriber.start();
			coordinator.start();
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
