
/**
 * AllocateRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;
/**
 *  AllocateRoleSkeleton java skeleton for the axisService
 */
public class AllocateRoleSkeleton{

	public static final Boolean			DEBUG		= true;

	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param allocateRole
	 */

	public wtp.AllocateRoleResponse AllocateRole
	(
			wtp.AllocateRole allocateRole
	)
	{
		wtp.AllocateRoleResponse res = new AllocateRoleResponse();
		String result;

		if (DEBUG)
		{
			System.out.println("AcquireRole :");
			System.out.println("***AgentID..." + allocateRole.getAgentId());
			System.out.println("***RoleID()..." + allocateRole.getRoleName());
			System.out.println("***UnitID()..." + allocateRole.getUnitName());
			System.out.println("***TargetAgentName()..." + allocateRole.getTargetAgentName());
		}


		if (allocateRole.getRoleName().equals("null"))
			result =omsInterface.allocateRole(null, allocateRole.getUnitName(),allocateRole.getTargetAgentName(),allocateRole.getAgentId());
		else if (allocateRole.getUnitName().equals("null"))
			result =omsInterface.allocateRole(allocateRole.getRoleName(), null,allocateRole.getTargetAgentName(),allocateRole.getAgentId());
		else if (allocateRole.getTargetAgentName().equals("null"))
			result =omsInterface.allocateRole(allocateRole.getRoleName(), allocateRole.getUnitName(),null,allocateRole.getAgentId());
		else
			result =omsInterface.allocateRole(allocateRole.getRoleName(), allocateRole.getUnitName(),allocateRole.getTargetAgentName(),allocateRole.getAgentId());
		res.setResult(result);

		return res;

	}

}
