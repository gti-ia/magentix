package jasonAgConversations;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

public class MainFQ {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();		
		ConvJasonAgent fquery_initiator = new ConvJasonAgent(new AgentID("fquery_initiator"), "./src/test/java/jasonAgConversations/fquery_initiator.asl", arch,null,null);
		
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent fquery_participant = new ConvJasonAgent(new AgentID("fquery_participant"), "./src/test/java/jasonAgConversations/fquery_participant.asl", arch,null,null);

		
		fquery_initiator.start();
		fquery_participant.start();	


	}

}