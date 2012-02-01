
/**
 * InformUnitSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;

/**
 *  InformUnitSkeleton java skeleton for the axisService
 */
public class InformUnitSkeleton{


	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface();

	/**
	 * Auto generated method signature
	 * 
	 * @param informUnit
	 */

	public wtp.InformUnitResponse InformUnit
	(
			wtp.InformUnit informUnit
	)
	{
		wtp.InformUnitResponse res = new InformUnitResponse();
		String result = "";
		if (DEBUG)
		{
			System.out.println("InformUnit :");
			System.out.println("***AgentID..." + informUnit.getAgentID());
			System.out.println("***UnitID()..." + informUnit.getUnitID());

		}


		if (informUnit.getUnitID().equals("null"))
			result =omsInterface.informUnit(null,informUnit.getAgentID());
		else
			result =omsInterface.informUnit(informUnit.getUnitID(),informUnit.getAgentID());
		res.setResult(result);
		return res;

	}

}
