package myFirstCagent;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class Main {

	public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();

		// Once defined the new CAgent subclass, we may create
		// agents as new class objects
		
		HelloWorldAgentClass helloWorldAgent = new HelloWorldAgentClass(
				new AgentID("helloWorldAgent"));
		
		// finally we start the execution of the agent
		
		helloWorldAgent.start();
	}
}
