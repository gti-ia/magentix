
/**
 * JointUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;

/**
 *  JointUnitSkeleton java skeleton for the axisService
 */
public class JointUnitSkeleton{

	public static final Boolean		DEBUG		= true;
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
		if (DEBUG)
		{
			System.out.println("JointUnit :");
			System.out.println("***AgentID..." + jointUnit.getAgentID());
			System.out.println("***UnitID..." + jointUnit.getUnitName());
			System.out.println("***ParentUnitID..."
					+ jointUnit.getParentName());

		}


		if (jointUnit.getUnitName().equals("null"))
			result =omsInterface.jointUnit(null, jointUnit.getParentName(), jointUnit.getAgentID());
		else if (jointUnit.getParentName().equals("null"))
			result =omsInterface.jointUnit(jointUnit.getUnitName(), null, jointUnit.getAgentID());
		else
			result =omsInterface.jointUnit(jointUnit.getUnitName(), jointUnit.getParentName(), jointUnit.getAgentID());
		res.setResult(result);
		return res;



	}

}
