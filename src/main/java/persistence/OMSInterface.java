package persistence;

public class OMSInterface {
	
	private DataBaseInterface dbInterface;
	
	public OMSInterface()
	{
		dbInterface = new DataBaseInterface();
	}
	public String registerUnit(String UnitName, String UnitType, String AgentName, String CreatorName) throws THOMASException
	{
		return this.registerUnit(UnitName, UnitType, null, AgentName, CreatorName);
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
				if (existe)
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
						
						if  (results.contains("creator") || results.contains("supervisor"))
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
						
						if  (results.contains("creator") || results.contains("member"))
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
						
						if (results.contains("creator"))
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
				throw new THOMASException("Not allowed. The role "+ RoleName + " exists in the unit "+UnitName +"." );	
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}
		
		return result;
	}
	
	public String deregisterRole(String RoleName, String UnitName, String AgentName)
	{
		String result = "";
		String unitType = "";
		
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
									
									if  (results.contains("creator") || results.contains("supervisor"))
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
								
								if  (results.contains("creator") || results.contains("member"))
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
								
								if (results.contains("creator"))
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
				throw new THOMASException("Not allowed. Role "+ RoleName + " is not inside the unit.");
			}
		}
		else
		{
			throw new THOMASException("Not found. Unit "+ UnitName + " not found.");
		}
		return result;
	}
	
	public String AcquireRole(String UnitName, String RoleName, String AgentName)
	{
		String result = "";
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
					if (results.contains("creator") || results.contains("supervisor"))
					{
						
					}
					else //SOlamente tendre subordinado, por tanto no puede adquirir ningún rol con position creator o subordinate
					{
						
					}
				}
			}
			else
			{
				throw new THOMASException("Not allowed. Role "+ RoleName + " is not inside the unit.");
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
		
		return result;
	}
	
	public String allocateRole(String RoleName, Strimg UnitName, String TargetAgentName, String AgentName)
	{
		
	}

}
