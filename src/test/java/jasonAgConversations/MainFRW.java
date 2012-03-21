package jasonAgConversations;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

public class MainFRW {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent frequestw_initiator = new ConvJasonAgent(new AgentID("frequestw_initiator"), "./src/test/java/jasonAgConversations/frequestw_initiator.asl", arch,null,null);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent frequestw_participant = new ConvJasonAgent(new AgentID("frequestw_participant"), "./src/test/java/jasonAgConversations/frequestw_participant.asl", arch,null,null);


		arch = new ConvMagentixAgArch();
		ConvJasonAgent frequestw_agent = new ConvJasonAgent(new AgentID("frequestw_agent"), "./src/test/java/jasonAgConversations/frequestw_agent.asl", arch,null,null);

		

		frequestw_initiator.start();
		frequestw_participant.start();	
		frequestw_agent.start();


	}

}