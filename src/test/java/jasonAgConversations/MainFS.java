package jasonAgConversations;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

public class MainFS {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent fsubscribe_initiator = new ConvJasonAgent(new AgentID("fsubscribe_initiator"), "./src/test/java/jasonAgConversations/fsubscribe_initiator.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent fsubscribe_participant = new ConvJasonAgent(new AgentID("fsubscribe_participant"), "./src/test/java/jasonAgConversations/fsubscribe_participant.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent rate_change_agent = new ConvJasonAgent(new AgentID("rate_change_agent"), "./src/test/java/jasonAgConversations/rate_change_agent.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent mail_agent = new ConvJasonAgent(new AgentID("mail_agent"), "./src/test/java/jasonAgConversations/mail_agent.asl", arch,null,null);

		
		fsubscribe_initiator.start();
		fsubscribe_participant.start();	
		rate_change_agent.start();
		mail_agent.start();

	}

}