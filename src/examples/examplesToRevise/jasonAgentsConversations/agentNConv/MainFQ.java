package jasonAgentsConversations.agentNConv;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class MainFQ {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent fquery_initiator = new ConvJasonAgent(new AgentID("fquery_initiator"), "./src/test/java/jasonAgentsConversations/agentNConv/fquery_initiator.asl", arch);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent fquery_participant = new ConvJasonAgent(new AgentID("fquery_participant"), "./src/test/java/jasonAgentsConversations/agentNConv/fquery_participant.asl", arch);

		
		fquery_initiator.start();
		fquery_participant.start();	


	}

}