/**
 * 
 */
package mWaterWeb.Start;

import org.apache.log4j.xml.DOMConfigurator;

import mWaterWeb.bdConnection.mWaterBB;

import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvJasonAgent;
import es.upv.dsic.gti_ia.jason.conversationsFactory.ConvMagentixAgArch;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

/**
 * @author bexy
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {


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
		connargs[4]="[]"; 

		ConvMagentixAgArch arch = new ConvMagentixAgArch();	
		mWaterBB bb = new mWaterBB();
		ConvJasonAgent staff = new ConvJasonAgent(new AgentID("staff"), "./src/test/java/mWaterWeb/mwaterJasonAgents/staff.asl", arch,bb, connargs);

		ConvMagentixAgArch webInterfaceAgentarch = new ConvMagentixAgArch();
		ConvJasonAgent webInterfaceAgent = new ConvJasonAgent(new AgentID("webInterfaceAgent"), "./src/test/java/mWaterWeb/webInterface/webInterfaceAgent.asl", webInterfaceAgentarch,null,null);

		
		//staff.start();
		
		/*String[] participants = {"VBotti","AGiret","PNoriega","FIgual","AGarrido","JGimeno","ABella",
				"MJRamirez","EMDura","BAlfonso","final_agent1","final_agent2","final_agent3","final_agent4"};*/
		//Start domain specific agents of the platform
		/*ConvJasonAgent ag;
		for (String p: participants)
		{
			System.out.println(" - Creating agent: "+p+" - ");
			arch = new ConvMagentixAgArch();
			ag = new ConvJasonAgent(new AgentID(p), "./src/webInterface/webCommParticipant.asl", arch,null,null);
			ag.start();
		}*/
		
		/*ConvMagentixAgArch archVBotti = new ConvMagentixAgArch();
		ConvJasonAgent VBotti = new ConvJasonAgent(new AgentID("VBotti"), "./src/webInterface/webCommParticipant.asl", archVBotti,null,null);

		ConvMagentixAgArch archAGiret = new ConvMagentixAgArch();
		ConvJasonAgent AGiret = new ConvJasonAgent(new AgentID("AGiret"), "./src/webInterface/webCommParticipant.asl", archAGiret,null,null);

		ConvMagentixAgArch archPNoriega = new ConvMagentixAgArch();
		ConvJasonAgent PNoriega = new ConvJasonAgent(new AgentID("PNoriega"), "./src/webInterface/webCommParticipant.asl", archPNoriega,null,null);

		ConvMagentixAgArch archFIgual = new ConvMagentixAgArch();
		ConvJasonAgent FIgual = new ConvJasonAgent(new AgentID("FIgual"), "./src/mwaterJasonAgents/automatic_agent.asl", archFIgual,null,null);

		ConvMagentixAgArch archAGarrido = new ConvMagentixAgArch();
		ConvJasonAgent AGarrido = new ConvJasonAgent(new AgentID("AGarrido"), "./src/webInterface/webCommParticipant.asl", archAGarrido,null,null);

		ConvMagentixAgArch archJGimeno = new ConvMagentixAgArch();
		ConvJasonAgent JGimeno = new ConvJasonAgent(new AgentID("JGimeno"), "./src/webInterface/webCommParticipant.asl", archJGimeno,null,null);

		ConvMagentixAgArch archABella = new ConvMagentixAgArch();
		ConvJasonAgent ABella = new ConvJasonAgent(new AgentID("ABella"), "./src/mwaterJasonAgents/automatic_agent.asl", archABella,null,null);

		ConvMagentixAgArch archMJRamirez = new ConvMagentixAgArch();
		ConvJasonAgent MJRamirez = new ConvJasonAgent(new AgentID("MJRamirez"), "./src/webInterface/webCommParticipant.asl", archMJRamirez,null,null);

		ConvMagentixAgArch archEMDura = new ConvMagentixAgArch();
		ConvJasonAgent EMDura = new ConvJasonAgent(new AgentID("EMDura"), "./src/mwaterJasonAgents/automatic_agent.asl", archEMDura,null,null);

		ConvMagentixAgArch archBAlfonso = new ConvMagentixAgArch();
		ConvJasonAgent BAlfonso = new ConvJasonAgent(new AgentID("BAlfonso"), "./src/webInterface/webCommParticipant.asl", archBAlfonso,null,null);
*/
		staff.start();
		webInterfaceAgent.start();
	/*	VBotti.start();

		AGiret.start();
		PNoriega.start();
		FIgual.start();
		AGarrido.start();
		JGimeno.start();
		ABella.start();
		MJRamirez.start();
		EMDura.start();
		BAlfonso.start();*/
	}

}