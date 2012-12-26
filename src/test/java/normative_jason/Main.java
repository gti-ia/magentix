package normative_jason;

import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.norms.JasonAgent;




public class Main {

	/**
	 * @param args
	 * @throws Exception 
	 */
//	public static void main(String[] args) throws Exception {
//		DOMConfigurator.configure("configuration/loggin.xml");
//		AgentsConnection.connect();
//		
//		SimpleArchitecture archA = new SimpleArchitecture();
//		
//		SimpleArchitecture archCommander = new SimpleArchitecture();
//		
//		JasonAgent agentCommander = new JasonAgent(new AgentID("commanderAgent"), "./src/test/java/normative_jason/commanderAgent.asl", archCommander, null);
//		agentCommander.start();
//
//		JasonAgent agentA = new JasonAgent(new AgentID("agentA"), "./src/test/java/normative_jason/agentA.asl", archA, null);
//		agentA.start();
//	
//		OMS oms = new OMS(new AgentID("NormativeOMS"));
//		oms.start();
//
//	}
	
	public static void main(String[] args) throws Exception {
		DOMConfigurator.configure("configuration/loggin.xml");
		AgentsConnection.connect();
		
		SimpleArchitecture archEjemplo = new SimpleArchitecture();
		
		//MyNormativAgent ejemplo = new MyNormativAgent(new AgentID("Ejemplo"), "./src/test/java/normative_jason/ejemplo.asl", archEjemplo);
		//ejemplo.start();
		
		
		JasonAgent ag = new JasonAgent(new AgentID("JasonAgent"), "./src/test/java/normative_jason/ejemplo.asl", archEjemplo);
		ag.start();
		
		OMS_Normativo oms = new OMS_Normativo(new AgentID("NormativeOMS"));
		oms.start();
		
		
	}
}
