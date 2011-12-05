package es.upv.dsic.gti_ia.sfnew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class SFtesting {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SFinterface sf=new SFinterface();
		
//		sf.clean();
//		
		//sf.RegisterService("http://127.0.0.1/services/1.1/author_bookprice_service.owls");
//		sf.RegisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
//
//		sf.deregisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls", 
//				"CALCULATE_SUNRISE_PROFILE", "CALCULATE_SUNRISE_SERVICE");
//		
//		String res=sf.testQuery("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_AtomicProcessGrounding");
//		System.out.println("Result is: "+res);
//		
//		sf.getProvider("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE");
//		
//		sf.getProviderList("http://127.0.0.1/services/1.1/author_bookprice_service.owls#AUTHOR_BOOKPRICE_PROFILE");
//		
		
		ArrayList<String> inputs=new ArrayList<String>();
		inputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Code\"^^xsd:anyURI");
		inputs.add("\"http://127.0.0.1/ontology/protont.owl#longitude\"^^xsd:anyURI");
		//inputs.add("\"http://127.0.0.1/ontology/protonu.owl#Date\"^^xsd:anyURI");
		inputs.add("\"http://127.0.0.1/ontology/protont.owl#latitude\"^^xsd:anyURI");
		ArrayList<String> outputs=new ArrayList<String>();
		outputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Sunrise\"^^xsd:anyURI");
		
		
		//TODO tener en cuenta si nos llegan menos entradas, y el servicio candidato tiene más...
		String regServices=sf.searchRegisteredServices(inputs, outputs);
		System.out.println(regServices+ " equal registered service");
		
		
//		"http://127.0.0.1/ontology/geographydataset.owl#Code"^^xsd:anyURI
//		"http://127.0.0.1/ontology/protont.owl#longitude"^^xsd:anyURI
//		"http://127.0.0.1/ontology/protonu.owl#Date"^^xsd:anyURI
//		"http://127.0.0.1/ontology/protont.owl#latitude"^^xsd:anyURI
//		
//		"http://127.0.0.1/ontology/geographydataset.owl#Sunrise"^^xsd:anyURI
		
		
//		System.out.println("\n\nMODEL:\n\n");
//		sf.writeModel();
		
		
	}

}
