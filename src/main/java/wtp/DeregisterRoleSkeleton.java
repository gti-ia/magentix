/**
 * DeregisterRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.List;

import persistence.DataBaseInterface;

/**
 * DeregisterRoleSkeleton java skeleton for the axisService
 */
public class DeregisterRoleSkeleton
{
	public static final Boolean		DEBUG		= true;
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	
	/**
	 * Service used to deregister a norm. The role must exist, there must be no norm addressed to
	 * this role, this role must not be currently played by any agent, and the agent requesting
	 * the deregistration must have enough permissions (depending on the type of the
	 * organization) to deregister the role. If the type of this unit is 'FLAT' the agent is 
	 * allowed, if 'TEAM' the agent must be a member of this unit, and if the type is
	 * 'HIERARCHY' the agent must be a supervisor of this unit.
	 * @param deregisterRole containing:
	 * - RoleID
	 * - UnitID
	 * - AgentID
	 */
	public wtp.DeregisterRoleResponse DeregisterRole(
			wtp.DeregisterRole deregisterRole)
	{
		wtp.DeregisterRoleResponse res = new DeregisterRoleResponse();
		if (DEBUG)
		{
			System.out.println("DeregisterRole :");
			System.out.println("***AgentID..." + deregisterRole.getAgentID());
			System.out.println("*** RoleID()..." + deregisterRole.getRoleID());
			System.out.println("*** UnitID()..." + deregisterRole.getUnitID());
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		
		if (deregisterRole.getRoleID() == ""
				|| deregisterRole.getRoleID().equalsIgnoreCase("member")
				|| deregisterRole.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		
		if (!thomasBD.CheckExistsRoleInUnit(deregisterRole.getRoleID(),
				deregisterRole.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		
		if (thomasBD.CheckRoleHasNorms(deregisterRole.getRoleID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("ErrorNorm");
			return res;
		}
		if (thomasBD.CheckRoleIsPlayedInUnit(deregisterRole.getRoleID(),deregisterRole.getUnitID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("ErrorPlayed");
			return res;
		}
		// role based control
		if (!roleBasedControl(deregisterRole.getAgentID(), deregisterRole
				.getUnitID()))
		{
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			return res;
		}
		System.out.println("DEREGISTERROLE, exit rolebasedcontrol!!");
		if (!thomasBD.DeleteRole(deregisterRole.getRoleID(), deregisterRole
				.getUnitID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		return res;
	}
	private boolean roleBasedControl(String agentID, String unitID)
	{
		System.out.println("DEREGISTERROLE 1");
		if (unitID.equalsIgnoreCase("virtual"))
			return false;
		System.out.println("DEREGISTERROLE 2");
		if (!thomasBD.CheckExistsAgent(agentID))
			return false;
		System.out.println("DEREGISTERROLE 3");
		String unitType = thomasBD.GetUnitType(unitID);
		if (unitType.equalsIgnoreCase("flat"))
			return true;
		System.out.println("DEREGISTERROLE 4");
		if (unitType.equalsIgnoreCase("team"))
		{
			System.out.println("DEREGISTERROLE 5");
			if (thomasBD.CheckAgentPlaysRoleInUnit(unitID, agentID))
				return true;
			else
				return false;
		}
		System.out.println("DEREGISTERROLE 6");
		List<String> positions;
		try
		{
			System.out.println("DEREGISTERROLE agentID="+agentID+", unitID="+unitID);
			positions = thomasBD.GetAgentPosition(agentID, unitID);
			System.out.println("DEREGISTERROLE positions.size="+positions.size());
			for (int i = 0; i < positions.size(); i++){
				System.out.println("DEREGISTERROLE position["+i+"]='"+positions.get(i)+"'");
				if (positions.get(i).equalsIgnoreCase("supervisor")){
					System.out.println("DEREGISTERROLE somos supervisor");
					return true;
				}
			}
		}
		catch (Exception e)
		{
		}
		return false;
	}
}
