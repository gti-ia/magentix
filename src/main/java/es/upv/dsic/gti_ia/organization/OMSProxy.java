package es.upv.dsic.gti_ia.organization;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.THOMASMessages.MessageID;

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
public class OMSProxy extends THOMASProxy {

	ServiceTools           st = new ServiceTools();
	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages l10n;

	/**
	 * This class gives us the support to access to the services of the OMS
	 * 
	 * @param agent
	 *            is a Magentix2 agent, this agent implemented the communication
	 *            protocol.
	 * @param OMSServiceDesciptionLocation
	 *            The URL where the owl's document is located.
	 */
	public OMSProxy(BaseAgent agent, String OMSServiceDescriptionLocation) {
		super(agent, "OMS", OMSServiceDescriptionLocation);
		l10n = new THOMASMessages();

	}

	public OMSProxy(CProcessor agent, String OMSServiceDescriptionLocation) {
		super(agent, "OMS", OMSServiceDescriptionLocation);
		l10n = new THOMASMessages();

	}

	/**
	 * This class gives us the support to accede to the services of the OMS,
	 * Checked that the data contained in the file configuration/Settings.xml,
	 * the URL ServiceDescriptionLocation is not empty and is the correct path.
	 * 
	 * @param agent
	 *            is a Magentix2 Agent, this agent implemented the communication
	 *            protocol
	 * 
	 * 
	 */
	public OMSProxy(BaseAgent agent) {

		super(agent, "OMS");
		ServiceDescriptionLocation = c.getOMSServiceDescriptionLocation();
		l10n = new THOMASMessages();
	}

	public OMSProxy(CProcessor myProcessor) {

		super(myProcessor, "OMS");
		ServiceDescriptionLocation = c.getOMSServiceDescriptionLocation();
		l10n = new THOMASMessages();
	}

	/**
	 * Builds a new organizational message with the appropriate receivers
	 * according to the type of unit and position of the role that the agent is
	 * performing
	 * 
	 * @param OrganizationID
	 *            represents the ID of the organization to which the agent wants
	 *            to send a message
	 * @return returns the ACL message built
	 * @throws NotInUnitOrParentUnitException 
	 * @throws EmptyParametersException 
	 * @throws AgentNotExistsException 
	 * @throws VisibilityRoleException 
	 * @throws RoleNotExistsException 
	 * @throws InvalidVisibilityException 
	 * @throws THOMASException
	 *             If unit not found, the agent is not inside the unit, the
	 *             agent not play any role or the agent only play the role
	 *             creator.
	 */

