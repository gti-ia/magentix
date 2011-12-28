
/**
 * RegisterUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;




/**
 *  RegisterUnitSkeleton java skeleton for the axisService
 */
public class RegisterUnitSkeleton{
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Auto generated method signature
	 * 
	 * @param registerUnit
	 */

	public wtp.RegisterUnitResponse RegisterUnit(wtp.RegisterUnit registerUnit)
	{
		wtp.RegisterUnitResponse res = new RegisterUnitResponse();
		String result = "";
		if (DEBUG)
		{
			System.out.println("RegisterUnit :");
			System.out.println("***AgentID..." + registerUnit.getAgentID());
			System.out.println("***UnitID..." + registerUnit.getUnitID());
			System.out.println("***ParentUnitID..."
					+ registerUnit.getParentUnitID());
			System.out.println("***CreatorName..." + registerUnit.getCreatorName());
			System.out.println("***Type..." + registerUnit.getType());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		
		if (registerUnit.getUnitID() == "" || registerUnit.getCreatorName() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed");
			res.setStatus("Error");
			return res;
		}
		
		try{
			result =omsInterface.registerUnit(registerUnit.getUnitID(), registerUnit.getType(), registerUnit.getAgentID(), registerUnit.getCreatorName());
			res.setStatus(result);
			res.setErrorValue("");
			return res;
		}catch(THOMASException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getMessage());
			return res;
		}
		catch(SQLException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getMessage());
			return res;
		}
		
	}

}
