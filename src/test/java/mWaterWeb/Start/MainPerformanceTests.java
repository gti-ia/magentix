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
 * Application for executing 'mwater' prototype 'staff' and 'webInterfaceAgent' agents in a pc
 * 
 * @author Bexy Alfonso
 */
public class MainPerformanceTests {

	public static void main(String[] args) throws Exception {


		System.out.println("COMIENZO");
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();

		String[] connargs;
		//This data must be loaded from somewhere
		connargs = new String[5];
		connargs[0]="com.mysql.jdbc.Driver";
		connargs[1]="jdbc:mysql://localhost/mWaterDB"; //This must change to the name of the computer in the net
		connargs[2]="bexy";
		connargs[3]="bexy";
		connargs[4]="[]"; 

		ConvMagentixAgArch arch = new ConvMagentixAgArch();	
		mWaterBB bb = new mWaterBB();
		ConvJasonAgent staff_performance = new ConvJasonAgent(new AgentID("staff_performance"), "./src/test/java/mWaterWeb/mwaterJasonAgents/staff_performance.asl", arch,bb, connargs);

		ConvMagentixAgArch webInterfaceAgentarch = new ConvMagentixAgArch();
		ConvJasonAgent webInterfaceAgent = new ConvJasonAgent(new AgentID("webInterfaceAgent"), "./src/test/java/mWaterWeb/webInterface/webInterfaceAgent.asl", webInterfaceAgentarch,null,null);

		staff_performance.start();
		webInterfaceAgent.start();

	}

}