package TestJasonTest_1;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.jason.JasonAgent;


public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		SimpleArchitecture arch = new SimpleArchitecture();
		
		JasonAgent agent = new JasonAgent(new AgentID("bob"), "./src/test/java/jasonTest_1/demo.asl", arch);
		agent.start();
	}
}
