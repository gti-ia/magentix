package ThomasNOMindswap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import java.util.Hashtable;


import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class AgentClient extends QueueAgent {

	public AgentClient(AgentID aid) throws Exception {

		super(aid);

	}

	// We create the class that will make us the agent proxy oms, facilitates
	// access to the methods of the OMS
//	OMSProxy OMSservices = new OMSProxy(this);

	// We create the class that will make us the agent proxy sf, facilitates
	// access to the methods of the SF
	SFProxy SFservices = new SFProxy(this);

	ArrayList<String> results = new ArrayList<String>();
	Hashtable<AgentID, String> agents = new Hashtable<AgentID, String>();
	Hashtable<String, String> lista = new Hashtable<String, String>();
	private Oracle oracle;
	String URLProfile;
	String URLProcess;

	public String result;

	public void escenario1() {

//		result = OMSservices.acquireRole("member", "virtual");
//		System.out.println("[ClientAgent]Acquire Role member in virtual return: " + result + "\n");


		String inputInputs="\"http://127.0.0.1/ontology/geographydataset.owl#Code\"^^xsd:anyURI | "+
		"\"http://127.0.0.1/ontology/protont.owl#longitude\"^^xsd:anyURI | "+
		"\"http://127.0.0.1/ontology/protonu.owl#Date\"^^xsd:anyURI | "+
		"\"http://127.0.0.1/ontology/protont.owl#latitude\"^^xsd:anyURI ";
		
		String inputOutputs="\"http://127.0.0.1/ontology/geographydataset.owl#Sunrise\"^^xsd:anyURI";
		String inputKeywords="sunrise | time";
		String result="";
//		do {
			result = SFservices.searchService(inputInputs,inputOutputs,inputKeywords);
			System.out.println("[ClientAgent] searchService"+ result);
//		} while (result == "");

//		URLProfile = SFservices.getProfile(results.get(0));
//
//		System.out.println("[ClientAgent] getProfile: "+ URLProfile);
//		URL profile;
//		try {
//			profile = new URL(URLProfile);
//			oracle = new Oracle(profile);
//
//		} catch (MalformedURLException e) {
//			logger.error("ERROR: Profile URL Malformed!");
//			e.printStackTrace();
//		}
//
//
//
//		System.out.println("[ClientAgent] acquireRole "+oracle.getClientList().get(0)+" "+oracle.getClientUnitList().get(0)+":"+ 	    OMSservices.acquireRole( oracle.getClientList().get(0), oracle.getClientUnitList().get(0)));
//
//		agents = SFservices.getProcess(results.get(0));
//
//		System.out.println("[ClientAgent]agents that offered SearchCheapHotel service: "
//				+ agents.size() + "\n");

	}

	public void escenario2() {


		ArrayList<String> arg = new ArrayList<String>();

		int i = 0;
		for (String input : oracle.getInputs()) {
			switch (i) {

			case 0:
				System.out.println("Input: " + input);
				arg.add("5");
				break;
			case 1:
				System.out.println("Input: " + input);
				arg.add("Spain");
				break;
			case 2:
				System.out.println("Input: " + input);
				arg.add("Valencia");
				break;
			}

			i++;
		}

		Enumeration<AgentID> agents1 = agents.keys();

		AgentID agentToSend = agents1.nextElement();

		URLProcess = agents.get(agentToSend);

		// call the service SearchCheapHotel
		lista = SFservices.genericService(agentToSend, URLProfile, arg);

		Enumeration<String> e = lista.keys();

		while (e.hasMoreElements()) {
			String key = e.nextElement();
			System.out.println(" " + key + " = " + lista.get(key)); // obtiene
			// una
			// clase
			// y
			// avanza


		}

	}

	public void execute() {

		logger.info("Executing, I'm " + this.getName());
		this.escenario1();
		this.escenario2();

	}

}
