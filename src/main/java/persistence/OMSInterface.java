package persistence;

import java.sql.SQLException;

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
	 * Creates a new empty unit in the organization, with a specific type and creatorName.
	 * 
	 * @param UnitName
	 * @param UnitType
	 * @param AgentName
	 * @param CreatorName
	 * @return Returns <unitname + " created">
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String registerUnit(String UnitName, String UnitType, String AgentName, String CreatorName) throws THOMASException, SQLException
	{
		String result = "";

		try{
			this.registerUnit(UnitName, UnitType, null, AgentName, CreatorName);
		}
		catch(THOMASException e)
		{
			throw e;
		}
		catch(SQLException e)
		{
			throw e;
		}
		return result;
	}
	/**
	 * Creates a new empty unit in the organization, with a specific type, parent unit and creatorName.
	 * @param UnitName
	 * @param UnitType
	 * @param ParentUnitName
	 * @param AgentName
	 * @param CreatorName
	 * @return Returns <unitname + " created">
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String registerUnit(String UnitName, String UnitType, String ParentUnitName, String AgentName, String CreatorName) throws THOMASException, SQLException
	{

		String result="";
		try
		{
			if (dbInterface.checkUnit(UnitName))
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
					result = dbInterface.createUnit(UnitName, UnitType, ParentUnitName, AgentName, CreatorName);

					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
				}
				else
				{
					throw new THOMASException("Not allowed. The agent does not play any role with creator position in the parent unit.");
				}

			}
			else
			{
				throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
			}

		}catch(SQLException e)
		{
			throw e;
		}

		return result;
	}
	/**
	 * Method used for deleting a unit in the organization
	 * 
	 * @param UnitName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String deregisterUnit(String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result="";
		boolean play = false;


		if (dbInterface.checkUnit(UnitName) && !dbInterface.checkVirtualUnit(UnitName))
		{
			//TODO control de normas.
			if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
			{
				play= true;
			}
			//TODO en un futuro se deberan ver para todas las unidades padre.
			String parentsUnit = dbInterface.getParentsUnit(UnitName);

			if (dbInterface.checkPositionInUnit(AgentName, "creator", parentsUnit))
			{
				play= true;
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
						throw new THOMASException("Not allowed. There are subunits in unit ."+ UnitName);
					}
					else
					{

						result = dbInterface.deleteUnit(UnitName, AgentName);

						if (result.contains("Error"))
						{
							throw new THOMASException(result);
						}
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
		return result;
	}

	/**
	 * Method used for registering a new role inside a unit.
	 * 
	 * @param RoleName
	 * @param UnitName
	 * @param Accessibility
	 * @param Visibility
	 * @param Position
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String registerRole(String RoleName, String UnitName, String Accessibility, String Visibility, String Position, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		String unitType = "";


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
							result = dbInterface.createRole(RoleName, UnitName, Accessibility, Visibility, Position);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
						}
						else
						{
							throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or superversior position." );
						}


					}else if (unitType.equals("team") || unitType.equals("flat"))
					{

						if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName))
						{
							result = dbInterface.createRole(RoleName, UnitName, Accessibility, Visibility, Position);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
							result = dbInterface.createRole(RoleName, UnitName, Accessibility, Visibility, Position);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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

		return result;
	}

	/**
	 * Method used to deregister a role 
	 * 
	 * @param RoleName
	 * @param UnitName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String deregisterRole(String RoleName, String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		String unitType = "";


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
									result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
									if (result.contains("Error"))
									{
										throw new THOMASException(result);
									}
								}
								else
								{
									throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or superversior position." );
								}
							}
							else if (unitType.equals("team") || unitType.equals("flat"))
							{

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName))
								{
									result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
									if (result.contains("Error"))
									{
										throw new THOMASException(result);
									}
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
									result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
									if (result.contains("Error"))
									{
										throw new THOMASException(result);
									}
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
		return result;
	}

	/**
	 * Method used for acquiring a role in a specific unit.
	 * @param UnitName
	 * @param RoleName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String AcquireRole(String UnitName, String RoleName, String AgentName) throws THOMASException, SQLException
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
					String informRole = dbInterface.getInformRole(RoleName, UnitName, AgentName);
					String position = informRole.split(" ")[5];
					String visibility = informRole.split(" ")[3];
					String accessibility = informRole.split(" ")[1];

					if (!dbInterface.checkPositionInUnit(AgentName, "member", UnitName) && 
							!dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) &&
							!dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName))
					{
						//Solamente tiene el rol subordinate.



						if (informRole.contains("Error"))
						{
							throw new THOMASException(informRole);
						}
						//TODO Parseado del resultado, verificar que es correcto.
						if (!position.equals("subordinate") && !position.equals("member"))
						{
							throw new THOMASException("Not allowed. The position role cannot be creator or supervisor.");
						}


					}

					if (accessibility.equals("external"))
					{
						result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

						if (result.contains("Error"))
						{
							throw new THOMASException(result);
						}
					}
					else if (visibility.equals("public"))
					{
						result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

						if (result.contains("Error"))
						{
							throw new THOMASException(result);
						}
					}
					else
					{
						if (dbInterface.checkAgentInUnit(AgentName, UnitName))
						{
							result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
	 * @param UnitName
	 * @param RoleName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String leaveRole(String UnitName, String RoleName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";

		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRole(RoleName, UnitName))
			{
				//TODO Control de normas.
				if (!dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
				{
					//TODO Corregir ingles.
					throw new THOMASException("Not allowed. The agent "+ AgentName + " not play the role.");
				}
				else
				{
					result = dbInterface.leaveRole(UnitName, RoleName, AgentName);

					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}

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
	 * Method used in order to assign a role to an agent inside a unit 
	 * @param RoleName
	 * @param UnitName
	 * @param TargetAgentName
	 * @param AgentName
	 * @return
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
							result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
						result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);

						if (result.contains("Error"))
						{
							throw new THOMASException(result);
						}
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
							result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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

							result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
	 * @param RoleName
	 * @param UnitName
	 * @param TargetAgentName
	 * @param AgentName
	 * @return
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
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
						if (result.contains("Error"))
						{
							throw new THOMASException(result);
						}
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
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
	 * @param UnitName
	 * @param ParentName
	 * @param AgentName
	 * @return
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

							result = dbInterface.jointUnit(UnitName, ParentName, AgentName);
							if (result.contains("Error"))
							{
								throw new THOMASException(result);
							}
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
	 * @param RequestedAgentName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informAgentRole(String RequestedAgentName,String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		if (dbInterface.checkAgent(RequestedAgentName))
		{
			//TODO Control de normas

			result = dbInterface.getInformAgentRole(RequestedAgentName, AgentName);
			if (result.contains("Error"))
			{
				throw new THOMASException(result);
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
	 * @param UnitName
	 * @param RoleName
	 * @param PositionValue
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informMembers(String UnitName, String RoleName, String PositionValue, String AgentName) throws THOMASException, SQLException
	{
		String result = "";

		Flags flag = Flags.CASE_A;

		String roleInfo = dbInterface.getInformRole(RoleName, UnitName, AgentName);
		//TODO Comprobar el parseado.
		String visibility = roleInfo.split(" ")[3];
		String position = roleInfo.split(" ")[5];

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

					result = dbInterface.getAgentsRolesInUnit(UnitName, AgentName);
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
					break;//No se incluye ni el parametro role name ni position name. 
				case CASE_B: 

					result = dbInterface.getAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);
					result = result + RoleName;
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
					break;//Solo se incluye el roleName
				case CASE_C: 

					result = dbInterface.getAgentsPlayingPositionInUnit(UnitName, PositionValue, AgentName);
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
					break;//No se incluye el rolename pero si el postionName
				case CASE_D: 

					result = dbInterface.getAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);
					result = result + RoleName;
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
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
	 * @param UnitName
	 * @param RoleName
	 * @param PositionValue
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String quantityMembers(String UnitName, String RoleName, String PositionValue, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		Flags flag = Flags.CASE_A;

		String roleInfo = dbInterface.getInformRole(RoleName, UnitName, AgentName);
		//TODO Comprobar el parseado.
		String visibility = roleInfo.split(" ")[3];
		String position = roleInfo.split(" ")[5];

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

					result = dbInterface.getQuantityAgentsRolesInUnit(UnitName, AgentName);
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
					break;//No se incluye ni el parametro role name ni position name. 
				case CASE_B: 

					result = dbInterface.getQuantityAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
					break;//Solo se incluye el roleName
				case CASE_C: 

					result = dbInterface.getQuantityAgentsPlayingPositionInUnit(UnitName, PositionValue, AgentName);
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
					}
					break;//No se incluye el rolename pero si el postionName
				case CASE_D: 

					result = dbInterface.getQuantityAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);
					if (result.contains("Error"))
					{
						throw new THOMASException(result);
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
	 * Method used for requesting information about a specific unit.
	 * @param UnitName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informUnit(String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";

		if (dbInterface.checkUnit(UnitName))
		{
			//TODO Control de normas.
			String unitType = dbInterface.getUnitType(UnitName);

			if (unitType.equals("team") || unitType.equals("hierarchy"))
			{
				String ParentUnitName = dbInterface.getParentsUnit(UnitName);

				if (!dbInterface.checkAgentInUnit(AgentName, UnitName) && dbInterface.checkAgentInUnit(AgentName, ParentUnitName))
				{
					throw new THOMASException("Invalid. Agent "+ AgentName + " not play any rol in unit or parent unit.");
				}
			}
			else if (!unitType.equals("flat"))
			{
				throw new THOMASException("Invalid. Unit type not valid." );
			}

			result = dbInterface.getInformUnit(UnitName);

			if (result.contains("Error"))
			{
				throw new THOMASException(result);
			}

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}
		return result;
	}
	/**
	 * Method used for requesting the list of roles that have been registered inside a unit.
	 * @param UnitName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informUnitRoles(String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		if (dbInterface.checkUnit(UnitName))
		{
			//TODO Control de normas
			result = dbInterface.getInformUnitRoles(UnitName, AgentName);		
			if (result.contains("Error"))
			{
				throw new THOMASException(result);
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
	 * @param RoleName
	 * @param UnitName
	 * @param AgentName
	 * @return
	 * @throws THOMASException
	 * @throws SQLException
	 */
	public String informRole(String RoleName, String UnitName, String AgentName) throws THOMASException, SQLException
	{
		String result = "";
		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRole(RoleName, UnitName))
			{
				//TODO Control de normas

				String unitType = dbInterface.getUnitType(UnitName);

				if (unitType.equals("team") || unitType.equals("hierarchy"))
				{
					String ParentUnitName = dbInterface.getParentsUnit(UnitName);

					if (!dbInterface.checkAgentInUnit(AgentName, UnitName) && dbInterface.checkAgentInUnit(AgentName, ParentUnitName))
					{
						throw new THOMASException("Invalid. Agent "+ AgentName + " not play any rol in unit or parent unit.");
					}
				}
				else if (!unitType.equals("flat"))
				{
					throw new THOMASException("Invalid. Unit type not valid." );
				}

				result = dbInterface.getInformRole(RoleName, UnitName, AgentName);

				if (result.contains("Error"))
				{
					throw new THOMASException(result);
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

}
