
/**
 * DeregisterRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;

/**
 *  DeregisterRoleSkeleton java skeleton for the axisService
 */
public class DeregisterRoleSkeleton{

	
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param deregisterRole
	 */

	public wtp.DeregisterRoleResponse DeregisterRole
	(
			wtp.DeregisterRole deregisterRole
	)
	{
		wtp.DeregisterRoleResponse res = new DeregisterRoleResponse();
		String result= "";

		DOMConfigurator.configure(DeregisterRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(DeregisterRoleSkeleton.class);

		logger.info("DeregisterRole :");
		logger.info("***AgentID..." + deregisterRole.getAgentID());
		logger.info("*** RoleID()..." + deregisterRole.getRoleID());
		logger.info("*** UnitID()..." + deregisterRole.getUnitID());


		result =omsInterface.deregisterRole(deregisterRole.getRoleID(), deregisterRole.getUnitID(), deregisterRole.getAgentID());
		res.setResult(result);
		return res;

	}

}
