package jasonAgConversations;



import jasonAgConversations.bdConnection.mWaterBB;

import org.apache.log4j.xml.DOMConfigurator;



import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class MainmWater {

	/**
	 * @param args
	 */
	public static void main(String[] args)  throws Exception {
		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		String[] connargs;
		//This data must be loaded from somewhere
		connargs = new String[5];
		connargs[0]="com.mysql.jdbc.Driver";
		connargs[1]="jdbc:mysql://localhost/mWaterDB";
		connargs[2]="bexy";
		connargs[3]="bexy";
		connargs[4]="[]"; //this must be loaded from a file
		
		ConvMagentixAgArch arch = new ConvMagentixAgArch();	
		mWaterBB bb = new mWaterBB();
		ConvJasonAgent staff = new ConvJasonAgent(new AgentID("staff"), "./src/test/java/jasonAgConversations/staff.asl", arch,bb, connargs);
		//ConvJasonAgent staff = new ConvJasonAgent(new AgentID("staff"), "./src/agents/staff.asl", arch,null, null);

		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent final_agent1 = new ConvJasonAgent(new AgentID("final_agent1"), "./src/test/java/jasonAgConversations/final_agent1.asl", arch,null,null);

		arch = new ConvMagentixAgArch();
		ConvJasonAgent final_agent2 = new ConvJasonAgent(new AgentID("final_agent2"), "./src/test/java/jasonAgConversations/final_agent2.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent final_agent3 = new ConvJasonAgent(new AgentID("final_agent3"), "./src/test/java/jasonAgConversations/final_agent3.asl", arch,null,null);
		
		arch = new ConvMagentixAgArch();
		ConvJasonAgent final_agent4 = new ConvJasonAgent(new AgentID("final_agent4"), "./src/test/java/jasonAgConversations/final_agent4.asl", arch,null,null);
		
		//arch = new ConvMagentixAgArch();
		//ConvJasonAgent wu = new ConvJasonAgent(new AgentID("wu"), "./src/agents/wu.asl", arch,null,null);
		
		//wu.start();
		staff.start();
		final_agent1.start();	
		final_agent2.start();
		final_agent3.start();	
		final_agent4.start();	

	}

}
