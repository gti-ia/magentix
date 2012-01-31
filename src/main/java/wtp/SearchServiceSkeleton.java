
/**
 * SearchServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.util.ArrayList;
import java.util.StringTokenizer;

import persistence.SFinterface;

/**
 *  SearchServiceSkeleton java skeleton for the axisService
 */
public class SearchServiceSkeleton{

	public static final Boolean DEBUG = true;
	
	/**
	 * Search Service implementation 
	 * 
	 * @param searchService
	 */

	public wtp.SearchServiceResponse SearchService(wtp.SearchService searchService)
	{
		SFinterface sfInterface=new SFinterface();
		
		wtp.SearchServiceResponse response = new wtp.SearchServiceResponse();
		
		ArrayList<String> inputs=new ArrayList<String>();
		ArrayList<String> outputs=new ArrayList<String>();
		ArrayList<String> keywords=new ArrayList<String>();
		
		if(DEBUG){
			System.out.println("SearchService Input parameters:");
			System.out.println("\tInputs:");
		}
		StringTokenizer tokInputs=new StringTokenizer(searchService.getInputs(), "|");
		while(tokInputs.hasMoreTokens()){
			String in=tokInputs.nextToken().trim();
			inputs.add(in);
			if(DEBUG)
				System.out.println("\t\t"+in);
		}
		
		if(DEBUG)
			System.out.println("\tOutputs:");
		StringTokenizer tokOutputs=new StringTokenizer(searchService.getOutputs(), "|");
		while(tokOutputs.hasMoreTokens()){
			String out=tokOutputs.nextToken().trim();
			outputs.add(out);
			if(DEBUG)
				System.out.println("\t\t"+out);
		}
		
		if(DEBUG)
			System.out.println("\tKeywords:");
		StringTokenizer tokKeywords=new StringTokenizer(searchService.getKeywords(), "|");
		while(tokKeywords.hasMoreTokens()){
			String key=tokKeywords.nextToken().trim();
			keywords.add(key);
			if(DEBUG)
				System.out.println("\t\t"+key);
		}
		
		String result = sfInterface.SearchService(inputs, outputs,keywords);
		
		response.setResult(result);
		
		if(DEBUG)
			System.out.println("Search Service result:\n\treturn: "+response.getResult());
		
		return response;
	}

}
