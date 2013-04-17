/**
 * SquareSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

/**
 * SquareSkeleton java skeleton for the axisService
 */
public class SquareSkeleton {

	/**
	 * Auto generated method signature
	 * 
	 * @param square
	 */

	public wtp.SquareResponse Square(wtp.Square square) {
		SquareResponse response = new SquareResponse();

		double result = square.getX() * square.getX();

		System.out.println("Square result: " + result);

		response.setResult(result);

		return response;
	}

}
