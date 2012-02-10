package testSFServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;


public class Testing {

	static String separatorToken=" ";

	private static String executeWithJavaX(String inputParams){

		//http://localhost:8080/omsservices/OMSservices/owl/owls/AcquireRole.owl RoleID=miembro2 UnitID=plana2
		//
		StringTokenizer tokenInputParams = new StringTokenizer(inputParams, separatorToken);
		String serviceURL=tokenInputParams.nextToken().trim();
		//String serviceURL=sfServicesURLs.get(tokenInputParams.nextToken().trim());
		Oracle oracle = new Oracle();
		oracle.setURLProcess(serviceURL);

		ArrayList<String> processInputs=oracle.getWSDLInputs();

		HashMap<String,String> paramsComplete=new HashMap<String, String>();
		Iterator<String> iterProcessInputs=processInputs.iterator();
		while(iterProcessInputs.hasNext()){
			String in=iterProcessInputs.next().toLowerCase();
			//initialize the inputs
			paramsComplete.put(in, "");
		}


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
		String resultStr=serviceURL+"=" + "{";
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

	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String serviceURL="http://localhost:8080/testSFservices/testSFservices/owl/owls/Product.owl";
		String inputs="x=-232432432 y=2";
		String call=serviceURL+" "+inputs;
		String result=executeWithJavaX(call);
		
		System.out.println("Result: "+result);
		
		
	}


}
