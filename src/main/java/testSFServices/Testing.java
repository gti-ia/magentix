package testSFServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

import persistence.SFinterface;
import persistence.THOMASException;


public class Testing {



	private static String executeWithJavaX(String serviceURL, String inputParams){

		Oracle oracle = new Oracle();
		oracle.setURLProcess(serviceURL);

		ArrayList<String> processInputs=oracle.getProcessInputs();

		HashMap<String,String> paramsComplete=new HashMap<String, String>();
		Iterator<String> iterProcessInputs=processInputs.iterator();
		while(iterProcessInputs.hasNext()){
			String in=iterProcessInputs.next().toLowerCase();
			//initialize the inputs
			paramsComplete.put(in, "");
		}

		StringTokenizer tokenInputParams = new StringTokenizer(inputParams, "--");
		while(tokenInputParams.hasMoreTokens()){
			String inputToken=tokenInputParams.nextToken().trim();
			StringTokenizer anInputToken=new StringTokenizer(inputToken, "=");
			String in=anInputToken.nextToken().toLowerCase().trim();
			String value="";
			if(anInputToken.hasMoreTokens())
				value=anInputToken.nextToken().trim();
			if(paramsComplete.get(in)!=null){
				paramsComplete.put(in, value);
				System.out.println("inputParamName: "+in+" value: "+value);
			}
		}


		//construct params list with the value of the parameters ordered...
		ArrayList<String> params = new ArrayList<String>();
		Iterator<String> iterInputs=processInputs.iterator();
		while(iterInputs.hasNext()){
			String input=iterInputs.next().toLowerCase();
			params.add(paramsComplete.get(input));
			//System.out.println("inputParamValue: "+paramsComplete.get(input));
		}

		ServiceClient serviceClient = new ServiceClient();
		ArrayList<String> results = serviceClient.invoke(serviceURL, params);

		//String process_localName="SearchServiceProcess"; //TODO no estic segur si es això...
		//String resultStr=process_localName+ "=" + "{";
		String resultStr="{";
		for(int i=0;i<results.size();i++){
			resultStr+=serviceURL+"#"+results.get(i);
			if(i!=results.size()-1){
				resultStr+=", ";
			}
			else{
				resultStr+=" }";
			}
		}


		return resultStr;
	}

	private static String executeWithMindswap(String serviceURL, String inputs){

		OWLKnowledgeBase kb = OWLFactory.createKB();

		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

		// read msg content
		StringTokenizer Tok = new StringTokenizer(inputs, "--");


		System.out.println("Doc OWL-S: " + serviceURL);

		try {
			Service aService = kb.readService(serviceURL);

			// get the process for the server
			Process aProcess = aService.getProcess();
			// initialize the input values to be empty
			ValueMap values = new ValueMap();

			//inputName=value -- inputName=value  

			// get the input values
			for (int i = 0; i < aProcess.getInputs().size(); i++)
				values.setValue(aProcess.getInputs().inputAt(i),
						EntityFactory.createDataValue(""));
			while (Tok.hasMoreElements()) {
				String token = Tok.nextElement().toString().trim();
				System.out.println("token: "+token);
				for (int i = 0; i < aProcess.getInputs().size(); i++) {
					String paramName = aProcess.getInputs().inputAt(i).getLocalName().toLowerCase();
					System.out.println("paramName: "+paramName);
					if (paramName.equalsIgnoreCase(token.split("=")[0].toLowerCase())) {
						System.out.println("value: "+token.split("=")[1]);
						if (token.split("=").length >= 2)
							values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(token.split("=")[1]));
						else
							values.setValue(aProcess.getInputs().inputAt(i),EntityFactory.createDataValue(""));
					}
				}
			}// end while

			// execute the service
			System.out.println("[SF]Executing... "+ values.getValues().toString());
			values = exec.execute(aProcess, values);

			return aProcess.getLocalName() + "="+ values.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}


