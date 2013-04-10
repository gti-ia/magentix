
/**
 * DeallocateRoleSkeleton.java
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
 *  DeallocateRoleSkeleton java skeleton for the axisService
 */
public class DeallocateRoleSkeleton{

	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param deallocateRole
	 */

	public wtp.DeallocateRoleResponse DeallocateRole
	(
			wtp.DeallocateRole deallocateRole
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
		
		wtp.DeallocateRoleResponse res = new DeallocateRoleResponse();
		String result;

		DOMConfigurator.configure(DeallocateRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(DeallocateRoleSkeleton.class);

		logger.info("DellocateRole :");
		logger.info("***AgentID..." + deallocateRole.getAgentID());
		logger.info("***RoleID()..." + deallocateRole.getRoleID());
		logger.info("***UnitID()..." + deallocateRole.getUnitID());
		logger.info("***TargetAgentName()..." + deallocateRole.getTargetAgentID());


		if (deallocateRole.getRoleID().equals("null"))
			result =omsInterface.deallocateRole(null,deallocateRole.getUnitID(),deallocateRole.getTargetAgentID(),deallocateRole.getAgentID());
		else if (deallocateRole.getUnitID().equals("null"))
			result =omsInterface.deallocateRole(deallocateRole.getRoleID(),null,deallocateRole.getTargetAgentID(),deallocateRole.getAgentID());
		else if (deallocateRole.getTargetAgentID().equals("null"))
			result =omsInterface.deallocateRole(deallocateRole.getRoleID(),deallocateRole.getUnitID(),null,deallocateRole.getAgentID());
		else
			result =omsInterface.deallocateRole(deallocateRole.getRoleID(),deallocateRole.getUnitID(),deallocateRole.getTargetAgentID(),deallocateRole.getAgentID());
		res.setResult(result);
		return res;


	}

}
