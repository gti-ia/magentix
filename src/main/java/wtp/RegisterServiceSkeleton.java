
/**
 * RegisterServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;
import persistence.THOMASException;

/**
 *  RegisterServiceSkeleton java skeleton for the axisService
 */
public class RegisterServiceSkeleton{

	public static final Boolean DEBUG = true;
	
	SFinterface sfInterface=new SFinterface();
	/**
	 * Register Service implementation
	 * 
	 * @param registerService
	 */

	public wtp.RegisterServiceResponse RegisterService(wtp.RegisterService registerService)
	{
		wtp.RegisterServiceResponse response = new wtp.RegisterServiceResponse();
		
		String serviceURL=registerService.getServiceURL();
		
		if(DEBUG){
			System.out.println("Register Service service:");
			System.out.println("Inputs:");
			System.out.println("\tserviceURL: "+serviceURL);
		}
		
		String result;
		try {
			result = sfInterface.RegisterService(serviceURL);
		} catch (THOMASException e) {
			
			e.printStackTrace();
			result=e.getContent();
		}
		
		response.set_return(result);
		
		if(DEBUG){
			System.out.println("Register Service service result:\n\treturn: "+response.get_return());
		}
		
		return response;
	}

}
