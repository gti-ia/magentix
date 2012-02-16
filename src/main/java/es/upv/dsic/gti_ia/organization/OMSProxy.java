package es.upv.dsic.gti_ia.organization;



import java.util.ArrayList;

import es.upv.dsic.gti_ia.cAgents.CProcessor;
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
	
	public OMSProxy(CProcessor agent, String OMSServiceDesciptionLocation) {
		super(agent, "OMS",OMSServiceDesciptionLocation);


	}

	/**
	 * This class gives us the support to accede to the services of the OMS,
	 * Checked that the data contained in the file configuration/Settings.xml, the URL
	 * ServiceDescriptionLocation is not empty and is the correct path.
	 * 
	 *  @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol
	 *            
	 * 
	 */
	public OMSProxy(BaseAgent agent) {

		super(agent,"OMS");
		ServiceDescriptionLocation = c.getOMSServiceDesciptionLocation();

	}
	
	public OMSProxy(CProcessor myProcessor) {

		super(myProcessor,"OMS");
		ServiceDescriptionLocation = c.getOMSServiceDesciptionLocation();

	}

	/**
	 * Builds a new organizational message with the appropriate receivers according to the type of unit and position of the role that the agent is performing
	 * 
	 * @param OrganizationID represents the ID of the organization to which the agent wants to send a message
	 * @return returns the ACL message built 
	 * @throws THOMASException If unit not found, the agent is not inside the unit, the agent not play any role or 
	 * the agent only play the role creator.
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
				throw new THOMASException("The agent not play any role.");
			}
			else
			{

				for (ArrayList<String> agentRole : agentRoles)
				{
				
					rol_aux = agentRole.get(0);
					unit_aux = agentRole.get(1);


					ArrayList<String> informRole = this.informRole(rol_aux, unit_aux);
			
					String position = informRole.get(0);

					if (position!=null)
					{
						if (!position.equals("creator"))
							containsPositonNoCreator = true;
						agentPositions.add(position);
					}

				}

				//If only contains the role creator, then can not send a organizational message
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
						else //The agent is not inside the unit
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
	 * Service used for leaving a role inside a specific unit.
	 * 
	 * @param RoleID Identifier of the role
	 * @param UnitID Identifier of the organization unit
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If the role or unit not exists, 
	 * 	the agent not play the role or some parameter is empty or is invalid
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
	 * @param RequestedAgentName Identifier of the agent requested 
	 *    
	 * @return ArrayList<ArrayList<String>> The array list is formed by array lists of strings,
	 * each array list is formed by the fields (strings) role and unit
	 * 
	 * @throws THOMASException If the agent not exists or some parameter is empty or is invalid
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> informAgentRole(String RequestedAgentName) throws THOMASException
	{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		call = ServiceDescriptionLocation
		+ "InformAgentRole.owl RequestedAgentID=" + RequestedAgentName;
		result = (ArrayList<ArrayList<String>>) this.sendInform();
		return result;
	}

	/**
	 * Indicates entities that are members of a specific unit. Optionally, it is possible to specify a role and position of this unit, 
	 * so then only members playing this role or position are detailed. 
	 * 
	 * @param UnitID Identifier of the unit
	 * 
	 * @param RoleID Identifier of the role
	 *         
	 * @param Position Identifier of the position inside the unit, such as member, supervisor or subordinate
	 *          
	 * @return ArrayList<ArrayList<String>> The array list is formed by array list of strings, 
	 * each array list is formed by the fields (strings) agent name and role name
	 * 
	 * @throws THOMASException If unit not found, the role is not inside the unit, the agent is not allowed or some parameter is invalid
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
	 * Provides a role description of a specific unit.
	 * 
	 * @param RoleName Identifier of the role
	 * @param UnitName Identifier of the unit
	 * @return ArrayList<String> The array list is formed by the fields (strings) position, visibility and accessibility
	 * @throws THOMASException If unit not found, the role is not is not registered in the unit, the agent is not allowed, or some parameter is empty or is invalid
	 */
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
	 * Provides unit description.
	 *
	 * @param UnitID Identifier of the unit
	 *        
	 * @return ArrayList<String> The array list is formed by the fields (strings) unit type and parent name
	 * 
	 * @throws THOMASException If unit not found, the agent not play any role in unit or parent unit or some parameter is empty or is invalid
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
	 * @param UnitID Identifier of the unit
	 * 
	 * @return ArrayList<ArrayList<String>>  The array list is formed by array list of strings, 
	 * each array list is formed by the fields (strings) role name, position, visibility and accessibility
	 * 
	 * @throws THOMASException If unit not found 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>>  informUnitRoles(String UnitID) throws THOMASException
	{
		call = ServiceDescriptionLocation + "InformUnitRoles.owl UnitID="
		+ UnitID;
		return (ArrayList<ArrayList<String>>) this.sendInform();

	}

	/**
	 * Provides the number of current members of a specific unit. Optionally, if a role and position is indicated then only the quantity of 
	 * members playing this roles or position is detailed.
	 * 
 	 * @param UnitID Identifier of the unit
	 * 
	 * @param RoleID Identifier of the role
	 *         
	 * @param Position Identifier of the position inside the unit, such as member, supervisor or subordinate
	 *        
	 * @return Integer Quantity of members
	 * 
	 * @throws THOMASException If unit not found, the role is not inside the unit, the agent is not allowed or some parameter is invalid
	 */
	@SuppressWarnings("unchecked")
	public int quantityMembers(String UnitID, String RoleID, String PositionValue) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "QuantityMembers.owl RoleID="
		+ RoleID + " UnitID=" + UnitID + " PositionValue="+ PositionValue;
		ArrayList<String> a = (ArrayList<String>) this.sendInform();
		
		return Integer.parseInt(a.get(0));


	}


	/**
	 * Creates a new role inside a unit
	 *
	 * @param RoleID Identifier of the role
	 *            
	 * @param UnitID Identifier of the unit
	 *            
	 * @param Accessibility
	 *            considers two types of roles: (internal) internal roles, which are
	 *            assigned to internal agents of the system platform; and (external)
	 *            external roles, which can be enacted by any agent.
	 * @param Visibility
	 *            indicates whether agents can obtain information of this role
	 *            from outside the unit in which this role is defined (public)
	 *            or from inside (private). 
	 * @param Position
	 *            determines its structural position inside the unit, such as
	 *            member, supervisor or subordinate.
	 *            
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If unit not found, the role is already registered in the unit, the agent is not allowed or some parameter is empty or is invalid
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
	 * @param UnitID Identifier of the unit
	 *            
	 * @param Type
	 *            indicates the topology of the new unit: (i) Hierarchy, in
	 *            which a supervisor agent has control over other members; (ii)
	 *            Team, which are groups of agents that share a common goal,
	 *            collaborating and cooperating between them; and (iii) Flat, in
	 *            which there is none agent with control over other members.
	 * 
	 * @param ParentUnitID Identifier of the parent unit
	 *             
	 * @param CreatorName The name of the new creator role
	 *             
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If unit already exists, if the parent unit not exists, the agent is not allowed or some parameter is empty or is invalid
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

	/**
	 * Update the parent unit
	 * 
	 * @param UnitName Identifier of the unit
	 * @param ParentName Identifier of the new parent unit
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If unit or parent unit not found, the unit and parent unit are the same, the agent is not allowed or some parameter is empty or is invalid
	 */
	public String jointUnit(String UnitName, String ParentName)throws THOMASException
	{
	
		call = ServiceDescriptionLocation + "JointUnit.owl  UnitName="
		+ UnitName + " ParentName=" + ParentName;
		return 	(String) this.sendInform();


	}


	/**
	 * Removes a specific role from a unit. 
	 *
	 * @param RoleID Identifier of the role
	 * @param UnitID Identifier of the unit
	 *           
	 * @return  Status if result is OK
	 * 
	 * @throws THOMASException If unit not found, the role is not registered in the unit, if not allowed or some parameter is empty or is invalid
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
	 * @param UnitID Identifier of the unit
	 *           
	 * @return  Status if result is OK
	 * 
	 * @throws THOMASException If unit not found, if not allowed or some parameter is empty or is invalid
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
	 * @param RoleName Identifier of the role
	 *            
	 * @param UnitName Identifier of the unit
	 *        
	 * @param TargetAgentName Identifier of the agent
	 *       	
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If unit or role not found, if target agent not play the role, the agent is not allowed or some parameter is empty or is invalid
	 */
	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "DeallocateRole.owl TargetAgentName=" + TargetAgentName
		+ " RoleName=" + RoleName + " UnitName=" + UnitName;
		return (String) this.sendInform();


	}

	/**
	 * Forces an agent to acquire a specific role
	 *
	 * 
	 * @param RoleName Identifier of the role
	 *            
	 * @param UnitName Identifier of the unit
	 *        
	 * @param TargetAgentName Identifier of the agent
	 *       	
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If unit or role not found, if target agent already play the role, the agent is not allowed or some parameter is empty or is invalid
	 */
	public String allocateRole(String RoleName, String UnitName, String TargetAgentName) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "AllocateRole.owl RoleName=" + RoleName
		+ " UnitName=" + UnitName + " TargetAgentName="+ TargetAgentName;


		return (String) this.sendInform();


	}
	/**
	 * Service used for acquiring a role inside a specific unit.
	 * 
	 * @param RoleID Identifier of the role
	 * @param UnitID Identifier of the organization unit
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException If the role or unit not exists, 
	 * 	the agent play the role or some parameter is empty or is invalid
	 */
	public String acquireRole(String RoleID, String UnitID) throws THOMASException
	{
		
		call = ServiceDescriptionLocation + "AcquireRole.owl RoleID=" + RoleID
		+ " UnitID=" + UnitID;

		return (String) this.sendInform();
	}

}

