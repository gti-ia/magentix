
/**
 * RegisterServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;

/**
 *  RegisterServiceSkeleton java skeleton for the axisService
 */
public class RegisterServiceSkeleton{

	public static final Boolean DEBUG = true;
	
	
	/**
	 * Register Service implementation
	 * 
	 * @param registerService
	 */

	public wtp.RegisterServiceResponse RegisterService(wtp.RegisterService registerService)
	{
		SFinterface sfInterface=new SFinterface();
		
		wtp.RegisterServiceResponse response = new wtp.RegisterServiceResponse();
		
		String serviceURL=registerService.getServiceURL();
		
		if(DEBUG){
			System.out.println("Register Service service:");
			System.out.println("Inputs:");
			System.out.println("\tserviceURL: "+serviceURL);
		}
		
		String result = sfInterface.RegisterService(serviceURL);
		
		response.setResult(result);
		
		if(DEBUG){
			System.out.println("Register Service service result:\n\treturn: "+response.getResult());
		}
		
		return response;
	}

}
