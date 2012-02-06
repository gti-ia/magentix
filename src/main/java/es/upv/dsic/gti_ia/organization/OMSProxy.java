package es.upv.dsic.gti_ia.organization;



import java.util.ArrayList;

import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;




/**
 * This class gives us the support to accede to the services of the OMS. The OMS
 * provides a group of services for registering or deregistering structural
 * components, specific roles, norms and units. It also offers services for
 * reporting on these components.
 * 
 * 
 * @author Joan Bellver Faus
 * 
 */
public class OMSProxy extends THOMASProxy{




	/**
	 * This class gives us the support to access to the services of the OMS
	 * 
	 *  @param agent
	 *            is a Magentix2 agent, this agent implemented the communication
	 *            protocol.
	 * @param OMSServiceDesciptionLocation
	 *            The URL where the owl's document is located.
	 */
	public OMSProxy(BaseAgent agent, String OMSServiceDesciptionLocation) {
		super(agent, "OMS",OMSServiceDesciptionLocation);


	}

	/**
	 * 
	 *  @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol
	 *            
	 * This class gives us the support to accede to the services of the OMS,
	 * Checked that the data contained in the file configuration/Settings.xml, the URL
	 * ServiceDescriptionLocation is not empty and is the correct path.
	 */
	public OMSProxy(BaseAgent agent) {

		super(agent,"OMS");
		ServiceDescriptionLocation = c.getOMSServiceDesciptionLocation();

	}

	/**
	 * Builds a new organizational message with the appropriate receivers according to the type of unit and position of the role that the agent is performing
	 * @param OrganizationID represents the ID of the organization to which the agent wants to send a message
	 * @return returns the ACL message built 
	 * @throws THOMASException  in order to show the cause of exception uses getContent
	 */

	public ACLMessage buildOrganizationalMessage(String OrganizationID) throws THOMASException
	{
		ArrayList<String>  unit;
		ArrayList<ArrayList<String>> agentRoles = null;
		ArrayList<String> agentPositions = new ArrayList<String>();

		String rol_aux;
		String unit_aux;

		boolean insideUnit = false;
		boolean containsPositonNoCreator = false;

		//Create a new ACLMessage
		ACLMessage msg = new ACLMessage();

		msg.setSender(agent.getAid());

		//Inform Unit
		unit = this.informUnit(OrganizationID);

		//If unit not exist
		if (unit.isEmpty())
		{
			throw new THOMASException("Inform unit not allowed or unit "+ OrganizationID+" not found.");

		}else
		{
			
			String unitType = unit.get(0);
			agentRoles = this.informAgentRole(agent.getAid().name);

			if (agentRoles.isEmpty())
			{
				throw new THOMASException("The agent not play any rol.");
			}
			else
			{

				for (ArrayList<String> agentRole : agentRoles)
				{
				
					rol_aux = agentRole.get(0);
					unit_aux = agentRole.get(1);


					ArrayList<String> informRole = this.informRole(rol_aux, unit_aux);
					//st = new StringTokenizer(informRole,"<>, ");


					String position = informRole.get(0);

					if (position!=null)
					{
						if (!position.equals("creator"))
							containsPositonNoCreator = true;
						agentPositions.add(position);
					}

				}

				// Comprobaremos si tiene solamente el rol con la posición creator, en ese caso no puede enviar nada a ningún grupo.
				if (containsPositonNoCreator)
				{


					//agentPositions.clear();
					//The unit type is flat
					if (unitType.equals("flat"))
					{
						msg.putExchangeHeader("participant", OrganizationID);
						msg.setReceiver(new AgentID(OrganizationID));
					}
					else
					{
						for (ArrayList<String> agentRole : agentRoles)
						{
							//Cogemos primero el rol, para que solamente compruebe la unidad que es el segundo elemento.
							
							rol_aux = agentRole.get(0);
							

							if (agentRole.get(1).equals(OrganizationID))
							{
								insideUnit = true;
							}

						}
						if (insideUnit)
						{
							if (unitType.equals("team"))
							{
								msg.putExchangeHeader("participant", OrganizationID);
								msg.setReceiver(new AgentID(OrganizationID));
							}
							else if (unitType.equals("hierarchy"))
							{
								//Sacamos la posición del agente
								//Debemos elegir el de mayor

								if (agentPositions.contains("supervisor"))
								{
									msg.putExchangeHeader("supervisor", OrganizationID);
									msg.putExchangeHeader("participant", OrganizationID);
								}
								else if (agentPositions.contains("subordinate"))
								{
									msg.putExchangeHeader("supervisor", OrganizationID);

								}

								msg.setReceiver(new AgentID(OrganizationID));

							}
							else
							{
								throw new THOMASException("Unknown unit type.");
							}
						}
						else //El agente no esta dentro de la unidad
						{

							throw new THOMASException("The agent is not inside the unit "+ OrganizationID+".");
						}
					}
				}
				else
					throw new THOMASException("Communication is not allowed to agents which only play the role creator.");


			}


		}

		return msg;
	}




