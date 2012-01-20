/**
 * QuantityMembersSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 * QuantityMembersSkeleton java skeleton for the axisService
 */

public class QuantityMembersSkeleton
{
	
	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Service used for requesting the number of current members of a specific unit. If a
	 * role is specified only the members playing that role are taken into account. The unit and
	 * the role must exist and the agent must be a member of the parent unit.
	 * @param quantityMembers containing:
	 * - UnitID
	 * - RoleID
	 * - AgentID
	 */
	public wtp.QuantityMembersResponse QuantityMembers(
			wtp.QuantityMembers quantityMembers)
	{
		wtp.QuantityMembersResponse res = new QuantityMembersResponse();
		String result = "";
		if (DEBUG)
		{
			System.out.println("QuantityMembers :");
			System.out.println("***AgentID..." + quantityMembers.getAgentID());
			System.out.println("***UnitID..." + quantityMembers.getUnitID());
			System.out.println("***RoleID..." + quantityMembers.getRoleID());
		}
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setQuantity("0");
	
		try{
			if (quantityMembers.getUnitID().equals("null"))
				result = omsInterface.quantityMembers(null,quantityMembers.getRoleID(), quantityMembers.getPositionValue(), quantityMembers.getAgentID());
			else
				result = omsInterface.quantityMembers(quantityMembers.getUnitID(),quantityMembers.getRoleID(), quantityMembers.getPositionValue(), quantityMembers.getAgentID());
			res.setStatus("Ok");
			res.setQuantity(result);
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