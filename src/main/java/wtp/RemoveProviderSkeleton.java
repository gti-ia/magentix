
/**
 * RemoveProviderSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.SFinterface;
import persistence.THOMASException;

/**
 *  RemoveProviderSkeleton java skeleton for the axisService
 */
public class RemoveProviderSkeleton{

	public static final Boolean DEBUG = true;
	
	SFinterface sfInterface=new SFinterface();
	/**
	 * Remove Provider implementation
	 * 
	 * @param removeProvider
	 */

	public wtp.RemoveProviderResponse RemoveProvider(wtp.RemoveProvider removeProvider)
	{
		
		wtp.RemoveProviderResponse response=new wtp.RemoveProviderResponse();
		
		String serviceProfile=removeProvider.getServiceID().trim();
		String providerName=removeProvider.getProviderID().trim();
		
		
		
		if(providerName.contains("#")){
			providerName=providerName.split("#")[1];
		}
		
		if(DEBUG){
			System.out.println("Remove provider service:");
			System.out.println("Inputs:");
			System.out.println("\tserviceProfile: "+serviceProfile);
			System.out.println("\tproviderName: "+providerName);
		}
		
		boolean result;
		try {
			result = sfInterface.RemoveProvider(serviceProfile, providerName);
		} catch (THOMASException e) {
			result = false;
			e.printStackTrace();
		}
		
		if(result) response.set_return(1);
		else response.set_return(0);
		
		if(DEBUG){
			System.out.println("Remove provider service result:\n\treturn: "+response.get_return());
		}
		
		return response;
	}

}
