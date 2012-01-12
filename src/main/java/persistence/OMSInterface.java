package persistence;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class gives us the support to accede to the services of the OMS. The OMS
 * provides a group of services for registering or deregistering structural
 * components, specific roles, norms and units. It also offers services for
 * reporting on these components.
 * 
 * @author Joan Bellver Faus, jbellver@dsic.upv.es
 *
 */
public class OMSInterface {

	private DataBaseInterface dbInterface;
	private enum Flags{CASE_A,CASE_B,CASE_C,CASE_D};

	public OMSInterface()
	{
		dbInterface = new DataBaseInterface();
	}

	/**
	 * Checks if parameter is null or empty	
	 * @param parameter
	 * @return true, the parameter is not null or empty, false: the parameter is null or empty 
	 */
	boolean checkParameter(String parameter)
	{
		if (parameter != null)
		{
			if (!parameter.equals(""))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new empty unit in the organization, with a specific type and creatorName.
	 * 
	 * @param UnitName Identifier of the organization unit
	 * @param UnitType Indicates the topology of the new unit: (i) Hierarchy, (ii)Team, (iii) Flat.
	 * @param AgentName Identifier of the agent
	 * @param CreatorName Identifier of the new role with creator position 
	 * @return Returns <unitname + " created">
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String registerUnit(String UnitName, String UnitType, String AgentName, String CreatorName) throws THOMASException, SQLException
	{

		return this.registerUnit(UnitName, UnitType, null, AgentName, CreatorName);
	}
	/**
	 * Creates a new empty unit in the organization, with a specific type, parent unit and creatorName.
	 * @param UnitName Identifier of the organization unit
	 * @param UnitType Indicates the topology of the new unit: (i) Hierarchy, (ii)Team, (iii) Flat.
	 * @param ParentUnitName Identifier of the parent organizational unit
	 * @param AgentName Identifier of the agent
	 * @param CreatorName Identifier of the new role with creator position
	 * @return Returns <unitname +  created>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String registerUnit(String UnitName, String UnitType, String ParentUnitName, String AgentName, String CreatorName) throws THOMASException, SQLException
	{

		if (checkParameter(CreatorName) && checkParameter(UnitName))
		{

			if (!dbInterface.checkUnit(UnitName))
			{
				if (ParentUnitName != null)
				{
					if (!dbInterface.checkUnit(ParentUnitName))
					{
						throw new THOMASException("Not found. Parent unit "+ ParentUnitName + " not found.");
					}
				}
				else
				{
					ParentUnitName = "virtual";
				}
				//TODO comprobar las normas. Por ahora se acepta siempre

				if (dbInterface.checkPositionInUnit(AgentName,"creator",ParentUnitName))
				{

					return dbInterface.createUnit(UnitName, UnitType, ParentUnitName, AgentName, CreatorName);
				}
				else
				{
					throw new THOMASException("Not allowed. The agent does not play any role with creator position in the parent unit.");
				}

			}
			else
			{
				throw new THOMASException("Invalid. Unit "+ UnitName + " exists.");
			}
		}
		throw new THOMASException("Invalid. Empty parameters.");
	}
	/**
	 * Method used for deleting a unit in the organization
	 * 
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns <unitName + deleted>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String deregisterUnit(String UnitName, String AgentName) throws THOMASException, SQLException
	{

		boolean play = false;

		if (checkParameter(UnitName))
		{
			if (dbInterface.checkUnit(UnitName) && !UnitName.equals("virtual"))
			{
				//TODO control de normas.
				if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
				{
					play= true;
				}

				ArrayList<String> parentsUnit = dbInterface.getParentsUnit(UnitName);

				for(String parentUnit : parentsUnit)
				{
					if (dbInterface.checkPositionInUnit(AgentName, "creator", parentUnit))
					{
						play= true;
					}
				}


				if (play)
				{

					if (dbInterface.checkNoCreatorAgentsInUnit(UnitName))
					{
						throw new THOMASException("Not allowed. There are agents in unit playing roles with position different from creator.");
					}
					else
					{

						if (dbInterface.checkSubUnits(UnitName))
						{
							throw new THOMASException("Not allowed. There are subunits in unit "+ UnitName);
						}
						else
						{

							return dbInterface.deleteUnit(UnitName, AgentName);

						}
					}
				}
				else
				{
					throw new THOMASException("Not allowed. The agent does not play any role with creator position in the unit or the parent unit.");
				}
			}
			else
			{
				throw new THOMASException("Not found. Unit "+ UnitName + " not found or is the virtual unit.");
			}
		}
		throw new THOMASException("Invalid. Empty parameters.");

	}

	/**
	 * Method used for registering a new role inside a unit.
	 * 
	 * @param RoleName Identifier of the new role
	 * @param UnitName Identifier of the organizational unit
	 * @param Accesibility Considers two types of roles: internal or external
	 * @param Visibility Position inside the unit, such as member,supervisor or subordinate.
	 * @param Position Is defined (public) or from inside (private)
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + created>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String registerRole(String RoleName, String UnitName, String Accesibility, String Visibility, String Position, String AgentName) throws THOMASException, SQLException
	{
		String unitType = "";

		//Chekeo de parametros.
		if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(Accesibility) && checkParameter(Visibility) && checkParameter(Position))
		{
			if (dbInterface.checkUnit(UnitName))
			{
				if (!dbInterface.checkRole(RoleName, UnitName))
				{
					unitType = dbInterface.getUnitType(UnitName);

					if (unitType.equals("hierarchy"))
					{
						if (!Position.equals("supervisor") && !Position.equals("subordinate") && !Position.equals("creator"))
						{
							throw new THOMASException("Invalid. Position "+ Position + " not valid. Uses supervisor, subordinate or creator." );
						}

					}
					else if (unitType.equals("team") || unitType.equals("flat"))
					{

						if (!Position.equals("member") && !Position.equals("creator"))
						{
							throw new THOMASException("Invalid. Position "+ Position + " not valid. Uses member or creator." );
						}

					}
					else
					{
						throw new THOMASException("Invalid. Unit type not valid." );
					}
					//TODO control de normas.

					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						if (unitType.equals("hierarchy"))
						{

							if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName))
							{
								return dbInterface.createRole(RoleName, UnitName, Accesibility, Visibility, Position);

							}
							else
							{
								throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or supervisor position." );
							}


						}else if (unitType.equals("team") || unitType.equals("flat"))
						{

							if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName))
							{
								return dbInterface.createRole(RoleName, UnitName, Accesibility, Visibility, Position);

							}
							else
							{
								throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or member position." );
							}
						}else
						{
							throw new THOMASException("Invalid. Unit type not valid." );
						}
					}
					else
					{
						if (unitType.equals("flat"))
						{

							if (dbInterface.checkPosition(AgentName, "creator"))
							{
								return dbInterface.createRole(RoleName, UnitName, Accesibility, Visibility, Position);

							}
							else
							{
								throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator position." );
							}


						}
						else
						{
							throw new THOMASException("Not allowed. The agent "+ AgentName + " is not inside the unit." );
						}
					}
				}
				else
				{
					throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");			}
			}
			else
			{
				throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
			}

		}
		throw new THOMASException("Invalid. Empty parameters.");

	}

	/**
	 * Method used to deregister a role 
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + deleted>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String deregisterRole(String RoleName, String UnitName, String AgentName) throws THOMASException, SQLException
	{

		String unitType = "";

		if (checkParameter(RoleName) && checkParameter(UnitName))
		{
			if (dbInterface.checkUnit(UnitName)) 
			{
				if (dbInterface.checkRole(RoleName, UnitName))
				{
					//TODO Control de normas.
					if (!dbInterface.checkTargetRoleNorm(RoleName, UnitName))
					{
						if (!dbInterface.checkPlayedRoleInUnit(RoleName, UnitName))
						{
							unitType = dbInterface.getUnitType(UnitName);

							if (dbInterface.checkAgentInUnit(AgentName, UnitName))
							{
								if (unitType.equals("hierarchy"))
								{

									if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName))
									{
										return dbInterface.deleteRole(RoleName, UnitName, AgentName);

									}
									else
									{
										throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or supervisor position." );
									}
								}
								else if (unitType.equals("team") || unitType.equals("flat"))
								{

									if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName))
									{
										return dbInterface.deleteRole(RoleName, UnitName, AgentName);

									}
									else
									{
										throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or member position." );
									}
								}else
								{
									throw new THOMASException("Invalid. Unit type not valid." );
								}


							}
							else
							{
								if (unitType.equals("flat"))
								{
									if (dbInterface.checkPosition(AgentName, "creator"))
									{
										return dbInterface.deleteRole(RoleName, UnitName, AgentName);

									}
									else
									{
										throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator position." );
									}
								}
								else
								{
									throw new THOMASException("Not allowed. The agent "+ AgentName + " is not inside the unit." );
								}
							}
						}
						else
						{

							throw new THOMASException("Not allowed. The role is played by some agents.");
						}
					}
					else
					{
						throw new THOMASException("Not allowed. The role contains associated norms.");
					}
				}
				else
				{
					throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");
				}
			}
			else
			{
				throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
			}

		}
		throw new THOMASException("Invalid. Empty parameters.");

	}

	/**
	 * Method used for acquiring a role in a specific unit.
	 * @param UnitName Identifier of the organizational unit
	 * @param RoleName Identifier of the role
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + acquired>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String AcquireRole(String RoleName,String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result;


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRole(RoleName, UnitName))
			{
				//TODO Control de normas.
				if (dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
				{
					//TODO Corregir ingles.
					throw new THOMASException("Not allowed. The agent "+ AgentName + " is already playing the role.");
				}
				else
				{
					ArrayList<String> informRole = dbInterface.getInformRole(RoleName, UnitName);
					String accessibility = informRole.get(0);
					String visibility = informRole.get(1);
					String position = informRole.get(2);


					if (!dbInterface.checkPositionInUnit(AgentName, "member", UnitName) && 
							!dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) &&
							!dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName))
					{
						//Solamente tiene el rol subordinate.
						if (!position.equals("subordinate") && !position.equals("member"))
						{
							throw new THOMASException("Not allowed. The position role cannot be creator or supervisor.");
						}


					}

					if (accessibility.equals("external"))
					{
						result = dbInterface.acquireRole(UnitName, RoleName, AgentName);


					}
					else if (visibility.equals("public"))
					{
						result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

					}
					else
					{
						if (dbInterface.checkAgentInUnit(AgentName, UnitName))
						{
							result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. Agent "+ AgentName + " is not inside the unit.");
						}
					}

				}
			}//checkRole
			else
			{
				throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}


		return result;
	}

	/**
	 * Method used for an agent to leave a role in a unit.
	 * 
	 * @param UnitName Identifier of the unit
	 * @param RoleName Identifier of the role
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + left>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String leaveRole(String RoleName, String UnitName, String AgentName) throws THOMASException, SQLException
	{

		if (checkParameter(RoleName) && checkParameter(UnitName))
		{
			if (dbInterface.checkUnit(UnitName))
			{
				if (dbInterface.checkRole(RoleName, UnitName))
				{
					//TODO Control de normas.
					if (!dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
					{
						//TODO Corregir ingles.
						throw new THOMASException("Not allowed. The agent "+ AgentName + " not play the role "+RoleName+ ".");
					}
					else
					{
						return dbInterface.leaveRole(UnitName, RoleName, AgentName);


					}
				}
				else
				{
					throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");
				}
			}
			else
			{
				throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
			}
		}
		throw new THOMASException("Invalid. Empty parameters.");
	}

	/**
	 * Method used in order to assign a role to an agent inside a unit 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param TargetAgentName Identifier of the target agent 
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + acquired>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String allocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRole(RoleName, UnitName))
			{

				if (TargetAgentName.equals(AgentName))
				{
					throw new THOMASException("Invalid. The TargetAgentName is the same than AgentName.");
				}

				//TODO Control de normas.
				String type = dbInterface.getUnitType(UnitName);

				if (type.equals("hierarchy"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
						{
							result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position supervisor or creator in unit "+ UnitName+".");
						}
					}
					else
					{
						throw new THOMASException("Not allowed. The Agent "+ AgentName + " is not inside the unit.");
					}
				}else if (type.equals("team"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

					}
					else
					{
						throw new THOMASException("Not allowed. The Agent "+ AgentName + " is not inside the unit.");
					}
				}else if (type.equals("flat"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{

						if (dbInterface.checkPositionInUnit(AgentName, "member", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
						{
							result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position member or creator in unit "+ UnitName+".");
						}

					}
					else
					{
						if (dbInterface.checkPosition(AgentName, "creator"))
						{

							result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position creator.");
						}
					}
				}
				else
				{
					throw new THOMASException("Invalid. Unit type not valid." );
				}
			}
			else
			{
				throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;
	}


	/**
	 * Method used in order to remove a specific role to an agent inside a unit
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param TargetAgentName Identifier of the target agent
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + left>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRole(RoleName, UnitName))
			{
				//TODO Falta por hacer la comprobación del targetName, por ahora no se dispone de suficiente información para ello.
				if (TargetAgentName.equals(AgentName))
				{
					throw new THOMASException("Not allowed. The TargetAgentName is the same than AgentName.");
				}
				//TODO Control de normas
				String type = dbInterface.getUnitType(UnitName);

				if (type.equals("hierarchy"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
						{
							result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position supervisor or creator in unit "+ UnitName+".");
						}
					}
					else
					{
						throw new THOMASException("Not allowed. The Agent "+ AgentName + " is not inside the unit.");
					}
				}
				else if (type.equals("team"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);

					}
					else
					{
						throw new THOMASException("Not allowed. The Agent "+ AgentName + " is not inside the unit "+UnitName+".");
					}
				}else if (type.equals("flat"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{

						if (dbInterface.checkPositionInUnit(AgentName, "member", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
						{
							result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position member or creator in unit "+UnitName+".");
						}

					}
					else
					{
						if (dbInterface.checkPosition(AgentName, "creator"))
						{
							result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position creator.");
						}
					}

				}
				else
				{
					throw new THOMASException("Invalid. Unit type not valid." );	
				}
			}
			else
			{
				throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;

	}

	/**
	 * Method used in order to change the parent unit. 
	 * @param UnitName Identifier of the unit
	 * @param ParentName Identifier of the parent unit
	 * @param AgentName Identifier of the agent
	 * @return Returns <unitName + jointed to parentName>
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String jointUnit(String UnitName, String ParentName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkUnit(ParentName))
			{
				if (UnitName.equals(ParentName))
				{
					throw new THOMASException("Invalid. The parent unit is the same than unit.");
				}
				//TODO control de normas
				if (dbInterface.checkAgentInUnit(AgentName, UnitName))
				{
					if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
					{
						if (dbInterface.checkPositionInUnit(AgentName, "creator", ParentName))
						{

							result = dbInterface.jointUnit(UnitName, ParentName);

						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position creator inside the parent unit.");
						}
					}
					else
					{
						throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position creator inside the unit.");
					}
				}
				else
				{
					throw new THOMASException("Not allowed. The Agent "+ AgentName + " is not inside the unit.");
				}

			}
			else
			{
				throw new THOMASException("Not found. Parent unit "+ ParentName + " not found.");
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;
	}

	/**
	 * Method used for requesting the list of roles and units where an agent is, given the 
	 * specific moment.
	 * @param RequestedAgentName Identifier of the agent requested 
	 * @param AgentName Identifier of the agent
	 * @return Returns a set of tuples formed by < roleName , UnitName > and separated by -  
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informAgentRole(String RequestedAgentName,String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();


		if (dbInterface.checkAgent(RequestedAgentName))
		{
			//TODO Control de normas

			methodResult = dbInterface.getInformAgentRole(RequestedAgentName, AgentName);

			for (ArrayList<String> agentPair : methodResult)
			{	//< RoleName , UnitName >
				result = result + "< " + agentPair.get(0)+" , "+agentPair.get(1)+" > - "; 
			}
		}
		else
		{
			throw new THOMASException("Not found. The agent "+ RequestedAgentName + " not exists.");
		}

		return result;
	}


	/**
	 *  Method used for requesting the list of entities that are members of a specific unit.
	 * If a role is specified only the members playing this role are detailed. If a position is specified 
	 * only the members playing this position are detailed.
	 * @param UnitName Identifier of the unit
	 * @param RoleName Identifier of the role
	 * @param PositionValue Position inside the unit, such as member, supervisor or subordinate
	 * @param AgentName Identifier of the agent
	 * @return Returns a set of tuples formed by < agentName , roleName > and separated by - 
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informMembers(String UnitName, String RoleName, String PositionValue, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		ArrayList<String> arrayResult = new ArrayList<String>();
		Flags flag = Flags.CASE_A;


		ArrayList<String> roleInfo = dbInterface.getInformRole(RoleName, UnitName);

		String visibility = roleInfo.get(1);
		String position = roleInfo.get(2);

		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkAgent(AgentName))
			{
				if (RoleName != null)
				{//No contien el roleName
					if (!RoleName.equals(""))
					{
						if (!dbInterface.checkRole(RoleName, UnitName))
						{
							throw new THOMASException("Not found. Role "+ RoleName + " is not inside the unit.");
						}
						else
						{
							flag = Flags.CASE_B;

							if (!visibility.equals("public"))
							{
								if (!dbInterface.checkAgentInUnit(AgentName, UnitName));
								{
									throw new THOMASException("Not Allowed. Agent "+ AgentName + " is not inside the unit.");
								}
							}
						}
					}
				}


				if (PositionValue != null)
				{
					if (!PositionValue.equals(""))
					{
						if (flag == Flags.CASE_A)
						{
							flag = Flags.CASE_C;
						}
						else
						{
							flag = Flags.CASE_D;
						}
						String unitType = dbInterface.getUnitType(UnitName);

						if (unitType.equals("flat") || unitType.equals("team"))
						{

							if (!position.equals("member") && !position.equals("creator"))
							{
								throw new THOMASException("Invalid. Role "+ RoleName + " position is not member or creator.");
							}		
						}else if (unitType.equals("hierarchy"))
						{
							if (!position.equals("supervisor") && !position.equals("creator") && !position.equals("subordinate"))
							{
								throw new THOMASException("Invalid. Role "+ RoleName + " position is not supervisor, subordinate or creator.");
							}
						}
						else
						{
							throw new THOMASException("Invalid. Invalid role position.");
						}
					}
				}

				//TODO Control de normas.

				switch (flag){

				case CASE_A: 
					methodResult = dbInterface.getAgentsRolesInUnit(UnitName, AgentName);

					for (ArrayList<String> agentPair : methodResult)
					{	//< RequestesAgentName , RoleName >
						result = result + "< " + agentPair.get(0)+" , "+agentPair.get(1)+" > - "; 
					}
					break;//No se incluye ni el parametro role name ni position name. 
				case CASE_B: 

					arrayResult = dbInterface.getAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);

					for(String agent : arrayResult)
					{	//< agentName , roleName >
						result = result + "< " +agent+" , "+ RoleName+" > - ";	
					}

					break;//Solo se incluye el roleName
				case CASE_C: 

					methodResult = dbInterface.getAgentsPlayingPositionInUnit(UnitName, PositionValue);
					for (ArrayList<String> agentPair : methodResult)
					{
						//< requestedAgentNameX , roleNameY >
						result = result + "< " + agentPair.get(0)+" , "+agentPair.get(1)+" > - "; 
					}
					break;//No se incluye el rolename pero si el positionName
				case CASE_D: 

					arrayResult = dbInterface.getAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);

					for(String agent : arrayResult)
					{	//< agentName , roleName >
						result = result + "< " +agent+" , "+ RoleName+" > - ";	
					}
					break; //Se incluyen todos los parametros
				}


			}
			else
			{
				throw new THOMASException("Not found. Agent "+ AgentName + " not exists.");
			}

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;
	}


	/**
	 * Method used for requesting the number of current members of a specific unit. If a
	 * 	role is specified only the members playing that role are taken into account. If a position
	 *  is specified only the members playing that position are taken into account.
	 * 
	 * @param UnitName Identifier of the unit
	 * @param RoleName Identifier of the role
	 * @param PositionValue Position inside the unit, such as member, supervisor or subordinate
	 * @param AgentName Identifier of the name
	 * @return Returns a quantity of members
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String quantityMembers(String UnitName, String RoleName, String PositionValue, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		int intResult = 0;
		Flags flag = Flags.CASE_A;


		ArrayList<String> roleInfo = dbInterface.getInformRole(RoleName, UnitName);

		String visibility = roleInfo.get(1);
		String position = roleInfo.get(2);

		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkAgent(AgentName))
			{
				if (RoleName != null)
				{
					if (!RoleName.equals(""))
					{
						if (!dbInterface.checkRole(RoleName, UnitName))
						{
							throw new THOMASException("Not found. Role "+ RoleName + " is not inside the unit.");
						}
						else
						{
							flag = Flags.CASE_B;

							if (!visibility.equals("public"))
							{
								if (!dbInterface.checkAgentInUnit(AgentName, UnitName));
								{
									throw new THOMASException("Not Allowed. Agent "+ AgentName + " is not inside the unit.");
								}
							}
						}
					}
				}


				if (PositionValue != null)
				{
					if (!PositionValue.equals(""))
					{
						if (flag == Flags.CASE_A)
						{
							flag = Flags.CASE_C;
						}
						else
						{
							flag = Flags.CASE_D;
						}
						String unitType = dbInterface.getUnitType(UnitName);

						if (unitType.equals("flat") || unitType.equals("team"))
						{


							if (!position.equals("member") && !position.equals("creator"))
							{
								throw new THOMASException("Invalid. Role "+ RoleName + " position is not member or creator.");
							}		
						}else if (unitType.equals("hierarchy"))
						{

							if (!position.equals("supervisor") && !position.equals("creator") && !position.equals("subordinate"))
							{
								throw new THOMASException("Invalid. Role "+ RoleName + " position is not supervisor, subordinate or creator.");
							}
						}
						else
						{
							throw new THOMASException("Invalid. Invalid role position.");
						}
					}
				}

				//TODO Control de normas.

				switch (flag){

				case CASE_A: 

					intResult = dbInterface.getQuantityAgentsRolesInUnit(UnitName, AgentName);

					result = intResult +"";

					break;//No se incluye ni el parametro role name ni position name. 
				case CASE_B: 

					intResult = dbInterface.getQuantityAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);
					result = intResult +"";
					break;//Solo se incluye el roleName
				case CASE_C: 

					intResult = dbInterface.getQuantityAgentsPlayingPositionInUnit(UnitName, PositionValue, AgentName);
					result = intResult +"";
					break;//No se incluye el rolename pero si el postionName
				case CASE_D: 

					intResult = dbInterface.getQuantityAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);
					result = intResult +"";
					break; //Se incluyen todos los parametros
				}


			}
			else
			{
				throw new THOMASException("Not found. Agent "+ AgentName + " not exists.");
			}

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;

	}
	/**
	 * Method used for requesting information about a specific unit.
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns < UnitType , ParentName >
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informUnit(String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		ArrayList<String> arrayResult = new ArrayList<String>();
		boolean play = false;


		if (dbInterface.checkUnit(UnitName))
		{
			//TODO Control de normas.
			String unitType = dbInterface.getUnitType(UnitName);

			if (unitType.equals("team") || unitType.equals("hierarchy"))
			{
				ArrayList<String> parentsUnitName = dbInterface.getParentsUnit(UnitName);

				for(String parentUnit : parentsUnitName)
				{
					if (dbInterface.checkAgentInUnit(AgentName, parentUnit))
					{
						play = true;
					}
				}

				if (dbInterface.checkAgentInUnit(AgentName, UnitName))
				{
					play = true;
				}

				if (!play)
				{
					throw new THOMASException("Invalid. Agent "+ AgentName + " not play any rol in unit or parent unit.");
				}
			}
			else if (!unitType.equals("flat"))
			{
				throw new THOMASException("Invalid. Unit type not valid." );
			}

			arrayResult = dbInterface.getInformUnit(UnitName);
			//< UnitType , ParentName >
			result = "< "+arrayResult.get(0)+" , "+ arrayResult.get(1)+" >";

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}


		return result;
	}
	/**
	 * Method used for requesting the list of roles that have been registered inside a unit.
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns < RoleName , Accessibility , Visibility , Position >
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informUnitRoles(String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();


		if (dbInterface.checkUnit(UnitName))
		{
			//TODO Control de normas
			methodResult = dbInterface.getInformUnitRoles(UnitName, AgentName);		

			for (ArrayList<String> agentPair : methodResult)
			{
				//< RoleName , Accessibility , Visibility , Position >
				result = result + "< " + agentPair.get(0)+" , "+agentPair.get(1)+" , "+ agentPair.get(2)+ " , "+ agentPair.get(3)+ " > - "; 
			}

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;
	}

	/**
	 * Method used for requesting information about a specific role.
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns < Accessibility - Visibility - Position >
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informRole(String RoleName, String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		ArrayList<String> arrayResult = new ArrayList<String>();
		boolean play = false;

		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRole(RoleName, UnitName))
			{
				//TODO Control de normas

				String unitType = dbInterface.getUnitType(UnitName);

				if (unitType.equals("team") || unitType.equals("hierarchy"))
				{
					ArrayList<String> parentsUnitName = dbInterface.getParentsUnit(UnitName);

					for(String parentUnit : parentsUnitName)
					{
						if (dbInterface.checkAgentInUnit(AgentName, parentUnit))
						{
							play = true;
						}
					}

					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						play = true;
					}

					if (!play)
					{
						throw new THOMASException("Invalid. Agent "+ AgentName + " not play any rol in unit or parent unit.");
					}
				}
				else if (!unitType.equals("flat"))
				{
					throw new THOMASException("Invalid. Unit type not valid." );
				}

				arrayResult = dbInterface.getInformRole(RoleName, UnitName);

				//< Accessibility - Visibility - Position >
				result = "< "+ arrayResult.get(0)+ " , "+ arrayResult.get(1)+ " , "+ arrayResult.get(2)+ " >";




			}
			else
			{
				throw new THOMASException("Not allowed. Role "+ RoleName + " is not registered in the unit.");
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

		return result;
	}

}
