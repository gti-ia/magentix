package BridgeAgent_Example;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


import BaseAgent_Example.ConsumerAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.BridgeAgentInOut;
import es.upv.dsic.gti_ia.core.BridgeAgentOutIn;

/**
 * Run class is a example in which simulated an internal sender agent,
 * wants to send a message to an external consumer agent of our platform.
 * BridgeAgentInOut and BridgeAgentOutIn agents carry out this work, via HTTP.
 * 
 * @author Sergio Pajares - spajares@dsic.upv.es
 * @author Joan Bellver - jbellver@dsic.upv.es
 */
public class RunBridges {

	public static void main(String[] args) {
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect("gtiiaprojects2.dsic.upv.es");
		Logger logger = Logger.getLogger(RunBridges.class);
		
		
		try {

			/**
			 * Instantiating a BridgeAgentInOut SingleAgent
			 */
			BridgeAgentInOut agenteInOut = new BridgeAgentInOut(new AgentID(
					"BridgeAgentInOut", "qpid", "serpafer.dsic.upv.es", "5000"));
			/**
			 * Instantiating a BridgeAgentOutIn SingleAgent
			 */
			BridgeAgentOutIn agenteOutIn = new BridgeAgentOutIn(new AgentID(
					"BridgeAgentOutIn", "qpid", "serpafer.dsic.upv.es", "5000"));

			/**
			 * Instantiating a EmisorAgent BaseAgent
			 */
		//	SenderAgent agente1 = new SenderAgent(new AgentID("emisor-agent",
		//			"qpid", "localhost", "8080"));
			
			/**
			 * Instantiating a ConsumerAgent BaseAgent
			 */
			ConsumerAgent agente2 = new ConsumerAgent(new AgentID(
					"consumer", "qpid", "localhost", "8080"));
			/**
			 * Execute the four agents
			 */
			agenteInOut.start();
			agenteOutIn.start();

			agente2.start();
		//	agente1.start();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
