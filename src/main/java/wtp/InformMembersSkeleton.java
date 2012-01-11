/**
 * InformMembersSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;
import java.util.List;


import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * InformMembersSkeleton java skeleton for the axisService
 */

public class InformMembersSkeleton
{
	
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Service used for requesting the list of entities that are members of a specific unit.
	 * If a role is specified only the members playing this role are detailed. The unit must
	 * exist and the agent is allowed to request this information. This is if the type of the
	 * unit is 'FLAT' the agent is allowed, if 'TEAM' the agent must belong to this unit, and
	 * if the type is 'HIERARCHY' the agent must be a supervisor of this unit.
	 * @param informMembers containing:
	 * - UnitID
	 * - RoleID
	 * - AgentID
	 */
	public wtp.InformMembersResponse InformMembers(
			wtp.InformMembers informMembers)
	{
		wtp.InformMembersResponse res = new InformMembersResponse();
		String result = "";
		
		if (DEBUG)
		{
			System.out.println("InformMembers :");
			System.out.println("***AgentID..." + informMembers.getAgentID());
			System.out.println("***UnitID()..." + informMembers.getUnitID());
			System.out.println("***RoleID()..." + informMembers.getRoleID());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setEntityRoleList("");
		
		if (informMembers.getUnitID() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}
		try{
			result =omsInterface.informMembers(informMembers.getUnitID(),informMembers.getRoleID(),informMembers.getPositionValue(),informMembers.getAgentID());
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