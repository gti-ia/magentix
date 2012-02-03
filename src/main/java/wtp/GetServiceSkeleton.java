
/**
 * GetServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;

/**
 *  GetServiceSkeleton java skeleton for the axisService
 */
public class GetServiceSkeleton{


	public static final Boolean DEBUG = true;

	
	/**
	 * 
	 * Get Service implementation
	 * @param getService
	 */

	public wtp.GetServiceResponse GetService(wtp.GetService getService)
	{
		SFinterface sfInterface=new SFinterface();
		
		wtp.GetServiceResponse response = new wtp.GetServiceResponse();

		String serviceID=getService.getServiceID();

		if(DEBUG){
			System.out.println("GetService Input parameters:");
			System.out.println("\tserviceID="+serviceID);
		}

		String result = sfInterface.getService(serviceID);
		
		response.setResult(result);
		
		if(DEBUG){
			System.out.println("Get Service service result:\n\treturn: "+response.getResult());
		}
		
		return response;
	}

}
