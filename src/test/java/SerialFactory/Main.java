package SerialFactory;

import java.util.ArrayList;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class Main {

	public static void main(String[] args) throws Exception {

		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect("127.0.0.1");

		SallyClass Sally = new SallyClass(new AgentID("Sally"));
		Sally.start();

		ArrayList<String> Last = new ArrayList<String>();

		Last.add("Smith");
		Last.add("Burns");
		Last.add("Palmer");
		HarryClass aHarry;
		for (String l : Last) {
			aHarry = new HarryClass(new AgentID("Harry_" + l));
			aHarry.start();
		}
	}
}
