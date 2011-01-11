/**
 * QuantityMembersSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.math.BigInteger;
import java.util.List;

import persistence.DataBaseInterface;

/**
 * QuantityMembersSkeleton java skeleton for the axisService
 */

public class QuantityMembersSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	public static final Boolean		DEBUG		= true;
	
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
		if (DEBUG)
		{
			System.out.println("QuantityMembers :");
			System.out.println("***AgentID..." + quantityMembers.getAgentID());
			System.out.println("***UnitID..." + quantityMembers.getUnitID());
			System.out.println("***RoleID..." + quantityMembers.getRoleID());
		}
		res.setErrorValue("");
		res.setStatus("Ok");
		java.math.BigInteger quantity = BigInteger.valueOf(0);
		res.setQuantity(quantity);
		
		if (quantityMembers.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsUnit(quantityMembers.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (quantityMembers.getRoleID()!="" && !thomasBD.CheckExistsRole(quantityMembers.getRoleID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		// role based control
		if (!roleBasedControl(quantityMembers.getAgentID(), quantityMembers
				.getUnitID()))
		{
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			return res;
		}
		quantity =
				BigInteger.valueOf(thomasBD.GetQuantityMember(quantityMembers
						.getUnitID(), quantityMembers.getRoleID()));
		res.setQuantity(quantity);
		return res;
	}
	private boolean roleBasedControl(String agentID, String unitID)
	{
		if (unitID.equalsIgnoreCase("virtual"))
			return true;
		if (!thomasBD.CheckExistsAgent(agentID))
			return false;
		String parentUnitID = thomasBD.GetParentUnitID(unitID);
		if (thomasBD.CheckAgentPlaysRoleInUnit(parentUnitID, agentID))
			return true;
		else
			return false;
	}
	
}
