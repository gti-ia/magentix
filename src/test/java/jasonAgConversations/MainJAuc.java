package jasonAgConversations;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class MainJAuc {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception{
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent jauc_initiator = new ConvJasonAgent(new AgentID("jauc_initiator"), "./src/test/java/jasonAgConversations/JapaneseAuction/jauc_initiator.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent jauc_agent1 = new ConvJasonAgent(new AgentID("jauc_agent1"), "./src/test/java/jasonAgConversations/JapaneseAuction/jauc_agent1.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent jauc_agent2 = new ConvJasonAgent(new AgentID("jauc_agent2"), "./src/test/java/jasonAgConversations/JapaneseAuction/jauc_agent2.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent jauc_agent3 = new ConvJasonAgent(new AgentID("jauc_agent3"), "./src/test/java/jasonAgConversations/JapaneseAuction/jauc_agent3.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent jauc_agent4 = new ConvJasonAgent(new AgentID("jauc_agent4"), "./src/test/java/jasonAgConversations/JapaneseAuction/jauc_agent4.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent jauc_agent5 = new ConvJasonAgent(new AgentID("jauc_agent5"), "./src/test/java/jasonAgConversations/JapaneseAuction/jauc_agent5.asl", arch,null,null);
		

		jauc_agent1.start();
		jauc_agent2.start();
		jauc_agent3.start();
		jauc_agent4.start();
		jauc_agent5.start();
		jauc_initiator.start();

	}

}
