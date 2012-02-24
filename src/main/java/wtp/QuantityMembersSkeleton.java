
/**
 * QuantityMembersSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;
/**
 *  QuantityMembersSkeleton java skeleton for the axisService
 */
public class QuantityMembersSkeleton{

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

		DOMConfigurator.configure(QuantityMembersSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(QuantityMembersSkeleton.class);

		logger.info("QuantityMembers :");
		logger.info("***AgentID..." + quantityMembers.getAgentID());
		logger.info("***UnitID..." + quantityMembers.getUnitID());
		logger.info("***RoleID..." + quantityMembers.getRoleID());
		logger.info("***PositionID..." + quantityMembers.getPositionID());



		if (quantityMembers.getUnitID().equals("null"))
			result = omsInterface.quantityMembers(null,quantityMembers.getRoleID(), quantityMembers.getPositionID(), quantityMembers.getAgentID());
		else
			result = omsInterface.quantityMembers(quantityMembers.getUnitID(),quantityMembers.getRoleID(), quantityMembers.getPositionID(), quantityMembers.getAgentID());
		res.setResult(result);
		return res;
	}

}
