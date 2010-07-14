/**
 * InformRoleNormsSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.List;

import persistence.DataBaseInterface;

/**
 * InformRoleNormsSkeleton java skeleton for the axisService
 */

public class InformRoleNormsSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	public static final Boolean		DEBUG		= true;
	
	/**
	 * Service used for requesting the list of norms addressed to a specific role. The role must
	 * exist and the agent must be a member of THOMAS.
	 * @param informRoleNorms containing:
	 * - RoleID
	 * - AgentID
	 */
	public wtp.InformRoleNormsResponse InformRoleNorms(
			wtp.InformRoleNorms informRoleNorms)
	{
		wtp.InformRoleNormsResponse res = new InformRoleNormsResponse();
		if (DEBUG)
		{
			System.out.println("InformRoleNorms :");
			System.out.println("***AgentID..." + informRoleNorms.getAgentID());
			System.out.println("***RoleID()..." + informRoleNorms.getRoleID());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setNormList("");
		if (informRoleNorms.getRoleID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			if (DEBUG)
			{
				System.out.println("***NormList..." + res.getNormList());
				System.out.println("***ErrorValue..." + res.getErrorValue());
				System.out.println("***Status..." + res.getStatus());
			}
			return res;
		}
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		if (!thomasBD.CheckExistsRole(informRoleNorms.getRoleID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			if (DEBUG)
			{
				System.out.println("***NormList..." + res.getNormList());
				System.out.println("***ErrorValue..." + res.getErrorValue());
				System.out.println("***Status..." + res.getStatus());
			}
			return res;
		}
		// role based control
		if (!roleBasedControl(informRoleNorms.getAgentID()))
		{
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			if (DEBUG)
			{
				System.out.println("***NormList..." + res.getNormList());
				System.out.println("***ErrorValue..." + res.getErrorValue());
				System.out.println("***Status..." + res.getStatus());
			}
			return res;
		}
		res.setNormList(thomasBD.GetRoleNormsList(informRoleNorms.getRoleID())
				.toString());
		if (DEBUG)
		{
			System.out.println("***NormList..." + res.getNormList());
			System.out.println("***ErrorValue..." + res.getErrorValue());
			System.out.println("***Status..." + res.getStatus());
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
