/**
 * AdditionSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

/**
 * AdditionSkeleton java skeleton for the axisService
 */
public class AdditionSkeleton {

	/**
	 * Auto generated method signature
	 * 
	 * @param addition
	 */

	public wtp.AdditionResponse Addition(wtp.Addition addition) {

		AdditionResponse response = new AdditionResponse();

		double result = addition.getX() + addition.getY();

		System.out.println("Addition result: " + result);

		response.setResult(result);

		return response;

	}

}
