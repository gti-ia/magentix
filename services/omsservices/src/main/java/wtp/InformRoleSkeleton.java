
/**
 * InformRoleSkeleton.java
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
 *  InformRoleSkeleton java skeleton for the axisService
 */
public class InformRoleSkeleton{

	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param informRole
	 */

	public wtp.InformRoleResponse InformRole
	(
			wtp.InformRole informRole
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
		
		wtp.InformRoleResponse res = new InformRoleResponse();
		String result = "";

		DOMConfigurator.configure(InformRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(InformRoleSkeleton.class);

		logger.info("InformRole :");
		logger.info("***AgentID..." + informRole.getAgentID());
		logger.info("***UnitID()..." + informRole.getUnitID());
		logger.info("***RoleID()..." + informRole.getRoleID());


		if (informRole.getRoleID().equals("null"))
			result =omsInterface.informRole(null,informRole.getUnitID(),informRole.getAgentID());
		else if (informRole.getUnitID().equals("null"))
			result =omsInterface.informRole(informRole.getRoleID(),null,informRole.getAgentID());
		else
			result =omsInterface.informRole(informRole.getRoleID(),informRole.getUnitID(),informRole.getAgentID());
		res.setResult(result);
		return res;
	}

}
