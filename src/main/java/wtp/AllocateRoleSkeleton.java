
/**
 * AllocateRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;
/**
 *  AllocateRoleSkeleton java skeleton for the axisService
 */
public class AllocateRoleSkeleton{



	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param allocateRole
	 */

	public wtp.AllocateRoleResponse AllocateRole
	(
			wtp.AllocateRole allocateRole
	)
	{
		wtp.AllocateRoleResponse res = new AllocateRoleResponse();
		String result;

		DOMConfigurator.configure(AllocateRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(AllocateRoleSkeleton.class);

		logger.info("AllocateRole :");
		logger.info("***AgentID..." + allocateRole.getAgentID());
		logger.info("***RoleID()..." + allocateRole.getRoleID());
		logger.info("***UnitID()..." + allocateRole.getUnitID());
		logger.info("***TargetAgentName()..." + allocateRole.getTargetAgentID());



		if (allocateRole.getRoleID().equals("null"))
			result =omsInterface.allocateRole(null, allocateRole.getUnitID(),allocateRole.getTargetAgentID(),allocateRole.getAgentID());
		else if (allocateRole.getUnitID().equals("null"))
			result =omsInterface.allocateRole(allocateRole.getRoleID(), null,allocateRole.getTargetAgentID(),allocateRole.getAgentID());
		else if (allocateRole.getTargetAgentID().equals("null"))
			result =omsInterface.allocateRole(allocateRole.getRoleID(), allocateRole.getUnitID(),null,allocateRole.getAgentID());
		else
			result =omsInterface.allocateRole(allocateRole.getRoleID(), allocateRole.getUnitID(),allocateRole.getTargetAgentID(),allocateRole.getAgentID());
		res.setResult(result);

		return res;
	}

}
