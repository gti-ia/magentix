/**
 * AcquireRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2
 * Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * AcquireRoleSkeleton java skeleton for the axisService
 */
public class AcquireRoleSkeleton
{
	public static final Boolean			DEBUG		= true;

	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Service used for acquiring a role in a specific unit. The role must exist in this unit, the
	 * agent must not play already that role, the agent must be inside the parent unit of this unit,
	 * and the rules of cardinality and compatibility must be passed.
	 * @param acquireRole containing:
	 * - UnitID
	 * - RoleID
	 * - AgentID
	 */
	public wtp.AcquireRoleResponse AcquireRole(wtp.AcquireRole acquireRole)
	{
		wtp.AcquireRoleResponse res = new AcquireRoleResponse();
		String result;

		if (DEBUG)
		{
			System.out.println("AcquireRole :");
			System.out.println("***AgentID..." + acquireRole.getAgentID());
			System.out.println("***RoleID()..." + acquireRole.getRoleID());
			System.out.println("***UnitID()..." + acquireRole.getUnitID());
		}


		res.setStatus("Ok");
		res.setErrorValue("");
		if (acquireRole.getAgentID() == "" || acquireRole.getRoleID() == ""
			|| acquireRole.getUnitID() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}
		try{
			result = omsInterface.AcquireRole(acquireRole.getRoleID(), acquireRole.getUnitID(),acquireRole.getAgentID());
			
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
