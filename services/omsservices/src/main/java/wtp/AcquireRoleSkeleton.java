
/**
 * AcquireRoleSkeleton.java
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
 *  AcquireRoleSkeleton java skeleton for the axisService
 */
public class AcquireRoleSkeleton{

	public static final Boolean			DEBUG		= true;

	private Agent myAgent = null;
	private OMSInterface omsInterface = null;

	/**
	 * Service used for acquiring a role in a specific unit. The role must exist in this unit, the
	 * agent must not play already that role, the agent must be inside the parent unit of this unit,
	 * and the rules of cardinality and compatibility must be passed.
	 * @param acquireRole containing:
	 * - UnitID
	 * - RoleID
	 * - AgentID
	 */

	public wtp.AcquireRoleResponse AcquireRole
	(
			wtp.AcquireRole acquireRole
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
		
		wtp.AcquireRoleResponse res = new AcquireRoleResponse();
		String result;


		DOMConfigurator.configure(AcquireRoleSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(AcquireRoleSkeleton.class);

		logger.info("AcquireRole:");
		logger.info("***AgentID..." + acquireRole.getAgentID());
		logger.info("***RoleID()..." + acquireRole.getRoleID());
		logger.info("***UnitID()..." + acquireRole.getUnitID());


		if (acquireRole.getRoleID().equals("null"))
			result = omsInterface.acquireRole(null, acquireRole.getUnitID(),acquireRole.getAgentID());
		else if (acquireRole.getUnitID().equals("null"))
			result = omsInterface.acquireRole(acquireRole.getRoleID(), null,acquireRole.getAgentID());
		else
			result = omsInterface.acquireRole(acquireRole.getRoleID(), acquireRole.getUnitID(),acquireRole.getAgentID());

		res.setResult(result);
		return res;

	}

}
