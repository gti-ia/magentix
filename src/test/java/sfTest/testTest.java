package sfTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.Provider;
import es.upv.dsic.gti_ia.organization.ResponseParser;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class testTest extends QueueAgent{

	public testTest(AgentID aid) throws Exception {
		super(aid);
		// TODO Auto-generated constructor stub
	}

	public void execute(){
		try {
			
			SFProxy sf=new SFProxy(this);

			String response=sf.getService("http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#DivisionProfile");

			System.out.println("testAgent. Response:\n"+response);
//			response="<?xml version=\"1.0\" encoding=\"UTF-8\"?><rdf:RDF> <profile:hasInput rdf:resource=\"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#x\"/>"+
//			"<profile:hasInput rdf:resource=\"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#y\"/>"+
//			"<profile:hasOutput rdf:resource=\"http://localhost:8080/testSFservices/testSFservices/owl/owls/Division.owl#Result\"/>"+
//			"</rdf:RDF>";
			Oracle oracle=new Oracle(response);
//			File f=new File("tmp.owl");
//			Oracle oracle=new Oracle(f);

			System.out.println("serviceName: "+oracle.getServiceName());
			
			
			ArrayList<String> wsdlInputs=oracle.getInputs();
			System.out.println("inputs size:" +wsdlInputs.size());
			Iterator<String> iterInputs=wsdlInputs.iterator();
			while(iterInputs.hasNext()) {
				String in=iterInputs.next();
				System.out.println("input: "+in);
			}
			ArrayList<String> outputs=oracle.getOutputs();
			System.out.println("outputs size:" +outputs.size());
			Iterator<String> iterOutputs=outputs.iterator();
			while(iterOutputs.hasNext()) {
				String in=iterOutputs.next();
				System.out.println("output: "+in);
			}
			
			
			ArrayList<String> wsdls=oracle.getProvidersGroundingWSDL();
			Iterator<String> iterWSDLs=wsdls.iterator();
			while(iterWSDLs.hasNext()){
				String wsdl=iterWSDLs.next();
				System.out.println("wsdl: "+wsdl);
			}
			ArrayList<Provider> provList=oracle.getProviders();
			Iterator<Provider> iterProv=provList.iterator();
			while(iterProv.hasNext()){
				Provider prov=iterProv.next();
				System.out.println("prov: "+prov.getEntityID());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BaseAgent bA;
		

	}

}
