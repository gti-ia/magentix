
/**
 * EvenSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;
/**
 *  EvenSkeleton java skeleton for the axisService
 */
public class EvenSkeleton{


	/**
	 * Auto generated method signature
	 * 
	 * @param even
	 */

	public wtp.EvenResponse Even
	(wtp.Even even)
	{
		EvenResponse response=new EvenResponse();
		
		boolean result=even.getX()%2==0;
		
		System.out.println("Even result: "+result);
		
		response.setResult(result);
	
		return response;
	}

}
