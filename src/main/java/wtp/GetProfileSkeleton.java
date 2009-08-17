/**
 * GetProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.*;
import java.io.*;

import persistence.DataBaseInterface;

/**
 * GetProfileSkeleton java skeleton for the axisService
 */
public class GetProfileSkeleton {
	

	public static final Boolean DEBUG = true;



	/**
	 * GetProfile
	 * @param GetProfile contains two elements: the service ID (is a string: service profile id) and the 
	 * agent id (is a string).
	 * @return GetProfileResponse contains three elements: service profile (is a string: the url profile), 
	 * the goal of the profile (currently is not in use) and the return (is an integer) which indicates if
	 * an error occurs. 
	 */
	
	 public wtp.GetProfileResponse GetProfile(wtp.GetProfile getProfile) {
		
		GetProfileResponse response = new GetProfileResponse();
		
		if (DEBUG) {
			System.out.println("GetProfile Service:");
			System.out.println("***ServiceID... " + getProfile.getServiceID());
			System.out.println("***AgentID... " + getProfile.getAgentID());
		}
		
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		String urlprofile = thomasBD.GetServiceProfileURL(getProfile.getServiceID());
		System.out.println("profile: "+urlprofile);
		if(urlprofile!=null){
			response.setServiceProfile(urlprofile);
			response.setGoal("");
			response.set_return(1);
		}
		else{
			response.setServiceProfile("[Error] The service id does not exist");
			response.set_return(0);
			response.setGoal("");
		}
		return(response);

	 
}
}