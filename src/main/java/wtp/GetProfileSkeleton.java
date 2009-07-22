/**
 * GetProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.*;
import java.io.*;

/**
 * GetProfileSkeleton java skeleton for the axisService
 */
public class GetProfileSkeleton {
	

	public static final Boolean DEBUG = true;

	// database connection parameters, with defaults
	private static String s_dbURL;
	private static String s_dbUser;
	private static String s_dbPw;
	private static String s_dbType;
	private static String s_dbDriver;

	/**
	 * GetProfile
	 *
	 * @param getProfile contains the service ID
	 * @return response contains the profile URL
	 * @throws
	 */
	 public wtp.GetProfileResponse GetProfile(wtp.GetProfile getProfile) {
		
		GetProfileResponse response = new GetProfileResponse();
		
		Properties properties = new Properties();
		
		
		  
		  try {
			   properties.loadFromXML(GetProfileSkeleton.class.getResourceAsStream("/"+"THOMASDemoConfiguration.xml"));
				for (Enumeration e = properties.keys(); e.hasMoreElements() ; ) {
				    // Obtenemos el objeto
				    Object obj = e.nextElement();
				    if (obj.toString().equalsIgnoreCase("DB_URL"))
				    {
				    	s_dbURL= properties.getProperty(obj.toString());	
				    }
				    else if (obj.toString().equalsIgnoreCase("DB_USER"))
				    {
				    	s_dbUser = properties.getProperty(obj.toString());
				    }
				    else    if (obj.toString().equalsIgnoreCase("DB_PASSWD"))
				    {
				    	s_dbPw = properties.getProperty(obj.toString());
				    }
				    else    if (obj.toString().equalsIgnoreCase("DB"))
				    {
				    	s_dbType = properties.getProperty(obj.toString());
				    }
				    else    if (obj.toString().equalsIgnoreCase("DB_DRIVER"))
				    {
				    	s_dbDriver = properties.getProperty(obj.toString());
				    }
				}

		    } catch (IOException e) {
		    	System.out.print(e);
		    }
		
		if (DEBUG) {
			System.out.println("GetProfile Service:");
			System.out.println("***ServiceID... " + getProfile.getServiceID());
		}
		
		response.set_return(1);
		response.setGoal("");
		StringTokenizer Tok = new StringTokenizer(getProfile.getServiceID());
		String profile = Tok.nextToken("#");
		response.setServiceProfile(profile);

		return (response);

	 }

	
}
