
/**
 * InformAgentRoleSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
package wtp;

import persistence.OMSInterface;

/**
 *  InformAgentRoleSkeleton java skeleton for the axisService
 */
public class InformAgentRoleSkeleton{


	public static final Boolean	DEBUG	= true;
	private static OMSInterface omsInterface = new OMSInterface();

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
		String result = "";
		wtp.InformAgentRoleResponse res = new InformAgentRoleResponse();
		if (DEBUG)
		{
			System.out.println("InformAgentRole :");
			System.out.println("***AgentID..." + informAgentRole.getAgentID());
			System.out.println("***RequestedAgentID()..."
					+ informAgentRole.getRequestedAgentID());
		}




		if (informAgentRole.getRequestedAgentID().equals("null"))
			result =omsInterface.informAgentRole(null,informAgentRole.getAgentID());
		else
			result =omsInterface.informAgentRole(informAgentRole.getRequestedAgentID(),informAgentRole.getAgentID());
		res.setResult(result);
		return res;

	}

}
