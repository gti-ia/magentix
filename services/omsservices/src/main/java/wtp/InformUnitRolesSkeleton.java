
/**
 * InformUnitRolesSkeleton.java
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
 *  InformUnitRolesSkeleton java skeleton for the axisService
 */
public class InformUnitRolesSkeleton{

	
	private Agent myAgent = null;
	private OMSInterface omsInterface = null;
	/**
	 * Auto generated method signature
	 * 
	 * @param informUnitRoles
	 */

	public wtp.InformUnitRolesResponse InformUnitRoles
	(
			wtp.InformUnitRoles informUnitRoles
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
		
		wtp.InformUnitRolesResponse res = new InformUnitRolesResponse();
		String result = "";

		DOMConfigurator.configure(InformAgentRole.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(InformAgentRole.class);
		
		logger.info("InformUnitRoles :");
		logger.info("***AgentID..." + informUnitRoles.getAgentID());
		logger.info("***UnitID()..." + informUnitRoles.getUnitID());

		


		if (informUnitRoles.getUnitID().equals("null"))
			result =omsInterface.informUnitRoles(null,informUnitRoles.getAgentID());
		else
			result =omsInterface.informUnitRoles(informUnitRoles.getUnitID(),informUnitRoles.getAgentID());
		res.setResult(result);
		return res;

	}

}
