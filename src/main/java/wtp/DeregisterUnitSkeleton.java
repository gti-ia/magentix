
/**
 * DeregisterUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;

/**
 *  DeregisterUnitSkeleton java skeleton for the axisService
 */
public class DeregisterUnitSkeleton{


	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param deregisterUnit
	 */

	public wtp.DeregisterUnitResponse DeregisterUnit
	(
			wtp.DeregisterUnit deregisterUnit
	)
	{
		wtp.DeregisterUnitResponse res = new DeregisterUnitResponse();
		String result = "";

		if (DEBUG)
		{
			System.out.println("DeregisterUnit :");
			System.out.println("***AgentID..." + deregisterUnit.getAgentID());
			System.out.println("*** UnitID()..." + deregisterUnit.getUnitID());
		}


		if (deregisterUnit.getUnitID().equals("null"))
			result =omsInterface.deregisterUnit(null, deregisterUnit.getAgentID());
		else
			result =omsInterface.deregisterUnit(deregisterUnit.getUnitID(), deregisterUnit.getAgentID());
		res.setResult(result);
		return res;

	}

}
