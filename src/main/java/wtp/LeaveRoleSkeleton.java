/**
 * LeaveRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2
 * Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import persistence.DataBaseInterface;

/**
 * LeaveRoleSkeleton java skeleton for the axisService
 */

public class LeaveRoleSkeleton
{
	
	public static final Boolean	DEBUG	= true;
	
	/**
	 * Service used for an agent to leave a role in a unit. The role and the unit must exist,
	 * the agent must play this role in this unit.
	 * @param leaveRole containing:
	 * - UnitID
	 * - RoleID
	 * - AgentID
	 */
	public wtp.LeaveRoleResponse LeaveRole(wtp.LeaveRole leaveRole)
	{
		wtp.LeaveRoleResponse res = new LeaveRoleResponse();
		if (DEBUG)
		{
			System.out.println("LeaveRole :");
			System.out.println("***AgentID..." + leaveRole.getAgentID());
			System.out.println("***UnitID()..." + leaveRole.getUnitID());
			System.out.println("***RoleID()..." + leaveRole.getRoleID());
			
		}
		res.setStatus("Ok");
		res.setErrorValue("");
		if (leaveRole.getAgentID() == "" || leaveRole.getRoleID() == ""
				|| leaveRole.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		if (!thomasBD.CheckExistsAgent(leaveRole.getAgentID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsUnit(leaveRole.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsRole(leaveRole.getRoleID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckAgentPlaysRole(leaveRole.getRoleID(), leaveRole
				.getUnitID(), leaveRole.getAgentID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.DeleteAgentPlaysRole(leaveRole.getRoleID(), leaveRole
				.getUnitID(), leaveRole.getAgentID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		return res;
	}
	
}
