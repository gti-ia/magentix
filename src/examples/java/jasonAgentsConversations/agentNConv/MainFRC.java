package jasonAgentsConversations.agentNConv;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class MainFRC {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent frecruiting_initiator = new ConvJasonAgent(new AgentID("frecruiting_initiator"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_initiator.asl", arch);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent frecruiting_participant = new ConvJasonAgent(new AgentID("frecruiting_participant"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_participant.asl", arch);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent frecruiting_agent1 = new ConvJasonAgent(new AgentID("frecruiting_agent1"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_agent.asl", arch);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent frecruiting_agent2 = new ConvJasonAgent(new AgentID("frecruiting_agent2"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_agent.asl", arch);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent frecruiting_agent3 = new ConvJasonAgent(new AgentID("frecruiting_agent3"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_agent.asl", arch);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent frecruiting_agent4 = new ConvJasonAgent(new AgentID("frecruiting_agent4"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_agent.asl", arch);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent frecruiting_agent5 = new ConvJasonAgent(new AgentID("frecruiting_agent5"), "./src/test/java/jasonAgentsConversations/agentNConv/frecruiting_agent.asl", arch);
		
		frecruiting_initiator.start();
		frecruiting_participant.start();	
		frecruiting_agent1.start();
		frecruiting_agent2.start();
		frecruiting_agent3.start();
		frecruiting_agent4.start();
		frecruiting_agent5.start();


	}

}