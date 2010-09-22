/**
 * DeregisterUnitSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version:
 * 1.2 Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import java.util.List;

import persistence.DataBaseInterface;

/**
 * DeregisterUnitSkeleton java skeleton for the axisService
 */
public class DeregisterUnitSkeleton
{
	persistence.DataBaseInterface	thomasBD	= new DataBaseInterface();
	public static final Boolean		DEBUG		= true;
	
	/**
	 * Service used for deleting a unit (organization). The unit must exist, there must be no
	 * role defined inside this unit and the requester agent must have enough permissions
	 * (depending on the type of the organization) to delete the unit: if the type of the
	 * unit is 'FLAT' the agent is allowed, if 'TEAM' the agent must be a member of this
	 * unit's parent unit and if the type is 'HIERARCHY' the agent must be a supervisor
	 * or this unit's parent unit.
	 * @param deregisterUnit containing:
	 * - UnitID
	 * - AgentID
	 */
	public wtp.DeregisterUnitResponse DeregisterUnit(
			wtp.DeregisterUnit deregisterUnit)
	{
		wtp.DeregisterUnitResponse res = new DeregisterUnitResponse();
		
		if (DEBUG)
		{
			System.out.println("DeregisterUnit :");
			System.out.println("***AgentID..." + deregisterUnit.getAgentID());
			System.out.println("*** UnitID()..." + deregisterUnit.getUnitID());
		}
		
		res.setErrorValue("");
		res.setStatus("Ok");
		
		if (deregisterUnit.getUnitID() == ""
				|| deregisterUnit.getUnitID().equalsIgnoreCase("virtual"))
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsUnit(deregisterUnit.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
//		if (thomasBD.CheckUnitHasRole(deregisterUnit.getUnitID()))
//		{
//			res.setErrorValue("Invalid");
//			res.setStatus("Error");
//			return res;
//		}
//		if (thomasBD.CheckUnitHasUnit(deregisterUnit.getUnitID()))
//		{
//			res.setErrorValue("Invalid");
//			res.setStatus("Error");
//			return res;
//		}
//		if (thomasBD.CheckUnitHasMember(deregisterUnit.getUnitID()))
//		{
//			res.setErrorValue("Invalid");
//			res.setStatus("Error");
//			return res;
//		}
//		// role based control
		System.out.println("DeregisterUnit 3");
		if (!roleBasedControl(deregisterUnit.getAgentID(), deregisterUnit
				.getUnitID()))
		{
			System.out.println("DeregisterUnit 4");
			res.setErrorValue("Not-Allowed");
			res.setStatus("Error");
			return res;
		}
		System.out.println("DeregisterUnit 5");
		
		if (!thomasBD.DeleteUnit(deregisterUnit.getUnitID()))
		{
			System.out.println("DeregisterUnit 6");
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		System.out.println("DeregisterUnit ENDED");
		return res;
	}
	
	private boolean roleBasedControl(String agentID, String unitID)
	{
		if (unitID.equalsIgnoreCase("virtual"))
			return false;
		if (!thomasBD.CheckExistsAgent(agentID))
			return false;
		
		//String parentUnitID = thomasBD.GetParentUnitID(unitID);
		String unitType = thomasBD.GetUnitType(unitID);
		
		if (unitType.equalsIgnoreCase("flat"))
		{
			if(thomasBD.CheckUnitIsEmpty(unitID))
				return true;
			else if (thomasBD.CheckUnitHasOnlyThisMemberWithOneRole(unitID,agentID))
				return true;
			else
				return false;
		}
		else if (unitType.equalsIgnoreCase("team"))
		{
//			if (thomasBD.CheckAgentPlaysRoleInUnit(unitID, agentID))
//				return true;
//			else
//				return false;
			if(thomasBD.CheckUnitHasOnlyThisMemberWithOneRole(unitID, agentID))
				return true;
			else
				return false;
		}
		
		// unit is hierarchy
		List<String> positions;
		boolean isSupervisor=false;
		try
		{
			positions = thomasBD.GetAgentPosition(agentID, unitID);
			for (int i = 0; i < positions.size(); i++)
				if (positions.get(i).equalsIgnoreCase("supervisor"))
					isSupervisor=true;
		}
		catch (Exception e)
		{
			System.err.println("DeregisterUnit: exception caught:");
			System.err.println(e.toString());
		}
		if(isSupervisor && thomasBD.CheckUnitHasOnlyThisMemberWithOneRole(unitID, agentID))
			return true;
		else
			return false;
	}
}
