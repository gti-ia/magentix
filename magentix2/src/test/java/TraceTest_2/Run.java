package TraceTest_2;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/*****************************************************************************************
/*                                      Trace_Basic                                      *
/*****************************************************************************************
/*                     Author: Luis Burdalo (lburdalo@dsic.upv.es)                       *
/*****************************************************************************************
/*                                     DESCRIPTION                                       *
/*****************************************************************************************
    Simple test with three agents: a PUBLISHER agent, a SUBSCRIBER agent and a
    COORDINATOR agent.
    
    The PUBLISHER agent publishes a tracing service called 'DD_Test_TS' which content is
    just a string. Each second, the publisher generates the trace event and then sends a
    message to the COORDINATOR agent with the content of the trace event. After 10
    seconds, the PUBLISHER unpublishes the tracing service and says 'Bye!'.
    
    The SUBSCRIBER agent subscribes to the tracing service 'DD_Test_TS' and waits for 10
    seconds for trace events to arrive. After this time, the SUBSCRIBER agent
    unsubscribes from the tracing service and says 'Bye!'. Each time a trace event is
    received, the SUBSCRIBER sends a message to the coordinator with the content of the
    trace event.
    
    The COORDINATOR agent waits for 10 seconds for messages to arrive. Each time a
    message arrives, it prints in the screen the content of the message and the AgentID
    of the agent which sent it.
*****************************************************************************************/
public class Run {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int N_PUBLISHERS = 1;
		final int N_SUBSCRIBERS = 1;
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
			tm.start();
			
			/**
			 * Instantiating the coordinator agent
			 */
//			Coordinator coordinator = new Coordinator(new AgentID("qpid://coordinator@localhost:8080"));
			
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

			/**
			 * Execute the agents
			 */
//			coordinator.start();
			
//			for (i=0; i < N_SUBSCRIBERS; i++){
//				subscribers[i].start();
//			}
			
			for (i=0; i < N_PUBLISHERS; i++){
				publishers[i].start();
			}
			
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
