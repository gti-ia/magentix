package jasonAgentsConversations.agentNConv;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class MainFR {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent frequest_initiator = new ConvJasonAgent(new AgentID("frequest_initiator"), "./src/test/java/jasonAgentsConversations/agentNConv/frequest_initiator.asl", arch);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent frequest_participant = new ConvJasonAgent(new AgentID("frequest_participant"), "./src/test/java/jasonAgentsConversations/agentNConv/frequest_participant.asl", arch);

		
		frequest_initiator.start();
		frequest_participant.start();	


	}

}