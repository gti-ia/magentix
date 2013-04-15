package MMS_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import es.upv.dsic.gti_ia.core.AgentID;


/**
 * Run class is the example for changing identity.
 *  User Alice execute the agentA, this agent sends a message to agentB (user Bob) and then
 * changes his identity to agentC.  Next sends the message again to agentB as agentC.
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class Run_Example3 {

    /**
     * @param args
     */
    public static void main(String[] args) {

	/**
	 * Setting the Logger
	 */
	DOMConfigurator.configure("configuration/loggin.xml");
	Logger logger = Logger.getLogger(Run_Example3.class);

	/**
	 * Connecting to Qpid Broker
	 */


	try {

	    /**
	     * 
	     * Instantiating a consumer agent
	     */
	    AgentAC agent = new AgentAC(new AgentID("agentA"));

	    /**
	     * Execute the agents
	     */
	    agent.start();


	} catch (Exception e) {
	    logger.error("Error  " + e.getMessage());
	}
    }

}
