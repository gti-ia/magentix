package es.upv.dsic.gti_ia.sfnew;

import java.util.ArrayList;
import java.util.Iterator;


public class SFtesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SFinterface sf=new SFinterface();
		
//		long initTime=System.currentTimeMillis();
//		sf.registerNServices("servicesList.txt");
//		
//		long finalTime=System.currentTimeMillis()-initTime;
//		System.out.println(finalTime + " millis " + finalTime/1000 + " seconds");
		
		sf.clean();

		sf.RegisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
//		
//		sf.RegisterService("http://127.0.0.1/services/1.1/author_bookprice_service.owls");
//		
//		sf.RegisterService("http://127.0.0.1/services/1.1/calculateSunriseTime2.owls");
		//sf.RegisterService("http://127.0.0.1/services/1.1/calculateSunriseTime2.owls");
		
//		sf.removeProvider("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE"
//				, "Provider3");
		
		//sf.deregisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls", "CALCULATE_SUNRISE_PROFILE");
		
//		System.out.println("\n\nMODEL:\n\n");
//		sf.writeModel();
//		
		
//		sf.testQuery();
		
//		sf.deregisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls", "CALCULATE_SUNRISE_PROFILE");
		
//		sf.removeProvider("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE"
//				, "Provider3");
		
		
//		System.out.println("\n\nMODEL:\n\n");
//		sf.writeModel();
		
		
		//sf.RegisterService("http://127.0.0.1/services/1.1/author_bookprice_service.owls");		
//
//		sf.deregisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls", 
//				"CALCULATE_SUNRISE_PROFILE", "CALCULATE_SUNRISE_SERVICE");
//		

		
		ArrayList<String> inputs=new ArrayList<String>();
		inputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Code\"^^xsd:anyURI");
		inputs.add("\"http://127.0.0.1/ontology/protont.owl#longitude\"^^xsd:anyURI");
		inputs.add("\"http://127.0.0.1/ontology/protonu.owl#Date\"^^xsd:anyURI");
		inputs.add("\"http://127.0.0.1/ontology/protont.owl#latitude\"^^xsd:anyURI");
		ArrayList<String> outputs=new ArrayList<String>();
		outputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Sunrise\"^^xsd:anyURI");
		
		
		ArrayList<Profile> foundServices=sf.SearchService(inputs, outputs);
		Iterator<Profile> iterProfs=foundServices.iterator();
		while(iterProfs.hasNext()){
			Profile prof=iterProfs.next();
			System.out.println("Profile: "+prof.getUrl()+" similarity: "+prof.getSuitability());
		}
		
		
//		"http://127.0.0.1/ontology/geographydataset.owl#Code"^^xsd:anyURI
//		"http://127.0.0.1/ontology/protont.owl#longitude"^^xsd:anyURI
//		"http://127.0.0.1/ontology/protonu.owl#Date"^^xsd:anyURI
//		"http://127.0.0.1/ontology/protont.owl#latitude"^^xsd:anyURI
//		
//		"http://127.0.0.1/ontology/geographydataset.owl#Sunrise"^^xsd:anyURI
		
		
		
	}

}
