
/**
 * DivisionSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;
/**
 *  DivisionSkeleton java skeleton for the axisService
 */
public class DivisionSkeleton{


	/**
	 * Auto generated method signature
	 * 
	 * @param division
	 */

	public wtp.DivisionResponse Division
	(wtp.Division division)
	{
		DivisionResponse response=new DivisionResponse();
		
		double result=division.getX()/division.getY();
		
		System.out.println("Division result: "+result);
		
		response.setResult(result);
	
		return response;
	}

}
