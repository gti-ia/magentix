package es.upv.dsic.gti_ia.organization;



import java.util.ArrayList;
import java.util.Iterator;

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

	//TODO Función ad hoc para el ejemplo. Se substituirá cuando el nuevo thomas
	//tenga un servicio que devuelva la posición del agente.
	public String getAgentPosition(String agent, String unit, String rol, String unitType)
	{

		String position = null;

		if (agent.equals("agente_creador") && unit.equals("calculin") && rol.equals("operador"))
		{
			return "member";
		}
		if (agent.equals("agente_creador") && unit.equals("calculin") && rol.equals("creador"))
		{
			position = "creator";

		}else if (agent.equals("agente_ruidoso"))
		{
			if (rol.equals("creador") && unit.equals("externa"))
				position = "creator";
			if (rol.equals("manager") && unit.equals("externa"))
				position = "member";

		}if (agent.contains("agente_suma") || agent.contains("agente_producto"))
		{
			if (unit.equals("calculin") && rol.equals("operador"))
			{
				if (unitType.equals("flat") || unitType.equals("team"))
					position = "member";	
				else
					position = "subordinate";	
			}



		}	
		if (agent.equals("agente_sumatorio") || agent.equals("agente_visor") || agent.equals("agente_sumaPotencias"))
		{

			if  (unit.equals("calculin") && (rol.equals("manager")))
			{
				if (unitType.equals("flat") || unitType.equals("team"))
					position = "member";	
				else
					position = "supervisor";
			}

		}
		return position;
	}


	/**
	 * Builds a new organizational message with the appropriate receivers according to the type of unit and position of the role that the agent is performing
	 * @param OrganizationID represents the ID of the organization to which the agent wants to send a message
	 * @return returns the ACL message built 
	 * @throws THOMASException  in order to show the cause of exception uses getContent
	 */

	public ACLMessage buildOrganizationalMessage(String OrganizationID) throws THOMASException
	{
		ArrayList<String> unit;
		ArrayList<String> agentRole = null;
		ArrayList<String> agentPositions = new ArrayList<String>();

		String rol_aux;
		String unit_aux;

		boolean insideUnit = false;


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

			agentRole = this.informAgentRole(agent.getAid().name);

			if (agentRole.isEmpty())
			{
				throw new THOMASException("The agent not play any rol.");
			}
			else
			{

				Iterator<String> iterator1 = agentRole.iterator();

				while(iterator1.hasNext())
				{
					rol_aux = iterator1.next();
					unit_aux = iterator1.next();

					String position = getAgentPosition(agent.getName(),unit_aux,rol_aux, unit.get(2));
					if (position!=null)
						agentPositions.add(position);

				}
				// Comprobaremos si tiene solamente el rol con la posición creator, en ese caso no puede enviar nada a ningún grupo.
				if (agentPositions.contains("creator") && agentPositions.size() == 1)
				{
					throw new THOMASException("Communication is not allowed to agents which only play the role creator.");
				}

				agentPositions.clear();
				//The unit type is flat
				if (unit.get(2).equals("flat"))
				{
					msg.putExchangeHeader("participant", OrganizationID);
					msg.setReceiver(new AgentID(OrganizationID));
				}
				else
				{
					iterator1 = agentRole.iterator();
					while(iterator1.hasNext())
					{
						//Cogemos primero el rol, para que solamente compruebe la unidad que es el segundo elemento.
						rol_aux = iterator1.next();

						agentPositions.add(getAgentPosition(agent.getName(),OrganizationID,rol_aux, unit.get(2)));

						if (iterator1.next().equals(OrganizationID))
						{
							insideUnit = true;
						}
					}
					if (insideUnit)
					{
						if (unit.get(2).equals("team"))
						{
							msg.putExchangeHeader("participant", OrganizationID);
							msg.setReceiver(new AgentID(OrganizationID));
						}
						else if (unit.get(2).equals("hierarchy"))
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
			String UnitID) {
		serviceName = "LeaveRoleProcess";
		call = ServiceDescriptionLocation + "LeaveRoleProcess.owl AgentID=" + agent.getAid().name.replace('~', '@')
		+ " RoleID=" + RoleID + " UnitID=" + UnitID;
		return (String) this.sendInform();
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
	public ArrayList<String> informAgentRole(String AgentID){
		serviceName = "InformAgentRoleProcess";
		listResults.clear();

		call = ServiceDescriptionLocation
		+ "InformAgentRoleProcess.owl RequestedAgentID=" + AgentID;
		return (ArrayList<String>) this.sendInform();
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
	public ArrayList<String> informMembers(String UnitID, String RoleID, String PositionValue){

		serviceName ="InformMembersProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformMembersProcess.owl RoleID="
		+ RoleID + " UnitID=" + UnitID+ " PositionValue="+PositionValue;
		return (ArrayList<String>) this.sendInform();

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
	public ArrayList<String> informTargetNorms(String TargetName, String Type, String UnitName)
	{
		serviceName ="InformTargetNormsProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformTargetNormsProcess.owl TargetName="
		+ TargetName + " Type="+ Type+ " UnitName="+UnitName;
		return (ArrayList<String>) this.sendInform();

	}
	@SuppressWarnings("unchecked")
	public ArrayList<String> informNorm(String NormName, String UnitName)
	{
		serviceName ="InformNormProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformNormProcess.owl NormName="
		+ NormName + " UnitName="+UnitName;
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
	public ArrayList<String> informRoleProfiles(String UnitID)
	{
		serviceName ="InformRoleProfilesProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformRoleProfilesProcess.owl UnitID="
		+ UnitID;
		return (ArrayList<String>) this.sendInform();

	}


	@SuppressWarnings("unchecked")
	public ArrayList<String> informRole(String RoleName, String UnitName)
	{
		serviceName ="InformRoleProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformRoleProcess.owl UnitName="
		+ UnitName + " RoleName="+RoleName;
		return (ArrayList<String>) this.sendInform();

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
	public ArrayList<String> informUnit(String UnitID)
	{
		serviceName ="InformUnitProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformUnitProcess.owl UnitID=" + UnitID;
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
	public ArrayList<String> informUnitRoles(String UnitID)
	{
		serviceName ="InformUnitRolesProcess";
		listResults.clear();

		call = ServiceDescriptionLocation + "InformUnitRolesProcess.owl UnitID="
		+ UnitID;
		return (ArrayList<String>) this.sendInform();

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
	public int quantityMembers(String UnitID, String RoleID, String PositionValue)
	{
		serviceName ="QuantityMembersProcess";
		call = ServiceDescriptionLocation + "QuantityMembersProcess.owl RoleID="
		+ RoleID + " UnitID=" + UnitID + " PositionValue="+ PositionValue;
		return Integer.parseInt(this.sendInform().toString());


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
			String NormContent, String UnitName) {
		serviceName ="RegisterNormProcess";
		call = ServiceDescriptionLocation + "RegisterNormProcess.owl NormID="
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
			String Accessibility, String Visibility, String Position){
		serviceName ="RegisterRoleProcess";
		call = ServiceDescriptionLocation + "RegisterRoleProcess.owl RoleID="
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
			String ParentUnitID, String CreatorName) {
		serviceName ="RegisterUnitProcess";
		if (ParentUnitID == null)
			call = ServiceDescriptionLocation + "RegisterUnitProcess.owl  UnitID="
			+ UnitID + " Type=" + Type + " CreatorName=" + CreatorName + " ParentUnitID=";
		else
			call = ServiceDescriptionLocation + "RegisterUnitProcess.owl  UnitID="
			+ UnitID + " Type=" + Type + " CreatorName=" + CreatorName + " ParentUnitID="
			+ ParentUnitID;

		return 	(String) this.sendInform();

	}

	public String jointUnit(String UnitName, String ParentName) {
		serviceName ="JointUnitProcess";
		call = ServiceDescriptionLocation + "JointUnitProcess.owl  UnitName="
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
	public String deregisterNorm(String NormID, String UnitName)
	{
		serviceName ="DeregisterNormProcess";
		call = ServiceDescriptionLocation + "DeregisterNormProcess.owl  NormID="
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
	public String deregisterRole(String RoleID, String UnitID)
	{
		serviceName = "DeregisterRoleProcess";
		call = ServiceDescriptionLocation + "DeregisterRoleProcess.owl  RoleID="
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
	public String deregisterUnit(String UnitID)
	{
		serviceName ="DeregisterUnitProcess";
		call = ServiceDescriptionLocation + "DeregisterUnitProcess.owl  UnitID="
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
	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName 
	) {
		serviceName ="DeallocateRoleProcess";
		call = ServiceDescriptionLocation + "DeallocateRoleProcess.owl TargetAgentName=" + TargetAgentName
		+ " RoleName=" + RoleName + " UnitName=" + UnitName;
		return (String) this.sendInform();


	}


	public String allocateRole(String RoleName, String UnitName, String TargetAgentName)
	{
		serviceName ="AllocateRoleProcess";
		call = ServiceDescriptionLocation + "AllocateRoleProcess.owl RoleName=" + RoleName
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
	public String acquireRole(String RoleID, String UnitID)
	{
		serviceName ="AcquireRoleProcess";
		call = ServiceDescriptionLocation + "AcquireRoleProcess.owl RoleID=" + RoleID
		+ " UnitID=" + UnitID;


		return (String) this.sendInform();


	}

}

