/**
 * DeregisterUnitSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp.copy;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * DeregisterUnitSkeleton java skeleton for the axisService
 */
public class DeregisterUnitSkeleton
{
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	
	/**
	 * Service used for deleting a unit (organization). The unit must exist, there must be no
	 * role defined inside this unit and the requester agent must have enough permissions
	 * (depending on the type of the organization) to delete the unit: if the type of the
	 * unit is 'FLAT' the agent is allowed, if 'TEAM' the agent must be a member of this
	 * unit's parent unit and if the type is 'HIERARCHY' the agent must be a supervisor
	 * or this unit's parent unit.
	 * @param deregisterUnit containing:
	 * - UnitID
	 * - AgentID
	 */
	public wtp.DeregisterUnitResponse DeregisterUnit(
			wtp.DeregisterUnit deregisterUnit)
	{
		wtp.DeregisterUnitResponse res = new DeregisterUnitResponse();
		String result = "";
		
		if (DEBUG)
		{
			System.out.println("DeregisterUnit :");
			System.out.println("***AgentID..." + deregisterUnit.getAgentID());
			System.out.println("*** UnitID()..." + deregisterUnit.getUnitID());
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		
		if (deregisterUnit.getUnitID() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}
		try{
			result =omsInterface.deregisterUnit(deregisterUnit.getUnitID(), deregisterUnit.getAgentID());
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
