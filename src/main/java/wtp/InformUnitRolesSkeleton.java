/**
 * InformUnitRolesSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;
import java.util.List;

import persistence.DataBaseInterface;
import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * InformUnitRolesSkeleton java skeleton for the axisService
 */

public class InformUnitRolesSkeleton
{
	
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Service used for requesting the list of roles that have been registered inside a unit.
	 * The unit must exist and the requester agent is allowed to use the service: if the type
	 * of the unit is 'FLAT' the agent is allowed, if it is 'TEAM' or 'HIERARCHY' the agent
	 * must be a member of this unit.
	 * @param informUnitRoles containing:
	 * - UnitID
	 * - AgentID
	 */
	public wtp.InformUnitRolesResponse InformUnitRoles(
			wtp.InformUnitRoles informUnitRoles)
	{
		wtp.InformUnitRolesResponse res = new InformUnitRolesResponse();
		String result = "";
		if (DEBUG)
		{
			System.out.println("InformUnitRoles :");
			System.out.println("***AgentID..." + informUnitRoles.getAgentID());
			System.out.println("***UnitID()..." + informUnitRoles.getUnitID());
			
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setRoleList("");
		if (informUnitRoles.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		try{
			result =omsInterface.informUnitRoles(informUnitRoles.getUnitID(),informUnitRoles.getAgentID());
			res.setStatus("Ok");
			res.setRoleList(result);
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
