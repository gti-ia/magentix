
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


		res.setStatus("Ok");
		res.setErrorValue("");
		if (deallocateRole.getAgentID() == "" || deallocateRole.getRoleName() == ""
			|| deallocateRole.getUnitName() == "" || deallocateRole.getTargetAgentName() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}
		try{
			result =omsInterface.deallocateRole(deallocateRole.getRoleName(),deallocateRole.getUnitName(),deallocateRole.getTargetAgentName(),deallocateRole.getAgentID());
			res.setStatus(result);
			res.setErrorValue("");
			return res;
		}catch(THOMASException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getMessage());
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
