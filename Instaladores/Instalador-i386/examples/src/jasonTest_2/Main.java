package jasonTest_2;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;
import es.upv.dsic.gti_ia.jason.MagentixAgArch;

public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		MagentixAgArch arch = new MagentixAgArch();		
		JasonAgent bob = new JasonAgent(new AgentID("bob"), "./src/test/java/jasonTest_2/bob.asl", arch);
		
		arch = new MagentixAgArch();
		JasonAgent maria = new JasonAgent(new AgentID("maria"), "./src/test/java/jasonTest_2/maria.asl", arch);
		
		bob.start();
		maria.start();
	}
}
