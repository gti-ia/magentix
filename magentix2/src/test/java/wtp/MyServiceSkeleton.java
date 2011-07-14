/**
 * MyServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

/**
 * MyServiceSkeleton java skeleton for the axisService
 */
public class MyServiceSkeleton {

	/**
	 * Auto generated method signature
	 * 
	 * @param myService
	 */

	public wtp.MyServiceResponse MyService(wtp.MyService myService) {
		// TODO : fill this with the necessary business logic

		System.out.println("Hello " + myService.getName());
		int result = myService.getNumber1() + myService.getNumber2();

		wtp.MyServiceResponse response = new wtp.MyServiceResponse();
		response.setResult(result);

		return response;
	}

}