		return "";
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//		String inputInputs="\"http://127.0.0.1/ontology/geographydataset.owl#Code\"^^xsd:anyURI | "+
		//		"\"http://127.0.0.1/ontology/protont.owl#longitude\"^^xsd:anyURI | "+
		//		"\"http://127.0.0.1/ontology/protonu.owl#Date\"^^xsd:anyURI | "+
		//		"\"http://127.0.0.1/ontology/protont.owl#latitude\"^^xsd:anyURI ";
		//		
		//		String inputOutputs="\"http://127.0.0.1/ontology/geographydataset.owl#Sunrise\"^^xsd:anyURI";
		//		
		//		String paramsSearch="searchserviceinputinputs="+inputInputs +" -- "+ "searchserviceinputoutputs="+inputOutputs;
		//		
		//		String result=executeWithJavaX("http://localhost:8080/sfservices/SFservices/owl/owls/SearchService.owl", paramsSearch);
		//		
		//		System.out.println("RESULT: "+result);

		//		String serviceID="http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE";
		//		String getServiceInput="getserviceinputserviceid="+serviceID;
		//		String res=executeWithJavaX("http://localhost:8080/sfservices/SFservices/owl/owls/GetService.owl", getServiceInput);

		//		String serviceID="http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE";
		//		String providerID="Provider2";
		//		
		//		String inputParams="removeproviderinputserviceid="+serviceID+" -- "+"removeproviderinputproviderid="+providerID;
		//
		//		String res= executeWithJavaX("http://localhost:8080/sfservices/SFservices/owl/owls/RemoveProvider.owl", inputParams);

		//		String serviceURL="http://127.0.0.1/services/1.1/calculateSunriseTime.owls";
		//		String inputParams="registerserviceinputserviceurl="+serviceURL;
		//		String res= executeWithJavaX("http://localhost:8080/sfservices/SFservices/owl/owls/RegisterService.owl", inputParams);

		//		String serviceURL="http://127.0.0.1/services/1.1/calculateSunriseTime2.owls#CALCULATE_SUNRISE_PROFILE2";
		//		
		//		String inputParams="deregisterserviceinputserviceid="+serviceURL;
		//
		//		String res= executeWithJavaX("http://localhost:8080/sfservices/SFservices/owl/owls/DeregisterService.owl", inputParams);
		//		
		//		System.out.println("res: "+res);


		SFinterface sf= new SFinterface();

		//sf.writeModel();

		//		sf.clean();

		//		try {
		//sf.DeregisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
		//			String res=sf.RegisterService("http://127.0.0.1/services/1.1/calculateSunriseTime2.owls");
		//			System.out.println(res);



					ArrayList<String> inputs=new ArrayList<String>();
					inputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Code\"^^xsd:anyURI");
					inputs.add("\"http://127.0.0.1/ontology/protont.owl#longitude\"^^xsd:anyURI");
					inputs.add("\"http://127.0.0.1/ontology/protonu.owl#Date\"^^xsd:anyURI");
					inputs.add("\"http://127.0.0.1/ontology/protont.owl#latitude\"^^xsd:anyURI");
					ArrayList<String> outputs=new ArrayList<String>();
					outputs.add("\"http://127.0.0.1/ontology/geographydataset.owl#Sunrise\"^^xsd:anyURI");
					ArrayList<String> keywords=new ArrayList<String>();
					keywords.add("sunrise");keywords.add("time");
		//			
		//			ArrayList<Profile> foundServices=sf.SearchService(inputs, outputs, keywords);
		//			Iterator<Profile> iterProfs=foundServices.iterator();
		//			while(iterProfs.hasNext()){
		//				Profile prof=iterProfs.next();
		//				System.out.println("Profile: "+prof.getUrl()+" similarity: "+prof.getSuitability());
		//			}


		String ground="http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_GROUNDING";


//		sf.clean();
//		String r=sf.RegisterService("http://127.0.0.1/services/1.1/calculateSunriseTime.owls");
//		System.out.println(r);
//		r=sf.RegisterService("http://127.0.0.1/services/1.1/author_bookprice_service.owls");
//		System.out.println(r);
		
//		String r=sf.SearchService(inputs, outputs, keywords);
//		System.out.println(r);
		
		String r=sf.DeregisterService("http://127.0.0.1/services/1.1/author_bookprice_service.owls#AUTHOR_BOOKPRICE_PROFILE");

//		String r=sf.RemoveProvider("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE", ground);
//		String r=sf.RemoveProvider("http://127.0.0.1/services/1.1/calculateSunriseTime.owls#CALCULATE_SUNRISE_PROFILE", "Provider2");
		
		System.out.println(r);
		
		System.out.println("MODEL:");
		sf.writeModel();
	}


}
