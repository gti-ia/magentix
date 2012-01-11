
/**
 * AllocateRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

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


		res.setStatus("Ok");
		res.setErrorValue("");
		if (allocateRole.getAgentId() == "" || allocateRole.getRoleName() == ""
			|| allocateRole.getUnitName() == "" || allocateRole.getTargetAgentName() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}
		try{
			result =omsInterface.allocateRole(allocateRole.getRoleName(), allocateRole.getUnitName(),allocateRole.getTargetAgentName(),allocateRole.getAgentId());
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
