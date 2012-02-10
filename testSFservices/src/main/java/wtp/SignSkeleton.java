
/**
 * SignSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;
/**
 *  SignSkeleton java skeleton for the axisService
 */
public class SignSkeleton{


	/**
	 * Auto generated method signature
	 * 
	 * @param sign
	 */

	public wtp.SignResponse Sign
	(wtp.Sign sign)
	{
		SignResponse response=new SignResponse();
		String result;
		if(sign.getX()<0)
			result="negative";
		else
			result="positive";
		
		System.out.println("Sign result: "+result);
		
		response.setResult(result);
	
		return response;
	}

}
