package es.upv.dsic.gti_ia.organization;

import jason.asSyntax.Literal;
import jason.asSyntax.Rule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.norms.BeliefDataBaseInterface;
import es.upv.dsic.gti_ia.norms.Norm;
import es.upv.dsic.gti_ia.norms.NormParser;
import es.upv.dsic.gti_ia.norms.Normative_Protocol;
import es.upv.dsic.gti_ia.organization.exception.AgentNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.AgentNotInUnitException;
import es.upv.dsic.gti_ia.organization.exception.EmptyParametersException;
import es.upv.dsic.gti_ia.organization.exception.ForbiddenNormException;
import es.upv.dsic.gti_ia.organization.exception.InvalidPositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidRolePositionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidUnitTypeException;
import es.upv.dsic.gti_ia.organization.exception.MySQLException;
import es.upv.dsic.gti_ia.organization.exception.NormExistsInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NormNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorAgentInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotCreatorInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitAndNotCreatorException;
import es.upv.dsic.gti_ia.organization.exception.NotInUnitOrParentUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotMemberOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotPlaysRoleException;
import es.upv.dsic.gti_ia.organization.exception.NotSupervisorOrCreatorInUnitException;
import es.upv.dsic.gti_ia.organization.exception.NotValidIdentifierException;
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
import es.upv.dsic.gti_ia.organization.exception.THOMASMessages.MessageID;
import es.upv.dsic.gti_ia.organization.exception.UnitExistsException;
import es.upv.dsic.gti_ia.organization.exception.UnitNotExistsException;
import es.upv.dsic.gti_ia.organization.exception.VirtualParentException;
import es.upv.dsic.gti_ia.organization.exception.VirtualUnitException;
import es.upv.dsic.gti_ia.organization.exception.VisibilityRoleException;

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
	private BeliefDataBaseInterface belifeDbInterface;
	private CAgent omsAgent = null;
	/**
	 * Used for retrieve local messages.
	 */
	private THOMASMessages    l10n;

	private enum Flags {
		CASE_A, CASE_B, CASE_C, CASE_D
	};

	public OMSInterface(CAgent oms) {
		dbInterface = new DataBaseInterface();
		belifeDbInterface = new BeliefDataBaseInterface();
		l10n = new THOMASMessages();
		omsAgent = oms;
	}

	/**
	 * Checks if parameter is null or empty
	 * 
	 * @param parameter
	 * @return true, the parameter is not null or empty, false: the parameter is
	 *         null or empty
	 */
	boolean checkParameter(String parameter) {
		if (parameter != null) {
			if (!parameter.equals("")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Creates a new empty unit in the organization, with a specific type and
	 * creatorName.
	 * 
	 * @param UnitName
	 *            Identifier of the organization unit
	 * @param UnitType
	 *            Indicates the topology of the new unit: (i) Hierarchy,
	 *            (ii)Team, (iii) Flat.
	 * @param AgentName
	 *            Identifier of the agent
	 * @param CreatorName
	 *            Identifier of the new role with creator position
	 * @return Returns <unitname + " created">
	 */
	public String registerUnit(String UnitName, String UnitType, String AgentName, String CreatorName) {

		return this.registerUnit(UnitName, UnitType, null, AgentName, CreatorName);
	}

	/**
	 * Creates a new empty unit in the organization, with a specific type,
	 * parent unit and creatorName.
	 * 
	 * @param UnitName
	 *            Identifier of the organization unit
	 * @param UnitType
	 *            Indicates the topology of the new unit: (i) Hierarchy,
	 *            (ii)Team, (iii) Flat.
	 * @param ParentUnitName
	 *            Identifier of the parent organizational unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @param CreatorName
	 *            Identifier of the new role with creator position
	 * @return Returns <unitname + created>
	 */
	public String registerUnit(String UnitName, String UnitType, String ParentUnitName, String AgentName, String CreatorName) {

		String resultXML = "<response>\n<serviceName>RegisterUnit</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(CreatorName) && checkParameter(UnitName)) {
				if (dbInterface.checkValidIdentifier(UnitName))
				{
					if (!dbInterface.checkUnit(UnitName)) {
						if (ParentUnitName != null && !ParentUnitName.equals("")) {
							if (!dbInterface.checkUnit(ParentUnitName)) {
								String message = l10n.getMessage(MessageID.PARENT_UNIT_NOT_EXISTS, ParentUnitName);
								throw new ParentUnitNotExistsException(message);
							}

						} else {
							ParentUnitName = "virtual";
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent norms
						// ----------------------
						// --------------------------------------------------------------------------------
						if (checkPermitNorms(AgentName, ParentUnitName, "registerUnit("+UnitName.toLowerCase()+","+UnitType.toLowerCase()+","+ParentUnitName.toLowerCase()+","+AgentName.toLowerCase()+","+CreatorName.toLowerCase()+")"))
						{

							String result = dbInterface.createUnit(UnitName, UnitType, ParentUnitName, AgentName, CreatorName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, ParentUnitName, "registerUnit("+UnitName.toLowerCase()+","+UnitType.toLowerCase()+","+ParentUnitName.toLowerCase()+","+AgentName.toLowerCase()+","+CreatorName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking structural norms
						// ----------------------------
						// --------------------------------------------------------------------------------

						if (dbInterface.checkPositionInUnit(AgentName, "creator", ParentUnitName)) {

							String result = dbInterface.createUnit(UnitName, UnitType, ParentUnitName, AgentName, CreatorName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						} else {
							String message = l10n.getMessage(MessageID.NOT_CREATOR_IN_PARENT_UNIT, AgentName);
							throw new NotCreatorInParentUnitException(message);
						}

					} else {
						String message = l10n.getMessage(MessageID.UNIT_EXISTS, UnitName);
						throw new UnitExistsException(message);
					}
				}
				String message = l10n.getMessage(MessageID.NOT_VALID_IDENTIFIER);
				throw new NotValidIdentifierException(message);
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for deleting a unit in the organization
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <unitName + deleted>
	 */
	public String deregisterUnit(String UnitName, String AgentName) {

		boolean play = false;
		String resultXML = "<response>\n<serviceName>DeregisterUnit</serviceName>\n";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (!UnitName.equals("virtual")) {
						if (dbInterface.checkNoCreatorAgentsInUnit(UnitName)) {
							String message = l10n.getMessage(MessageID.NOT_CREATOR_AGENT_IN_UNIT);
							throw new NotCreatorAgentInUnitException(message);
						}
						if (dbInterface.checkSubUnits(UnitName)) {
							String message = l10n.getMessage(MessageID.SUBUNITS_IN_UNIT, UnitName);
							throw new SubunitsInUnitException(message);
						}
						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent
						// norms
						// ----------------------
						// --------------------------------------------------------------------------------
						if (checkPermitNorms(AgentName, UnitName, "deregisterUnit("+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{

							String result = dbInterface.deleteUnit(UnitName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "deregisterUnit("+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}
						// --------------------------------------------------------------------------------
						// ------------------------- Checking structural norms
						// ----------------------------
						// --------------------------------------------------------------------------------
						if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName)) {
							play = true;
						}

						ArrayList<String> parentsUnit = dbInterface.getParentsUnit(UnitName);

						for (String parentUnit : parentsUnit) {
							if (dbInterface.checkPositionInUnit(AgentName, "creator", parentUnit)) {
								play = true;
							}
						}

						if (play) {

							String result = dbInterface.deleteUnit(UnitName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;

						} else {
							String message = l10n.getMessage(MessageID.NOT_CREATOR_IN_UNIT_OR_PARENT_UNIT);
							throw new NotCreatorInUnitOrParentUnitException(message);
						}

					} else {
						String message = l10n.getMessage(MessageID.VIRTUAL_UNIT);
						throw new VirtualUnitException(message);
					}


				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}

			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);
		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for registering a new role inside a unit.
	 * 
	 * @param RoleName
	 *            Identifier of the new role
	 * @param UnitName
	 *            Identifier of the organizational unit
	 * @param accessibility
	 *            Considers two types of roles: internal or external
	 * @param Visibility
	 *            Position inside the unit, such as member,supervisor or
	 *            subordinate.
	 * @param Position
	 *            Is defined (public) or from inside (private)
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <roleName + created>
	 */
	public String registerRole(String RoleName, String UnitName, String accessibility, String Visibility, String Position, String AgentName) {
		String unitType = "";
		String resultXML = "<response>\n<serviceName>RegisterRole</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(accessibility) && checkParameter(Visibility) && checkParameter(Position)) {

				if (dbInterface.checkValidIdentifier(RoleName))
				{
					if (dbInterface.checkUnit(UnitName)) {
						if (!dbInterface.checkRole(RoleName, UnitName)) {

							unitType = dbInterface.getUnitType(UnitName);

							if (unitType.equals("hierarchy")) {
								if (!Position.equals("supervisor") && !Position.equals("subordinate") && !Position.equals("creator")) {
									String message = l10n.getMessage(MessageID.INVALID_POSITION, Position);
									throw new InvalidPositionException(message);
								}

							} else if (unitType.equals("team") || unitType.equals("flat")) {

								if (!Position.equals("member") && !Position.equals("creator")) {
									String message = l10n.getMessage(MessageID.INVALID_POSITION, Position);
									throw new InvalidPositionException(message);
								}

							} else {
								String message = l10n.getMessage(MessageID.INVALID_POSITION, Position);
								throw new InvalidPositionException(message);
							}
							// --------------------------------------------------------------------------------
							// ------------------------- Checking domain-dependent
							// norms ----------------------
							// --------------------------------------------------------------------------------
							if (checkPermitNorms(AgentName, UnitName, "registerRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+accessibility.toLowerCase()+","+Visibility.toLowerCase()+","+Position.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{

								String result = dbInterface.createRole(RoleName, UnitName, accessibility, Visibility, Position);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else if (checkFordibbenNorms(AgentName, UnitName, "registerRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+accessibility.toLowerCase()+","+Visibility.toLowerCase()+","+Position.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{
								String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
								throw new ForbiddenNormException(message);
							}

							if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
								if (unitType.equals("hierarchy")) {

									if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName)) {
										String result = dbInterface.createRole(RoleName, UnitName, accessibility, Visibility, Position);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
										throw new NotSupervisorOrCreatorInUnitException(message);
									}

								} else if (unitType.equals("team") || unitType.equals("flat")) {

									if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName)) {
										String result = dbInterface.createRole(RoleName, UnitName, accessibility, Visibility, Position);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT, AgentName, UnitName);
										throw new NotMemberOrCreatorInUnitException(message);
									}

								} else {
									String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
									throw new InvalidUnitTypeException(message);
								}

							} else {
								if (unitType.equals("flat")) {

									if (dbInterface.checkPosition(AgentName, "creator")) {
										String result = dbInterface.createRole(RoleName, UnitName, accessibility, Visibility, Position);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR, AgentName);
										throw new NotInUnitAndNotCreatorException(message);
									}

								} else {
									String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
									throw new AgentNotInUnitException(message);
								}
							}
						} else {
							String message = l10n.getMessage(MessageID.ROLE_EXISTS_IN_UNIT, RoleName);
							throw new RoleExistsInUnitException(message);
						}
					} else {
						String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
						throw new UnitNotExistsException(message);
					}

				}
				String message = l10n.getMessage(MessageID.NOT_VALID_IDENTIFIER);
				throw new NotValidIdentifierException(message);
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used to deregister a role
	 * 
	 * @param RoleName
	 *            Identifier of the role
	 * @param UnitName
	 *            Identifier of the unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <roleName + deleted>
	 */
	public String deregisterRole(String RoleName, String UnitName, String AgentName) {

		String unitType = "";
		String resultXML = "<response>\n<serviceName>DeregisterRole</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkRole(RoleName, UnitName)) {

						if (!dbInterface.checkTargetRoleNorm(RoleName, UnitName)) {
							if (!dbInterface.checkPlayedRoleInUnit(RoleName, UnitName)) {

								// --------------------------------------------------------------------------------
								// ------------------------- Checking domain-dependent
								// norms ----------------------
								// --------------------------------------------------------------------------------
								if (checkPermitNorms(AgentName, UnitName, "deregisterRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
								{

									String result = dbInterface.deleteRole(RoleName, UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;
								}
								else if (checkFordibbenNorms(AgentName, UnitName, "deregisterRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
								{
									String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
									throw new ForbiddenNormException(message);
								}

								unitType = dbInterface.getUnitType(UnitName);

								if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
									if (unitType.equals("hierarchy")) {

										if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName)) {
											String result = dbInterface.deleteRole(RoleName, UnitName);

											resultXML += "<status>Ok</status>\n";
											resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
											resultXML += "</response>";

											return resultXML;

										} else {
											String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
											throw new NotSupervisorOrCreatorInUnitException(message);
										}
									} else if (unitType.equals("team") || unitType.equals("flat")) {

										if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName)) {
											String result = dbInterface.deleteRole(RoleName, UnitName);

											resultXML += "<status>Ok</status>\n";
											resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
											resultXML += "</response>";

											return resultXML;

										} else {
											String message = l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT, AgentName, UnitName);
											throw new NotMemberOrCreatorInUnitException(message);
										}
									} else {
										String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
										throw new InvalidUnitTypeException(message);
									}

								} else {
									if (unitType.equals("flat")) {
										if (dbInterface.checkPosition(AgentName, "creator")) {
											String result = dbInterface.deleteRole(RoleName, UnitName);

											resultXML += "<status>Ok</status>\n";
											resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
											resultXML += "</response>";

											return resultXML;

										} else {
											String message = l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR, AgentName, UnitName);
											throw new NotInUnitAndNotCreatorException(message);
										}
									} else {
										String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
										throw new AgentNotInUnitException(message);
									}
								}
							} else {
								String message = l10n.getMessage(MessageID.ROLE_IN_USE, RoleName);
								throw new RoleInUseException(message);
							}
						} else {
							String message = l10n.getMessage(MessageID.ROLE_CONTAINS_NORMS, RoleName);
							throw new RoleContainsNormsException(message);
						}
					} else {
						String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
						throw new RoleNotExistsException(message);
					}
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}

			}
			throw new THOMASException("Invalid. Empty parameters.");
		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}
	/**
	 * Method used to register a new norm
	 * @param UnitName
	 * @param NormContent
	 * @param AgentName
	 * @return Returns <norm + registered>
	 */
	public String registerNorm(String UnitName, String NormContent, String AgentName)
	{
		String resultXML = "<response>\n<serviceName>RegisterNorm</serviceName>\n";
		String unitType = "";
		NormParser parser = null;
		Norm parsedNorm = null;

		try
		{
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(NormContent) && checkParameter(UnitName)) {

				if (dbInterface.checkUnit(UnitName)) {


					//-------------- norm parser------------------
					StringBuffer StringBuffer1 = new StringBuffer(NormContent);

					InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

					parser = new NormParser(input);

					//The method parser returns the norm.
					parsedNorm = parser.parser();

					//--------------------------------------------

					String normName = parsedNorm.getId();

					if (!dbInterface.checkNormName(normName, UnitName))
					{					
						if (!parsedNorm.getTargetValue().equals("_"))
						{
							if (parsedNorm.getTargetType().equals("roleName"))
							{
								String roleName = parsedNorm.getTargetValue();
								if (!dbInterface.checkRole(roleName, UnitName))
								{
									String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, roleName, UnitName);
									throw new RoleNotExistsException(message);
								}		
							}else if (parsedNorm.getTargetType().equals("positionName"))
							{
								String positionName = parsedNorm.getTargetValue();
								unitType = dbInterface.getUnitType(UnitName);

								if (unitType.equals("hierarchy"))
								{
									if (!positionName.equals("supervisor") && !positionName.equals("subordinate") &&
											!positionName.equals("creator"))
									{
										String message = l10n.getMessage(MessageID.INVALID_POSITION, positionName);
										throw new InvalidPositionException(message);
									}	
								}
								else if (unitType.equals("team") || unitType.equals("flat"))
								{
									if (!positionName.equals("member") && !positionName.equals("creator"))
									{
										String message = l10n.getMessage(MessageID.INVALID_POSITION, positionName);
										throw new InvalidPositionException(message);
									}
								}
								else
								{
									String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE,unitType );
									throw new InvalidUnitTypeException(message);
								}
							}
						}
						
						if (checkPermitNorms(AgentName, UnitName, "registerNorm("+UnitName.toLowerCase()+","+parsedNorm.getDeontic().toLowerCase()+","+parsedNorm.getTargetType().toLowerCase() +","+parsedNorm.getTargetValue().toLowerCase()+","+parsedNorm.getActionName().toLowerCase()+","+ AgentName.toLowerCase()+")"))
						{

							Literal normRule = belifeDbInterface.buildNormRule(parsedNorm);
							String result = dbInterface.createNorm(UnitName, NormContent, parsedNorm, normRule);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "registerNorm("+UnitName.toLowerCase()+","+parsedNorm.getDeontic().toLowerCase()+","+parsedNorm.getTargetType().toLowerCase() +","+parsedNorm.getTargetValue().toLowerCase()+","+parsedNorm.getActionName().toLowerCase()+","+ AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}

						unitType = dbInterface.getUnitType(UnitName);

						if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {

							if (unitType.equals("hierarchy")) {

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName)) {

									Literal normRule = belifeDbInterface.buildNormRule(parsedNorm);
									String result = dbInterface.createNorm(UnitName, NormContent, parsedNorm, normRule);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
									throw new NotSupervisorOrCreatorInUnitException(message);
								}

							} else if (unitType.equals("team") || unitType.equals("flat")) {

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName)) {

									Literal normRule = belifeDbInterface.buildNormRule(parsedNorm);
									String result = dbInterface.createNorm(UnitName, NormContent, parsedNorm, normRule);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT, AgentName, UnitName);
									throw new NotMemberOrCreatorInUnitException(message);
								}

							} else {
								String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
								throw new InvalidUnitTypeException(message);
							}

						} else {
							if (unitType.equals("flat")) {

								if (dbInterface.checkPosition(AgentName, "creator")) {
									Literal normRule = belifeDbInterface.buildNormRule(parsedNorm);
									String result = dbInterface.createNorm(UnitName, NormContent, parsedNorm, normRule);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";
									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR, AgentName);
									throw new NotInUnitAndNotCreatorException(message);
								}

							} else {
								String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
								throw new AgentNotInUnitException(message);
							}
						}

					}
					else {
						String message = l10n.getMessage(MessageID.NORM_EXISTS_IN_UNIT, normName);
						throw new NormExistsInUnitException(message);
					}
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}else
			{
				String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
				throw new EmptyParametersException(message);
			}
		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}
	/**
	 * Method used in order to deregister a norm.
	 * 
	 * @param NormName
	 * @param UnitName
	 * @param AgentName
	 * @return Returns <norm + deleted>
	 */
	public String deregisterNorm(String NormName, String UnitName, String AgentName){
		String resultXML = "<response>\n<serviceName>DeregisterNorm</serviceName>\n";
		String unitType = "";
		try
		{

			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(NormName) && checkParameter(UnitName) && checkParameter(AgentName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkNormName(NormName, UnitName))
					{
						if (checkPermitNorms(AgentName, UnitName, "deregisterNorm("+NormName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{


							String result = dbInterface.deleteNorm(NormName, UnitName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "deregisterNorm("+NormName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}


						unitType = dbInterface.getUnitType(UnitName);

						if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {

							if (unitType.equals("hierarchy")) {

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName)) {


									String result = dbInterface.deleteNorm(NormName, UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
									throw new NotSupervisorOrCreatorInUnitException(message);
								}

							} else if (unitType.equals("team") || unitType.equals("flat")) {

								if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName) || dbInterface.checkPositionInUnit(AgentName, "member", UnitName)) {


									String result = dbInterface.deleteNorm(NormName, UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT, AgentName, UnitName);
									throw new NotMemberOrCreatorInUnitException(message);
								}

							} else {
								String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
								throw new InvalidUnitTypeException(message);
							}

						} else {
							if (unitType.equals("flat")) {

								if (dbInterface.checkPosition(AgentName, "creator")) {

									String result = dbInterface.deleteNorm(NormName, UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";
									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR, AgentName);
									throw new NotInUnitAndNotCreatorException(message);
								}

							} else {
								String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
								throw new AgentNotInUnitException(message);
							}
						}



					}else {
						String message = l10n.getMessage(MessageID.NORM_NOT_EXISTS, NormName, UnitName);
						throw new NormNotExistsException(message);
					}	
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}else
			{
				String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
				throw new EmptyParametersException(message);
			}
		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}
	/**
	 * Method used for acquiring a role in a specific unit.
	 * 
	 * @param UnitName
	 *            Identifier of the organizational unit
	 * @param RoleName
	 *            Identifier of the role
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <roleName + acquired>
	 */
	public String acquireRole(String RoleName, String UnitName, String AgentName) {

		boolean contains = false;
		String resultXML = "<response>\n<serviceName>AcquireRole</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkRole(RoleName, UnitName)) {

						// --------------------------------------------------------------------------------
						// ------------------------- Checking structural norms
						// ----------------------------
						// --------------------------------------------------------------------------------
						if (dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName)) {
							String message = l10n.getMessage(MessageID.PLAYING_ROLE, AgentName);
							throw new PlayingRoleException(message);

						} else {
							// --------------------------------------------------------------------------------
							// ------------------------- Checking domain-dependent
							// norms ----------------------
							// --------------------------------------------------------------------------------
							if (checkPermitNorms(AgentName, UnitName, "acquireRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{

								String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else if (checkFordibbenNorms(AgentName, UnitName, "acquireRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{
								String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
								throw new ForbiddenNormException(message);
							}

							ArrayList<String> informRole = dbInterface.getInformRole(RoleName, UnitName);
							String accessibility = informRole.get(0);
							String position = informRole.get(2);

							if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
								ArrayList<ArrayList<String>> rolesInUnit = dbInterface.getInformAgentRolesPlayedInUnit(UnitName, AgentName);

								for (ArrayList<String> role : rolesInUnit) {
									if (role.contains("creator") || role.contains("supervisor") || role.contains("member")) {
										contains = true;
									}

								}

								if (contains) {
									String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;
								} else {
									if (!position.equals("creator") && !position.equals("supervisor")) {
										String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
										throw new NotSupervisorOrCreatorInUnitException(message);
									}

								}

							} else {
								if (accessibility.equals("external")) {
									String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else if (dbInterface.checkAgentInUnit(AgentName, dbInterface.getParentsUnit(UnitName).get(0)))// Check
									// agent
									// in
									// parent
									// unit
								{
									String result = dbInterface.acquireRole(UnitName, RoleName, AgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_IN_UNIT_OR_PARENT_UNIT, AgentName);
									throw new NotInUnitOrParentUnitException(message);
								}
							}

						}
					}// checkRole
					else {
						String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
						throw new RoleNotExistsException(message);
					}
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}

			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}

	}

	/**
	 * Method used for an agent to leave a role in a unit.
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param RoleName
	 *            Identifier of the role
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <roleName + left>
	 */
	public String leaveRole(String RoleName, String UnitName, String AgentName) {

		String resultXML = "<response>\n<serviceName>LeaveRole</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkRole(RoleName, UnitName)) {

						if (!dbInterface.checkAgentPlaysRole(AgentName, RoleName, UnitName)) {
							String message = l10n.getMessage(MessageID.NOT_PLAYS_ROLE, AgentName, RoleName);
							throw new NotPlaysRoleException(message);
						} else {

							// --------------------------------------------------------------------------------
							// ------------------------- Checking domain-dependent
							// norms ----------------------
							// --------------------------------------------------------------------------------
							if (checkPermitNorms(AgentName, UnitName, "leaveRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{

								String result = dbInterface.leaveRole(UnitName, RoleName, AgentName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else if (checkFordibbenNorms(AgentName, UnitName, "leaveRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{
								String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
								throw new ForbiddenNormException(message);
							}

							String result = dbInterface.leaveRole(UnitName, RoleName, AgentName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;

						}
					} else {
						String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName,UnitName);
						throw new RoleNotExistsException(message);
					}

				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used in order to assign a role to an agent inside a unit
	 * 
	 * @param RoleName
	 *            Identifier of the role
	 * @param UnitName
	 *            Identifier of the unit
	 * @param TargetAgentName
	 *            Identifier of the target agent
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <roleName + acquired>
	 */
	public String allocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName) {

		String resultXML = "<response>\n<serviceName>AllocateRole</serviceName>\n";
		try {



			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(TargetAgentName)) {
				if (dbInterface.checkValidIdentifier(TargetAgentName))
				{

					if (dbInterface.checkUnit(UnitName)) {
						if (dbInterface.checkRole(RoleName, UnitName)) {

							if (TargetAgentName.equals(AgentName)) {
								String message = l10n.getMessage(MessageID.SAME_AGENT_NAME);
								throw new SameAgentNameException(message);
							}

							if (dbInterface.checkAgentPlaysRole(TargetAgentName, RoleName, UnitName)) {
								String message = l10n.getMessage(MessageID.PLAYING_ROLE, AgentName);
								throw new PlayingRoleException(message);
							}

							// --------------------------------------------------------------------------------
							// ------------------------- Checking domain-dependent
							// norms ----------------------
							// --------------------------------------------------------------------------------
							if (checkPermitNorms(AgentName, UnitName, "allocateRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+TargetAgentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{

								String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else if (checkFordibbenNorms(AgentName, UnitName, "allocateRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+TargetAgentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{
								String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
								throw new ForbiddenNormException(message);
							}
							// --------------------------------------------------------------------------------
							// ------------------------- Checking structural norms
							// ----------------------------
							// --------------------------------------------------------------------------------

							String type = dbInterface.getUnitType(UnitName);

							if (type.equals("hierarchy")) {
								if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
									if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName)) {
										String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
										throw new NotSupervisorOrCreatorInUnitException(message);
									}
								} else {
									String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
									throw new AgentNotInUnitException(message);
								}
							} else if (type.equals("team")) {
								if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
									String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
									throw new AgentNotInUnitException(message);
								}
							} else if (type.equals("flat")) {
								if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {

									if (dbInterface.checkPositionInUnit(AgentName, "member", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName)) {
										String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT, AgentName, UnitName);
										throw new NotMemberOrCreatorInUnitException(message);
									}

								} else {
									if (dbInterface.checkPosition(AgentName, "creator")) {

										String result = dbInterface.acquireRole(UnitName, RoleName, TargetAgentName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
										resultXML += "</response>";

										return resultXML;

									} else {
										String message = l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR, AgentName);
										throw new NotInUnitAndNotCreatorException(message);
									}
								}
							} else {
								String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, type);
								throw new InvalidUnitTypeException(message);
							}
						} else {
							String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
							throw new RoleNotExistsException(message);
						}
					} else {
						String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
						throw new UnitNotExistsException(message);
					}
				}
				String message = l10n.getMessage(MessageID.NOT_VALID_IDENTIFIER);
				throw new NotValidIdentifierException(message);
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used in order to remove a specific role to an agent inside a unit
	 * 
	 * @param RoleName
	 *            Identifier of the role
	 * @param UnitName
	 *            Identifier of the unit
	 * @param TargetAgentName
	 *            Identifier of the target agent
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <roleName + left>
	 */
	public String deallocateRole(String RoleName, String UnitName, String TargetAgentName, String AgentName) {

		String resultXML = "<response>\n<serviceName>DeallocateRole</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(TargetAgentName)) {

				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkRole(RoleName, UnitName)) {
						// TODO Falta por hacer la comprobación del targetName,
						// por ahora no se dispone de suficiente información
						// para ello.
						if (TargetAgentName.equals(AgentName)) {
							String message = l10n.getMessage(MessageID.SAME_AGENT_NAME);
							throw new SameAgentNameException(message);
						}

						if (!dbInterface.checkAgentPlaysRole(TargetAgentName, RoleName, UnitName)) {
							String message = l10n.getMessage(MessageID.NOT_PLAYS_ROLE, TargetAgentName, RoleName);
							throw new NotPlaysRoleException(message);
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent
						// norms ----------------------
						// --------------------------------------------------------------------------------
						if (checkPermitNorms(AgentName, UnitName, "deallocateRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+TargetAgentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{

							String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "deallocateRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+TargetAgentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}
						// --------------------------------------------------------------------------------
						// ------------------------- Checking structural norms
						// ----------------------------
						// --------------------------------------------------------------------------------
						String type = dbInterface.getUnitType(UnitName);

						if (type.equals("hierarchy")) {
							if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
								if (dbInterface.checkPositionInUnit(AgentName, "supervisor", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName)) {
									String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_SUPERVISOR_OR_CREATOR_IN_UNIT, AgentName, UnitName);
									throw new NotSupervisorOrCreatorInUnitException(message);
								}
							} else {
								String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
								throw new AgentNotInUnitException(message);
							}
						} else if (type.equals("team")) {
							if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {

								String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
								resultXML += "</response>";

								return resultXML;

							} else {
								String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
								throw new AgentNotInUnitException(message);
							}
						} else if (type.equals("flat")) {
							if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {

								if (dbInterface.checkPositionInUnit(AgentName, "member", UnitName) || dbInterface.checkPositionInUnit(AgentName, "creator", UnitName)) {
									String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_MEMBER_OR_CREATOR_IN_UNIT, AgentName, UnitName);
									throw new NotMemberOrCreatorInUnitException(message);
								}

							} else {
								if (dbInterface.checkPosition(AgentName, "creator")) {
									String result = dbInterface.deallocateRole(RoleName, UnitName, TargetAgentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_IN_UNIT_AND_NOT_CREATOR, AgentName, UnitName);
									throw new NotInUnitAndNotCreatorException(message);
								}
							}

						} else {
							String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, type);
							throw new InvalidUnitTypeException(message);
						}
					} else {
						String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
						throw new RoleNotExistsException(message);
					}
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);
		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}

	}

	/**
	 * Method used in order to change the parent unit.
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param ParentName
	 *            Identifier of the parent unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns <unitName + jointed to parentName>
	 */
	public String joinUnit(String UnitName, String ParentName, String AgentName) {

		String resultXML = "<response>\n<serviceName>JointUnit</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(ParentName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkUnit(ParentName)) {
						if (UnitName.equals(ParentName)) {
							String message = l10n.getMessage(MessageID.SAME_UNIT);
							throw new SameUnitException(message);
						}

						if (UnitName.equals("virtual")) {
							String message = l10n.getMessage(MessageID.VIRTUAL_PARENT);
							throw new VirtualParentException(message);
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent
						// norms ----------------------
						// --------------------------------------------------------------------------------
						if (checkPermitNorms(AgentName, UnitName, "joinUnit("+UnitName.toLowerCase()+","+ParentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{

							String result = dbInterface.joinUnit(UnitName, ParentName);

							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "joinUnit("+UnitName.toLowerCase()+","+ParentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}
						if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
							if (dbInterface.checkPositionInUnit(AgentName, "creator", UnitName)) {
								if (dbInterface.checkPositionInUnit(AgentName, "creator", ParentName)) {

									String result = dbInterface.joinUnit(UnitName, ParentName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n<description>" + result + "</description>\n</result>\n";
									resultXML += "</response>";

									return resultXML;

								} else {
									String message = l10n.getMessage(MessageID.NOT_CREATOR_IN_PARENT_UNIT, AgentName);
									throw new NotCreatorInParentUnitException(message);
								}

							} else {
								String message = l10n.getMessage(MessageID.NOT_CREATOR_IN_UNIT, AgentName, UnitName);
								throw new NotCreatorInUnitException(message);
							}

						} else {
							String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
							throw new AgentNotInUnitException(message);
						}

					} else {
						String message = l10n.getMessage(MessageID.PARENT_UNIT_NOT_EXISTS, ParentName);
						throw new ParentUnitNotExistsException(message);
					}

				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}

			}

			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}



	private String informUnitAgentRole(String UnitName, String RequestedAgentName, String AgentName)
	{
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		String resultXML = "";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RequestedAgentName)) {
				if (dbInterface.checkAgent(RequestedAgentName)) {
					// --------------------------------------------------------------------------------
					// ------------------------- Checking domain-dependent norms
					// ----------------------
					// --------------------------------------------------------------------------------

					if (!checkPermitNorms(AgentName, UnitName, "informUnitAgentRole("+UnitName.toLowerCase()+","+RequestedAgentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						if (checkFordibbenNorms(AgentName, UnitName, "informUnitAgentRole("+UnitName.toLowerCase()+","+RequestedAgentName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}

					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						methodResult = dbInterface.getInformAgentRolesPlayedInUnit(UnitName, RequestedAgentName);



						for (ArrayList<String> agentPair : methodResult) { // <
							// RoleName
							// ,
							// UnitName
							// >
							resultXML += "<item>\n";
							resultXML += "<rolename>" + agentPair.get(0) + "</rolename>\n";
							resultXML += "<unitname>" + UnitName + "</unitname>\n";
							resultXML += "</item>\n";
						}

						return resultXML;
					}
					else
					{
						methodResult = dbInterface.getInformAgentRolesPlayedInUnit(UnitName, RequestedAgentName);


						for (ArrayList<String> agentPair : methodResult) { // <

							if (agentPair.get(1).equals("public"))
							{
								resultXML += "<item>\n";
								resultXML += "<rolename>" + agentPair.get(0) + "</rolename>\n";
								resultXML += "<unitname>" + UnitName + "</unitname>\n";
								resultXML += "</item>\n";
							}
						}

						return resultXML;
					}

				} else {
					String message = l10n.getMessage(MessageID.AGENT_NOT_EXISTS, RequestedAgentName);
					throw new AgentNotExistsException(message);
				}

			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting the list of roles and units where an agent is,
	 * given the specific moment.
	 * 
	 * @param RequestedAgentName
	 *            Identifier of the agent requested
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns a set of tuples formed by < roleName , UnitName > and
	 *         separated by -
	 */
	public String informAgentRole(String RequestedAgentName, String AgentName) {


		ArrayList<ArrayList<String>> agentRole = new ArrayList<ArrayList<String>>();
		ArrayList<String> units = new ArrayList<String>();

		String resultInformUnitAgentRole = "";

		String resultXML = "<response>\n<serviceName>InformAgentRole</serviceName>\n";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RequestedAgentName)) {
				if (dbInterface.checkAgent(RequestedAgentName)) {


					agentRole = dbInterface.getInformAgentRole(RequestedAgentName, AgentName);

					for (ArrayList<String> agentPair : agentRole) { 

						if (!units.contains(agentPair.get(1)))
						{
							units.add(agentPair.get(1));
						}
					}
					if (units.size() != 0)
					{
						for (String unit : units) { 
							resultInformUnitAgentRole += informUnitAgentRole(unit, RequestedAgentName, AgentName);

						}


					}


					resultXML += "<status>Ok</status>\n";
					resultXML += "<result>\n";
					resultXML += resultInformUnitAgentRole;
					resultXML += "</result>\n";
					resultXML += "</response>";

					return resultXML;
				} else {
					String message = l10n.getMessage(MessageID.AGENT_NOT_EXISTS, RequestedAgentName);
					throw new AgentNotExistsException(message);
				}

			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}
	/**
	 * 
	 * Method used for requesting the content of the norm.
	 * 
	 * @param NormName
	 * @param UnitName
	 * @param AgentName
	 * @return Returns a string with the content of the norm.
	 */
	public String informNorm(String NormName, String UnitName, String AgentName)
	{
		String resultXML = "<response>\n<serviceName>InformNorm</serviceName>\n";
		String unitType = "";
		try
		{
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(NormName) && checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkNormName(NormName, UnitName))
					{
						if (checkPermitNorms(AgentName, UnitName, "informNorm("+NormName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{


							String result = dbInterface.getNormContent(NormName, UnitName);


							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n";
							resultXML += "<description><!-- " + result + " --></description>\n";
							resultXML += "</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "informNorm("+NormName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}

						unitType = dbInterface.getUnitType(UnitName);

						if (unitType.equals("flat")) {
							String result = dbInterface.getNormContent(NormName, UnitName);


							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n";
							resultXML += "<description><!-- " + result + " --></description>\n";
							resultXML += "</result>\n";
							resultXML += "</response>";

							return resultXML;

						}else if (unitType.equals("hierarchy") || unitType.equals("team"))
						{
							//Extracts the parent unit.
							ArrayList<String> parentUnit = dbInterface.getInformUnit(UnitName);

							if (dbInterface.checkAgentInUnit(AgentName, UnitName) || dbInterface.checkAgentInUnit(AgentName, parentUnit.get(1)))
							{
								String result = dbInterface.getNormContent(NormName, UnitName);


								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";
								resultXML += "<description><!-- " + result + " --></description>\n";
								resultXML += "</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else
							{
								String message = l10n.getMessage(MessageID.NOT_IN_UNIT_OR_PARENT_UNIT, AgentName);
								throw new NotInUnitOrParentUnitException(message);
							}

						}
						else
						{
							String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
							throw new InvalidUnitTypeException(message);
						}



					}else {
						String message = l10n.getMessage(MessageID.NORM_NOT_EXISTS, NormName, UnitName);
						throw new NormNotExistsException(message);
					}

				}else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}else
			{
				String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
				throw new EmptyParametersException(message);
			}	

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting the list of entities that are members of a
	 * specific unit. If a role is specified only the members playing this role
	 * are detailed. If a position is specified only the members playing this
	 * position are detailed.
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param RoleName
	 *            Identifier of the role
	 * @param PositionValue
	 *            Position inside the unit, such as member, supervisor or
	 *            subordinate
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns a set of tuples formed by < agentName , roleName > and
	 *         separated by -
	 */

	public String informMembers(String UnitName, String RoleName, String PositionValue, String AgentName) {

		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		ArrayList<String> arrayResult = new ArrayList<String>();
		Flags flag = Flags.CASE_A;
		String resultXML = "<response>\n<serviceName>InformMembers</serviceName>\n";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkAgent(AgentName)) {
						if (RoleName != null) {
							if (!RoleName.equals("")) {

								if (!dbInterface.checkRole(RoleName, UnitName)) {
									String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
									throw new RoleNotExistsException(message);

								} else {

									flag = Flags.CASE_B;

								}
							}
						}

						if (PositionValue != null) {
							if (!PositionValue.equals("")) {

								if (flag == Flags.CASE_A) {
									flag = Flags.CASE_C;
								} else {
									flag = Flags.CASE_D;
								}
								String unitType = dbInterface.getUnitType(UnitName);

								if (unitType.equals("flat") || unitType.equals("team")) {

									if (!PositionValue.equals("member") && !PositionValue.equals("creator")) {
										String message = l10n.getMessage(MessageID.INVALID_ROLE_POSITION, RoleName);
										throw new InvalidRolePositionException(message);
									}
								} else if (unitType.equals("hierarchy")) {
									if (!PositionValue.equals("supervisor") && !PositionValue.equals("creator") && !PositionValue.equals("subordinate")) {
										String message = l10n.getMessage(MessageID.INVALID_ROLE_POSITION, RoleName);
										throw new InvalidRolePositionException(message);
									}
								} else {
									String message = l10n.getMessage(MessageID.INVALID_ROLE_POSITION, RoleName);
									throw new InvalidRolePositionException(message);
								}
							}
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent
						// norms ----------------------
						// --------------------------------------------------------------------------------


						if (!checkPermitNorms(AgentName, UnitName, "informMembers("+UnitName.toLowerCase()+","+RoleName.toLowerCase()+","+PositionValue.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{

							if (checkFordibbenNorms(AgentName, UnitName, "informMembers("+UnitName.toLowerCase()+","+RoleName.toLowerCase()+","+PositionValue.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{
								String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
								throw new ForbiddenNormException(message);
							}
							else
							{

								// --------------------------------------------------------------------------------
								// ------------------------- Checking structural norms
								// ----------------------------
								// --------------------------------------------------------------------------------

								switch (flag) {

								case CASE_A:

									//Basado en la revisión 20
									if (dbInterface.checkAgentInUnit(AgentName, UnitName))
									{	

										methodResult = dbInterface.getAgentsRolesInUnit(UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) { // <
											// agentName
											// ,
											// RoleName
											// >
											resultXML += "<item>\n";
											resultXML += "<agentname>" + agentPair.get(0) + "</agentname>\n";
											resultXML += "<rolename>" + agentPair.get(1) + "</rolename>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";
									}
									else
									{
										methodResult = dbInterface.getAgentsVisibilityRolesInUnit(UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) { // <
											// agentName
											// ,
											// RoleName
											// >
											resultXML += "<item>\n";
											resultXML += "<agentname>" + agentPair.get(0) + "</agentname>\n";
											resultXML += "<rolename>" + agentPair.get(1) + "</rolename>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";
									}
									break;// No se incluye ni el parametro role name ni
									// position name.
								case CASE_B:

									arrayResult = dbInterface.getAgentsPlayingRoleInUnit(UnitName, RoleName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";

									for (String agent : arrayResult) { // < agentName ,
										// roleName >
										resultXML += "<item>\n";
										resultXML += "<agentname>" + agent + "</agentname>\n";
										resultXML += "<rolename>" + RoleName + "</rolename>\n";
										resultXML += "</item>\n";
									}
									resultXML += "</result>\n";
									resultXML += "</response>";
									break;// Solo se incluye el roleName
								case CASE_C:

									if (dbInterface.checkAgentInUnit(AgentName, UnitName))
									{
										methodResult = dbInterface.getAgentsPlayingPositionInUnit(UnitName, PositionValue);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) {
											// < requestedAgentNameX , roleNameY >
											resultXML += "<item>\n";
											resultXML += "<agentname>" + agentPair.get(0) + "</agentname>\n";
											resultXML += "<rolename>" + agentPair.get(1) + "</rolename>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";
									}
									else
									{
										methodResult = dbInterface.getAgentsPlayingVisibilityPositionInUnit(UnitName, PositionValue);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) {
											// < requestedAgentNameX , roleNameY >
											resultXML += "<item>\n";
											resultXML += "<agentname>" + agentPair.get(0) + "</agentname>\n";
											resultXML += "<rolename>" + agentPair.get(1) + "</rolename>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";
									}
									break;// No se incluye el rolename pero si el
									// positionName
								case CASE_D:

									arrayResult = dbInterface.getAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";

									for (String agent : arrayResult) { // < agentName ,
										// roleName >
										resultXML += "<item>\n";
										resultXML += "<agentname>" + agent + "</agentname>\n";
										resultXML += "<rolename>" + RoleName + "</rolename>\n";
										resultXML += "</item>\n";
									}
									resultXML += "</result>\n";
									resultXML += "</response>";
									break; // Se incluyen todos los parametros
								}
								return resultXML;
							}
						}
						else // Check permit norm
						{

							ArrayList<String> informRole = null;

							switch (flag) {

							case CASE_A:
								methodResult = dbInterface.getAgentsRolesInUnit(UnitName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (ArrayList<String> agentPair : methodResult) { // <
									// agentName
									// ,
									// RoleName
									// >
									resultXML += "<item>\n";
									resultXML += "<agentname>" + agentPair.get(0) + "</agentname>\n";
									resultXML += "<rolename>" + agentPair.get(1) + "</rolename>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";
								break;// No se incluye ni el parametro role name ni
								// position name.
							case CASE_B:
								/*
									informRole = dbInterface.getInformRole(RoleName, UnitName);

									if (!informRole.get(1).equals("public"))
									{
										if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
										{
											String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
											throw new AgentNotInUnitException(message);
										}
									}
								 */
								arrayResult = dbInterface.getAgentsPlayingRoleInUnit(UnitName, RoleName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (String agent : arrayResult) { // < agentName ,
									// roleName >
									resultXML += "<item>\n";
									resultXML += "<agentname>" + agent + "</agentname>\n";
									resultXML += "<rolename>" + RoleName + "</rolename>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";

								break;// Solo se incluye el roleName
							case CASE_C:



								methodResult = dbInterface.getAgentsPlayingPositionInUnit(UnitName, PositionValue);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (ArrayList<String> agentPair : methodResult) {
									// < requestedAgentNameX , roleNameY >
									resultXML += "<item>\n";
									resultXML += "<agentname>" + agentPair.get(0) + "</agentname>\n";
									resultXML += "<rolename>" + agentPair.get(1) + "</rolename>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";
								break;// No se incluye el rolename pero si el
								// positionName
							case CASE_D:

								/*
									informRole = dbInterface.getInformRole(RoleName, UnitName);

									if (!informRole.get(1).equals("public"))
									{
										if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
										{
											String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
											throw new AgentNotInUnitException(message);
										}
									}
								 */
								arrayResult = dbInterface.getAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (String agent : arrayResult) { // < agentName ,
									// roleName >
									resultXML += "<item>\n";
									resultXML += "<agentname>" + agent + "</agentname>\n";
									resultXML += "<rolename>" + RoleName + "</rolename>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";
								break; // Se incluyen todos los parametros
							}
							return resultXML;

						}

					} else {
						String message = l10n.getMessage(MessageID.AGENT_NOT_EXISTS, AgentName);
						throw new AgentNotExistsException(message);

					}

				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}

			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}

	}







	/**
	 * Method used for requesting the number of current members of a specific
	 * unit. If a role is specified only the members playing that role are taken
	 * into account. If a position is specified only the members playing that
	 * position are taken into account.
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param RoleName
	 *            Identifier of the role
	 * @param PositionValue
	 *            Position inside the unit, such as member, supervisor or
	 *            subordinate
	 * @param AgentName
	 *            Identifier of the name
	 * @return Returns a quantity of members
	 */
	@SuppressWarnings("unused")
	public String informQuantityMembers(String UnitName, String RoleName, String PositionValue, String AgentName) {

		int intResult = 0;
		Flags flag = Flags.CASE_A;
		String resultXML = "<response>\n<serviceName>QuantityMembers</serviceName>\n";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkAgent(AgentName)) {
						if (RoleName != null) {
							if (!RoleName.equals("")) {

								if (!dbInterface.checkRole(RoleName, UnitName)) {
									String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
									throw new RoleNotExistsException(message);

								} else {
									flag = Flags.CASE_B;

								}
							}
						}

						if (PositionValue != null) {
							if (!PositionValue.equals("")) {
								if (flag == Flags.CASE_A) {
									flag = Flags.CASE_C;
								} else {
									flag = Flags.CASE_D;
								}
								String unitType = dbInterface.getUnitType(UnitName);

								if (unitType.equals("flat") || unitType.equals("team")) {

									if (!PositionValue.equals("member") && !PositionValue.equals("creator")) {
										String message = l10n.getMessage(MessageID.INVALID_ROLE_POSITION, RoleName);
										throw new InvalidRolePositionException(message);
									}
								} else if (unitType.equals("hierarchy")) {

									if (!PositionValue.equals("supervisor") && !PositionValue.equals("creator") && !PositionValue.equals("subordinate")) {
										String message = l10n.getMessage(MessageID.INVALID_ROLE_POSITION, RoleName);
										throw new InvalidRolePositionException(message);
									}
								} else {
									String message = l10n.getMessage(MessageID.INVALID_ROLE_POSITION, RoleName);
									throw new InvalidRolePositionException(message);

								}
							}
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent
						// norms ----------------------
						// --------------------------------------------------------------------------------

						if (!checkPermitNorms(AgentName, UnitName, "quantityMembers("+UnitName.toLowerCase()+","+RoleName.toLowerCase()+","+PositionValue.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{

							// --------------------------------------------------------------------------------
							// ------------------------- Checking structural norms
							// ----------------------------
							// --------------------------------------------------------------------------------

							if (checkFordibbenNorms(AgentName, UnitName, "quantityMembers("+UnitName.toLowerCase()+","+RoleName.toLowerCase()+","+PositionValue.toLowerCase()+","+AgentName.toLowerCase()+")"))
							{
								String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
								throw new ForbiddenNormException(message);
							}
							else
							{
								switch (flag) {

								case CASE_A:

									if (dbInterface.checkAgentInUnit(AgentName, UnitName))
									{
										intResult = dbInterface.getInformQuantityAgentsRolesInUnit(UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";
										resultXML += "<quantity>" + intResult + "</quantity>\n";
										resultXML += "</result>\n";
										resultXML += "</response>";

									}
									else
									{
										intResult = dbInterface.getInformQuantityAgentsVisibilityRolesInUnit(UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";
										resultXML += "<quantity>" + intResult + "</quantity>\n";
										resultXML += "</result>\n";
										resultXML += "</response>";
									}
									break;// No se incluye ni el parametro role name ni
									// position name.
								case CASE_B:

									intResult = dbInterface.getInformQuantityAgentsPlayingRoleInUnit(UnitName, RoleName);
									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";
									resultXML += "<quantity>" + intResult + "</quantity>\n";
									resultXML += "</result>\n";
									resultXML += "</response>";
									break;// Solo se incluye el roleName
								case CASE_C:

									if (dbInterface.checkAgentInUnit(AgentName, UnitName))
									{
										intResult = dbInterface.getInformQuantityAgentsPlayingPositionInUnit(UnitName, PositionValue);
										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";
										resultXML += "<quantity>" + intResult + "</quantity>\n";
										resultXML += "</result>\n";
										resultXML += "</response>";
									}
									else
									{
										intResult = dbInterface.getInformQuantityAgentsPlayingVisibilityPositionInUnit(UnitName, PositionValue);
										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";
										resultXML += "<quantity>" + intResult + "</quantity>\n";
										resultXML += "</result>\n";
										resultXML += "</response>";

									}
									break;// No se incluye el rolename pero si el
									// postionName
								case CASE_D:

									intResult = dbInterface.getInformQuantityAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue);
									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";
									resultXML += "<quantity>" + intResult + "</quantity>\n";
									resultXML += "</result>\n";
									resultXML += "</response>";
									break; // Se incluyen todos los parametros
								}

								return resultXML;
							}
						}
						else // Check permit norm
						{

							ArrayList<String> informRole = null;

							switch (flag) {

							case CASE_A:

								intResult = dbInterface.getInformQuantityAgentsRolesInUnit(UnitName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";
								resultXML += "<quantity>" + intResult + "</quantity>\n";
								resultXML += "</result>\n";
								resultXML += "</response>";

								break;// No se incluye ni el parametro role name ni
								// position name.
							case CASE_B:
								/*
									informRole = dbInterface.getInformRole(RoleName, UnitName);

									if (!informRole.get(1).equals("public"))
									{
										if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
										{
											String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
											throw new AgentNotInUnitException(message);
										}
									}
								 */
								intResult = dbInterface.getInformQuantityAgentsPlayingRoleInUnit(UnitName, RoleName);
								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";
								resultXML += "<quantity>" + intResult + "</quantity>\n";
								resultXML += "</result>\n";
								resultXML += "</response>";
								break;// Solo se incluye el roleName
							case CASE_C:

								intResult = dbInterface.getInformQuantityAgentsPlayingPositionInUnit(UnitName, PositionValue);
								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";
								resultXML += "<quantity>" + intResult + "</quantity>\n";
								resultXML += "</result>\n";
								resultXML += "</response>";
								break;// No se incluye el rolename pero si el
								// postionName
							case CASE_D:
								/*
									informRole = dbInterface.getInformRole(RoleName, UnitName);

									if (!informRole.get(1).equals("public"))
									{
										if (!dbInterface.checkAgentInUnit(AgentName, UnitName))
										{
											String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT, AgentName, UnitName);
											throw new AgentNotInUnitException(message);
										}
									}
								 */
								intResult = dbInterface.getInformQuantityAgentsPlayingRolePositionInUnit(UnitName, RoleName, PositionValue);
								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";
								resultXML += "<quantity>" + intResult + "</quantity>\n";
								resultXML += "</result>\n";
								resultXML += "</response>";
								break; // Se incluyen todos los parametros
							}

							return resultXML;

						}
					} else {
						String message = l10n.getMessage(MessageID.AGENT_NOT_EXISTS, AgentName);
						throw new AgentNotExistsException(message);
					}

				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}

			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}

	}

	/**
	 * Method used for requesting information about a specific unit.
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns < UnitType , ParentName >
	 */
	public String informUnit(String UnitName, String AgentName) {

		ArrayList<String> arrayResult = new ArrayList<String>();
		boolean play = false;
		String resultXML = "<response>\n<serviceName>InformUnit</serviceName>\n";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(AgentName)) {
				if (dbInterface.checkUnit(UnitName)) {
					// --------------------------------------------------------------------------------
					// ------------------------- Checking domain-dependent norms
					// ----------------------
					// --------------------------------------------------------------------------------
					if (checkPermitNorms(AgentName, UnitName, "informUnit("+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
					{


						arrayResult = dbInterface.getInformUnit(UnitName);
						// < UnitType , ParentName >
						resultXML += "<status>Ok</status>\n";
						resultXML += "<result>\n";
						resultXML += "<unittype>" + arrayResult.get(0) + "</unittype>\n";
						resultXML += "<parentName>" + arrayResult.get(1) + "</parentName>\n";
						resultXML += "</result>\n";
						resultXML += "</response>";

						return resultXML;
					}
					else if (checkFordibbenNorms(AgentName, UnitName, "informUnit("+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
					{
						String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
						throw new ForbiddenNormException(message);
					}
					String unitType = dbInterface.getUnitType(UnitName);

					if (unitType.equals("team") || unitType.equals("hierarchy")) {
						ArrayList<String> parentsUnitName = dbInterface.getParentsUnit(UnitName);

						for (String parentUnit : parentsUnitName) {
							if (dbInterface.checkAgentInUnit(AgentName, parentUnit)) {
								play = true;
							}
						}

						if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {
							play = true;
						}

						if (!play) {
							String message = l10n.getMessage(MessageID.NOT_IN_UNIT_OR_PARENT_UNIT, AgentName);
							throw new NotInUnitOrParentUnitException(message);
						}
					} else if (!unitType.equals("flat")) {
						String message = l10n.getMessage(MessageID.INVALID_UNIT_TYPE, unitType);
						throw new InvalidUnitTypeException(message);

					}

					arrayResult = dbInterface.getInformUnit(UnitName);
					// < UnitType , ParentName >
					resultXML += "<status>Ok</status>\n";
					resultXML += "<result>\n";
					resultXML += "<unittype>" + arrayResult.get(0) + "</unittype>\n";
					resultXML += "<parentName>" + arrayResult.get(1) + "</parentName>\n";
					resultXML += "</result>\n";
					resultXML += "</response>";

					return resultXML;
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting the list of roles that have been registered
	 * inside a unit.
	 * 
	 * @param UnitName
	 *            Identifier of the unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns < RoleName , Accessibility , Visibility , Position >
	 */

	public String informUnitRoles(String UnitName, String AgentName) {

		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		String resultXML = "<response>\n<serviceName>InformUnitRoles</serviceName>\n";

		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(AgentName)) {
				if (dbInterface.checkUnit(UnitName)) {
					// --------------------------------------------------------------------------------
					// ------------------------- Checking domain-dependent norms
					// ----------------------
					// --------------------------------------------------------------------------------


					if (checkPermitNorms(AgentName, UnitName, "informUnitRoles("+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
					{
						methodResult = dbInterface.getInformUnitRoles(UnitName, true);

						resultXML += "<status>Ok</status>\n";
						resultXML += "<result>\n";

						for (ArrayList<String> agentPair : methodResult) {
							// < RoleName , Accessibility , Visibility , Position
							resultXML += "<item>\n";
							resultXML += "<rolename>" + agentPair.get(0) + "</rolename>\n";
							resultXML += "<position>" + agentPair.get(3) + "</position>\n";
							resultXML += "<visibility>" + agentPair.get(2) + "</visibility>\n";
							resultXML += "<accessibility>" + agentPair.get(1) + "</accessibility>\n";
							resultXML += "</item>\n";
						}
						resultXML += "</result>\n";
						resultXML += "</response>";

						return resultXML;
					}else if (checkFordibbenNorms(AgentName, UnitName, "informUnitRoles("+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
					{
						String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
						throw new ForbiddenNormException(message);
					}


					if (dbInterface.checkAgentInUnit(AgentName, UnitName))
					{
						methodResult = dbInterface.getInformUnitRoles(UnitName, true);

						resultXML += "<status>Ok</status>\n";
						resultXML += "<result>\n";

						for (ArrayList<String> agentPair : methodResult) {
							// < RoleName , Accessibility , Visibility , Position
							resultXML += "<item>\n";
							resultXML += "<rolename>" + agentPair.get(0) + "</rolename>\n";
							resultXML += "<position>" + agentPair.get(3) + "</position>\n";
							resultXML += "<visibility>" + agentPair.get(2) + "</visibility>\n";
							resultXML += "<accessibility>" + agentPair.get(1) + "</accessibility>\n";
							resultXML += "</item>\n";
						}
						resultXML += "</result>\n";
						resultXML += "</response>";

						return resultXML;
					}
					else
					{
						methodResult = dbInterface.getInformUnitRoles(UnitName, false);

						resultXML += "<status>Ok</status>\n";
						resultXML += "<result>\n";

						for (ArrayList<String> agentPair : methodResult) {
							// < RoleName , Accessibility , Visibility , Position
							resultXML += "<item>\n";
							resultXML += "<rolename>" + agentPair.get(0) + "</rolename>\n";
							resultXML += "<position>" + agentPair.get(3) + "</position>\n";
							resultXML += "<visibility>" + agentPair.get(2) + "</visibility>\n";
							resultXML += "<accessibility>" + agentPair.get(1) + "</accessibility>\n";
							resultXML += "</item>\n";
						}
						resultXML += "</result>\n";
						resultXML += "</response>";

						return resultXML;
					}
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting information about a specific role.
	 * 
	 * @param RoleName
	 *            Identifier of the role
	 * @param UnitName
	 *            Identifier of the unit
	 * @param AgentName
	 *            Identifier of the agent
	 * @return Returns < Accessibility - Visibility - Position >
	 */
	public String informRole(String RoleName, String UnitName, String AgentName) {

		ArrayList<String> arrayResult = new ArrayList<String>();
		String resultXML = "<response>\n<serviceName>InformRole</serviceName>\n";
		try {
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(RoleName) && checkParameter(UnitName) && checkParameter(AgentName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkRole(RoleName, UnitName)) {
						// --------------------------------------------------------------------------------
						// ------------------------- Checking domain-dependent
						// norms ----------------------
						// --------------------------------------------------------------------------------
						if (checkPermitNorms(AgentName, UnitName, "informRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{


							arrayResult = dbInterface.getInformRole(RoleName, UnitName);

							// < Accessibility - Visibility - Position >
							resultXML += "<status>Ok</status>\n";
							resultXML += "<result>\n";
							resultXML += "<position>" + arrayResult.get(2) + "</position>\n";
							resultXML += "<visibility>" + arrayResult.get(1) + "</visibility>\n";
							resultXML += "<accessibility>" + arrayResult.get(0) + "</accessibility>\n";
							resultXML += "</result>\n";
							resultXML += "</response>";

							return resultXML;
						}
						else if (checkFordibbenNorms(AgentName, UnitName, "informRole("+RoleName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}

						// --------------------------------------------------------------------------------
						// ------------------------- Checking structural norms
						// ----------------------------
						// --------------------------------------------------------------------------------

						String roleVisibility = dbInterface.getInformRole(RoleName, UnitName).get(1);

						if (!roleVisibility.equals("public")) {
							if (!dbInterface.checkAgentInUnit(AgentName, UnitName)) {
								String message = l10n.getMessage(MessageID.VISIBILITY_ROLE, AgentName, UnitName);
								throw new VisibilityRoleException(message);
							}
						}

						arrayResult = dbInterface.getInformRole(RoleName, UnitName);

						// < Accessibility - Visibility - Position >
						resultXML += "<status>Ok</status>\n";
						resultXML += "<result>\n";
						resultXML += "<position>" + arrayResult.get(2) + "</position>\n";
						resultXML += "<visibility>" + arrayResult.get(1) + "</visibility>\n";
						resultXML += "<accessibility>" + arrayResult.get(0) + "</accessibility>\n";
						resultXML += "</result>\n";
						resultXML += "</response>";

						return resultXML;

					} else {
						String message = l10n.getMessage(MessageID.ROLE_NOT_EXISTS, RoleName, UnitName);
						throw new RoleNotExistsException(message);
					}
				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);
		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}
	}

	/**
	 * Method used for requesting information about a specific norm.
	 * 
	 * @param TargetTypeName
	 * @param TargetValueName
	 * @param UnitName
	 * @param AgentName
	 * 
	 * @return
	 */
	public String informTargetNorms(String TargetTypeName, String TargetValueName, String UnitName, String AgentName)
	{
		String resultXML = "<response>\n<serviceName>InformTargetNorms</serviceName>\n";
		ArrayList<ArrayList<String>> methodResult = new ArrayList<ArrayList<String>>();
		boolean permitFlag = false;

		try
		{
			// --------------------------------------------------------------------------------
			// ------------------------- Checking input parameters
			// ----------------------------
			// --------------------------------------------------------------------------------
			if (checkParameter(UnitName) && checkParameter(AgentName)) {
				if (dbInterface.checkUnit(UnitName)) {
					if (dbInterface.checkTargetType(TargetTypeName))
					{
						if (checkPermitNorms(AgentName, UnitName, "informTargetNorms("+TargetTypeName.toLowerCase()+","+TargetValueName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{

							permitFlag = true;
						}
						else
							permitFlag = false;

						if (checkFordibbenNorms(AgentName, UnitName, "informTargetNorms("+TargetTypeName.toLowerCase()+","+TargetValueName.toLowerCase()+","+UnitName.toLowerCase()+","+AgentName.toLowerCase()+")"))
						{
							String message = l10n.getMessage(MessageID.FORBIDDEN_NORM);
							throw new ForbiddenNormException(message);
						}

						if (TargetTypeName.equals("agentName"))
						{
							if (TargetValueName.equals(""))
							{
								methodResult = dbInterface.getAgentNorms(UnitName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (ArrayList<String> agentPair : methodResult) { 

									resultXML += "<item>\n";
									resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
									resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
									resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
									resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else
							{
								methodResult = dbInterface.getAgentNorms(TargetValueName, UnitName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (ArrayList<String> agentPair : methodResult) { 

									resultXML += "<item>\n";
									resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
									resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
									resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
									resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
						}else if (TargetTypeName.equals("positionName"))
						{
							if (TargetValueName.equals(""))
							{
								methodResult = dbInterface.getPositionNorms(UnitName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (ArrayList<String> agentPair : methodResult) { 

									resultXML += "<item>\n";
									resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
									resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
									resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
									resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
							else
							{
								methodResult = dbInterface.getPositionNorms(TargetValueName, UnitName);

								resultXML += "<status>Ok</status>\n";
								resultXML += "<result>\n";

								for (ArrayList<String> agentPair : methodResult) { 

									resultXML += "<item>\n";
									resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
									resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
									resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
									resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
									resultXML += "</item>\n";
								}
								resultXML += "</result>\n";
								resultXML += "</response>";

								return resultXML;
							}
						}else if (TargetTypeName.equals("roleName"))
						{
							if (permitFlag)
							{
								if (TargetValueName.equals(""))
								{
									methodResult = dbInterface.getRoleNorms(UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";

									for (ArrayList<String> agentPair : methodResult) { 

										resultXML += "<item>\n";
										resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
										resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
										resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
										resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
										resultXML += "</item>\n";
									}
									resultXML += "</result>\n";
									resultXML += "</response>";

									return resultXML;
								}
								else
								{
									methodResult = dbInterface.getRoleNorms(TargetValueName, UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";

									for (ArrayList<String> agentPair : methodResult) { 

										resultXML += "<item>\n";
										resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
										resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
										resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
										resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
										resultXML += "</item>\n";
									}
									resultXML += "</result>\n";
									resultXML += "</response>";

									return resultXML;
								}	
							}
							else //else permitFlag
							{
								if (TargetValueName.equals(""))
								{
									if (dbInterface.checkAgentInUnit(AgentName, UnitName))
									{

										methodResult = dbInterface.getRoleNorms(UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) { 

											resultXML += "<item>\n";
											resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
											resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
											resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
											resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";

										return resultXML;
									}
									else
									{
										methodResult = dbInterface.getPublicRoleNorms(UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) { 

											resultXML += "<item>\n";
											resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
											resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
											resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
											resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";

										return resultXML;
									}
								}
								else if (TargetValueName.equals("_"))
								{
									methodResult = dbInterface.getRoleNorms(TargetValueName, UnitName);

									resultXML += "<status>Ok</status>\n";
									resultXML += "<result>\n";

									for (ArrayList<String> agentPair : methodResult) { 

										resultXML += "<item>\n";
										resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
										resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
										resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
										resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
										resultXML += "</item>\n";
									}
									resultXML += "</result>\n";
									resultXML += "</response>";

									return resultXML;
								}
								else
								{
									String visibility = dbInterface.getInformRole(TargetValueName, UnitName).get(1);

									if (visibility.equals("public"))
									{
										methodResult = dbInterface.getRoleNorms(TargetValueName, UnitName);

										resultXML += "<status>Ok</status>\n";
										resultXML += "<result>\n";

										for (ArrayList<String> agentPair : methodResult) { 

											resultXML += "<item>\n";
											resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
											resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
											resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
											resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
											resultXML += "</item>\n";
										}
										resultXML += "</result>\n";
										resultXML += "</response>";

										return resultXML;
									}
									else //else visibility
									{
										if (dbInterface.checkAgentInUnit(AgentName, UnitName))
										{
											methodResult = dbInterface.getRoleNorms(TargetValueName, UnitName);

											resultXML += "<status>Ok</status>\n";
											resultXML += "<result>\n";

											for (ArrayList<String> agentPair : methodResult) { 

												resultXML += "<item>\n";
												resultXML += "<normname>" + agentPair.get(0) + "</normname>\n";
												resultXML += "<unitname>" + agentPair.get(1) + "</unitname>\n";
												resultXML += "<targetname>" + agentPair.get(2) + "</targetname>\n";
												resultXML += "<targetvalue>" + agentPair.get(3) + "</targetvalue>\n";
												resultXML += "</item>\n";
											}
											resultXML += "</result>\n";
											resultXML += "</response>";

											return resultXML;
										}
										else
										{
											String message = l10n.getMessage(MessageID.AGENT_NOT_IN_UNIT);
											throw new AgentNotInUnitException(message);
										}

									}
								}
							}
						}


					}else {
						String message = l10n.getMessage(MessageID.INVALID_TARGET_TYPE, 1, TargetTypeName);
						throw new InvalidTargetTypeException(message);
					}

				} else {
					String message = l10n.getMessage(MessageID.UNIT_NOT_EXISTS, UnitName);
					throw new UnitNotExistsException(message);
				}
			}
			String message = l10n.getMessage(MessageID.EMPTY_PARAMETERS);
			throw new EmptyParametersException(message);

		} catch (Exception e) {
			resultXML += "<status>Error</status>\n";
			resultXML += "<result>\n<description>" + e.getMessage() + "</description>\n</result>\n";
			resultXML += "</response>";
			return resultXML;
		}

	}
	
	
	/**
	 * Check if exists any rule in the unit U which allows that agent A can request the service S.
	 * 
	 * @param AgentName 		Identifier of the agent A.
	 * @param UnitName			Identifier of the organization unit U.
	 * @param ServiceName		Service S fully instantiated.
	 * 
	 * @return					Returns True if exists any rule in the unit U which allows that agent A 
	 * 							can request the service S. Otherwise, returns False.
	 */
	boolean checkPermitNorms(String AgentName, String UnitName, String ServiceName) {
		
		boolean result = false;
		ArrayList<String> stringRules = new ArrayList<String>();
		ArrayList<String> aux = new ArrayList<String>();
		
		StringTokenizer st = new StringTokenizer(ServiceName, "(");
		String service = st.nextToken();
		
		stringRules.add(ServiceName);

		try {
			
			aux = dbInterface.getAgentNormRules(UnitName, AgentName, "p", service);

			for (String rule : aux) {
				stringRules.add(rule);
			}

			//if (dbInterface.checkAgentInUnit(AgentName, UnitName)) {

				aux = dbInterface.getPositionNormRules(UnitName, AgentName, "p", service);

				for (String rule : aux) {
					stringRules.add(rule);
				}

				aux = dbInterface.getRoleNormRules(UnitName, AgentName, "p", service);

				for (String rule : aux) {
					stringRules.add(rule);
				}
			//}
			
			if (stringRules.size() == 1)
				return result;
			
			class myNormativeProtocol extends Normative_Protocol {
				protected void doReceive(CProcessor myProcessor, ACLMessage msg) {
					System.out.println("Lo tengo: "+ msg.getHeaderValue("activatedNorm"));
					
				}
			}
			
			ArrayList<String> r = new ArrayList<String>();
			CFactory talk = new myNormativeProtocol().newFactory("TALK", null, 1, omsAgent, stringRules, "JasonNormativeAgent", r) ;
			
			omsAgent.addFactoryAsInitiator(talk);

			omsAgent.startSyncConversation("TALK");
			
			System.out.println("RESULTADO: "+ r.get(0));

			if (r.get(0).equals("false"))
				result = false;
			if (r.get(0).equals("true"))
				result = true;
			
		} catch (MySQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	
	/**
	 * Check if exists any rule in the unit U which forbids that agent A can request the service S.
	 * 
	 * @param AgentName 		Identifier of the agent A.
	 * @param UnitName			Identifier of the organization unit U.
	 * @param ServiceName		Service S fully instantiated.
	 * 
	 * @return					Returns True if exists any rule in the unit U which forbids that agent A 
	 * 							can request the service S. Otherwise, returns False.
	 */
	boolean checkFordibbenNorms(String AgentName, String UnitName, String ServiceName) {
	
		boolean result = false;
		ArrayList<String> stringRules = new ArrayList<String>();
		ArrayList<String> aux = new ArrayList<String>();
		

		StringTokenizer st = new StringTokenizer(ServiceName, "(");
		String service = st.nextToken();
		
		stringRules.add(ServiceName);

		try {
			aux = dbInterface.getAgentNormRules(UnitName, AgentName, "f", service);

			for (String rule : aux)
			{
				stringRules.add(rule);
			}

			//if (dbInterface.checkAgentInUnit(AgentName, UnitName)){

				aux = dbInterface.getPositionNormRules(UnitName, AgentName, "f", service);

				for (String rule : aux)
				{
					stringRules.add(rule);
				}

				aux = dbInterface.getRoleNormRules(UnitName, AgentName, "f", service);

				for (String rule : aux)
				{
					stringRules.add(rule);
				}
			//}
			
			if (stringRules.size() == 1)
				return result;
			
			class myNormativeProtocol extends Normative_Protocol {
				protected void doReceive(CProcessor myProcessor, ACLMessage msg) {
					System.out.println("Lo tengo: "+ msg.getHeaderValue("activatedNorm"));
					
				}
				
			}
			ArrayList<String> r = new ArrayList<String>();
			
			CFactory talk = new myNormativeProtocol().newFactory("TALK", null, 1, omsAgent, stringRules, "JasonNormativeAgent", r) ;
			
			omsAgent.addFactoryAsInitiator(talk);


			omsAgent.startSyncConversation("TALK");
			
			System.out.println("RESULTADO: "+ r.get(0));

			if (r.get(0).equals("false"))
				result = false;
			if (r.get(0).equals("true"))
				result = true;
			
			

		} catch (MySQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

}
