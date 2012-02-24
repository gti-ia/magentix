
/**
 * JointUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import persistence.OMSInterface;
/**
 *  JointUnitSkeleton java skeleton for the axisService
 */
public class JointUnitSkeleton{
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param jointUnit
	 */

	public wtp.JointUnitResponse JointUnit
	(
			wtp.JointUnit jointUnit
	)
	{
		wtp.JointUnitResponse res = new JointUnitResponse();
		String result = "";
		DOMConfigurator.configure(JointUnitSkeleton.class.getResource("/"+"loggin.xml"));
		Logger logger = Logger.getLogger(JointUnitSkeleton.class);
		logger.info("JointUnit :");
		logger.info("***AgentID..." + jointUnit.getAgentID());
		logger.info("***UnitID..." + jointUnit.getUnitID());
		logger.info("***ParentUnitID..."
				+ jointUnit.getParentUnitID());




		if (jointUnit.getUnitID().equals("null"))
			result =omsInterface.jointUnit(null, jointUnit.getParentUnitID(), jointUnit.getAgentID());
		else if (jointUnit.getParentUnitID().equals("null"))
			result =omsInterface.jointUnit(jointUnit.getUnitID(), null, jointUnit.getAgentID());
		else
			result =omsInterface.jointUnit(jointUnit.getUnitID(), jointUnit.getParentUnitID(), jointUnit.getAgentID());
		res.setResult(result);
		return res;
	}

}
