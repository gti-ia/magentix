
/**
 * RegisterRoleSkeleton.java
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
 *  RegisterRoleSkeleton java skeleton for the axisService
 */
public class RegisterRoleSkeleton{
	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param registerRole
	 */

	public wtp.RegisterRoleResponse RegisterRole
	(
			wtp.RegisterRole registerRole
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
		
		wtp.RegisterRoleResponse res = new RegisterRoleResponse();
		String result = "";

		DOMConfigurator.configure(RegisterRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(RegisterRoleSkeleton.class);


		logger.info("RegisterRole :");
		logger.info("***AgentID..." + registerRole.getAgentID());
		logger.info("***UnitID..." + registerRole.getUnitID());
		logger.info("***RoleID..." + registerRole.getRoleID());
		logger.info("***Accessibility..."
				+ registerRole.getAccessibilityID());
		logger.info("***Position..." + registerRole.getPositionID());
		logger.info("***Visibility..."
				+ registerRole.getVisibilityID());



		if (registerRole.getRoleID().equals("null"))
			result =omsInterface.registerRole(null, registerRole.getUnitID(), registerRole.getAccessibilityID(), registerRole.getVisibilityID(), registerRole.getPositionID(), registerRole.getAgentID());
		else if (registerRole.getUnitID().equals("null"))
			result =omsInterface.registerRole(registerRole.getRoleID(), null, registerRole.getAccessibilityID(), registerRole.getVisibilityID(), registerRole.getPositionID(), registerRole.getAgentID());
		else if (registerRole.getAccessibilityID().equals("null"))
			result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), null, registerRole.getVisibilityID(), registerRole.getPositionID(), registerRole.getAgentID());
		else if (registerRole.getVisibilityID().equals("null"))
			result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibilityID(), null, registerRole.getPositionID(), registerRole.getAgentID());
		else if (registerRole.getPositionID().equals("null"))
			result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibilityID(), registerRole.getVisibilityID(), null, registerRole.getAgentID());
		else
			result =omsInterface.registerRole(registerRole.getRoleID(), registerRole.getUnitID(), registerRole.getAccessibilityID(), registerRole.getVisibilityID(), registerRole.getPositionID(), registerRole.getAgentID());
		res.setResult(result);
		return res;
	}

}
