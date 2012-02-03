
/**
 * DeregisterServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;

/**
 *  DeregisterServiceSkeleton java skeleton for the axisService
 */
public class DeregisterServiceSkeleton{

	public static final Boolean DEBUG = true;
	
	
	/**
	 * 
	 * Deregister Service implementation
	 * @param deregisterService
	 */

	public wtp.DeregisterServiceResponse DeregisterService(wtp.DeregisterService deregisterService)
	{
		SFinterface sfInterface=new SFinterface();
		
		wtp.DeregisterServiceResponse response=new wtp.DeregisterServiceResponse();
		
		String serviceID=deregisterService.getServiceID();
		
		if(DEBUG){
			System.out.println("Deregister Service service:");
			System.out.println("Inputs:");
			System.out.println("\tserviceID: "+serviceID);
		}
		
		String result = sfInterface.deregisterService(serviceID);
		
		response.setResult(result);
		
		if(DEBUG){
			System.out.println("Deregister Service service result:\n\treturn: "+response.getResult());
		}
		
		return response;
	}

}
