/**
 * LeaveRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2
 * Apr 27, 2007 (04:35:37 IST)
 */
package wtp.copy;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * LeaveRoleSkeleton java skeleton for the axisService
 */

public class LeaveRoleSkeleton
{
	
	public static final Boolean	DEBUG	= true;
	private static OMSInterface omsInterface = new OMSInterface();
	
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
		String result = "";
		if (DEBUG)
		{
			System.out.println("LeaveRole :");
			System.out.println("***AgentID..." + leaveRole.getAgentID());
			System.out.println("***UnitID()..." + leaveRole.getUnitID());
			System.out.println("***RoleID()..." + leaveRole.getRoleID());
			
		}
		res.setStatus("Ok");
		res.setErrorValue("");

		try{
			if (leaveRole.getRoleID().equals("null"))
				result =omsInterface.leaveRole(null, leaveRole.getUnitID(),leaveRole.getAgentID());
			else if (leaveRole.getUnitID().equals("null"))
				result =omsInterface.leaveRole(leaveRole.getRoleID(), null,leaveRole.getAgentID());
			else
				result =omsInterface.leaveRole(leaveRole.getRoleID(), leaveRole.getUnitID(),leaveRole.getAgentID());
			res.setStatus(result);
			res.setErrorValue("");
			return res;
		}catch(THOMASException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getContent());
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
