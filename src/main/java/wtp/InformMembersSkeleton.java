
/**
 * InformMembersSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;

/**
 *  InformMembersSkeleton java skeleton for the axisService
 */
public class InformMembersSkeleton{


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
	public wtp.InformMembersResponse InformMembers
	(
			wtp.InformMembers informMembers
	)
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


		if (informMembers.getUnitID().equals("null"))
			result =omsInterface.informMembers(null,informMembers.getRoleID(),informMembers.getPositionValue(),informMembers.getAgentID());
		else
			result =omsInterface.informMembers(informMembers.getUnitID(),informMembers.getRoleID(),informMembers.getPositionValue(),informMembers.getAgentID());
		res.setResult(result);
		return res;

	}

}
