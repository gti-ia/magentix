
/**
 * RemoveProviderSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;

/**
 *  RemoveProviderSkeleton java skeleton for the axisService
 */
public class RemoveProviderSkeleton{

	public static final Boolean DEBUG = true;
	
	
	/**
	 * Remove Provider implementation
	 * 
	 * @param removeProvider
	 */

	public wtp.RemoveProviderResponse RemoveProvider(wtp.RemoveProvider removeProvider)
	{
		SFinterface sfInterface=new SFinterface();
		
		wtp.RemoveProviderResponse response=new wtp.RemoveProviderResponse();
		
		String serviceProfile=removeProvider.getServiceID().trim();
		String providerName=removeProvider.getProviderID().trim();
		
		
//		TODO
//		if(providerName.contains("#")){
//			providerName=providerName.split("#")[1];
//		}
		
		if(DEBUG){
			System.out.println("Remove provider service:");
			System.out.println("Inputs:");
			System.out.println("\tserviceProfile: "+serviceProfile);
			System.out.println("\tproviderName: "+providerName);
		}
		
		String result = sfInterface.RemoveProvider(serviceProfile, providerName);
		
		response.setResult(result);
		
		if(DEBUG){
			System.out.println("Remove provider service result:\n\treturn: "+response.getResult());
		}
		
		return response;
	}

}
