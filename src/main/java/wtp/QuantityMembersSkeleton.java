
/**
 * QuantityMembersSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;

/**
 *  QuantityMembersSkeleton java skeleton for the axisService
 */
public class QuantityMembersSkeleton{

	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param quantityMembers
	 */

	public wtp.QuantityMembersResponse QuantityMembers
	(
			wtp.QuantityMembers quantityMembers
	)
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


		if (quantityMembers.getUnitID().equals("null"))
			result = omsInterface.quantityMembers(null,quantityMembers.getRoleID(), quantityMembers.getPositionValue(), quantityMembers.getAgentID());
		else
			result = omsInterface.quantityMembers(quantityMembers.getUnitID(),quantityMembers.getRoleID(), quantityMembers.getPositionValue(), quantityMembers.getAgentID());
		res.setResult(result);
		return res;

	}

}
