package es.upv.dsic.gti_ia.organization;

import java.util.ArrayList;
import java.util.HashMap;

import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;
import es.upv.dsic.gti_ia.organization.exception.AgentNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.DeletingTableException;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.IDUnitTypeNotFoundException;
import es.upv.dsic.gti_ia.organization.exception.InsertingTableException;
import es.upv.dsic.gti_ia.organization.exception.InvalidAccessibilityException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidRolePositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidUnitTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidVisibilityException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorAgentInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotMemberOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysAnyRoleException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysRoleException;
import es.upv.dsic.gti_ia.organization.exception.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.OnlyPlaysCreatorException;
import es.upv.dsic.gti_ia.organization.exception.ParentUnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.PlayingRoleException;
import es.upv.dsic.gti_ia.organization.exception.RoleContainsNormsException;
import es.upv.dsic.gti_ia.organization.exception.RoleExistsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.RoleInUseException;
import es.upv.dsic.gti_ia.organization.exception.RoleNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.SameAgentNameException;
import es.upv.dsic.gti_ia.organization.exception.SameUnitException;
import es.upv.dsic.gti_ia.organization.exception.SubunitsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.THOMASException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages;
import es.upv.dsic.gti_ia.organization.exception.UnitExistsException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.VirtualParentException;
import es.upv.dsic.gti_ia.organization.exception.VirtualUnitException;
import es.upv.dsic.gti_ia.organization.exception.VisibilityRoleException;
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;

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
     * 
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws AgentNotInUnitException
     *             If agent does not play any role in unit.
     * @throws NotPlaysAnyRoleException
     *             If agent does not play any role.
     * @throws OnlyPlaysCreatorException
     *             If agent only plays the role creator.
     * @throws NotInUnitOrParentUnitException
     *             If agent does not play any role in unit or parent unit.
     * @throws EmptyParametersException
     *             If any parameter is empty or null
     * @throws AgentNotExistsException
     *             If agent not exists.
     * @throws VisibilityRoleException
     *             If visibility role is not valid.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws InvalidVisibilityException
     *             If visibility is not valid.
     * @throws InvalidUnitTypeException
     *             If unit type  is not valid.
     */

    public ACLMessage buildOrganizationalMessage(String OrganizationID) throws UnitNotExistsException, AgentNotInUnitException, NotPlaysAnyRoleException, InvalidUnitTypeException, OnlyPlaysCreatorException, EmptyParametersException, NotInUnitOrParentUnitException, AgentNotExistsException, RoleNotExistsException, VisibilityRoleException, InvalidVisibilityException {
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
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws NotPlaysRoleException
     *             If agent does not play the role.
     * @throws MySQLException
     *             If a MySql exception occurs.
     */
    public String leaveRole(String RoleID, String UnitID) throws EmptyParametersException, UnitNotExistsException, RoleNotExistsException, NotPlaysRoleException, MySQLException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("LeaveRole", inputs);

        String result = new String();
        try {
            result = (String) this.sendInform();

        } catch (EmptyParametersException e) {
            throw e;
        } catch (UnitNotExistsException e) {
            throw e;
        } catch (RoleNotExistsException e) {
            throw e;
        } catch (NotPlaysRoleException e) {
            throw e;
        } catch (MySQLException e) {
            throw e;
        } catch (THOMASException e) {

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
     * @return ArrayList<ArrayList<String>> The array list is formed by array
     *         lists of strings, each array list is formed by the fields
     *         (strings) role and unit
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws AgentNotExistsException
     *             If agent not exists.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList<String>> informAgentRole(String RequestedAgentID) throws EmptyParametersException, AgentNotExistsException{
        ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RequestedAgentID", RequestedAgentID);

        call = st.buildServiceContent("InformAgentRole", inputs);

        try {
            result = (ArrayList<ArrayList<String>>) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (AgentNotExistsException e) {

            throw e;
        } catch (THOMASException e) {

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
     * @param RoleID
     *            Identifier of the role
     * @param PositionID
     *            Identifier of the position inside the unit, such as member,
     *            supervisor or subordinate
     * @return ArrayList<ArrayList<String>> The array list is formed by array
     *         list of strings, each array list is formed by the fields
     *         (strings) agent name and role name
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws AgentNotExistsException
     *             If agent not exists.
     * @throws InvalidRolePositionException
     *             If role position is invalid.
     * @throws VisibilityRoleException
     *             If visibility of the role is not valid.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws UnitNotExistsException
     *             If unit not exists.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList<String>> informMembers(String UnitID, String RoleID, String PositionID) throws EmptyParametersException, UnitNotExistsException, AgentNotExistsException, InvalidRolePositionException, VisibilityRoleException, RoleNotExistsException, UnitNotExistsException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);
        inputs.put("PositionID", PositionID);

        call = st.buildServiceContent("InformMembers", inputs);

        try {
            return (ArrayList<ArrayList<String>>) this.sendInform();

        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (AgentNotExistsException e) {

            throw e;
        } catch (InvalidRolePositionException e) {

            throw e;
        } catch (VisibilityRoleException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (THOMASException e) {

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
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws VisibilityRoleException
     *             If visibility role is not valid.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> informRole(String RoleID, String UnitID) throws EmptyParametersException, UnitNotExistsException, RoleNotExistsException, VisibilityRoleException {

        ArrayList<String> result = new ArrayList<String>();

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("UnitID", UnitID);
        inputs.put("RoleID", RoleID);

        call = st.buildServiceContent("InformRole", inputs);

        try {
            result = (ArrayList<String>) this.sendInform();

        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (VisibilityRoleException e) {

            throw e;
        } catch (THOMASException e) {

            e.printStackTrace();
        }
        return result;

    }

    /**
     * Provides unit description.
     * 
     * @param UnitID
     *            Identifier of the unit
     * @return ArrayList<String> The array list is formed by the fields
     *         (strings) unit type and parent name
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws InvalidUnitTypeException
     *             If unit type is not valid.
     * @throws NotInUnitOrParentUnitException
     *             If agent does not play any role in unit or parent unit.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<String> informUnit(String UnitID) throws EmptyParametersException, UnitNotExistsException, InvalidUnitTypeException, NotInUnitOrParentUnitException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("InformUnit", inputs);

        try {
            return (ArrayList<String>) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (InvalidUnitTypeException e) {

            throw e;
        } catch (NotInUnitOrParentUnitException e) {

            throw e;
        } catch (THOMASException e) {

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
     * @return ArrayList<ArrayList<String>> The array list is formed by array
     *         list of strings, each array list is formed by the fields
     *         (strings) role name, position, visibility and accessibility
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws InvalidVisibilityException
     *             If visibility is not valid.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ArrayList<String>> informUnitRoles(String UnitID) throws EmptyParametersException, UnitNotExistsException, InvalidVisibilityException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("InformUnitRoles", inputs);
        try {
            return (ArrayList<ArrayList<String>>) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (InvalidVisibilityException e) {

            throw e;
        } catch (THOMASException e) {

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
     * @param RoleID
     *            Identifier of the role
     * @param PositionID
     *            Identifier of the position inside the unit, such as member,
     *            supervisor or subordinate
     * @return Integer Quantity of members
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws AgentNotExistsException
     *             If agent not exists.
     * @throws InvalidRolePositionException
     *             If role position is not valid.
     * @throws VisibilityRoleException
     *             If role visibility is not valid.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws InvalidVisibilityException
     *             If visibility is not valid.
     * @throws InvalidPositionException
     *             If position is not valid.
     */
    @SuppressWarnings("unchecked")
    public int quantityMembers(String UnitID, String RoleID, String PositionID) throws EmptyParametersException, UnitNotExistsException, AgentNotExistsException, InvalidRolePositionException, VisibilityRoleException, RoleNotExistsException, InvalidVisibilityException, InvalidPositionException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);
        inputs.put("PositionID", PositionID);

        call = st.buildServiceContent("QuantityMembers", inputs);

        try {
            ArrayList<String> a = (ArrayList<String>) this.sendInform();

            return Integer.parseInt(a.get(0));
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (AgentNotExistsException e) {

            throw e;
        } catch (InvalidRolePositionException e) {

            throw e;
        } catch (VisibilityRoleException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (InvalidVisibilityException e) {

            throw e;
        } catch (InvalidPositionException e) {

            throw e;
        } catch (THOMASException e) {

            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Creates a new role inside a unit.
     * 
     * @param RoleID
     *            Identifier of the role
     * @param UnitID
     *            Identifier of the unit
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
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleExistsInUnitException
     *             If role not exists.
     * @throws AgentNotInUnitException
     *             If agent is not inside the unit.
     * @throws NotInUnitAndNotCreatorException
     *             If agent is not inside the unit and does not play any role
     *             with position creator.
     * @throws InvalidUnitTypeException
     *             If unit type is not valid.
     * @throws NotMemberOrCreatorInUnitException
     *             If agent does not play any role with position member or
     *             creator inside the unit.
     * @throws NotSupervisorOrCreatorInUnitException
     *             If agent does not play any role with position supervisor or
     *             creatir inside the unit.
     * @throws InvalidPositionException
     *             If position is not valid.
     * @throws InvalidAccessibilityException
     *             If accessibility is not valid.
     * @throws InvalidVisibilityException
     *             If visibility is not valid.
     * @throws MySQLException
     *             If a MySql exception occurs.
     */
    public String registerRole(String RoleID, String UnitID, String AccessibilityID, String VisibilityID, String PositionID) throws EmptyParametersException, UnitNotExistsException, RoleExistsInUnitException, AgentNotInUnitException, NotInUnitAndNotCreatorException, InvalidUnitTypeException, NotMemberOrCreatorInUnitException, NotSupervisorOrCreatorInUnitException, InvalidPositionException, InvalidAccessibilityException, InvalidVisibilityException, MySQLException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);
        inputs.put("AccessibilityID", AccessibilityID);
        inputs.put("PositionID", PositionID);
        inputs.put("VisibilityID", VisibilityID);

        call = st.buildServiceContent("RegisterRole", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (RoleExistsInUnitException e) {

            throw e;
        } catch (AgentNotInUnitException e) {

            throw e;
        } catch (NotInUnitAndNotCreatorException e) {

            throw e;
        } catch (InvalidUnitTypeException e) {

            throw e;
        } catch (NotMemberOrCreatorInUnitException e) {

            throw e;
        } catch (NotSupervisorOrCreatorInUnitException e) {

            throw e;
        } catch (InvalidPositionException e) {

            throw e;
        } catch (InvalidAccessibilityException e) {

            throw e;
        } catch (InvalidVisibilityException e) {

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
     * Creates a new empty unit in the organization, with a specific structure,
     * creatorName and parent unit.
     * 
     * @param UnitID
     *            Identifier of the unit
     * @param TypeID
     *            indicates the topology of the new unit: (i) Hierarchy, in
     *            which a supervisor agent has control over other members; (ii)
     *            Team, which are groups of agents that share a common goal,
     *            collaborating and cooperating between them; and (iii) Flat, in
     *            which there is none agent with control over other members.
     * @param ParentUnitID
     *            Identifier of the parent unit
     * @param CreatorID
     *            The name of the new creator role
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitExistsException
     *             If unit exists.
     * @throws NotCreatorInParentUnitException
     *             If agent does not play any role with position creator inside
     *             the parent unit.
     * @throws ParentUnitNotExistsException
     *             If agent parent unit not exists.
     * @throws InvalidVisibilityException
     *             If visibility is not valid.
     * @throws InvalidAccessibilityException
     *             If accessibility is not valid.
     * @throws InvalidPositionException
     *             If position is not valid.
     * @throws InsertingTableException
     *             If any error occurs inserting table.
     * @throws InvalidUnitTypeException
     *             If unit type is invalid.
     */
    public String registerUnit(String UnitID, String TypeID, String ParentUnitID, String CreatorID) throws EmptyParametersException, UnitExistsException, NotCreatorInParentUnitException, ParentUnitNotExistsException, InvalidVisibilityException, InvalidAccessibilityException, InvalidPositionException, InsertingTableException, InvalidUnitTypeException, MySQLException {

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
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitExistsException e) {

            throw e;
        } catch (NotCreatorInParentUnitException e) {

            throw e;
        } catch (ParentUnitNotExistsException e) {

            throw e;
        } catch (InvalidVisibilityException e) {

            throw e;
        } catch (InvalidAccessibilityException e) {

            throw e;
        } catch (InvalidPositionException e) {

            throw e;
        } catch (InsertingTableException e) {

            throw e;
        } catch (InvalidUnitTypeException e) {

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
     * Update the parent unit.
     * 
     * @param UnitID
     *            Identifier of the unit
     * @param ParentUnitID
     *            Identifier of the new parent unit
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws ParentUnitNotExistsException
     *             If parent unit not exists.
     * @throws AgentNotInUnitException
     *             If agent is not inside the unit.
     * @throws NotCreatorInUnitException
     *             If agent does not play any role with position creator inside
     *             the unit.
     * @throws NotCreatorInParentUnitException
     *             If agent does not play any role with position creator inside
     *             the parent unit.
     * @throws VirtualParentException
     *             The Parent Unit can not be changed.
     * @throws SameUnitException
     *             If the unit and the parent unit are same.
     * @throws MySQLException
     */
    public String jointUnit(String UnitID, String ParentUnitID) throws EmptyParametersException, UnitNotExistsException, ParentUnitNotExistsException, AgentNotInUnitException, NotCreatorInUnitException, NotCreatorInParentUnitException, VirtualParentException, SameUnitException, MySQLException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("UnitID", UnitID);
        inputs.put("ParentUnitID", ParentUnitID);

        call = st.buildServiceContent("JointUnit", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (ParentUnitNotExistsException e) {

            throw e;
        } catch (AgentNotInUnitException e) {

            throw e;
        } catch (NotCreatorInUnitException e) {

            throw e;
        } catch (NotCreatorInParentUnitException e) {

            throw e;
        } catch (VirtualParentException e) {

            throw e;
        } catch (SameUnitException e) {

            throw e;
        } catch (MySQLException e) {

            throw e;
        } catch (THOMASException e) {

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
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws RoleContainsNormsException
     *             If role contains associated norms.
     * @throws RoleInUseException
     *             If role is played by some agents.
     * @throws AgentNotInUnitException
     *             If agent does not play any role in unit.
     * @throws NotInUnitAndNotCreatorException
     *             If agent does not play any role in unit and does not play any
     *             role with position creator.
     * @throws InvalidUnitTypeException
     *             If unit type is invalid.
     * @throws NotMemberOrCreatorInUnitException
     *             If agent does not play any role with position member or
     *             creator inside the unit.
     * @throws NotSupervisorOrCreatorInUnitException
     *             If agent does not play any role with position superverisor or
     *             creator inside the unit.
     * @throws MySQLException
     * 
     */
    public String deregisterRole(String RoleID, String UnitID) throws EmptyParametersException, UnitNotExistsException, RoleNotExistsException, RoleContainsNormsException, RoleInUseException, AgentNotInUnitException, NotInUnitAndNotCreatorException, InvalidUnitTypeException, NotMemberOrCreatorInUnitException, NotSupervisorOrCreatorInUnitException, MySQLException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("DeregisterRole", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (RoleContainsNormsException e) {

            throw e;
        } catch (RoleInUseException e) {

            throw e;
        } catch (AgentNotInUnitException e) {

            throw e;
        } catch (NotInUnitAndNotCreatorException e) {

            throw e;
        } catch (InvalidUnitTypeException e) {

            throw e;
        } catch (NotMemberOrCreatorInUnitException e) {

            throw e;
        } catch (NotSupervisorOrCreatorInUnitException e) {

            throw e;
        } catch (MySQLException e) {

            throw e;
        } catch (THOMASException e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Removes a unit from an organization.
     * 
     * @param UnitID
     *            Identifier of the unit
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             Unit not exists.
     * @throws VirtualUnitException
     *             If unit is the virtual unit.
     * @throws NotCreatorInUnitOrParentUnitException
     *             The agent does not play any role with position creator in
     *             unit or parent unit.
     * @throws SubunitsInUnitException
     *             The unit contains subunits.
     * @throws NotCreatorAgentInUnitException
     *             If agent does not play any role with position creator in
     *             unit.
     * @throws DeletingTableException
     *             If any error occurs deleting table.
     * @throws InvalidPositionException
     *             If position is invalid.
     */
    public String deregisterUnit(String UnitID) throws EmptyParametersException, UnitNotExistsException, VirtualUnitException, NotCreatorInUnitOrParentUnitException, SubunitsInUnitException, NotCreatorAgentInUnitException, DeletingTableException, InvalidPositionException, MySQLException{

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("DeregisterUnit", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (VirtualUnitException e) {

            throw e;
        } catch (NotCreatorInUnitOrParentUnitException e) {

            throw e;
        } catch (SubunitsInUnitException e) {

            throw e;
        } catch (NotCreatorAgentInUnitException e) {

            throw e;
        } catch (DeletingTableException e) {

            throw e;
        } catch (InvalidPositionException e) {

            throw e;
    	} catch (MySQLException e) {

        	throw e;
        }
        catch (THOMASException e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Forces an agent to leave a specific role.
     * 
     * @param RoleID
     *            Identifier of the role
     * @param UnitID
     *            Identifier of the unit
     * @param TargetAgentID
     *            Identifier of the agent
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws InvalidUnitTypeException
     *             If unit type is not valid.
     * @throws NotInUnitAndNotCreatorException
     *             If agent does not play any role in unit and does not play any
     *             role with position creator.
     * @throws NotMemberOrCreatorInUnitException
     *             If agent does not play any role with position member or
     *             creator inside the unit.
     * @throws AgentNotInUnitException
     *             If agent does not play any role in unit.
     * @throws NotSupervisorOrCreatorInUnitException
     *             If agent does not play any role with position supervisor or
     *             creator in unit.
     * @throws NotPlaysRoleException
     *             If agent does not play the role.
     * @throws SameAgentNameException
     *             If the agent is the same than target agent.
     * @throws MySQLException
     */
    public String deallocateRole(String RoleID, String UnitID, String TargetAgentID) throws EmptyParametersException, UnitNotExistsException, RoleNotExistsException, InvalidUnitTypeException, NotInUnitAndNotCreatorException, NotMemberOrCreatorInUnitException, AgentNotInUnitException, NotSupervisorOrCreatorInUnitException, NotPlaysRoleException, SameAgentNameException, MySQLException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("TargetAgentID", TargetAgentID);
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("DeallocateRole", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (InvalidUnitTypeException e) {

            throw e;
        } catch (NotInUnitAndNotCreatorException e) {

            throw e;
        } catch (NotMemberOrCreatorInUnitException e) {

            throw e;
        } catch (AgentNotInUnitException e) {

            throw e;
        } catch (NotSupervisorOrCreatorInUnitException e) {

            throw e;
        } catch (NotPlaysRoleException e) {

            throw e;
        } catch (SameAgentNameException e) {

            throw e;
        } catch (MySQLException e) {

            throw e;
        } catch (THOMASException e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * Forces an agent to acquire a specific role.
     * 
     * @param RoleID
     *            Identifier of the role
     * @param UnitID
     *            Identifier of the unit
     * @param TargetAgentID
     *            Identifier of the agent
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws InvalidUnitTypeException
     *             If unit type is not valid.
     * @throws NotInUnitAndNotCreatorException
     *             If agent does not play any role in unit and does not play any
     *             role with position creator.
     * @throws NotMemberOrCreatorInUnitException
     *             If agent does not play any role with position member or
     *             creator inside the unit.
     * @throws AgentNotInUnitException
     *             If agent does not play any role inside the unit.
     * @throws NotSupervisorOrCreatorInUnitException
     *             If agent does not play any role with position supervisor or
     *             creator inside the unit.
     * @throws PlayingRoleException
     *             If agent is already playing the role.
     * @throws SameAgentNameException
     *             If the agent is the same than target agent.
     */
    public String allocateRole(String RoleID, String UnitID, String TargetAgentID) throws EmptyParametersException, UnitNotExistsException, RoleNotExistsException, InvalidUnitTypeException, NotInUnitAndNotCreatorException, NotMemberOrCreatorInUnitException, AgentNotInUnitException, NotSupervisorOrCreatorInUnitException, PlayingRoleException, SameAgentNameException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);
        inputs.put("TargetAgentID", TargetAgentID);

        call = st.buildServiceContent("AllocateRole", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (InvalidUnitTypeException e) {

            throw e;
        } catch (NotInUnitAndNotCreatorException e) {

            throw e;
        } catch (NotMemberOrCreatorInUnitException e) {

            throw e;
        } catch (AgentNotInUnitException e) {

            throw e;
        } catch (NotSupervisorOrCreatorInUnitException e) {

            throw e;
        } catch (PlayingRoleException e) {

            throw e;
        } catch (SameAgentNameException e) {

            throw e;
        } catch (THOMASException e) {

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
     * @return Status if result is OK
     * @throws EmptyParametersException
     *             If any parameter is empty or null.
     * @throws UnitNotExistsException
     *             If unit not exists.
     * @throws RoleNotExistsException
     *             If role not exists.
     * @throws NotInUnitOrParentUnitException
     *             If agent does not play any role in unit or parent unit.
     * @throws NotSupervisorOrCreatorInUnitException
     *             If agent does not play any role with position supervisor or
     *             creator in unit.
     * @throws PlayingRoleException
     *             If agent is already playing the role.
     */
    public String acquireRole(String RoleID, String UnitID) throws EmptyParametersException, UnitNotExistsException, RoleNotExistsException, NotInUnitOrParentUnitException, NotSupervisorOrCreatorInUnitException, PlayingRoleException {

        HashMap<String, String> inputs = new HashMap<String, String>();

        inputs.put("AgentID", agent.getAid().name.replace('~', '@'));
        inputs.put("RoleID", RoleID);
        inputs.put("UnitID", UnitID);

        call = st.buildServiceContent("AcquireRole", inputs);
        try {
            return (String) this.sendInform();
        } catch (EmptyParametersException e) {

            throw e;
        } catch (UnitNotExistsException e) {

            throw e;
        } catch (RoleNotExistsException e) {

            throw e;
        } catch (NotInUnitOrParentUnitException e) {

            throw e;
        } catch (NotSupervisorOrCreatorInUnitException e) {

            throw e;
        } catch (PlayingRoleException e) {

            throw e;
        } catch (THOMASException e) {

            e.printStackTrace();
            return null;
        }
    }

}