	/**
	 * Service used for leaving a role inside a specific unit. The agent plays this role inside the unit.
	 * 
	 * The execution of this service implies:
	 *	– Check that the role and the unit exist (Preconditions Pre1 and Pre2).
	 *	– Check that the agent plays this role inside the unit (Precondition Pre3).
	 *	– Deregister Agent - Role - Unit entry in EntityPlayList (using DeregisterAgentRole service)
	 *
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 */
	public String leaveRole(String RoleID,
			String UnitID) throws THOMASException {
	
		call = ServiceDescriptionLocation + "LeaveRole.owl AgentID=" + agent.getAid().name.replace('~', '@')
		+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		String result = new String();
		result = (String) this.sendInform();
		return result;
	}

	/**
	 * Requesting the list of roles and units in which an agent is in a specific moment.
	 * 
	 *The execution of this service checks:
	 *	– That the requested agent exists
	 *	– Whether the agent (AgentID) asks information about its own roles (i.e.
	 *		AgentID=RequestedAgentID).
	 *
	 * 
	 * @param AgentID
	 *            entity,this agent is protocol://name@host:port
	 *            ej.qpid://clientagent2@localhost:8080 , we can extract this
	 *            inform with the method getAid().toString().
	 * @return ArrayLis<String>t RoleUnitList [<role, unit>,<role, unit>]
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> informAgentRole(String AgentID) throws THOMASException
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		call = ServiceDescriptionLocation
		+ "InformAgentRole.owl RequestedAgentID=" + AgentID;
		result = (ArrayList<ArrayList<String>>) this.sendInform();
		return result;
	}

	/**
	 * Indicates entities that are members of a specific unit. Optionally, it is possible to specify a role of this unit, 
	 * so then only members playing this role are detailed. 
	 * 
	 *  A agent can make use of this service, depending on the type of unit (UnitID): 
	 *  if FLAT, the agent is allowed, if TEAM, he is only allowed if he is a member of this unit, if HIERARCHY, then he is
	 *  only allowed if he is a supervisor of this unit.
	 * 
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return ArrayList<String> EntityRoleList
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>>  informMembers(String UnitID, String RoleID, String PositionValue) throws THOMASException
	{

		call = ServiceDescriptionLocation + "InformMembers.owl "
		+ "RoleID=" + RoleID 
		+ " UnitID=" + UnitID
		+ " PositionValue="+PositionValue;
		
		return (ArrayList<ArrayList<String>>) this.sendInform();

	}

	/**
	 * Provides all norms addressed to a specific role.
	 * 
	 * The execution of this service checks:
	 *	– That the role exists.
	 *	– That the requester agent (AgentID) is member of THOMAS.
	 *
	 * 
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * 
	 * @return ArrayList<String> NormList
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> informTargetNorms(String TargetName, String Type, String UnitName) throws THOMASException
	{
			call = ServiceDescriptionLocation + "InformTargetNorms.owl " 
		+ "TargetName=" + TargetName 
		+ " Type="+ Type
		+ " UnitName="+ UnitName;
			
		return (ArrayList<String>) this.sendInform();

	}
	@SuppressWarnings("unchecked")
	public ArrayList<String> informNorm(String NormName, String UnitName) throws THOMASException
	{		

		call = ServiceDescriptionLocation + "InformNorm.owl"
		+" NormName="+ NormName
		+ " UnitName="+UnitName;
		return (ArrayList<String>) this.sendInform();

	}

	/**
	 * Request profiles associated to a specific role
	 * 
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return ArrayList<String> ProfileList
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> informRoleProfiles(String UnitID) throws THOMASException
	{
		call = ServiceDescriptionLocation + "InformRoleProfiles.owl "
		+ "UnitID="+ UnitID;
		return (ArrayList<String>) this.sendInform();

	}


	@SuppressWarnings("unchecked")
	public ArrayList<String>  informRole(String RoleName, String UnitName) throws THOMASException
	{
		
		ArrayList<String> result = new ArrayList<String>();
		call = ServiceDescriptionLocation + "InformRole.owl " 
		+"UnitName="+ UnitName
		+ " RoleName="+RoleName;
		result = (ArrayList<String>) this.sendInform();
		return result;

	}

	/**
	 * Provides unit description
	 * 
	 * The execution of this service checks:
	 *	– That the unit exists.
	 *	– That the requester agent (AgentID) is member of the ParentUnit.
	 *
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return ArrayList<String> UnitType UnitGoal ParentID
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String>  informUnit(String UnitID) throws THOMASException
	{
		call = ServiceDescriptionLocation + "InformUnit.owl UnitID=" + UnitID;
		return (ArrayList<String>) this.sendInform();


	}

	/**
	 * Used for requesting the list of roles that have been registered inside a unit. 
	 * 
	 * Agent can make use of this service depending on the type of unit (UnitID):
	 * 
	 * if FLAT, the agent is allowed, if TEAM or HIERARCHY he is only if he is a member
	 * of this unit.
	 * 
	 * 
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * 
	 * @return ArrayList<String> RoleList
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>>  informUnitRoles(String UnitID) throws THOMASException
	{
		call = ServiceDescriptionLocation + "InformUnitRoles.owl UnitID="
		+ UnitID;
		return (ArrayList<ArrayList<String>>) this.sendInform();

	}

	/**
	 * Provides the number of current members of a specific unit. Optionally, if a role is indicated then only the quantity of 
	 * members of a specific unit.
	 * 
	 * @param RoleID
	 *            if a role is indicated then only the quantity of members playing this roles is detailed.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * 
	 * @return Integer Quantity
	 */
	public int quantityMembers(String UnitID, String RoleID, String PositionValue) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "QuantityMembers.owl RoleID="
		+ RoleID + " UnitID=" + UnitID + " PositionValue="+ PositionValue;
		ArrayList<String> a = (ArrayList<String>) this.sendInform();
		
