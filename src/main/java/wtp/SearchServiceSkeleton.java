
/**
 * SearchServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import persistence.Profile;
import persistence.SFinterface;
import persistence.THOMASException;

/**
 *  SearchServiceSkeleton java skeleton for the axisService
 */
public class SearchServiceSkeleton{

	public static final Boolean DEBUG = true;
	
	SFinterface sfInterface=new SFinterface();
	/**
	 * Search Service implementation 
	 * 
	 * @param searchService
	 */

	public wtp.SearchServiceResponse SearchService(wtp.SearchService searchService)
	{
		wtp.SearchServiceResponse response = new wtp.SearchServiceResponse();
		
		ArrayList<String> inputs=new ArrayList<String>();
		ArrayList<String> outputs=new ArrayList<String>();
		
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
		
		
		ArrayList<Profile> foundServices=new ArrayList<Profile>();
		String servicesList="";
		try {
			foundServices = sfInterface.SearchService(inputs, outputs);
			Iterator<Profile> iterServices=foundServices.iterator();
			
			while(iterServices.hasNext()){
				Profile prof=iterServices.next();
				servicesList+=prof.getUrl()+":"+prof.getSuitability()+" | ";
			}
		} catch (THOMASException e) {
			servicesList=e.getContent();
			e.printStackTrace();
		}
		
		
		if(DEBUG)
			System.out.println("SearchService result: "+ servicesList);
		
		response.setServicesList(servicesList);
		response.set_return(foundServices.size());

		return response;
	}

}
