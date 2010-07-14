/**
 * AcquireRoleSkeleton.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.2
 * Apr 27, 2007 (04:35:37 IST)
 */
package wtp;

import NormativeManagement.NormativeChecker;
import NormativeManagement.NormativeManager;
import persistence.DataBaseInterface;

/**
 * AcquireRoleSkeleton java skeleton for the axisService
 */
public class AcquireRoleSkeleton
{
	public static final Boolean			DEBUG		= true;
	
	private static NormativeChecker		normCheck	= new NormativeChecker();
	private static DataBaseInterface	thomasBD	= new DataBaseInterface();
	private static NormativeManager		normManager	= new NormativeManager();
	
	/**
	 * Service used for acquiring a role in a specific unit. The role must exist in this unit, the
	 * agent must not play already that role, the agent must be inside the parent unit of this unit,
	 * and the rules of cardinality and compatibility must be passed.
	 * @param acquireRole containing:
	 * - UnitID
	 * - RoleID
	 * - AgentID
	 */
	public wtp.AcquireRoleResponse AcquireRole(wtp.AcquireRole acquireRole)
	{
		wtp.AcquireRoleResponse res = new AcquireRoleResponse();
		if (DEBUG)
		{
			System.out.println("AcquireRole :");
			System.out.println("***AgentID..." + acquireRole.getAgentID());
			System.out.println("***RoleID()..." + acquireRole.getRoleID());
			System.out.println("***UnitID()..." + acquireRole.getUnitID());
		}
		res.setStatus("Ok");
		res.setErrorValue("");
		if (acquireRole.getAgentID() == "" || acquireRole.getRoleID() == ""
				|| acquireRole.getUnitID() == "")
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		persistence.DataBaseInterface thomasBD = new DataBaseInterface();
		if (!thomasBD.CheckExistsUnit(acquireRole.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsRole(acquireRole.getRoleID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.CheckExistsRoleInUnit(acquireRole.getRoleID(),
				acquireRole.getUnitID()))
		{
			res.setErrorValue("NotFound");
			res.setStatus("Error");
			return res;
		}
		if (thomasBD.CheckAgentPlaysRole(acquireRole.getRoleID(), acquireRole
				.getUnitID(), acquireRole.getAgentID()))
		{
			res.setErrorValue("Duplicate");
			res.setStatus("Error");
			return res;
		}
		if (!acquireRole.getUnitID().equalsIgnoreCase("virtual")
				& !thomasBD.CheckAgentPlaysRoleInUnit(thomasBD
						.GetParentUnitID(acquireRole.getUnitID()), acquireRole
						.getAgentID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		// COMPROBACION DE LAS NORMAS
		if (!normManager.checkMaxCardinalityNorms(acquireRole.getRoleID(),
				acquireRole.getAgentID()))
		{
			res.setErrorValue("MaxCardinalityConstraint");
			res.setStatus("Error");
			return res;
		}
		if (!normManager.checkIncompatibilityNorms(acquireRole.getRoleID(),
				acquireRole.getAgentID()))
		{
			res.setErrorValue("IncompatibilityConstraint");
			res.setStatus("Error");
			return res;
		}
		if (!thomasBD.AddNewAgentPlaysRole(acquireRole.getRoleID(), acquireRole
				.getUnitID(), acquireRole.getAgentID()))
		{
			res.setErrorValue("Invalid");
			res.setStatus("Error");
			return res;
		}
		return res;
	}
}
