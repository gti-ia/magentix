/**
 * DeregisterRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * DeregisterRoleSkeleton java skeleton for the axisService
 */
public class DeregisterRoleSkeleton
{
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Service used to deregister a norm. The role must exist, there must be no norm addressed to
	 * this role, this role must not be currently played by any agent, and the agent requesting
	 * the deregistration must have enough permissions (depending on the type of the
	 * organization) to deregister the role. If the type of this unit is 'FLAT' the agent is 
	 * allowed, if 'TEAM' the agent must be a member of this unit, and if the type is
	 * 'HIERARCHY' the agent must be a supervisor of this unit.
	 * @param deregisterRole containing:
	 * - RoleID
	 * - UnitID
	 * - AgentID
	 */
	public wtp.DeregisterRoleResponse DeregisterRole(
			wtp.DeregisterRole deregisterRole)
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

		res.setErrorValue("");
		res.setStatus("Ok");

		//		if (deregisterRole.getRoleID() == ""
		//				|| deregisterRole.getRoleID().equalsIgnoreCase("member")
		//				|| deregisterRole.getUnitID() == "")
		if (deregisterRole.getRoleID() == ""
			|| deregisterRole.getUnitID() == "") // se permite borrar el rol member, ya que no se crea por defecto al crear una unidad.
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}

		try{
			result =omsInterface.deregisterRole(deregisterRole.getRoleID(), deregisterRole.getUnitID(), deregisterRole.getAgentID());
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
