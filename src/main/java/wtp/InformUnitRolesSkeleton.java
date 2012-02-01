
/**
 * InformUnitRolesSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;

/**
 *  InformUnitRolesSkeleton java skeleton for the axisService
 */
public class InformUnitRolesSkeleton{

	public static final Boolean		DEBUG		= true;
	private static OMSInterface omsInterface = new OMSInterface(); 
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
		wtp.InformUnitRolesResponse res = new InformUnitRolesResponse();
		String result = "";
		if (DEBUG)
		{
			System.out.println("InformUnitRoles :");
			System.out.println("***AgentID..." + informUnitRoles.getAgentID());
			System.out.println("***UnitID()..." + informUnitRoles.getUnitID());

		}


		if (informUnitRoles.getUnitID().equals("null"))
			result =omsInterface.informUnitRoles(null,informUnitRoles.getAgentID());
		else
			result =omsInterface.informUnitRoles(informUnitRoles.getUnitID(),informUnitRoles.getAgentID());
		res.setResult(result);
		return res;

	}

}
