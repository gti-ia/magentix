package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;




public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String inputsAPinyo="";
		String outputsAPinyo="";
		
		String processURL="http://localhost:8080/sfservices/SFservices/owl/owls/SearchService.owl";
		
//		Oracle oracle = new Oracle();
//		oracle.setURLProcess(processURL);
//		
//		ArrayList<String> processInputs=oracle.getProcessInputs();
		
		ArrayList<String> processInputs=new ArrayList<String>();
		processInputs.add("Inputs");
		processInputs.add("Outputs");
		
		HashMap<String,String> paramsComplete=new HashMap<String, String>();
		paramsComplete.put("Inputs", inputsAPinyo);
		paramsComplete.put("Outputs", outputsAPinyo);
		
		//construct params list with the value of the parameters ordered...
		ArrayList<String> params = new ArrayList<String>();
		Iterator<String> iterInputs=processInputs.iterator();
		while(iterInputs.hasNext()){
			String input=iterInputs.next().toLowerCase();
			params.add(paramsComplete.get(input));
		}
		
		ServiceClient serviceClient = new ServiceClient();
	    ArrayList<String> results = serviceClient.invoke(processURL, params);
	    
	    String process_localName="SearchServiceProcess"; //TODO no estic segur si es això...
	    
		String resultStr=process_localName+ "=" + "{";
		for(int i=0;i<results.size();i++){
			resultStr+=processURL+"#"+results.get(i);
			if(i!=results.size()-1){
				resultStr+=", ";
			}
			else{
				resultStr+=" }";
			}
		}
		
		
		System.out.println(resultStr);
	}

}
