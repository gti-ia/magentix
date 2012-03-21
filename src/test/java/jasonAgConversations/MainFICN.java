package jasonAgConversations;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

public class MainFICN {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent ficn_initiator = new ConvJasonAgent(new AgentID("ficn_initiator"), "./src/test/java/jasonAgConversations/ficn_initiator.asl", arch,null,null);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent ficn_participant1 = new ConvJasonAgent(new AgentID("ficn_participant1"), "./src/test/java/jasonAgConversations/ficn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent ficn_participant2 = new ConvJasonAgent(new AgentID("ficn_participant2"), "./src/test/java/jasonAgConversations/ficn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent ficn_participant3 = new ConvJasonAgent(new AgentID("ficn_participant3"), "./src/test/java/jasonAgConversations/ficn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent ficn_participant4 = new ConvJasonAgent(new AgentID("ficn_participant4"), "./src/test/java/jasonAgConversations/ficn_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent ficn_participant5 = new ConvJasonAgent(new AgentID("ficn_participant5"), "./src/test/java/jasonAgConversations/ficn_participant.asl", arch,null,null);

		
		ficn_initiator.start();
		ficn_participant1.start();
		ficn_participant2.start();
		ficn_participant3.start();
		ficn_participant4.start();
		ficn_participant5.start();
		
	}
}
