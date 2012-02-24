
/**
 * LeaveRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;

/**
 *  LeaveRoleSkeleton java skeleton for the axisService
 */
public class LeaveRoleSkeleton{
	
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param leaveRole
	 */

	public wtp.LeaveRoleResponse LeaveRole
	(
			wtp.LeaveRole leaveRole
	)
	{
		wtp.LeaveRoleResponse res = new LeaveRoleResponse();
		String result = "";
	
		DOMConfigurator.configure(InformRole.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(InformRole.class);
		
		logger.info("LeaveRole :");
		logger.info("***AgentID..." + leaveRole.getAgentID());
		logger.info("***UnitID()..." + leaveRole.getUnitID());
		logger.info("***RoleID()..." + leaveRole.getRoleID());

	

		if (leaveRole.getRoleID().equals("null"))
			result =omsInterface.leaveRole(null, leaveRole.getUnitID(),leaveRole.getAgentID());
		else if (leaveRole.getUnitID().equals("null"))
			result =omsInterface.leaveRole(leaveRole.getRoleID(), null,leaveRole.getAgentID());
		else
			result =omsInterface.leaveRole(leaveRole.getRoleID(), leaveRole.getUnitID(),leaveRole.getAgentID());
		res.setResult(result);
		return res;

	}

}
