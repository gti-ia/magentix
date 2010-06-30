package TraceTest_2;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/*****************************************************************************************/
/*                                      TraceTest_2                                      */
/*****************************************************************************************/
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       */
/*****************************************************************************************/
/*                                     DESCRIPTION                                       */
/*****************************************************************************************

    Simple test with two types of agents: 100 PUBLISHER agents and 30 SUBSCRIBER agents.
    
    PUBLISHER agents publish 2 different DD tracing services each and generate 10 trace
    events for each tracing service during 10 seconds (one per second). After that,
    the tracing services are unpublished. Waiting times before and after unpublishing
    tracing services are there just to let SUBSCRIBER agents time enough to unsubscribe
    and to print messages on the screen. 
    
    SUBSCRIBER agents subscribe randomly to two of the services offered by the PUBLISHER
    agents and wait during 12 seconds for events to arrive. Each time a trace event is
    received, the SUBSCRIBER agent updates the corresponding counter so that it is
    possible to verify after the execution that the number of received events of each
    tracing service is 10. Before finishing, each SUBSCRIBER agent displays the number
    of trace events of each tracing service which have been received.
    
    Messages to be displayed on the screen during the execution have been commented in
    order to make the execution more easily readable.

*****************************************************************************************/
public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int N_PUBLISHERS = 100;
		final int N_SUBSCRIBERS = 30;
		Publisher publishers[] = new Publisher[N_PUBLISHERS];
		Subscriber subscribers[] = new Subscriber[N_SUBSCRIBERS];
		int i;
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

			System.out.println("INITIALIZING...");
			
			/**
			 * Instantiating publisher agents
			 */
			for (i=0; i < N_PUBLISHERS; i++){
				publishers[i] = new Publisher(new AgentID("qpid://publisher"+ (i+1) +"@localhost:8080"));
			}

			/**
			 * Instantiating the subscriber agents
			 */
			for (i=0; i < N_SUBSCRIBERS; i++){
				subscribers[i] = new Subscriber(new AgentID("qpid://subscriber"+ (i+1) +"@localhost:8080"));
			}

			System.out.println("NOW, WAIT ABOUT 10 SECONDS...");
			
			/**
			 * Execute the agents
			 */
			for (i=0; i < N_SUBSCRIBERS; i++){
				subscribers[i].start();
			}
			
			for (i=0; i < N_PUBLISHERS; i++){
				publishers[i].start();
			}
			
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
