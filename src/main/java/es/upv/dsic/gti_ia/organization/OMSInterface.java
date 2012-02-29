package es.upv.dsic.gti_ia.organization;

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
	 */
	public String registerUnit(String UnitName, String UnitType, String AgentName, String CreatorName)
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
	 */
	public String registerUnit(String UnitName, String UnitType, String ParentUnitName, String AgentName, String CreatorName)
	{

		String resultXML="<response>\n<serviceName>RegisterUnit</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
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

					//--------------------------------------------------------------------------------
					//------------------------- Checking domain-dependent norms ----------------------
					//--------------------------------------------------------------------------------
					//TODO
					//--------------------------------------------------------------------------------
					//------------------------- Checking structural norms ----------------------------
					//--------------------------------------------------------------------------------

					if (dbInterface.checkPositionInUnit(AgentName,"creator",ParentUnitName))
					{

						String result = dbInterface.createUnit(UnitName, UnitType, ParentUnitName, AgentName, CreatorName);
						
						resultXML+="<status>Ok</status>\n";
						resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
						resultXML+="</response>";	
						
						return resultXML;
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

		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}
	/**
	 * Method used for deleting a unit in the organization
	 * 
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns <unitName + deleted>
	 */
	public String deregisterUnit(String UnitName, String AgentName)
	{

		boolean play = false;
		String resultXML="<response>\n<serviceName>DeregisterUnit</serviceName>\n";
		
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(UnitName))
			{
				if (dbInterface.checkUnit(UnitName) && !UnitName.equals("virtual"))
				{
					//--------------------------------------------------------------------------------
					//------------------------- Checking domain-dependent norms ----------------------
					//--------------------------------------------------------------------------------
					//TODO
					//--------------------------------------------------------------------------------
					//------------------------- Checking structural norms ----------------------------
					//--------------------------------------------------------------------------------
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

								String result = dbInterface.deleteUnit(UnitName, AgentName);
								
								resultXML+="<status>Ok</status>\n";
								resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
								resultXML+="</response>";
								
								return resultXML;

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
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
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
	 */
	public String registerRole(String RoleName, String UnitName, String Accesibility, String Visibility, String Position, String AgentName)
	{
		String unitType = "";
		String resultXML="<response>\n<serviceName>RegisterRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
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
						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO

						if (dbInterface.checkAgentInUnit(AgentName, UnitName))
						{
							if (unitType.equals("hierarchy"))
							{

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName))
								{
									String result = dbInterface.createRole(RoleName, UnitName, Accesibility, Visibility, Position);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or supervisor position." );
								}


							}else if (unitType.equals("team") || unitType.equals("flat"))
							{

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName))
								{
									String result = dbInterface.createRole(RoleName, UnitName, Accesibility, Visibility, Position);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

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
									String result = dbInterface.createRole(RoleName, UnitName, Accesibility, Visibility, Position);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

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
						throw new THOMASException("Not allowed. Role "+ RoleName + " is already registered in the unit.");			}
				}
				else
				{
					throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
				}

			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}

	/**
	 * Method used to deregister a role 
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + deleted>
	 */
	public String deregisterRole(String RoleName, String UnitName, String AgentName)
	{

		String unitType = "";
		String resultXML="<response>\n<serviceName>DeregisterRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName))
			{
				if (dbInterface.checkUnit(UnitName)) 
				{
					if (dbInterface.checkRole(RoleName, UnitName))
					{
						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
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
											String result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
											
											resultXML+="<status>Ok</status>\n";
											resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
											resultXML+="</response>";
											
											return resultXML;

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
											String result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
											
											resultXML+="<status>Ok</status>\n";
											resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
											resultXML+="</response>";
											
											return resultXML;

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
											String result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
											
											resultXML+="<status>Ok</status>\n";
											resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
											resultXML+="</response>";
											
											return resultXML;

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
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for acquiring a role in a specific unit.
	 * @param UnitName Identifier of the organizational unit
	 * @param RoleName Identifier of the role
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + acquired>
	 */
	public String acquireRole(String RoleName,String UnitName, String AgentName)
	{


		boolean contains = false;
		String resultXML="<response>\n<serviceName>AcquireRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName))
			{
				if (dbInterface.checkUnit(UnitName))
				{
					if (dbInterface.checkRole(RoleName, UnitName))
					{
						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
						//--------------------------------------------------------------------------------
						//------------------------- Checking structural norms ----------------------------
						//--------------------------------------------------------------------------------
						if (dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
						{

							throw new THOMASException("Not allowed. The agent "+ AgentName + " is already playing the role.");
						}
						else
						{
							ArrayList<String> informRole = dbInterface.getInformRole(RoleName, UnitName);
							String accesibility = informRole.get(0);
							String position = informRole.get(2);

							if (dbInterface.checkAgentInUnit(AgentName, UnitName))
							{
								ArrayList<ArrayList<String>> rolesInUnit = dbInterface.getInformAgentRolesPlayedInUnit(UnitName, AgentName);

								for (ArrayList<String> role : rolesInUnit)
								{
									if (role.contains("creator") || role.contains("supervisor") || role.contains("member"))
									{
										contains = true;
									}

								}

								if (contains)
								{
									String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;
								}
								else
								{
									if (!position.equals("creator") && 
											!position.equals("supervisor"))
									{
										String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
										
										resultXML+="<status>Ok</status>\n";
										resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
										resultXML+="</response>";
										
										return resultXML;
										
									}
									else
										throw new THOMASException("Not allowed. The agent not have enough permissions for acquire role with position creator or supervisor.");

								}


							}
							else
							{
								if (accesibility.equals("external"))
								{
									String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;


								}
								else if (dbInterface.checkAgentInUnit(AgentName, dbInterface.getParentsUnit(UnitName).get(0)))//Check agent in parent unit
								{
									String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. Agent "+ AgentName + " is not inside the unit or parent unit.");
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

			}
			throw new THOMASException("Invalid. Empty parameters.");

		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}

	}

	/**
	 * Method used for an agent to leave a role in a unit.
	 * 
	 * @param UnitName Identifier of the unit
	 * @param RoleName Identifier of the role
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + left>
	 */
	public String leaveRole(String RoleName, String UnitName, String AgentName)
	{

		String resultXML="<response>\n<serviceName>LeaveRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName))
			{
				if (dbInterface.checkUnit(UnitName))
				{
					if (dbInterface.checkRole(RoleName, UnitName))
					{
						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
						if (!dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
						{
							//TODO Corregir ingles.
							throw new THOMASException("Not allowed. The agent "+ AgentName + " not play the role "+RoleName+ ".");
						}
						else
						{
							String result = dbInterface.leaveRole(UnitName, RoleName, AgentName);
							
							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
							resultXML+="</response>";
							
							return resultXML;


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
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}

	/**
	 * Method used in order to assign a role to an agent inside a unit 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param TargetAgentName Identifier of the target agent 
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + acquired>
	 */
	public String allocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName)
	{

		String resultXML="<response>\n<serviceName>AllocateRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(TargetAgentName))
			{

				if (dbInterface.checkUnit(UnitName))
				{
					if (dbInterface.checkRole(RoleName, UnitName))
					{

						if (TargetAgentName.equals(AgentName))
						{
							throw new THOMASException("Invalid. The TargetAgentName is the same than AgentName.");
						}

						if (dbInterface.checkAgentPlaysRole(TargetAgentName, RoleName, UnitName))
						{
							throw new THOMASException("Not allowed. The agent "+ AgentName + " is already playing the role.");
						}

						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
						//--------------------------------------------------------------------------------
						//------------------------- Checking structural norms ----------------------------
						//--------------------------------------------------------------------------------

						String type = dbInterface.getUnitType(UnitName);

						if (type.equals("hierarchy"))
						{
							if (dbInterface.checkAgentInUnit(AgentName, UnitName))
							{
								if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
								{
									String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position supervisor or creator in unit "+ UnitName+".");
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
								String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);
								
								resultXML+="<status>Ok</status>\n";
								resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
								resultXML+="</response>";
								
								return resultXML;

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
									String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position member or creator in unit.");
								}

							}
							else
							{
								if (dbInterface.checkPosition(AgentName, "creator"))
								{

									String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position creator.");
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
			}
			throw new THOMASException("Invalid. Empty parameters.");

		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}


	/**
	 * Method used in order to remove a specific role to an agent inside a unit
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param TargetAgentName Identifier of the target agent
	 * @param AgentName Identifier of the agent
	 * @return Returns <roleName + left>
	 */
	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName)
	{

		String resultXML="<response>\n<serviceName>DeallocateRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(TargetAgentName))
			{

				if (dbInterface.checkUnit(UnitName))
				{
					if (dbInterface.checkRole(RoleName, UnitName))
					{
						//TODO Falta por hacer la comprobación del targetName, por ahora no se dispone de suficiente información para ello.
						if (TargetAgentName.equals(AgentName))
						{
							throw new THOMASException("Not allowed. The TargetAgentName is the same than AgentName.");
						}

						if (!dbInterface.checkAgentPlaysRole(TargetAgentName, RoleName, UnitName))
						{
							throw new THOMASException("Not allowed. The agent "+ TargetAgentName + " is not playing the role.");
						}

						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
						//--------------------------------------------------------------------------------
						//------------------------- Checking structural norms ----------------------------
						//--------------------------------------------------------------------------------
						String type = dbInterface.getUnitType(UnitName);

						if (type.equals("hierarchy"))
						{
							if (dbInterface.checkAgentInUnit(AgentName, UnitName))
							{
								if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
								{
									String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position supervisor or creator in unit "+ UnitName+".");
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
								
								String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
								
								resultXML+="<status>Ok</status>\n";
								resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
								resultXML+="</response>";
								
								return resultXML;

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
									String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position member or creator in unit "+UnitName+".");
								}

							}
							else
							{
								if (dbInterface.checkPosition(AgentName, "creator"))
								{
									String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " is not inside the unit and not play any role with position creator.");
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
			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}

	}

	/**
	 * Method used in order to change the parent unit. 
	 * @param UnitName Identifier of the unit
	 * @param ParentName Identifier of the parent unit
	 * @param AgentName Identifier of the agent
	 * @return Returns <unitName + jointed to parentName>
	 */
	public String jointUnit(String UnitName, String ParentName, String AgentName)
	{

		String resultXML="<response>\n<serviceName>JointUnit</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(ParentName))
			{
				if (dbInterface.checkUnit(UnitName))
				{
					if (dbInterface.checkUnit(ParentName))
					{
						if (UnitName.equals(ParentName))
						{
							throw new THOMASException("Invalid. The parent unit is the same than unit.");
						}

						if (UnitName.equals("virtual"))
						{
							throw new THOMASException("Not allowed. Cannot be changed the parent unit of the virtual unit.");
						}
						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
						if (dbInterface.checkAgentInUnit(AgentName, UnitName))
						{
							if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
							{
								if (dbInterface.checkPositionInUnit(AgentName, "creator", ParentName))
								{

									String result = dbInterface.jointUnit(UnitName, ParentName);
									
									resultXML+="<status>Ok</status>\n";
									resultXML+="<result>\n<description>"+result+"</description>\n</result>\n";
									resultXML+="</response>";
									
									return resultXML;

								}
								else
								{
									throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position creator inside the parent unit.");
								}
							}
							else
							{
								throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any role with position creator inside the unit.");
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
			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting the list of roles and units where an agent is, given the 
	 * specific moment.
	 * @param RequestedAgentName Identifier of the agent requested 
	 * @param AgentName Identifier of the agent
	 * @return Returns a set of tuples formed by < roleName , UnitName > and separated by -  
	 */
	public String informAgentRole(String RequestedAgentName,String AgentName)
	{

		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		String resultXML="<response>\n<serviceName>InformAgentRole</serviceName>\n";

		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RequestedAgentName))
			{
				if (dbInterface.checkAgent(RequestedAgentName))
				{
					//--------------------------------------------------------------------------------
					//------------------------- Checking domain-dependent norms ----------------------
					//--------------------------------------------------------------------------------
					//TODO

					methodResult = dbInterface.getInformAgentRole(RequestedAgentName, AgentName);

					resultXML+="<status>Ok</status>\n";
					resultXML+="<result>\n";
					for (ArrayList<String> agentPair : methodResult)
					{	//< RoleName , UnitName >
						resultXML+="<item>\n";
						resultXML+="<rolename>"+ agentPair.get(0)+"</rolename>\n";
						resultXML+="<unitname>"+ agentPair.get(1)+"</unitname>\n"; 
						resultXML+="</item>\n";
					}
					resultXML+="</result>\n";
					resultXML+="</response>";
					
					return resultXML;
				}
				else
				{
					throw new THOMASException("Not found. The agent "+ RequestedAgentName + " not exists.");
				}

			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
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
	 */
	public String informMembers(String UnitName, String RoleName, String PositionValue, String AgentName)
	{
		
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		ArrayList<String> arrayResult = new ArrayList<String>();
		Flags flag = Flags.CASE_A;
		String resultXML="<response>\n<serviceName>InformMembers</serviceName>\n";

		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(UnitName))
			{
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
									ArrayList<String> roleInfo = dbInterface.getInformRole(RoleName, UnitName);

									String visibility = roleInfo.get(1);

									flag = Flags.CASE_B;

									if (!visibility.equals("public"))
									{

										if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
										{

											throw new THOMASException("Not Allowed. Private info is not available if agent "+ AgentName + " is not inside the unit.");
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

									if (!PositionValue.equals("member") && !PositionValue.equals("creator"))
									{
										throw new THOMASException("Invalid. Role "+ RoleName + " position is not member or creator.");
									}		
								}else if (unitType.equals("hierarchy"))
								{
									if (!PositionValue.equals("supervisor") && !PositionValue.equals("creator") && !PositionValue.equals("subordinate"))
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

						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO
						//--------------------------------------------------------------------------------
						//------------------------- Checking structural norms ----------------------------
						//--------------------------------------------------------------------------------

						switch (flag){

						case CASE_A: 
							methodResult = dbInterface.getAgentsRolesInUnit(UnitName, AgentName);

							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							
							for (ArrayList<String> agentPair : methodResult)
							{	//< agentName , RoleName >			
								resultXML+="<item>\n";
								resultXML+="<agentname>"+ agentPair.get(0)+"</agentname>\n";
								resultXML+="<rolename>"+ agentPair.get(1)+"</rolename>\n"; 
								resultXML+="</item>\n";
							}
							resultXML+="</result>\n";
							resultXML+="</response>";
							break;//No se incluye ni el parametro role name ni position name. 
						case CASE_B: 

							arrayResult = dbInterface.getAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);

							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							
							for(String agent : arrayResult)
							{	//< agentName , roleName >
								resultXML+="<item>\n";
								resultXML+="<agentname>"+ agent+"</agentname>\n";
								resultXML+="<rolename>"+ RoleName+"</rolename>\n"; 
								resultXML+="</item>\n";
							}
							resultXML+="</result>\n";
							resultXML+="</response>";
							break;//Solo se incluye el roleName
						case CASE_C: 

							methodResult = dbInterface.getAgentsPlayingPositionInUnit(UnitName, PositionValue, AgentName);
							
							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							
							for (ArrayList<String> agentPair : methodResult)
							{
								//< requestedAgentNameX , roleNameY > 
								resultXML+="<item>\n";
								resultXML+="<agentname>"+ agentPair.get(0)+"</agentname>\n";
								resultXML+="<rolename>"+ agentPair.get(1)+"</rolename>\n"; 
								resultXML+="</item>\n";
							}
							resultXML+="</result>\n";
							resultXML+="</response>";
							break;//No se incluye el rolename pero si el positionName
						case CASE_D: 

							arrayResult = dbInterface.getAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);

							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							
							for(String agent : arrayResult)
							{	//< agentName , roleName >	
								resultXML+="<item>\n";
								resultXML+="<agentname>"+ agent+"</agentname>\n";
								resultXML+="<rolename>"+ RoleName+"</rolename>\n"; 
								resultXML+="</item>\n";
							}
							resultXML+="</result>\n";
							resultXML+="</response>";
							break; //Se incluyen todos los parametros
						}
						return resultXML;

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

			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}

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
	 */
	public String quantityMembers(String UnitName, String RoleName, String PositionValue, String AgentName)
	{
		
		int intResult = 0;
		Flags flag = Flags.CASE_A;
		String resultXML="<response>\n<serviceName>QuantityMembers</serviceName>\n";

		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(UnitName))
			{
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
									ArrayList<String> roleInfo = dbInterface.getInformRole(RoleName, UnitName);

									String visibility = roleInfo.get(1);
									flag = Flags.CASE_B;

									if (!visibility.equals("public"))
									{
										if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
										{
											throw new THOMASException("Not Allowed. Private info is not available if agent "+ AgentName + " is not inside the unit.");
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


									if (!PositionValue.equals("member") && !PositionValue.equals("creator"))
									{
										throw new THOMASException("Invalid. Role "+ RoleName + " position is not member or creator.");
									}		
								}else if (unitType.equals("hierarchy"))
								{

									if (!PositionValue.equals("supervisor") && !PositionValue.equals("creator") && !PositionValue.equals("subordinate"))
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

						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO

						switch (flag){

						case CASE_A: 

							intResult = dbInterface.getQuantityAgentsRolesInUnit(UnitName, AgentName);

							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							resultXML+="<quantity>"+ intResult+"</quantity>\n";
							resultXML+="</result>\n";
							resultXML+="</response>";
							
						

							break;//No se incluye ni el parametro role name ni position name. 
						case CASE_B: 

							intResult = dbInterface.getQuantityAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);
							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							resultXML+="<quantity>"+ intResult+"</quantity>\n";
							resultXML+="</result>\n";
							resultXML+="</response>";
							break;//Solo se incluye el roleName
						case CASE_C: 

							intResult = dbInterface.getQuantityAgentsPlayingPositionInUnit(UnitName, PositionValue, AgentName);
							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							resultXML+="<quantity>"+ intResult+"</quantity>\n";
							resultXML+="</result>\n";
							resultXML+="</response>";
							break;//No se incluye el rolename pero si el postionName
						case CASE_D: 

							intResult = dbInterface.getQuantityAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);
							resultXML+="<status>Ok</status>\n";
							resultXML+="<result>\n";
							resultXML+="<quantity>"+ intResult+"</quantity>\n";
							resultXML+="</result>\n";
							resultXML+="</response>";
							break; //Se incluyen todos los parametros
						}

						return resultXML;
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

			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}

	}
	/**
	 * Method used for requesting information about a specific unit.
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns < UnitType , ParentName >
	 */
	public String informUnit(String UnitName, String AgentName)
	{
		
		ArrayList<String> arrayResult = new ArrayList<String>();
		boolean play = false;
		String resultXML="<response>\n<serviceName>InformUnit</serviceName>\n";
		
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(AgentName))
			{
				if (dbInterface.checkUnit(UnitName))
				{
					//--------------------------------------------------------------------------------
					//------------------------- Checking domain-dependent norms ----------------------
					//--------------------------------------------------------------------------------
					//TODO
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
							throw new THOMASException("Not allowed. Agent "+ AgentName + " not play any role in unit or parent unit.");
						}
					}
					else if (!unitType.equals("flat"))
					{
						throw new THOMASException("Invalid. Unit type not valid." );
					}

					arrayResult = dbInterface.getInformUnit(UnitName);
					//< UnitType , ParentName >
					resultXML+="<status>Ok</status>\n";
					resultXML+="<result>\n";
					resultXML+="<unittype>"+ arrayResult.get(0)+"</unittype>\n";
					resultXML+="<parentName>"+ arrayResult.get(1)+"</parentName>\n";
					resultXML+="</result>\n";
					resultXML+="</response>";

					return resultXML;
				}
				else
				{
					throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
				}
			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}
	/**
	 * Method used for requesting the list of roles that have been registered inside a unit.
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns < RoleName , Accessibility , Visibility , Position >
	 */
	public String informUnitRoles(String UnitName, String AgentName)
	{
		
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		String resultXML="<response>\n<serviceName>InformUnitRoles</serviceName>\n";

		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(AgentName))
			{
				if (dbInterface.checkUnit(UnitName))
				{
					//--------------------------------------------------------------------------------
					//------------------------- Checking domain-dependent norms ----------------------
					//--------------------------------------------------------------------------------
					//TODO
					methodResult = dbInterface.getInformUnitRoles(UnitName, AgentName);		

					resultXML+="<status>Ok</status>\n";
					resultXML+="<result>\n";
					
					for (ArrayList<String> agentPair : methodResult)
					{
						//< RoleName , Accessibility , Visibility , Position 
						resultXML+="<item>\n";
						resultXML+="<rolename>"+  agentPair.get(0)+"</rolename>\n"; 
						resultXML+="<position>"+  agentPair.get(3)+"</position>\n";
						resultXML+="<visibility>"+  agentPair.get(2)+"</visibility>\n";
						resultXML+="<accesibility>"+  agentPair.get(1)+"</accesibility>\n";
						resultXML+="</item>\n";
					}
					resultXML+="</result>\n";
					resultXML+="</response>";
					
					return resultXML;
				}
				else
				{
					throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
				}
			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting information about a specific role.
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @param AgentName Identifier of the agent
	 * @return Returns < Accessibility - Visibility - Position >
	 */
	public String informRole(String RoleName, String UnitName, String AgentName)
	{
		
		ArrayList<String> arrayResult = new ArrayList<String>();
		String resultXML="<response>\n<serviceName>InformRole</serviceName>\n";
		try
		{
			//--------------------------------------------------------------------------------
			//------------------------- Checking input parameters ----------------------------
			//--------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(AgentName))
			{
				if (dbInterface.checkUnit(UnitName))
				{
					if (dbInterface.checkRole(RoleName, UnitName))
					{
						//--------------------------------------------------------------------------------
						//------------------------- Checking domain-dependent norms ----------------------
						//--------------------------------------------------------------------------------
						//TODO


						//--------------------------------------------------------------------------------
						//------------------------- Checking structural norms ----------------------------
						//--------------------------------------------------------------------------------

						String roleVisibility = dbInterface.getInformRole(RoleName, UnitName).get(1);

						if (!roleVisibility.equals("public"))
						{
							if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
							{

								throw new THOMASException("Not allowed. The visibility of the role is private and agent "+AgentName+" not play any role in the unit.");
							}
						}

						arrayResult = dbInterface.getInformRole(RoleName, UnitName);

						//< Accessibility - Visibility - Position >
						resultXML+="<status>Ok</status>\n";
						resultXML+="<result>\n";
						resultXML+="<position>"+ arrayResult.get(2)+"</position>\n";
						resultXML+="<visibility>"+ arrayResult.get(1)+"</visibility>\n";
						resultXML+="<accesibility>"+ arrayResult.get(0)+"</accesibility>\n";
						resultXML+="</result>\n";
						resultXML+="</response>";
						
						return resultXML;

					}
					else
					{
						throw new THOMASException("Invalid. Role "+ RoleName + " is not registered in the unit.");
					}
				}
				else
				{
					throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
				}
			}
			throw new THOMASException("Invalid. Empty parameters.");
		}catch(Exception e)
		{
			resultXML+="<status>Error</status>\n";
			resultXML+="<result>\n<description>"+e.getMessage()+"</description>\n</result>\n";
			resultXML+="</response>";
			return resultXML;
		}
	}

}
