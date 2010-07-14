/**
 * InformRoleProfilesSkeleton.java This file was auto-generated from WSDL by the Apache Axis2
 * version: 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import persistence.DataBaseInterface;

/**
 * InformRoleProfilesSkeleton java skeleton for the axisService
 */

public class InformRoleProfilesSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	
	/**
	 * TODO: this service seems to have no returning value.
	 * @param informRoleProfiles containing:
	 * - RoleID
	 * - AgentID
	 */
	public wtp.InformRoleProfilesResponse InformRoleProfiles(
			wtp.InformRoleProfiles informRoleProfiles)
	{
		wtp.InformRoleProfilesResponse res = new InformRoleProfilesResponse();
		res.setProfileList("");
		res.setStatus("Ok");
		if (informRoleProfiles.getRoleID() == "")
		{
			res.setStatus("Error");
			return res;
		}
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		if (!thomasBD.CheckExistsRole(informRoleProfiles.getRoleID()))
		{
			res.setStatus("Error");
			return res;
		}
		// role based control
		if (!roleBasedControl(informRoleProfiles.getAgentID()))
		{
			res.setStatus("Error");
			return res;
		}
		return res;
	}
	private boolean roleBasedControl(String agentID)
	{
		if (thomasBD.CheckExistsAgent(agentID))
			return true;
		return false;
	}
}
