
/**
 * DeregisterRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 *  DeregisterRoleSkeleton java skeleton for the axisService
 */
public class DeregisterRoleSkeleton{

	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param deregisterRole
	 */

	public wtp.DeregisterRoleResponse DeregisterRole
	(
			wtp.DeregisterRole deregisterRole
	)
	{
		wtp.DeregisterRoleResponse res = new DeregisterRoleResponse();
		String result= "";

		if (DEBUG)
		{
			System.out.println("DeregisterRole :");
			System.out.println("***AgentID..." + deregisterRole.getAgentID());
			System.out.println("*** RoleID()..." + deregisterRole.getRoleID());
			System.out.println("*** UnitID()..." + deregisterRole.getUnitID());
		}

		result =omsInterface.deregisterRole(deregisterRole.getRoleID(), deregisterRole.getUnitID(), deregisterRole.getAgentID());
		res.setResult(result);
		return res;

	}

}
