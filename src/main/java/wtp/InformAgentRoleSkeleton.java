/**
 * InformAgentRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.sql.SQLException;

import persistence.OMSInterface;
import persistence.THOMASException;


/**
 * InformAgentRoleSkeleton java skeleton for the axisService
 */

public class InformAgentRoleSkeleton
{
	
	public static final Boolean	DEBUG	= true;
	private static OMSInterface omsInterface = new OMSInterface();
	/**
	 * Service used for requesting the list of roles and units where an agent is, given the 
	 * specific moment. The agent must exist and must be the same one as the requester.
	 * @param informAgentRole containing:
	 * - RequestedAgentID
	 * - RequesterAgentID
	 */
	public wtp.InformAgentRoleResponse InformAgentRole(
			wtp.InformAgentRole informAgentRole)
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
		
		res.setErrorValue("");
		res.setStatus("Ok");
		res.setRoleUnitList("");
		
		if (informAgentRole.getAgentID() == "")
		{
			res.setErrorValue("Invalid. Empty parameters are not allowed.");
			res.setStatus("Error");
			return res;
		}
		try{
			if (informAgentRole.getRequestedAgentID().equals("null"))
				result =omsInterface.informAgentRole(null,informAgentRole.getAgentID());
			else
				result =omsInterface.informAgentRole(informAgentRole.getRequestedAgentID(),informAgentRole.getAgentID());
			res.setRoleUnitList(result);
			res.setStatus("Ok");
			res.setErrorValue("");
			return res;
		}catch(THOMASException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getContent());
			return res;
		}
		catch(SQLException e)
		{
			res.setStatus("Error");
			res.setErrorValue(e.getMessage());
			return res;
		}
		
 }
	
}
