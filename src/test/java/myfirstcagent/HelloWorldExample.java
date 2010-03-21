package myfirstcagent;


import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class HelloWorldExample {

	public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect("localhost");

		// Una vez definida la nueva subclase de CAgent, podemos crear
		// agentes como objetos de dicha clase.
		
		HelloWorldAgentClass helloWorldAgent = new HelloWorldAgentClass(
				new AgentID("helloWorldAgent"));
		
		// y finalmente iniciamos ejecución de dicho agente.
		
		helloWorldAgent.start();
	}
}
