package contractNetFactory;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class Main {

	public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();

		SallyClass Sally = new SallyClass(new AgentID("Sally"));
		Sally.start();
		
		SallyClass Sally2 = new SallyClass(new AgentID("Sally2"));
		Sally2.start();

		HarryClass Harry = new HarryClass(new AgentID("Harry"));
		Harry.start();
	}
}
