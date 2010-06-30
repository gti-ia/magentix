package TraceTest_1;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.trace.TraceManager;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

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
			//Coordinator coordinator = new Coordinator(new AgentID("qpid://coordinator@localhost:8080"));
			
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
			//coordinator.start();
			subscriber.start();
			publisher.start();
		} catch (Exception e) {
			logger.error("Error  " + e.getMessage());
		}
	}

}
