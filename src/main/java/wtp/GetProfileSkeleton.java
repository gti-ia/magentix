/**
 * GetProfileSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.*;

/**
 * GetProfileSkeleton java skeleton for the axisService
 */
public class GetProfileSkeleton {
	
	public static final String DB_URL = "jdbc:mysql://localhost/thomas";
	public static final String DB_USER = "thomas";
	public static final String DB_PASSWD = "thomas";
	public static final String DB = "MySQL";
	public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	public static final Boolean DEBUG = true;

	// database connection parameters, with defaults
	private static String s_dbURL = DB_URL;
	private static String s_dbUser = DB_USER;
	private static String s_dbPw = DB_PASSWD;
	private static String s_dbType = DB;
	private static String s_dbDriver = DB_DRIVER;

	/**
	 * GetProfile
	 *
	 * @param getProfile contains the service ID
	 * @return response contains the profile URL
	 * @throws
	 */
	 public wtp.GetProfileResponse GetProfile(wtp.GetProfile getProfile) {
		
		GetProfileResponse response = new GetProfileResponse();
		
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
