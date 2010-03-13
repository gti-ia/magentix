package myfirstcagent;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class HelloWorldExample {

	public static void main(String[] args) throws Exception {
		
//		AgentsConnection.connect("192.168.56.101");
		AgentsConnection.connect("158.42.184.115");

		HelloWorldAgentClass helloWorldAgent = new HelloWorldAgentClass(
				new AgentID("helloWorldAgent"));
		helloWorldAgent.start();
	}
}
