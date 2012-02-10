
/**
 * ProductSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;
/**
 *  ProductSkeleton java skeleton for the axisService
 */
public class ProductSkeleton{


	/**
	 * Auto generated method signature
	 * 
	 * @param product
	 */

	public wtp.ProductResponse Product
	(wtp.Product product)
	{
		
		ProductResponse response=new ProductResponse();
		
		double result=product.getX()*product.getY();
		
		System.out.println("Product result: "+result);
		
		response.setResult(result);
	
		return response;
	}

}
