
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

/**
 *  SearchServiceSkeleton java skeleton for the axisService
 */
public class SearchServiceSkeleton{

	SFinterface sfInterface=new SFinterface();
	/**
	 * Auto generated method signature
	 * 
	 * @param searchService
	 */

	public wtp.SearchServiceResponse SearchService(wtp.SearchService searchService)
	{
		wtp.SearchServiceResponse response = new wtp.SearchServiceResponse();
		
		ArrayList<String> inputs=new ArrayList<String>();
		ArrayList<String> outputs=new ArrayList<String>();
		
		StringTokenizer tokInputs=new StringTokenizer(searchService.getInputs(), "|");
		while(tokInputs.hasMoreTokens()){
			inputs.add(tokInputs.nextToken());
		}
		StringTokenizer tokOutputs=new StringTokenizer(searchService.getOutputs(), "|");
		while(tokOutputs.hasMoreTokens()){
			outputs.add(tokOutputs.nextToken());
		}
		
		
		
		ArrayList<Profile> foundServices=sfInterface.SearchService(inputs, outputs);
		Iterator<Profile> iterServices=foundServices.iterator();
		String servicesList="";
		while(iterServices.hasNext()){
			Profile prof=iterServices.next();
			servicesList+=prof.getUrl()+":"+prof.getSuitability()+" | ";
		}
		
		response.setServicesList(servicesList);
		response.set_return(0);



		return response;
	}

}
