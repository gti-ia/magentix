package fipaRecruitingTest;

import java.util.ArrayList;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;

public class Run {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		ArrayList<TargetAgent> ambulances = new ArrayList<TargetAgent>();
		ArrayList<TargetAgent> backups = new ArrayList<TargetAgent>();
		
		for(int i=0; i<5; i++){
			ambulances.add(new TargetAgent(new AgentID("ambulance"+i)));
			ambulances.get(i).start();
		}
		
		for(int i=0; i<5; i++){
			backups.add(new TargetAgent(new AgentID("backup"+i)));
			backups.get(i).start();
		}
		
		PoliceCentral policeCentral = new PoliceCentral(new AgentID("policeCentral"));
		policeCentral.start();

		PolicePatrol policePatrol = new PolicePatrol(new AgentID("policePatrol"));
		policePatrol.start();	
	}
}
