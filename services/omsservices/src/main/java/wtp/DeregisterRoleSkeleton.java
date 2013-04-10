
/**
 * DeregisterRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSInterface;

/**
 *  DeregisterRoleSkeleton java skeleton for the axisService
 */
public class DeregisterRoleSkeleton{

	
	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

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
		try {
			myAgent = new Agent(new AgentID("myAgent"));
			myAgent.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		omsInterface = new OMSInterface(myAgent);
		
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
