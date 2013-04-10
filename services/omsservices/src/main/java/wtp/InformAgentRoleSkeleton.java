
/**
 * InformAgentRoleSkeleton.java
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
 *  InformAgentRoleSkeleton java skeleton for the axisService
 */
public class InformAgentRoleSkeleton{


	
	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param informAgentRole
	 */

	public wtp.InformAgentRoleResponse InformAgentRole
	(
			wtp.InformAgentRole informAgentRole
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
		
		String result = "";
		wtp.InformAgentRoleResponse res = new InformAgentRoleResponse();
		
		DOMConfigurator.configure(InformAgentRole.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(InformAgentRole.class);
		
	
		logger.info("InformAgentRole :");
		logger.info("***AgentID..." + informAgentRole.getAgentID());
		logger.info("***RequestedAgentID()..."
					+ informAgentRole.getRequestedAgentID());
	




		if (informAgentRole.getRequestedAgentID().equals("null"))
			result =omsInterface.informAgentRole(null,informAgentRole.getAgentID());
		else
			result =omsInterface.informAgentRole(informAgentRole.getRequestedAgentID(),informAgentRole.getAgentID());
		res.setResult(result);
		return res;

	}

}
