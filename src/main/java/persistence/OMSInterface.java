package persistence;

import java.util.ArrayList;

public class OMSInterface {

	private DataBaseInterface dbInterface;
	private enum Flags{CASE_A,CASE_B,CASE_C,CASE_D};

	public OMSInterface()
	{
		dbInterface = new DataBaseInterface();
	}
	public String registerUnit(String UnitName, String UnitType, String AgentName, String CreatorName) throws THOMASException
	{
		String result = "";

		try{
			this.registerUnit(UnitName, UnitType, null, AgentName, CreatorName);
		}
		catch(THOMASException e)
		{
			throw e;
		}
		return result;
	}
	public String registerUnit(String UnitName, String UnitType, String ParentUnitName, String AgentName, String CreatorName) throws THOMASException
	{

		String result="";

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

			if (dbInterface.checkAgentPlaysPosition(AgentName,"creator",ParentUnitName))
			{
				try{
					result = dbInterface.createUnit(UnitName, UnitType, ParentUnitName, AgentName, CreatorName);
				}catch(Exception e)
				{
					throw new THOMASException("Error in createUnit: "+ e);
					throw e; // en el caso de que me devuelva una THOMASException. 
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
		return result;
	}

	public String deregisterUnit(String UnitName, String AgentName) throws THOMASException
	{
		String result="";
		boolean play = false;
		boolean exist = false;

		if (dbInterface.checkUnit(UnitName) && !dbInterface.checkVirtualUnit(UnitName))
		{
			//TODO control de normas.
			if (dbInterface.checkAgentPlaysPosition(AgentName, "creator", UnitName))
			{
				play= true;
			}

			//TODO aqui tendremos una función que mire todas las unidades padres de la unidad, por cada una
			//comprobaremos si el agente juega o no un rol con posicion creator.

			if (play)
			{
				//TODO Comprobar que no haya ningún agente en la unidad que tenga un rol distinto a creator.
				String results = dbInterface.getAgentsInUnit(UnitName);
				//Si existe alguna tupla con position diferente a creator no puedo eliminar la unidad.

				String[] agentInfo = results.split(">");//Sacamos todos las tripletas

				for (String s : agentInfo)//Por cada tripleta
				{
					if (s.split(",")[2].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
					{
						exist = true;
					}
				}

				if (exist)
				{
					throw new THOMASException("Not allowed. There are agents in unit playing roles with position different from creator.");
				}
				else
				{
					if (dbInterface.checkSubUnits(UnitName))
					{
						throw new THOMASException("Not allowed. There are subunits in unit.");
					}
					else
					{
						try
						{
							result = dbInterface.deleteUnit(UnitName, AgentName);
						}catch(THOMASException e)
						{
							throw new THOMASException("Error in deleteUnit: "+ e);
							throw e; // en el caso de que me devuelva una THOMASException.
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

	public String registerRole(String RoleName, String UnitName, String Accessibility, String Visibility, String Position, String AgentName) throws THOMASException
	{
		String result = "";
		String unitType = "";
		boolean exists = false;

		if (dbInterface.checkUnit(UnitName))
		{
			if (!dbInterface.checkRoleInUnit(RoleName, UnitName))
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
						//TODO devuelve la tripleta agente, rol, posición
						String results = dbInterface.getAgentsInUnit(UnitName);
						//TODO De todos estos tengo que ver el que solicita 
						String[] agentInfo = results.split(">");//Sacamos todos las tripletas

						for (String s : agentInfo)//Por cada tripleta
						{
							if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
							{
								if  (s.contains("creator") || s.contains("supervisor"))
								{
									exists = true;
								}
							}
						}

						if (exists)
						{
							try
							{
								result = dbInterface.createRole(RoleName, UnitName, Accessibility, Visibility, Position);
							}catch(THOMASException e)
							{
								throw new THOMASException("Error in create role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
							}
						}
						else
						{
							throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or superversior position." );
						}



					}else if (unitType.equals("team") || unitType.equals("flat"))
					{
						//TODO devuelve la tripleta agente, rol, posición
						String results = dbInterface.getAgentsInUnit(UnitName);

						//TODO De todos estos tengo que ver el que solicita 
						String[] agentInfo = results.split(">");//Sacamos todos las tripletas

						for (String s : agentInfo)//Por cada tripleta
						{
							if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
							{
								if  (s.contains("creator") || s.contains("member"))
								{

									exists = true;
								}
							}
						}

						if (exists)
						{
							try
							{
								result = dbInterface.createRole(RoleName, UnitName, Accessibility, Visibility, Position);
							}catch(THOMASException e)
							{
								throw new THOMASException("Error in create role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
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
						//TODO devuelve la tripleta agente, rol, posición
						String results = dbInterface.getAgentsInUnit(UnitName);

						//TODO De todos estos tengo que ver el que solicita 
						String[] agentInfo = results.split(">");//Sacamos todos las tripletas

						for (String s : agentInfo)//Por cada tripleta
						{
							if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
							{
								if (s.contains("creator"))
								{
									exists = true;
								}		
							}
						}
						if (exists)
						{
							try
							{
								result = dbInterface.createRole(RoleName, UnitName, Accessibility, Visibility, Position);
							}catch(THOMASException e)
							{
								throw new THOMASException("Error in create role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
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

	public String deregisterRole(String RoleName, String UnitName, String AgentName) throws THOMASException
	{
		String result = "";
		String unitType = "";
		boolean exists = false;

		if (dbInterface.checkUnit(UnitName)) 
		{
			if (dbInterface.checkRoleInUnit(RoleName, UnitName))
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

								//TODO devuelve la tripleta agente, rol, posición
								String results = dbInterface.getAgentsInUnit(UnitName);

								//TODO De todos estos tengo que ver el que solicita 
								String[] agentInfo = results.split(">");//Sacamos todos las tripletas

								for (String s : agentInfo)//Por cada tripleta
								{
									if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
									{
										if  (s.contains("creator") || s.contains("supervisor"))
										{
											exists = true;
										}

									}
								}
								if (exists)
								{
									try
									{
										result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
									}catch(THOMASException e)
									{
										throw new THOMASException("Error in create role: "+ e);
										throw e; // en el caso de que me devuelva una THOMASException.
									}
								}
								else
								{
									throw new THOMASException("Not allowed. The agent "+ AgentName + " is not playing any role with creator or superversior position." );
								}
							}
							else if (unitType.equals("team") || unitType.equals("flat"))
							{
								//TODO devuelve la tripleta agente, rol, posición
								String results = dbInterface.getAgentsInUnit(UnitName);

								//TODO De todos estos tengo que ver el que solicita 
								String[] agentInfo = results.split(">");//Sacamos todos las tripletas

								for (String s : agentInfo)//Por cada tripleta
								{
									if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
									{
										if  (s.contains("creator") || s.contains("member"))
										{
											exists = true;
										}

									}
								}
								if (exists)
								{
									try
									{
										result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
									}catch(THOMASException e)
									{
										throw new THOMASException("Error in delete role: "+ e);
										throw e; // en el caso de que me devuelva una THOMASException.
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
								//TODO devuelve la tripleta agente, rol, posición
								String results = dbInterface.getAgentsInUnit(UnitName);

								//TODO De todos estos tengo que ver el que solicita 
								String[] agentInfo = results.split(">");//Sacamos todos las tripletas

								for (String s : agentInfo)//Por cada tripleta
								{
									if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
									{
										if (s.contains("creator"))
										{
											exists = true;
										}

									}
								}
								if (exists)
								{
									try
									{
										result = dbInterface.deleteRole(RoleName, UnitName, AgentName);
									}catch(THOMASException e)
									{
										throw new THOMASException("Error in delete role: "+ e);
										throw e; // en el caso de que me devuelva una THOMASException.
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

	public boolean AcquireRole(String UnitName, String RoleName, String AgentName) throws THOMASException
	{
		boolean result;
		boolean exists = false;
		ArrayList<String> agentRoles = new ArrayList<String>();
		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRoleInUnit(RoleName, UnitName))
			{
				//TODO Control de normas.
				if (dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
				{
					//TODO Corregir ingles.
					throw new THOMASException("Not allowed. The agent "+ AgentName + " is already playing the role.");
				}
				else
				{
					//TODO devuelve la tripleta agente, rol, posición
					String results = dbInterface.getAgentsInUnit(UnitName);

					//TODO El agente que solo tenga un rol y sea subordinate no puede adquirir un rol con position 
					//creator o supervisor
					//Puede adquirir el rol
					String informRole = getInformRole(RoleName);

					//TODO De todos estos tengo que ver el que solicita 
					String[] agentInfo = results.split(">");//Sacamos todos las tripletas

					for (String s : agentInfo)//Por cada tripleta
					{
						if (s.split(",")[0].substring(1).equals(AgentName))//Si esa tripleta contiene el agente que va a registrar el role
						{
							agentRoles.add(s.split(",")[2]);
						}
					}
					if (!agentRoles.contains("creator") && !agentRoles.contains("supervisor") && !agentRoles.contains("member"))
					{
						//TODO Extraer la position del rol					
						if (!informRole.contains("subordinate") && !informRole.contains("member"))
						{

							throw new THOMASException("Not allowed. The position role cannot be creator or supervisor.");
						}
					}
					if (informRole.contains("external"))
					{


						try
						{
							result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
						}
						catch(THOMASException e)
						{
							throw new THOMASException("Error in delete role: "+ e);
							throw e; // en el caso de que me devuelva una THOMASException.
						}
					}
					else
					{
						if (informRole.contains("public"))
						{
							try
							{
								result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in delete role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
							}
						}
						else
						{
							if (checkAgentInUnit(AgentName, UnitName))
							{
								try
								{
									result = dbInterface.acquireRole(UnitName, RoleName, AgentName);
								}
								catch(THOMASException e)
								{
									throw new THOMASException("Error in acquire role: "+ e);
									throw e; // en el caso de que me devuelva una THOMASException.
								}
							}
							else
							{
								throw new THOMASException("Not allowed. Agent "+ AgentName + " is not inside the unit.");
							}
						}
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

	public String leaveRole(String UnitName, String RoleName, String AgentName) throws THOMASException
	{
		String result = "";

		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRoleInUnit(RoleName, UnitName))
			{
				//TODO Control de normas.
				if (!dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName))
				{
					//TODO Corregir ingles.
					throw new THOMASException("Not allowed. The agent "+ AgentName + " not play the role.");
				}
				else
				{
					try
					{
						result = dbInterface.leaveRole(UnitName, RoleName, AgentName);
					}
					catch(THOMASException e)
					{
						throw new THOMASException("Error in leave role: "+ e);
						throw e; // en el caso de que me devuelva una THOMASException.
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

	public String allocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName) throws THOMASException
	{
		String result = "";


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRoleInUnit(RoleName, UnitName))
			{
				//TODO Falta por hacer la comprobación del targetName, por ahora no se dispone de suficiente información para ello.
				if (TargetAgentName.equals(AgentName))
				{
					throw new THOMASException("Invalid. The TargetAgentName is the same than AgentName.");
				}
				String type = dbInterface.getUnitType(UnitName);

				if (type.equals("hierarchy"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
						{
							try
							{
								result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in allocate role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
							}
						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position supervisor or creator.");
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
						try
						{
							result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
						}
						catch(THOMASException e)
						{
							throw new THOMASException("Error in allocate role: "+ e);
							throw e; // en el caso de que me devuelva una THOMASException.
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
							try
							{
								result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in allocate role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
							}
						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position member or creator.");
						}

					}
					else
					{
						if (dbInterface.checkAgentPosition(AgentName, "creator"))
						{
							try
							{
								result = dbInterface.allocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in allocate role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
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

	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName) throws THOMASException
	{
		String result = "";
		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRoleInUnit(RoleName, UnitName))
			{
				//TODO Falta por hacer la comprobación del targetName, por ahora no se dispone de suficiente información para ello.
				if (TargetAgentName.equals(AgentName))
				{
					throw new THOMASException("Not allowed. The TargetAgentName is the same than AgentName.");
				}
				String type = dbInterface.getUnitType(UnitName);

				if (type.equals("hierarchy"))
				{
					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName))
						{
							try
							{
								result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in deallocate role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
							}
						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position supervisor or creator.");
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
						try
						{
							result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
						}
						catch(THOMASException e)
						{
							throw new THOMASException("Error in deallocate role: "+ e);
							throw e; // en el caso de que me devuelva una THOMASException.
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
							try
							{
								result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in deallocate role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
							}
						}
						else
						{
							throw new THOMASException("Not allowed. The Agent "+ AgentName + " not play any rol with position member or creator.");
						}

					}
					else
					{
						if (dbInterface.checkAgentPosition(AgentName, "creator"))
						{
							try
							{
								result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in deallocate role: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
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

	public String jointUnit(String UnitName, String ParentName, String AgentName) throws THOMASException
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
					if (dbInterface.checkAgentPositionInUnit(AgentName, "creator", UnitName))
					{
						if (dbInterface.checkAgentPositionInUnit(AgentName, "creator", ParentName))
						{
							try
							{
								result = dbInterface.jointUnit(UnitName, ParentName, AgentName);
							}
							catch(THOMASException e)
							{
								throw new THOMASException("Error in joint unit: "+ e);
								throw e; // en el caso de que me devuelva una THOMASException.
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

	public String informAgentRole(String RequestedAgentName,String AgentName) throws THOMASException
	{
		String result = "";
		if (dbInterface.checkAgent(RequestedAgentName))
		{
			//TODO Control de normas
			try
			{
				result = dbInterface.getInformAgentRole(RequestedAgentName, AgentName);
			}
			catch(THOMASException e)
			{
				throw new THOMASException("Error in joint unit: "+ e);
				throw e; // en el caso de que me devuelva una THOMASException.
			}
		}
		else
		{
			throw new THOMASException("Not found. The agent "+ RequestedAgentName + " not exists.");
		}

		return result;
	}


	public String informMembers(String UnitName, String RoleName, String PositionValue, String AgentName) throws THOMASException
	{
		String result = "";

		Flags flag = Flags.CASE_A;


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkAgent(AgentName))
			{
				if (RoleName != null)
				{
					if (!RoleName.equals(""))
					{
						if (!dbInterface.checkRoleInUnit(RoleName, UnitName))
						{
							throw new THOMASException("Not found. Role "+ RoleName + " is not inside the unit.");
						}
						else
						{
							flag = Flags.CASE_B;
							String roleInfo = dbInterface.getInformAgentRole(RoleName, UnitName, AgentName);

							if (!roleInfo.contains("public"))
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

							String informRole = dbInterface.getInformRole(RoleName, UnitName, AgentName));

							if (!informRole.contains("member") && !informRole.contains("creator"))
							{
								throw new THOMASException("Invalid. Role "+ RoleName + " position is not member or creator.");
							}		
						}else if (unitType.equals("hierarchy"))
						{
							String informRole = dbInterface.getInformRole(RoleName, UnitName, AgentName));
							if (!informRole.contains("supervisor") && !informRole.contains("creator") && !informRole.contains("subordinate"))
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
					try{
						result = dbInterface.getAgentsRolesInUnit(UnitName, AgentName);
					}catch(THOMASException e){throw e;};
					break;//No se incluye ni el parametro role name ni position name. 
				case CASE_B: 
					try{
						result = dbInterface.getAgentsPlayingRoleInUnit(UnitName, RoleName, AgentName);
					}catch(THOMASException e){throw e;};
					break;//Solo se incluye el roleName
				case CASE_C: 
					try{	
						result = dbInterface.getAgentsPlayingPositionInUnit(UnitName, PositionValue, AgentName);
					}catch(THOMASException e){throw e;};
					break;//No se incluye el rolename pero si el postionName
				case CASE_D: 
					try{
						result = dbInterface.getAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);
					}catch(THOMASException e){throw e;};
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

	public String quantityMembers(String UnitName, String RoleName, String PositionValue, String AgentName) throws THOMASException
	{
		String result = "";
		Flags flag = Flags.CASE_A;


		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkAgent(AgentName))
			{
				if (RoleName != null)
				{
					if (!RoleName.equals(""))
					{
						if (!dbInterface.checkRoleInUnit(RoleName, UnitName))
						{
							throw new THOMASException("Not found. Role "+ RoleName + " is not inside the unit.");
						}
						else
						{
							flag = Flags.CASE_B;
							String roleInfo = dbInterface.getInformAgentRole(RoleName, UnitName, AgentName);

							if (!roleInfo.contains("public"))
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

							String informRole = dbInterface.getInformRole(RoleName, UnitName, AgentName));

							if (!informRole.contains("member") && !informRole.contains("creator"))
							{
								throw new THOMASException("Invalid. Role "+ RoleName + " position is not member or creator.");
							}		
						}else if (unitType.equals("hierarchy"))
						{
							String informRole = dbInterface.getInformRole(RoleName, UnitName, AgentName));
							if (!informRole.contains("supervisor") && !informRole.contains("creator") && !informRole.contains("subordinate"))
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
					try{
						result = dbInterface.getQuantityAgentsInUnit(UnitName, AgentName);
					}catch(THOMASException e){throw e;};
					break;//No se incluye ni el parametro role name ni position name. 
				case CASE_B: 
					try{
						result = dbInterface.getQuantityPlayingRoleInUnit(UnitName, RoleName, AgentName);
					}catch(THOMASException e){throw e;};
					break;//Solo se incluye el roleName
				case CASE_C: 
					try{	
						result = dbInterface.getQuantityPlayingPositionInUnit(UnitName, PositionValue, AgentName);
					}catch(THOMASException e){throw e;};
					break;//No se incluye el rolename pero si el postionName
				case CASE_D: 
					try{
						result = dbInterface.getQuantityPlayingRolePositionInUnit(UnitName, RoleName, PositionValue, AgentName);
					}catch(THOMASException e){throw e;};
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

	public String informUnit(String UnitName, String AgentName) throws THOMASException
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

			try
			{
				result = dbInterface.getInformUnit(UnitName);
			}catch(THOMASException e)
			{
				throw e;
			}

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}

	}
	public String informUnitRoles(String UnitName, String AgentName) throws THOMASException
	{
		String result = "";
		if (dbInterface.checkUnit(UnitName))
		{
			//TODO Control de normas
			try
			{
				result = dbInterface.getInformUnitRoles(UnitName, AgentName);
			}catch(THOMASException e)
			{
				throw e;
			}

		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}
		return result;
	}

	public String informRole(String RoleName, String UnitName, String AgentName) throws THOMASException
	{
		String result = "";
		if (dbInterface.checkUnit(UnitName))
		{
			if (dbInterface.checkRoleInUnit(RoleName, UnitName))
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

				try
				{
					result = dbInterface.getInformRole(RoleName, UnitName, AgentName);
				}catch(THOMASException e)
				{
					throw e;
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
