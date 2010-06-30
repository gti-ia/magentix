/**
 * ExpulseSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2 Apr
 * 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.List;

import persistence.DataBaseInterface;

/**
 * ExpulseSkeleton java skeleton for the axisService
 */
public class ExpulseSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	public static final Boolean		DEBUG		= true;
	/**
	 * Auto generated method signature
	 * @param expulse
	 */
	
	public wtp.ExpulseResponse Expulse(wtp.Expulse expulse)
	{
		
		wtp.ExpulseResponse res = new ExpulseResponse();
		if (DEBUG)
		{
			System.out.println("Expulse :");
			System.out.println("***AgentID..." + expulse.getAgentID());
			System.out.println("***Expulsed AgentID..."+expulse.getExpulsedAgentID());
			System.out.println("*** RoleID()..." + expulse.getRoleID());
			System.out.println("*** UnitID()..." + expulse.getUnitID());
		}
		res.setStatus("Ok");
		res.setErrorValue("");
		if (expulse.getAgentID() == "" || expulse.getRoleID() == ""
				|| expulse.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		if (!thomasBD.CheckExistsAgent(expulse.getAgentID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsAgent(expulse.getExpulsedAgentID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsUnit(expulse.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsRole(expulse.getRoleID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckAgentPlaysRole(expulse.getRoleID(), expulse
				.getUnitID(), expulse.getAgentID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!roleBasedControl(expulse.getAgentID(), expulse.getUnitID()))
		{
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.DeleteAgentPlaysRole(expulse.getRoleID(), expulse
				.getUnitID(), expulse.getExpulsedAgentID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		return res;
	}
	
	private boolean roleBasedControl(String agentID, String unitID)
	{
		if (unitID.equalsIgnoreCase("virtual"))
			return true;
		if (!thomasBD.CheckExistsAgent(agentID))
			return false;
		String unitType = thomasBD.GetUnitType(unitID);
		if (unitType.equalsIgnoreCase("flat"))
			return false;
		List<String> positions;
		try
		{
			positions = thomasBD.GetAgentPosition(agentID, unitID);
			for (int i = 0; i < positions.size(); i++)
				if (positions.get(i).equalsIgnoreCase("supervisor"))
					return true;
		}
		catch (Exception e)
		{
		}
		return false;
	}
}
