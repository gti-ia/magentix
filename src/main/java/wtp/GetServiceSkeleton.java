
/**
 * GetServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;
import persistence.THOMASException;

/**
 *  GetServiceSkeleton java skeleton for the axisService
 */
public class GetServiceSkeleton{


	public static final Boolean DEBUG = true;

	SFinterface sfInterface=new SFinterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param getService
	 */

	public wtp.GetServiceResponse GetService(wtp.GetService getService)
	{
		wtp.GetServiceResponse response = new wtp.GetServiceResponse();

		String serviceID=getService.getServiceID();

		if(DEBUG){
			System.out.println("GetService Input parameters:");
			System.out.println("\tserviceID="+serviceID);
		}

		String owlsSpecification;
		String exceptionContent="";
		try {
			owlsSpecification = sfInterface.getService(serviceID);
		} catch (THOMASException e) {
			owlsSpecification="";
			e.printStackTrace();
			exceptionContent=e.getContent();
		}

		
		if(owlsSpecification==null || owlsSpecification==""){
			response.set_return(0);
			response.setOwlsSpecification(exceptionContent);
		}
		else{
			response.set_return(1);
			response.setOwlsSpecification(owlsSpecification);
		}
		

		return response;
	}

}
