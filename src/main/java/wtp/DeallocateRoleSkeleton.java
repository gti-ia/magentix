
/**
 * DeallocateRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 *  DeallocateRoleSkeleton java skeleton for the axisService
 */
public class DeallocateRoleSkeleton{


	public static final Boolean			DEBUG		= true;

	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param deallocateRole
	 */

	public wtp.DeallocateRoleResponse DeallocateRole
	(
			wtp.DeallocateRole deallocateRole
	)
	{
		wtp.DeallocateRoleResponse res = new DeallocateRoleResponse();
		String result;

		if (DEBUG)
		{
			System.out.println("AcquireRole :");
			System.out.println("***AgentID..." + deallocateRole.getAgentID());
			System.out.println("***RoleID()..." + deallocateRole.getRoleName());
			System.out.println("***UnitID()..." + deallocateRole.getUnitName());
			System.out.println("***TargetAgentName()..." + deallocateRole.getTargetAgentName());
		}





		if (deallocateRole.getRoleName().equals("null"))
			result =omsInterface.deallocateRole(null,deallocateRole.getUnitName(),deallocateRole.getTargetAgentName(),deallocateRole.getAgentID());
		else if (deallocateRole.getUnitName().equals("null"))
			result =omsInterface.deallocateRole(deallocateRole.getRoleName(),null,deallocateRole.getTargetAgentName(),deallocateRole.getAgentID());
		else if (deallocateRole.getTargetAgentName().equals("null"))
			result =omsInterface.deallocateRole(deallocateRole.getRoleName(),deallocateRole.getUnitName(),null,deallocateRole.getAgentID());
		else
			result =omsInterface.deallocateRole(deallocateRole.getRoleName(),deallocateRole.getUnitName(),deallocateRole.getTargetAgentName(),deallocateRole.getAgentID());
		res.setResult(result);
		return res;

	}

}
