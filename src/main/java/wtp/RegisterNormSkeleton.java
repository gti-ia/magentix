
/**
 * RegisterNormSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;


import persistence.OMSInterface;
import persistence.THOMASException;

/**
 *  RegisterNormSkeleton java skeleton for the axisService
 */
public class RegisterNormSkeleton{

	public static final Boolean			DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	private String result = "";
	/**
	 * Auto generated method signature
	 * 
	 * @param registerNorm
	 */

	public wtp.RegisterNormResponse RegisterNorm
	(
			wtp.RegisterNorm registerNorm
	)
	{
		wtp.RegisterNormResponse res = new RegisterNormResponse();
		if (DEBUG)
		{
			System.out.println("RegisterNorm :");
			System.out.println("***AgentID..." + registerNorm.getAgentID());
			System.out.println("***NormID..." + registerNorm.getNormID());
			System.out.println("***UnitName..." + registerNorm.getUnitName());
			System.out.println("***NormContent..."
					+ registerNorm.getNormContent());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		if (registerNorm.getNormID() == ""
				|| registerNorm.getNormContent() == "" || registerNorm.getUnitName() == "")
		{
			res.setErrorValue("Invalid. Role id or unit id parameters are empty.");
			res.setStatus("Error");
			return res;
		}
		
		try{
			//result =omsInterfaceregisterRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibility(), registerRole.getVisibility(), registerRole.getPosition(), registerRole.getAgentID());
			res.setStatus(result);
			res.setErrorValue("");
			return res;
		}catch(THOMASException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getMessage());
			return res;
		}
		
		
	}

}