	public ACLMessage buildOrganizationalMessage(String OrganizationID) throws UnitNotExistsException, 
	AgentNotInUnitException, NotPlaysAnyRoleException, InvalidUnitTypeException,
	OnlyPlaysCreatorException, EmptyParametersException, NotInUnitOrParentUnitException, 
	AgentNotExistsException, RoleNotExistsException, VisibilityRoleException, InvalidVisibilityException {
		ArrayList<String> unit;
		ArrayList<ArrayList<String>> agentRoles = null;
		ArrayList<String> agentPositions = new ArrayList<String>();

		String rol_aux;
		String unit_aux;

		boolean insideUnit = false;
		boolean containsPositonNoCreator = false;

		// Create a new ACLMessage
		ACLMessage msg = new ACLMessage();

		msg.setSender(agent.getAid());
		// agentName
		String agentName = agent.getAid().name;

		// Inform Unit
		unit = this.informUnit(OrganizationID);

		// If unit not exist
		if (unit.isEmpty()) {
			String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, OrganizationID);
			throw new UnitNotExistsException(message);

		} else {
			String unitType = unit.get(0);
			agentRoles = this.informAgentRole(agentName);

			if (agentRoles.isEmpty()) {
				String message = l10n.getMessage(MessageID.NOT_PLAYS_ANY_ROLE);
				throw new NotPlaysAnyRoleException(message);

			} else {
				for (ArrayList<String> agentRole : agentRoles) {
					rol_aux = agentRole.get(0);
					unit_aux = agentRole.get(1);

					ArrayList<String> informRole = this.informRole(rol_aux, unit_aux);
					String position = informRole.get(0);

					if (position != null) {
						if (!position.equals("creator"))
							containsPositonNoCreator = true;
						agentPositions.add(position);
					}
				}

				// If only contains the role creator, then can not send a
				// organizational message
				if (containsPositonNoCreator) {

					// agentPositions.clear();
					// The unit type is flat
					if (unitType.equals("flat")) {
						msg.putExchangeHeader("participant", OrganizationID);
						msg.setReceiver(new AgentID(OrganizationID));
					} else {
						for (ArrayList<String> agentRole : agentRoles) {
							// Cogemos primero el rol, para que solamente
							// compruebe la unidad que es el segundo elemento.

							rol_aux = agentRole.get(0);

							if (agentRole.get(1).equals(OrganizationID)) {
								insideUnit = true;
							}

						}
						if (insideUnit) {
							if (unitType.equals("team")) {
								msg.putExchangeHeader("participant", OrganizationID);
								msg.setReceiver(new AgentID(OrganizationID));
							} else if (unitType.equals("hierarchy")) {
								// Sacamos la posición del agente
								// Debemos elegir el de mayor

								if (agentPositions.contains("supervisor")) {
									msg.putExchangeHeader("supervisor", OrganizationID);
									msg.putExchangeHeader("participant", OrganizationID);
								} else if (agentPositions.contains("subordinate")) {
									msg.putExchangeHeader("supervisor", OrganizationID);

								}

								msg.setReceiver(new AgentID(OrganizationID));

							} else {
								String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
								throw new InvalidUnitTypeException(message);
							}

						} else {
							// The agent is not inside the unit
							String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, OrganizationID, agentName);
							throw new AgentNotInUnitException(message);
						}
					}
				} else {
					String message = l10n.getMessage(MessageID.ONLY_PLAYS_CREATOR, agentName);
					throw new OnlyPlaysCreatorException(message);
				}
			}

		}

		return msg;
	}

	/**
	 * Service used for leaving a role inside a specific unit.
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * @param UnitID
	 *            Identifier of the organization unit
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If the role or unit not exists, the agent not play the role
	 *             or some parameter is empty or is invalid
	 */
	public String leaveRole(String RoleID, String UnitID) throws 
	EmptyParametersException, UnitNotExistsException, RoleNotExistsException, NotPlaysRoleException, 
	MySQLException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);

		call = st.buildServiceContent("LeaveRole", inputs);

		String result = new String();
		try{
			result = (String) this.sendInform();

		}catch(EmptyParametersException e)
		{
			throw e;
		}
		catch(UnitNotExistsException e)
		{
			throw e;
		}
		catch(RoleNotExistsException e)
		{
			throw e;
		}
		catch(NotPlaysRoleException e)
		{
			throw e;
		}
		catch(MySQLException e)
		{
			throw e;
		}
		catch (THOMASException e) {
			
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Requesting the list of roles and units in which an agent is in a specific
	 * moment.
	 * 
	 * @param RequestedAgentID
	 *            Identifier of the agent requested
	 * 
	 * @return ArrayList<ArrayList<String>> The array list is formed by array
	 *         lists of strings, each array list is formed by the fields
	 *         (strings) role and unit
	 * 
	 * @throws THOMASException
	 *             If the agent not exists or some parameter is empty or is
	 *             invalid
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> informAgentRole(String RequestedAgentID) throws 
	EmptyParametersException, AgentNotExistsException, InvalidVisibilityException{
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RequestedAgentID", RequestedAgentID);

		call = st.buildServiceContent("InformAgentRole", inputs);

		try
		{
			result = (ArrayList<ArrayList<String>>) this.sendInform();
		}
		catch (EmptyParametersException e) {
			
			throw e;
		}
		catch (AgentNotExistsException e) {
			
			throw e;
		}
		catch (InvalidVisibilityException e) {
			
			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Indicates entities that are members of a specific unit. Optionally, it is
	 * possible to specify a role and position of this unit, so then only
	 * members playing this role or position are detailed.
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * 
	 * @param PositionID
	 *            Identifier of the position inside the unit, such as member,
	 *            supervisor or subordinate
	 * 
	 * @return ArrayList<ArrayList<String>> The array list is formed by array
	 *         list of strings, each array list is formed by the fields
	 *         (strings) agent name and role name
	 * 
	 * @throws THOMASException
	 *             If unit not found, the role is not inside the unit, the agent
	 *             is not allowed or some parameter is invalid
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> informMembers(String UnitID, String RoleID, String PositionID) throws 
	EmptyParametersException, UnitNotExistsException, AgentNotExistsException, InvalidRolePositionException,
	VisibilityRoleException, RoleNotExistsException,  UnitNotExistsException, RoleNotExistsException, 
	 IDUnitTypeNotFoundException,  InvalidVisibilityException {

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);
		inputs.put("PositionID", PositionID);

		call = st.buildServiceContent("InformMembers", inputs);

		try
		{
			return (ArrayList<ArrayList<String>>) this.sendInform();

		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (AgentNotExistsException e) {

			throw e;
		}
		catch (InvalidRolePositionException e) {

			throw e;
		}
		catch (VisibilityRoleException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (IDUnitTypeNotFoundException e) {

			throw e;
		}
		catch (InvalidVisibilityException e) {

			throw e;
		}
		catch (THOMASException e) {

			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Provides a role description of a specific unit.
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * @param UnitID
	 *            Identifier of the unit
	 * @return ArrayList<String> The array list is formed by the fields
	 *         (strings) position, visibility and accessibility
	 * @throws THOMASException
	 *             If unit not found, the role is not is not registered in the
	 *             unit, the agent is not allowed, or some parameter is empty or
	 *             is invalid
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> informRole(String RoleID, String UnitID) throws EmptyParametersException,UnitNotExistsException
	,RoleNotExistsException,VisibilityRoleException{

		ArrayList<String> result = new ArrayList<String>();

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("UnitID", UnitID);
		inputs.put("RoleID", RoleID);

		call = st.buildServiceContent("InformRole", inputs);

		try
		{
			result = (ArrayList<String>) this.sendInform();

		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (VisibilityRoleException e) {

			throw e;
		}
		catch (THOMASException e) {
			
			e.printStackTrace();
		}
		return result;

	}

	/**
	 * Provides unit description.
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @return ArrayList<String> The array list is formed by the fields
	 *         (strings) unit type and parent name
	 * 
	 * @throws THOMASException
	 *             If unit not found, the agent not play any role in unit or
	 *             parent unit or some parameter is empty or is invalid
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String> informUnit(String UnitID) throws EmptyParametersException,
	UnitNotExistsException, InvalidUnitTypeException, NotInUnitOrParentUnitException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("UnitID", UnitID);


		call = st.buildServiceContent("InformUnit", inputs);

		try
		{
			return (ArrayList<String>) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (InvalidUnitTypeException e) {

			throw e;
		}
		catch (NotInUnitOrParentUnitException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Used for requesting the list of roles that have been registered inside a
	 * unit.
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @return ArrayList<ArrayList<String>> The array list is formed by array
	 *         list of strings, each array list is formed by the fields
	 *         (strings) role name, position, visibility and accessibility
	 * 
	 * @throws THOMASException
	 *             If unit not found
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ArrayList<String>> informUnitRoles(String UnitID) throws EmptyParametersException,
	UnitNotExistsException, InvalidVisibilityException {

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("UnitID", UnitID);

		call = st.buildServiceContent("InformUnitRoles", inputs);
		try
		{
			return (ArrayList<ArrayList<String>>) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (InvalidVisibilityException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Provides the number of current members of a specific unit. Optionally, if
	 * a role and position is indicated then only the quantity of members
	 * playing this roles or position is detailed.
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * 
	 * @param PositionID
	 *            Identifier of the position inside the unit, such as member,
	 *            supervisor or subordinate
	 * 
	 * @return Integer Quantity of members
	 * 
	 * @throws THOMASException
	 *             If unit not found, the role is not inside the unit, the agent
	 *             is not allowed or some parameter is invalid
	 */
	@SuppressWarnings("unchecked")
	public int quantityMembers(String UnitID, String RoleID, String PositionID) throws EmptyParametersException,
	UnitNotExistsException, AgentNotExistsException, InvalidRolePositionException, VisibilityRoleException, 
	RoleNotExistsException, InvalidVisibilityException, InvalidPositionException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);
		inputs.put("PositionID", PositionID);

		call = st.buildServiceContent("QuantityMembers", inputs);

		try
		{
			ArrayList<String> a = (ArrayList<String>) this.sendInform();
		
			return Integer.parseInt(a.get(0));
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (AgentNotExistsException e) {

			throw e;
		}
		catch (InvalidRolePositionException e) {

			throw e;
		}
		catch (VisibilityRoleException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (InvalidVisibilityException e) {

			throw e;
		}
		catch (InvalidPositionException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Creates a new role inside a unit
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @param AccessibilityID
	 *            considers two types of roles: (internal) internal roles, which
	 *            are assigned to internal agents of the system platform; and
	 *            (external) external roles, which can be enacted by any agent.
	 * @param VisibilityID
	 *            indicates whether agents can obtain information of this role
	 *            from outside the unit in which this role is defined (public)
	 *            or from inside (private).
	 * @param PositionID
	 *            determines its structural position inside the unit, such as
	 *            member, supervisor or subordinate.
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit not found, the role is already registered in the
	 *             unit, the agent is not allowed or some parameter is empty or
	 *             is invalid
	 */
	public String registerRole(String RoleID, String UnitID, String AccessibilityID, String VisibilityID, String PositionID) throws EmptyParametersException,
	UnitNotExistsException, RoleExistsInUnitException, AgentNotInUnitException, NotCreatorAgentInUnitException, InvalidUnitTypeException,
	NotMemberOrCreatorInUnitException, NotSupervisorOrCreatorInUnitException, InvalidPositionException, 
	 InvalidAccessibilityException, InvalidVisibilityException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);
		inputs.put("AccessibilityID", AccessibilityID);
		inputs.put("PositionID", PositionID);
		inputs.put("VisibilityID", VisibilityID);

		call = st.buildServiceContent("RegisterRole", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (RoleExistsInUnitException e) {

			throw e;
		}
		catch (AgentNotInUnitException e) {

			throw e;
		}
		catch (NotCreatorAgentInUnitException e) {

			throw e;
		}
		catch (InvalidUnitTypeException e) {

			throw e;
		}
		catch (NotMemberOrCreatorInUnitException e) {

			throw e;
		}
		catch (NotSupervisorOrCreatorInUnitException e) {

			throw e;
		}
		catch (InvalidPositionException e) {

			throw e;
		}
		catch (InvalidAccessibilityException e) {

			throw e;
		}
		catch (InvalidVisibilityException e) {

			throw e;
		}
		catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Creates a new empty unit in the organization, with a specific structure,
	 * creatorName and parent unit.
	 * 
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @param TypeID
	 *            indicates the topology of the new unit: (i) Hierarchy, in
	 *            which a supervisor agent has control over other members; (ii)
	 *            Team, which are groups of agents that share a common goal,
	 *            collaborating and cooperating between them; and (iii) Flat, in
	 *            which there is none agent with control over other members.
	 * 
	 * @param ParentUnitID
	 *            Identifier of the parent unit
	 * 
	 * @param CreatorID
	 *            The name of the new creator role
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit already exists, if the parent unit not exists, the
	 *             agent is not allowed or some parameter is empty or is invalid
	 */
	public String registerUnit(String UnitID, String TypeID, String ParentUnitID, String CreatorID) throws EmptyParametersException,
	UnitExistsException, NotCreatorInParentUnitException, ParentUnitNotExistsException, InvalidVisibilityException,
	InvalidAccessibilityException, InvalidPositionException, InsertingTableException, InvalidUnitTypeException{

		if (ParentUnitID == null) {

			HashMap<String, String> inputs = new HashMap<String, String>();

			inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
			inputs.put("UnitID", UnitID);
			inputs.put("TypeID", TypeID);
			inputs.put("CreatorID", CreatorID);

			call = st.buildServiceContent("RegisterUnit", inputs);

		} else {

			HashMap<String, String> inputs = new HashMap<String, String>();

			inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
			inputs.put("UnitID", UnitID);
			inputs.put("TypeID", TypeID);
			inputs.put("CreatorID", CreatorID);
			inputs.put("ParentUnitID", ParentUnitID);

			call = st.buildServiceContent("RegisterUnit", inputs);

		}
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitExistsException e) {

			throw e;
		}
		catch (NotCreatorInParentUnitException e) {

			throw e;
		}
		catch (ParentUnitNotExistsException e) {

			throw e;
		}
		catch (InvalidVisibilityException e) {

			throw e;
		}
		catch (InvalidAccessibilityException e) {

			throw e;
		}
		catch (InvalidPositionException e) {

			throw e;
		}
		catch (InsertingTableException e) {

			throw e;
		}
		catch (InvalidUnitTypeException e) {

			throw e;
		}
		catch (THOMASException e) {
		
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update the parent unit
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * @param ParentUnitID
	 *            Identifier of the new parent unit
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit or parent unit not found, the unit and parent unit
	 *             are the same, the agent is not allowed or some parameter is
	 *             empty or is invalid
	 */
	public String jointUnit(String UnitID, String ParentUnitID) throws EmptyParametersException,
	UnitNotExistsException, ParentUnitNotExistsException, AgentNotInUnitException, NotCreatorInUnitException,
	NotCreatorInParentUnitException, VirtualParentException, SameUnitException, MySQLException, ParentUnitNotExistsException, UnitNotExistsException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("UnitID", UnitID);
		inputs.put("ParentUnitID", ParentUnitID);

		call = st.buildServiceContent("JointUnit", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (ParentUnitNotExistsException e) {

			throw e;
		}
		catch (AgentNotInUnitException e) {

			throw e;
		}
		catch (NotCreatorInUnitException e) {

			throw e;
		}
		catch (NotCreatorInParentUnitException e) {

			throw e;
		}
		catch (VirtualParentException e) {

			throw e;
		}
		catch (SameUnitException e) {

			throw e;
		}
		catch (MySQLException e) {

			throw e;
		}
		catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Removes a specific role from a unit.
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit not found, the role is not registered in the unit, if
	 *             not allowed or some parameter is empty or is invalid
	 */
	public String deregisterRole(String RoleID, String UnitID) throws EmptyParametersException, UnitNotExistsException,
	RoleNotExistsException, RoleContainsNormsException, RoleInUseException, AgentNotInUnitException,
	NotCreatorException, InvalidUnitTypeException, NotMemberOrCreatorInUnitException, NotSupervisorOrCreatorInUnitException, 
	MySQLException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);

		call = st.buildServiceContent("DeregisterRole", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (RoleContainsNormsException e) {

			throw e;
		}
		catch (RoleInUseException e) {

			throw e;
		}
		catch (AgentNotInUnitException e) {

			throw e;
		}
		catch (NotCreatorException e) {

			throw e;
		}
		catch (InvalidUnitTypeException e) {

			throw e;
		}
		catch (NotMemberOrCreatorInUnitException e) {

			throw e;
		}
		catch (NotSupervisorOrCreatorInUnitException e) {

			throw e;
		}
		catch (MySQLException e) {

			throw e;
		}
		catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Removes a unit from an organization
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit not found, if not allowed or some parameter is empty
	 *             or is invalid
	 */
	public String deregisterUnit(String UnitID) throws EmptyParametersException,
	UnitNotExistsException, VirtualUnitException, NotCreatorInUnitOrParentUnitException,
	SubunitsInUnitException, NotCreatorAgentInUnitException, DeletingTableException, InvalidPositionException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("UnitID", UnitID);

		call = st.buildServiceContent("DeregisterUnit", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (VirtualUnitException e) {

			throw e;
		}
		catch (NotCreatorInUnitOrParentUnitException e) {

			throw e;
		}
		catch (SubunitsInUnitException e) {

			throw e;
		}
		catch (NotCreatorAgentInUnitException e) {

			throw e;
		}
		catch (DeletingTableException e) {

			throw e;
		}
		catch (InvalidPositionException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Forces an agent to leave a specific role
	 * 
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @param TargetAgentID
	 *            Identifier of the agent
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit or role not found, if target agent not play the role,
	 *             the agent is not allowed or some parameter is empty or is
	 *             invalid
	 */
	public String deallocateRole(String RoleID, String UnitID, String TargetAgentID) throws EmptyParametersException,
	UnitNotExistsException, RoleNotExistsException, InvalidUnitTypeException, NotInUnitAndNotCreatorException, 
	NotMemberOrCreatorInUnitException, AgentNotInUnitException, NotSupervisorOrCreatorInUnitException, NotPlaysRoleException,
	SameAgentNameException, MySQLException {

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("TargetAgentID", TargetAgentID);
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);

		call = st.buildServiceContent("DeallocateRole", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (InvalidUnitTypeException e) {

			throw e;
		}
		catch (NotInUnitAndNotCreatorException e) {

			throw e;
		}
		catch (NotMemberOrCreatorInUnitException e) {

			throw e;
		}
		catch (AgentNotInUnitException e) {

			throw e;
		}
		catch (NotSupervisorOrCreatorInUnitException e) {

			throw e;
		}
		catch (NotPlaysRoleException e) {

			throw e;
		}
		catch (SameAgentNameException e) {

			throw e;
		}
		catch (MySQLException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Forces an agent to acquire a specific role
	 * 
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * 
	 * @param UnitID
	 *            Identifier of the unit
	 * 
	 * @param TargetAgentID
	 *            Identifier of the agent
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If unit or role not found, if target agent already play the
	 *             role, the agent is not allowed or some parameter is empty or
	 *             is invalid
	 */
	public String allocateRole(String RoleID, String UnitID, String TargetAgentID) throws EmptyParametersException, 
	UnitNotExistsException, RoleNotExistsException, InvalidUnitTypeException, NotCreatorException, 
	NotMemberOrCreatorInUnitException, AgentNotInUnitException, NotSupervisorOrCreatorInUnitException,
	PlayingRoleException, SameAgentNameException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);
		inputs.put("TargetAgentID", TargetAgentID);

		call = st.buildServiceContent("AllocateRole", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (InvalidUnitTypeException e) {

			throw e;
		}
		catch (NotCreatorException e) {

			throw e;
		}
		catch (NotMemberOrCreatorInUnitException e) {

			throw e;
		}
		catch (AgentNotInUnitException e) {

			throw e;
		}
		catch (NotSupervisorOrCreatorInUnitException e) {

			throw e;
		}
		catch (PlayingRoleException e) {

			throw e;
		}
		catch (SameAgentNameException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Service used for acquiring a role inside a specific unit.
	 * 
	 * @param RoleID
	 *            Identifier of the role
	 * @param UnitID
	 *            Identifier of the organization unit
	 * 
	 * @return Status if result is OK
	 * 
	 * @throws THOMASException
	 *             If the role or unit not exists, the agent play the role or
	 *             some parameter is empty or is invalid
	 */
	public String acquireRole(String RoleID, String UnitID) throws EmptyParametersException, 
	UnitNotExistsException, RoleNotExistsException, NotInUnitOrParentUnitException, NotSupervisorOrCreatorInUnitException,
	PlayingRoleException{

		HashMap<String, String> inputs = new HashMap<String, String>();

		inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
		inputs.put("RoleID", RoleID);
		inputs.put("UnitID", UnitID);

		call = st.buildServiceContent("AcquireRole", inputs);
		try
		{
			return (String) this.sendInform();
		}
		catch (EmptyParametersException e) {

			throw e;
		}
		catch (UnitNotExistsException e) {

			throw e;
		}
		catch (RoleNotExistsException e) {

			throw e;
		}
		catch (NotInUnitOrParentUnitException e) {

			throw e;
		}
		catch (NotSupervisorOrCreatorInUnitException e) {

			throw e;
		}
		catch (PlayingRoleException e) {

			throw e;
		}catch (THOMASException e) {
			
			e.printStackTrace();
			return null;
		}
	}

}
