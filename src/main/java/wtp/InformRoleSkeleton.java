/**
 * InformRoleProfilesSkeleton.java This file was auto-generated from WSDL by the Apache Axis2
 * version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;


import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * InformRoleProfilesSkeleton java skeleton for the axisService
 */

public class InformRoleSkeleton
{
	private static OMSInterface omsInterface = new OMSInterface();
	
	/**
	 * This service is no longer supported.
	 * @param informRoleProfiles containing:
	 * - RoleID
	 * - AgentID
	 */
	public wtp.InformRoleResponse InformRole(
			wtp.InformRole informRole)
	{
		wtp.InformRoleResponse res = new InformRoleResponse();
		String result = "";
		res.setProfileList("");
		res.setStatus("Ok");
		if (informRole.getRoleName() == "")
		{
			res.setStatus("Invalid. Empty parameters are not allowed.");
			return res;
		}
		
		try{
			result =omsInterface.informRole(informRole.getRoleName(),informRole.getUnitName(),informRole.getAgentID());
			res.setStatus(result);
			return res;
		}catch(THOMASException e)
		{
			res.setStatus(e.getContent());
		
			return res;
		}
		catch(SQLException e)
		{
			res.setStatus(e.getMessage());
			return res;
		}
	}
}
