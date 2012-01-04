/**
 * InformUnitSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2
 * Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * InformUnitSkeleton java skeleton for the axisService
 */

public class InformUnitSkeleton
{
	
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Service used for requesting information about a specific unit. The unit must exist and 
	 * the requester agent must be a member of this unit's parent unit.
	 * @param informUnit containing:
	 * - UnitID
	 * - AgentID
	 */
	public wtp.InformUnitResponse InformUnit(wtp.InformUnit informUnit)
	{
		wtp.InformUnitResponse res = new InformUnitResponse();
		String result = "";
		if (DEBUG)
		{
			System.out.println("InformUnit :");
			System.out.println("***AgentID..." + informUnit.getAgentID());
			System.out.println("***UnitID()..." + informUnit.getUnitID());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setParentID("");
		res.setUnitType("");
		
		if (informUnit.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error. Empty parameters are not allowed.");
			return res;
		}
		
		try{
			result =omsInterface.informUnit(informUnit.getUnitID(),informUnit.getAgentID());
			res.setStatus("Ok");
			
			res.setUnitType(result.split(" ")[1]);
			System.out.println("type: "+ result.split(" ")[1]);
			res.setParentID(result.split(",")[3]);
			System.out.println("ParentID: "+ result.split(" ")[3]);
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
