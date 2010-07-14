/**
 * InformUnitSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2
 * Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.List;

import persistence.DataBaseInterface;

/**
 * InformUnitSkeleton java skeleton for the axisService
 */

public class InformUnitSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	public static final Boolean		DEBUG		= true;
	
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
		
		if (DEBUG)
		{
			System.out.println("InformUnit :");
			System.out.println("***AgentID..." + informUnit.getAgentID());
			System.out.println("***UnitID()..." + informUnit.getUnitID());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setParentID("");
		res.setUnitGoal("");
		res.setUnitType("");
		if (informUnit.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		
		if (!thomasBD.CheckExistsUnit(informUnit.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		// role based control
		if (!roleBasedControl(informUnit.getAgentID(), informUnit.getUnitID()))
		{
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			return res;
		}
		res.setParentID(thomasBD.GetParentUnitID(informUnit.getUnitID()));
		res.setUnitType(thomasBD.GetUnitType(informUnit.getUnitID()));
		res.setUnitGoal(thomasBD.GetUnitGoal(informUnit.getUnitID()));
		return res;
	}
	private boolean roleBasedControl(String agentID, String unitID)
	{
		if (unitID.equalsIgnoreCase("virtual"))
			return true;
		if (!thomasBD.CheckExistsAgent(agentID))
			return false;
		String parentUnitID = thomasBD.GetParentUnitID(unitID);
		if (thomasBD.CheckAgentPlaysRoleInUnit(parentUnitID, agentID))
			return true;
		else
			return false;
		
	}
	
}
