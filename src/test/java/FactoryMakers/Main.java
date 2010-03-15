package FactoryMakers;


import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

class Main {

	public static void main(String[] args) throws Exception {
		
//		AgentsConnection.connect("127.0.0.1");
		AgentsConnection.connect("192.168.56.101");

		HarryClass Harry = new HarryClass(
				new AgentID("Harry"));
//		SallyClass Sally = new SallyClass(
//				new AgentID("Sally"));
		Harry.start();
//		Sally.start();
	}
}