		return Integer.parseInt(a.get(0));


	}

	/**
	 * Includes a new norm inside a unit
	 * 
	 * @param NormID
	 *            norm for controlling role actions
	 * @param NormContent
	 *            The syntax of the rules of incompatibility is the following:
	 *            FORBIDDEN role1 REQUEST AcquireRole MESSAGE (CONTENT (role
	 *            'role2ID')) Applications for registration of a rule is
	 *            necessary to replace the spaces between the different words
	 *            for "_".
	 *@param UnitName
	 *            Name of the unit
	 * @return String Status ErrorValue
	 */
	public String registerNorm(String NormID,
			String NormContent, String UnitName) throws THOMASException
			{
		
		call = ServiceDescriptionLocation + "RegisterNorm.owl NormID="
		+ NormID + " NormContent=" + NormContent+ " UnitName="+ UnitName;
		return (String) this.sendInform();
	}

	/**
	 * Creates a new role inside a unit
	 * 
	 * 
	 * The execution of this service:
	 *	– Checks that the unit exists (Precondition Pre2), but there is not any role
	 *		inside this unit with the same name (Precondition Pre1).
	 *	– If a parent role has been defined, using the inheritance parameter, then
	 *		it checks that this parent role exists (Precondition Pre3).
	 *	– Checks that the requester agent (AgentID) can make use of this service,
	 *		depending on the type of the unit(indicated in UnitID): if FLAT, the
	 *		agent is allowed; if TEAM, he is only allowed if he is a member of the unit;
	 *		if HIERARCHY, then he can only register this role if he is a supervisor
	 *		of this unit
	 *
	 * @param RoleID
	 *            is the identifier of the new role
	 * @param UnitID
	 *            is the identifier of the organizational unit in which the new
	 *            role is defined
	 * @param Accessibility
	 *            considers two types of roles: (internal) internal roles, which are
	 *            assigned to internal agents of the system platform; and (external)
	 *            external roles, which can be enacted by any agent. Default is a External.
	 * @param Position
	 *            determines its structural position inside the unit, such as
	 *            member, supervisor or subordinate. Default is a Member.
	 * @param Visibility
	 *            indicates whether agents can obtain information of this role
	 *            from outside the unit in which this role is defined (public)
	 *            or from inside (private). Default is a Public.
	 * 
	 * @return String Status ErroValue
	 */
	public String registerRole(String RoleID, String UnitID,
			String Accessibility, String Visibility, String Position) throws THOMASException
			{
		
		call = ServiceDescriptionLocation + "RegisterRole.owl RoleID="
		+ RoleID + " UnitID=" + UnitID + " Accessibility="
		+ Accessibility + " Position=" + Position + " Visibility="
		+ Visibility;
		return (String) this.sendInform();


	}

	/**
	 * Creates a new empty unit in the organization, with a specific structure, creatorName and parent unit.
	 *
	 *
	 *The execution of this service checks:
	 *	– That the unit (UnitID) does not exist (Precondition Pre1).
	 *	– If a parent unit has been defined, then it checks that this parent unit
	 *		exists (Precondition Pre2). If this paremeter is not included, then the
	 *		"Virtual" unit is assumed as parent unit.
	 *	– That the requester agent (AgentID) can make use of this service, de-
	 *		pending on the type of the parent unit (indicated in ParentUnitID): if
	 *		FLAT, the agent is allowed; if TEAM, he is only allowed if he is a mem-
	 *		ber of the parent unit; if HIERARCHY, then he can only register this
	 *		unit if he is a supervisor of the parent unit.
	 *
	 * @param UnitID
	 *            is the identifier of the new unit
	 * @param Type
	 *            indicates the topology of the new unit: (i) Hierarchy, in
	 *            which a supervisor agent has control over other members; (ii)
	 *            Team, which are groups of agents that share a common goal,
	 *            collaborating and cooperating between them; and (iii) Flat, in
	 *            which there is none agent with control over other members. Default is a Flat.
	 * 
	 * @param ParentUnitID
	 *            is the identifier of the parent unit which contains the new
	 *            unit. Default is a Virtual.
	 * @param CreatorName
	 *            The name of the new creator role 
	 * @return String Status ErrorValue
	 */
	public String registerUnit(String UnitID, String Type,
			String ParentUnitID, String CreatorName) throws THOMASException
			{
		
		if (ParentUnitID == null)
			call = ServiceDescriptionLocation + "RegisterUnit.owl  UnitID="
			+ UnitID + " Type=" + Type + " CreatorName=" + CreatorName + " ParentUnitID=";
		else
			call = ServiceDescriptionLocation + "RegisterUnit.owl  UnitID="
			+ UnitID + " Type=" + Type + " CreatorName=" + CreatorName + " ParentUnitID="
			+ ParentUnitID;

		return 	(String) this.sendInform();

	}

	public String jointUnit(String UnitName, String ParentName)throws THOMASException
	{
	
		call = ServiceDescriptionLocation + "JointUnit.owl  UnitName="
		+ UnitName + " ParentName=" + ParentName;
		return 	(String) this.sendInform();


	}

	/**
	 * Removes a specific norm.
	 * 
	 * The execution of this service checks:
	 *	– That the norm exists and the requester agent (AgentID) is member of
	 *		THOMAS 
	 *
	 * 
	 * @param NormID
	 *            norm name
	 * @param UnitName
	 *            Name of the unit
	 * @return String Status ErrorValue
	 */
	public String deregisterNorm(String NormID, String UnitName) throws THOMASException
	{
	
		call = ServiceDescriptionLocation + "DeregisterNorm.owl  NormID="
		+ NormID+ " UnitName="+ UnitName;
		return 	(String) this.sendInform();


	}

	/**
	 * Removes a specific role description from a unit. 
	 * 
	 * The execution of this service checks:
	 * 
	 * - There must not be any agent playing this role nor any norm addressed to it. 
	 * - The agent can make use of this service, depending on the type of unit (UnitID). if FLAT, the agent
	 * is allowed; if TEAM, he is only allowed if he is a member of this unit; if HIERARCHY, then he is only allowed if he is
	 * a supervisor of this unit.
	 * @param RoleID
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 */
	public String deregisterRole(String RoleID, String UnitID) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "DeregisterRole.owl  RoleID="
		+ RoleID + " UnitID=" + UnitID;
		return	(String) this.sendInform();


	}

	/**
	 * Removes a unit from an organization
	 * 
	 * @param UnitID
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @return String Status ErrorValue
	 */
	public String deregisterUnit(String UnitID)throws THOMASException
	{
	
		call = ServiceDescriptionLocation + "DeregisterUnit.owl  UnitID="
		+ UnitID;
		return (String) this.sendInform();


	}

	/**
	 * Forces an agent to leave a specific role
	 * 
	 * 
	 * The execution of this service implies:
	 *	– Check that the specified agent (ExpulseAgentID) plays the indicated role inside the unit. 
	 *	– Check that the requester agent (AgentID) can make use of this service,
	 *		depending on the type of unit (UnitID): if FLAT, the agent is not allowed; 
	 *		if TEAM or HIERARCHY, he is only allowed if he is a supervisor
	 *		of this unit.
	 *	– Deregister Agent - Role - Unit entry in EntityPlayList (using DeregisterAgentRole service)
	 *
	 * 
	 * @param RoleName
	 *            represent all required functionality needed in order to
	 *            achieve the unit goal.
	 * @param UnitName
	 *            organizational units (OUs), which represent groups of entities
	 *            (agents or other units)
	 * @param TargetAgentName
	 *       	the agent deallocate role to agente with target agent name
	 * @return String Status ErrorValue
	 */
	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "DeallocateRole.owl TargetAgentName=" + TargetAgentName
		+ " RoleName=" + RoleName + " UnitName=" + UnitName;
		return (String) this.sendInform();


	}


	public String allocateRole(String RoleName, String UnitName, String TargetAgentName) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "AllocateRole.owl RoleName=" + RoleName
		+ " UnitName=" + UnitName + " TargetAgentName="+ TargetAgentName;


		return (String) this.sendInform();


	}
	/**
	 * Requests the adoption of a specific role inside a unit
	 * 
	 * The execution of this service implies:
	 *	– Check that the requested role exits inside the unit.
	 *	– Check that the agent is not already playing this role.
	 *	– Check that the agent is inside its parent unit.
	 *	– Check cardinality restrictions (maximum cardinality).
	 *	– Check compatibility restrictions, i.e. the requested role is not incompatible with the other roles played by the agent.
	 *	– Register Agent - Role - Unit entry in EntityPlayList (using RegisterAgentRole service)
	 *	– Activate agent norms related with this requested role.
	 *
	 * 
	 * @param RoleID
	 *            Role that the agent acquires inside the organization
	 * @param UnitID
	 *            Unit of which the agent was forming a part with the previous
	 *            role
	 * @return String Status ErrorValue
	 */
	public String acquireRole(String RoleID, String UnitID) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "AcquireRole.owl RoleID=" + RoleID
		+ " UnitID=" + UnitID;

		return (String) this.sendInform();
	}

}

