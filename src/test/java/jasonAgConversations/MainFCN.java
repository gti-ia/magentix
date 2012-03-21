package jasonAgConversations;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

public class MainFCN {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent fcn_initiator = new ConvJasonAgent(new AgentID("fcn_initiator"), "./src/test/java/jasonAgConversations/fcn_initiator.asl", arch,null,null);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent fcn_participant1 = new ConvJasonAgent(new AgentID("fcn_participant1"), "./src/test/java/jasonAgConversations/fcn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent fcn_participant2 = new ConvJasonAgent(new AgentID("fcn_participant2"), "./src/test/java/jasonAgConversations/fcn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent fcn_participant3 = new ConvJasonAgent(new AgentID("fcn_participant3"), "./src/test/java/jasonAgConversations/fcn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent fcn_participant4 = new ConvJasonAgent(new AgentID("fcn_participant4"), "./src/test/java/jasonAgConversations/fcn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent fcn_participant5 = new ConvJasonAgent(new AgentID("fcn_participant5"), "./src/test/java/jasonAgConversations/fcn_participant.asl", arch,null,null);

		
		fcn_initiator.start();
		fcn_participant1.start();
		fcn_participant2.start();
		fcn_participant3.start();
		fcn_participant4.start();
		fcn_participant5.start();
		
	}
}
