
/**
 * InformMembersSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;
/**
 *  InformMembersSkeleton java skeleton for the axisService
 */
public class InformMembersSkeleton{

	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Auto generated method signature
	 * 
	 * @param informMembers
	 */

	public wtp.InformMembersResponse InformMembers
	(
			wtp.InformMembers informMembers
	)
	{

		wtp.InformMembersResponse res = new InformMembersResponse();
		String result = "";

		DOMConfigurator.configure(InformMembersSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(InformMembersSkeleton.class);

		logger.info("InformMembers :");
		logger.info("***AgentID..." + informMembers.getAgentID());
		logger.info("***UnitID()..." + informMembers.getUnitID());
		logger.info("***RoleID()..." + informMembers.getRoleID());
		logger.info("***PositionID()..." + informMembers.getPositionID());
		
		if (informMembers.getUnitID().equals("null"))
			result =omsInterface.informMembers(null,informMembers.getRoleID(),informMembers.getPositionID(),informMembers.getAgentID());
		else
			result =omsInterface.informMembers(informMembers.getUnitID(),informMembers.getRoleID(),informMembers.getPositionID(),informMembers.getAgentID());
		res.setResult(result);
		return res;
	}

}

