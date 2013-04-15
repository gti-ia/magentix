package timerTest;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class Main {

	public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect("127.0.0.1");

		TimerAgent agent = new TimerAgent(new AgentID("TimerAgent"));
		agent.start();
	}
}
