/**
 * InformUnitRolesSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.List;

import persistence.DataBaseInterface;

/**
 * InformUnitRolesSkeleton java skeleton for the axisService
 */

public class InformUnitRolesSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	public static final Boolean		DEBUG		= true;
	
	/**
	 * Service used for requesting the list of roles that have been registered inside a unit.
	 * The unit must exist and the requester agent is allowed to use the service: if the type
	 * of the unit is 'FLAT' the agent is allowed, if it is 'TEAM' or 'HIERARCHY' the agent
	 * must be a member of this unit.
	 * @param informUnitRoles containing:
	 * - UnitID
	 * - AgentID
	 */
	public wtp.InformUnitRolesResponse InformUnitRoles(
			wtp.InformUnitRoles informUnitRoles)
	{
		wtp.InformUnitRolesResponse res = new InformUnitRolesResponse();
		
		if (DEBUG)
		{
			System.out.println("InformUnitRoles :");
			System.out.println("***AgentID..." + informUnitRoles.getAgentID());
			System.out.println("***UnitID()..." + informUnitRoles.getUnitID());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setRoleList("");
		if (informUnitRoles.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		
		if (!thomasBD.CheckExistsUnit(informUnitRoles.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		// role based control
		if (!roleBasedControl(informUnitRoles.getAgentID(), informUnitRoles
				.getUnitID()))
		{
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			return res;
		}
		res.setRoleList(thomasBD.GetRoleList(informUnitRoles.getUnitID())
				.toString());
		return res;
	}
	private boolean roleBasedControl(String agentID, String unitID)
	{
		if (unitID.equalsIgnoreCase("virtual"))
			return true;
		if (!thomasBD.CheckExistsAgent(agentID))
			return false;
		String unitType = thomasBD.GetUnitType(unitID);
		if (unitType.equalsIgnoreCase("flat"))
			return true;
		String parentUnitID = thomasBD.GetParentUnitID(unitID);
		if (thomasBD.CheckAgentPlaysRoleInUnit(parentUnitID, agentID))
			return true;
		else
			return false;
	}
	
}
