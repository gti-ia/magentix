package es.upv.dsic.gti_ia.organization;

/**
 * OMS.java
 * 
 * @version 2.0
 */

import java.net.URI;
import java.util.*;

import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;

import org.apache.log4j.Logger;
import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

/**
 * OMS agent is responsible for managing all the request messages from other
 * entities OMS agent follows a FIPA-Request protocol
 */
public class OMS extends QueueAgent {

	Configuration configuration = Configuration.getConfiguration();

	static private OMS oms = null;

	private String OMSServiceDesciptionLocation = configuration
			.getOMSServiceDesciptionLocation();
	private String SFServiceDesciptionLocation = configuration
			.getSFServiceDesciptionLocation();

	static Logger logger = Logger.getLogger(OMS.class);

	private Monitor mon = new Monitor();
	// create a kb
	OWLKnowledgeBase kb = OWLFactory.createKB();
	OWLKnowledgeBase kbaux = OWLFactory.createKB();

	// Debug
	private static final Boolean DEBUG = true;

	// URI where the SF service descriptions are located

	private final URI OWL_S_OMS_SERVICES = URI
			.create(OMSServiceDesciptionLocation);
	private final URI OWL_S_SF_SERVICES = URI
			.create(SFServiceDesciptionLocation);

	// URI of each SF services description parameters are located
	private final URI SF_REGISTERPROFILE_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProfileProcess.owl");
	private final URI SF_REGISTERPROCESS_PROCESS = URI.create(OWL_S_SF_SERVICES
			+ "RegisterProcessProcess.owl");

	// STRUCTURAL SERVICES
	private final URI OMS_REGISTERUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterUnitProfile.owl");
	private final URI OMS_REGISTERUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterUnitProcess.owl");
	private final URI OMS_REGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterUnitProfile.owl");
	private final URI OMS_REGISTERUNIT_GOAL = URI.create("RegisterUnit");

	@SuppressWarnings("unused")
	private final URI OMS_REGISTERNORM_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterNormProfile.owl");
	private final URI OMS_REGISTERNORM_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterNormProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERNORM_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "RegisterNormGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterNormProfile.owl#RegisterNormProfile");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERNORM_GOAL = URI.create("RegisterNorm");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERNORM_PROVIDER = URI.create("Provider");

	@SuppressWarnings("unused")
	private final URI OMS_REGISTERROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterRoleProfile.owl");
	private final URI OMS_REGISTERROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterRoleProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERROLE_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "RegisterRoleGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "RegisterRoleProfile.owl#RegisterRoleProfile");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERROLE_GOAL = URI.create("RegisterRole");
	@SuppressWarnings("unused")
	private final URI OMS_REGISTERROLE_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERUNIT_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterUnitProfile.owl");
	private final URI OMS_DEREGISTERUNIT_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterUnitProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERUNIT_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterUnitGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERUNIT_ID = URI.create(OWL_S_OMS_SERVICES
			+ "DeregisterUnitProfile.owl#DeregisterUnitProfile");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERUNIT_GOAL = URI.create("DeregisterUnit");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERUNIT_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERNORM_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterNormProfile.owl");
	private final URI OMS_DEREGISTERNORM_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterNormProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERNORM_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterNormGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERNORM_ID = URI.create(OWL_S_OMS_SERVICES
			+ "DeregisterNormProfile.owl#DeregisterNormProfile");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERNORM_GOAL = URI.create("DeregisterNorm");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERNORM_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERROLE_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterRoleProfile.owl");

	private final URI OMS_DEREGISTERROLE_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterRoleProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERROLE_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "DeregisterRoleGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERROLE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "DeregisterRoleProfile.owl#DeregisterRoleProfile");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERROLE_GOAL = URI.create("DeregisterRole");
	@SuppressWarnings("unused")
	private final URI OMS_DEREGISTERROLE_PROVIDER = URI.create("Provider");
	// DYNAMIC SERVICES
	@SuppressWarnings("unused")
	private final URI OMS_ACQUIREROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "AcquireRoleProfile.owl");
	private final URI OMS_ACQUIREROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "AcquireRoleProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_ACQUIREROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES
			+ "AcquireRoleGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_ACQUIREROLE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "AcquireRoleProfile.owl#AcquireRoleProfile");
	@SuppressWarnings("unused")
	private final URI OMS_ACQUIREROLE_GOAL = URI.create("AcquireRole");
	@SuppressWarnings("unused")
	private final URI OMS_ACQUIREROLE_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_LEAVEROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "LeaveRoleProfile.owl");
	private final URI OMS_LEAVEROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "LeaveRoleProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_LEAVEROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES
			+ "LeaveRoleGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_LEAVEROLE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "LeaveRoleProfile.owl#LeaveRoleProfile");
	@SuppressWarnings("unused")
	private final URI OMS_LEAVEROLE_GOAL = URI.create("LeaveRole");
	@SuppressWarnings("unused")
	private final URI OMS_LEAVEROLE_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_EXPULSE_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "ExpulseProfile.owl");
	private final URI OMS_EXPULSE_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "ExpulseProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_EXPULSE_GROUNDING = URI.create(OWL_S_OMS_SERVICES
			+ "ExpulseGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_EXPULSE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "ExpulseProfile.owl#ExpulseProfile");
	@SuppressWarnings("unused")
	private final URI OMS_EXPULSE_GOAL = URI.create("Expulse");
	@SuppressWarnings("unused")
	private final URI OMS_EXPULSE_PROVIDER = URI.create("Provider");
	// INFORMATIVE SERVICES
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNIT_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "InformUnitProfile.owl");
	private final URI OMS_INFORMUNIT_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "InformUnitProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNIT_GROUNDING = URI.create(OWL_S_OMS_SERVICES
			+ "InformUnitGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNIT_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformUnitProfile.owl#InformUnitProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNIT_GOAL = URI.create("InformUnit");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNIT_PROVIDER = URI.create("Provider");

	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLE_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "InformRoleProfile.owl");
	private final URI OMS_INFORMROLE_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "InformRoleProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLE_GROUNDING = URI.create(OWL_S_OMS_SERVICES
			+ "InformRoleGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformRoleProfile.owl#InformRoleProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLE_GOAL = URI.create("InformRole");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLE_PROVIDER = URI.create("Provider");

	@SuppressWarnings("unused")
	private final URI OMS_INFORMAGENTROLE_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "InformAgentRoleProfile.owl");
	private final URI OMS_INFORMAGENTROLE_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "InformAgentRoleProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMAGENTROLE_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "InformAgentRoleGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMAGENTROLE_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformAgentRoleProfile.owl#InformAgentRoleProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMAGENTROLE_GOAL = URI.create("InformAgentRole");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMAGENTROLE_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMMEMBERS_PROFILE = URI.create(OWL_S_OMS_SERVICES
			+ "InformMembersProfile.owl");
	private final URI OMS_INFORMMEMBERS_PROCESS = URI.create(OWL_S_OMS_SERVICES
			+ "InformMembersProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMMEMBERS_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "InformMembersGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformMembersProfile.owl#InformMembersProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMMEMBERS_GOAL = URI.create("InformMembers");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMMEMBERS_PROVIDER = URI.create("Provider");

	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLENORMS_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "InformRoleNormsProfile.owl");
	private final URI OMS_INFORMROLENORMS_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "InformRoleNormsProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLENORMS_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "InformRoleNormsGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLENORMS_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformRoleNormsProfile.owl#InformRoleNormsProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLENORMS_GOAL = URI.create("InformRoleNorms");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLENORMS_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLEPROFILES_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProfile.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLEPROFILES_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "InformRoleProfilesProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLEPROFILES_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "InformRoleProfilesGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLEPROFILES_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformRoleProfilesProfile.owl#InformRoleProfilesProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLEPROFILES_GOAL = URI
			.create("InformRoleProfiles");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMROLEPROFILES_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNITROLES_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "InformUnitRolesProfile.owl");
	private final URI OMS_INFORMUNITROLES_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "InformUnitRolesProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNITROLES_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "InformUnitRolesGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNITROLES_ID = URI.create(OWL_S_OMS_SERVICES
			+ "InformUnitRolesProfile.owl#InformUnitRolesProfile");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNITROLES_GOAL = URI.create("InformUnitRoles");
	@SuppressWarnings("unused")
	private final URI OMS_INFORMUNITROLES_PROVIDER = URI.create("Provider");
	@SuppressWarnings("unused")
	private final URI OMS_QUANTITYMEMBERS_PROFILE = URI
			.create(OWL_S_OMS_SERVICES + "QuantityMembersProfile.owl");
	private final URI OMS_QUANTITYMEMBERS_PROCESS = URI
			.create(OWL_S_OMS_SERVICES + "QuantityMembersProcess.owl");
	@SuppressWarnings("unused")
	private final URI OMS_QUANTITYMEMBERS_GROUNDING = URI
			.create(OWL_S_OMS_SERVICES + "QuantityMembersGrounding.owl");
	@SuppressWarnings("unused")
	private final URI OMS_QUANTITYMEMBERS_ID = URI.create(OWL_S_OMS_SERVICES
			+ "QuantityMembersProfile.owl#QuantityMembersProfile");
	@SuppressWarnings("unused")
	private final URI OMS_QUANTITYMEMBERS_GOAL = URI.create("QuantityMembers");
	@SuppressWarnings("unused")
	private final URI OMS_QUANTITYMEMBERS_PROVIDER = URI.create("Provider");

	// array with the OMS processes related with registration
	URI[] OMSServicesProfiles = { OMS_REGISTERUNIT_PROFILE /*
															 * ,OMS_REGISTERROLE_PROFILE
															 * ,
															 * OMS_REGISTERNORM_PROFILE
															 * ,
															 * OMS_DEREGISTERUNIT_PROFILE
															 * ,
															 * OMS_DEREGISTERROLE_PROFILE
															 * ,
															 * OMS_DEREGISTERNORM_PROFILE
															 * ,
															 * OMS_ACQUIREROLE_PROFILE
															 * ,
															 * OMS_LEAVEROLE_PROFILE
															 * ,
															 * OMS_EXPULSE_PROFILE
															 * ,
															 * OMS_INFORMUNIT_PROFILE
															 * ,
															 * OMS_INFORMUNITROLES_PROFILE
															 * ,
															 * OMS_INFORMUNITROLES_PROFILE
															 * ,
															 * OMS_INFORMROLEPROFILES_PROFILE
															 * ,
															 * OMS_INFORMROLENORMS_PROFILE
															 * ,
															 * OMS_INFORMMEMBERS_PROFILE
															 * ,
															 * OMS_INFORMROLE_PROFILE
															 * ,
															 * OMS_INFORMUNITROLES_PROFILE
															 * ,
															 * OMS_QUANTITYMEMBERS_PROFILE
															 */};
	// array with OMS service goals
	URI[] OMSServicesGoals = { OMS_REGISTERUNIT_GOAL /*
													 * ,OMS_REGISTERROLE_GOAL,OMS_REGISTERNORM_GOAL
													 * ,
													 * OMS_DEREGISTERUNIT_GOAL,
													 * OMS_DEREGISTERROLE_GOAL,
													 * OMS_DEREGISTERNORM_GOAL,
													 * OMS_ACQUIREROLE_GOAL
													 * ,OMS_LEAVEROLE_GOAL
													 * ,OMS_EXPULSE_GOAL,
													 * OMS_INFORMUNIT_GOAL
													 * ,OMS_INFORMUNITROLES_GOAL
													 * ,
													 * OMS_INFORMUNITROLES_GOAL,
													 * OMS_INFORMROLE_GOAL
													 * ,OMS_INFORMROLENORMS_GOAL
													 * ,OMS_INFORMMEMBERS_GOAL,
													 * OMS_INFORMROLE_GOAL
													 * ,OMS_INFORMUNITROLES_GOAL
													 * ,OMS_QUANTITYMEMBERS_GOAL
													 */
	};
	// array with OMS service ID
	URI[] OMSServicesIDs = { OMS_REGISTERUNIT_ID /*
												 * ,OMS_REGISTERROLE_ID,OMS_REGISTERNORM_ID
												 * ,OMS_DEREGISTERUNIT_ID,
												 * OMS_DEREGISTERROLE_ID,
												 * OMS_DEREGISTERNORM_ID,
												 * OMS_ACQUIREROLE_ID
												 * ,OMS_LEAVEROLE_ID
												 * ,OMS_EXPULSE_ID
												 * ,OMS_INFORMAGENTROLE_ID,
												 * OMS_INFORMUNIT_ID
												 * ,OMS_INFORMUNITROLES_ID
												 * ,OMS_INFORMUNITROLES_ID,
												 * OMS_INFORMROLE_ID
												 * ,OMS_INFORMROLENORMS_ID
												 * ,OMS_INFORMMEMBERS_ID,
												 * OMS_INFORMROLE_ID
												 * ,OMS_INFORMUNITROLES_ID
												 * ,OMS_QUANTITYMEMBERS_ID
												 */};
	// array with OMS service processes
	URI[] OMSServicesProcess = { OMS_REGISTERUNIT_PROCESS,
			OMS_REGISTERROLE_PROCESS, OMS_REGISTERNORM_PROCESS,
			OMS_DEREGISTERUNIT_PROCESS, OMS_DEREGISTERROLE_PROCESS,
			OMS_DEREGISTERNORM_PROCESS, OMS_ACQUIREROLE_PROCESS,
			OMS_LEAVEROLE_PROCESS, OMS_EXPULSE_PROCESS,
			OMS_INFORMAGENTROLE_PROCESS, OMS_INFORMUNIT_PROCESS,
			OMS_INFORMUNITROLES_PROCESS, OMS_INFORMUNITROLES_PROCESS,
			OMS_INFORMROLE_PROCESS, OMS_INFORMROLENORMS_PROCESS,
			OMS_INFORMMEMBERS_PROCESS, OMS_INFORMROLE_PROCESS,
			OMS_INFORMUNITROLES_PROCESS, OMS_QUANTITYMEMBERS_PROCESS };

	/**
	 * Returns an instance of the agents OMS
	 * 
	 * @param agent
	 *            a new Agent ID
	 * @return oms agent OMS
	 */
	static public OMS getOMS(AgentID agent) {
		if (oms == null) {
			try {
				oms = new OMS(agent);
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return oms;
	}

	/**
	 * Returns an instance of the agents OMS, the agentID of the agent is
	 * AgentID("OMS")
	 * 
	 * @return oms agent OMS
	 */
	static public OMS getOMS() {
		if (oms == null) {
			try {
				oms = new OMS(new AgentID("OMS"));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return oms;
	}

	/**
	 * Returns an instance of the agents OMS
	 * 
	 * @param aid
	 *            new AgentID
	 * @throws Exception
	 */
	private OMS(AgentID aid) throws Exception {
		super(aid);
	}

	/**
	 * Change the URL where the owl's document is located.
	 * 
	 * @param OMSUrl
	 */
	public void setOMSServiceDesciptionLocation(String OMSUrl) {

		this.OMSServiceDesciptionLocation = OMSUrl;
	}

	/**
	 * Change the URL where the owl's document is located.
	 * 
	 * @param SFUrl
	 *            ej. http://localhost:8080/sfservices/SFservices/owl/owls/
	 */
	public void setSFServiceDesciptionLocation(String SFUrl) {
		this.SFServiceDesciptionLocation = SFUrl;
	}

	/**
	 * Get the URL where the owl's document is located.
	 * 
	 * @param OMSUrl
	 *            ej. http://localhost:8080/omsservices/OMSservices/owl/owls/
	 */
	public String getOMSServiceDesciptionLocation() {

		return OMSServiceDesciptionLocation;
	}

	/**
	 * Get the URL where the owl's document is located.
	 * 
	 * @param SFUrl
	 *            ej. http://localhost:8080/sfservices/SFservices/owl/owls/
	 */
	public String getSFServiceDesciptionLocation() {
		return this.SFServiceDesciptionLocation;
	}

	// array with OMS service grounding
	/**
	 * Initial registration of the OMS service profiles
	 * 
	 */
	public void RegisterOMSServiceProfiles() {

		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

		try {

			// REGISTER SOMSSERVICES PROFILES
			Service RegisterProfileService = kb
					.readService(SF_REGISTERPROFILE_PROCESS);
			// get the process for the server
			Process RegisterProfileProcess = RegisterProfileService
					.getProcess();
			for (int k = 0; k < OMSServicesProfiles.length; k++) {
				// initialize the input values to be empty
				ValueMap values = new ValueMap();
				values.setDataValue(RegisterProfileProcess
						.getInput("RegisterProfileInputServiceProfile"),
						OMSServicesProfiles[k].toString());
				values.setDataValue(RegisterProfileProcess
						.getInput("RegisterProfileInputServiceGoal"),
						OMSServicesGoals[k].toString());
				values.setDataValue(RegisterProfileProcess
						.getInput("RegisterProfileInputAgentID"), "OMS");
				if (DEBUG)
					logger.info("[OMS]Executing... "
							+ values.getValues().toString());
				values = exec.execute(RegisterProfileProcess, values);
				if (DEBUG)
					logger
							.info("[OMS]Values obtained... :"
									+ values.toString());
				if (DEBUG)
					logger.info("[OMS]ServiceID... :"
							+ values.getValue("RegisterProfileOutputServiceID")
									.toString());
				OMSServicesIDs[k] = URI.create(values.getValue(
						"RegisterProfileOutputServiceID").toString());
			}// for k
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}// end RegisterOMSServiceProfiles

	/**
	 * Initial registration of the OMS service process
	 */
	public void RegisterOMSServiceProcess() {
		// create an execution engine
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

		try {
			// REGISTER OMS SERVICES PROCESS
			Service RegisterProcessService = kb
					.readService(SF_REGISTERPROCESS_PROCESS);
			// get the process
			Process RegisterProcessProcess = RegisterProcessService
					.getProcess();

			for (int k = 0; k < OMSServicesProcess.length; k++) {

				// initialize the input values to be empty
				ValueMap values = new ValueMap();
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputServiceID"),
						OMSServicesIDs[k].toString());
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputServiceModel"),
						OMSServicesProcess[k].toString());
				values.setDataValue(RegisterProcessProcess
						.getInput("RegisterProcessInputAgentID"), "OMS");
				if (DEBUG)
					logger.info("[OMS]Executing... "
							+ values.getValues().toString());
				values = exec.execute(RegisterProcessProcess, values);
				if (DEBUG)
					logger
							.info("[OMS]Values obtained... :"
									+ values.toString());

			}// for k
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}// end RegisterOMSServiceProcess

	/**
	 * Manages the messages for the OMS services
	 */
	public class OMSResponder extends FIPARequestResponder {

		public OMSResponder(es.upv.dsic.gti_ia.architecture.QueueAgent agent) {
			super(agent, new MessageTemplate(InteractionProtocol.FIPA_REQUEST));

		}// OMSResponder

		/**
		 * Receives the messages and takes the message content. Analyzes the
		 * message content and gets the service process and input parameters to
		 * invoke the service. After the service invocation, the OMS gets the
		 * answer and sends it to the requester agent.
		 * 
		 * @param
		 * @throws RuntimeException
		 */
		protected ACLMessage prepareResponse(ACLMessage msg) {

			ACLMessage response = msg.createReply();
	
			if (msg != null) {

				try {

					// read msg content
					StringTokenizer Tok = new StringTokenizer(msg.getContent());

					// read in the service description
					String token_process = Tok.nextElement().toString();
				
					Service aService = kb.readService(token_process);		
					
					//get the process for the server
					Process aProcess = aService.getProcess();
					Boolean exists = false;

					for (int i = 0; i < OMSServicesProcess.length; i++) {

						if (token_process.equals(OMSServicesProcess[i]
								.toString())) {
							exists = true;
						}
					}

					if (exists) {

						logger.info("AGREE");
						response
								.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.AGREE);
						response.setContent(aProcess.getLocalName()+"=Agree");

					} else {

						logger.info("REFUSE");
						response
								.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.REFUSE);
						response.setContent(aProcess.getLocalName()+"=Refuse");
					}

				} catch (Exception e) {

					logger.info("EXCEPTION"+ e);
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());

				}

			} else {

				logger.info("NOTUNDERSTOOD");
				response
						.setPerformative(es.upv.dsic.gti_ia.core.ACLMessage.NOT_UNDERSTOOD);
				response.setContent("NotUnderstood");
			}

			logger.info("[OMS]Sending First message:" + response);
			return (response);

		} // end prepareResponse

		/**
		 * This callback happens if the OMS sent a positive reply to the
		 * original request (i.e. an AGREE) if the OMS has agreed to supply the
		 * service, the OMS has to inform the other agent that what they have
		 * asked is now complete (or if it failed)
		 * 
		 * @param inmsg
		 *            Message messages sent by the initiator
		 * @param outmsg
		 *            Message which we will send to the initiator with the
		 *            notification
		 * @throws RuntimeException
		 */
		protected ACLMessage prepareResultNotification(ACLMessage inmsg,
				ACLMessage outmsg) {

			ACLMessage msg = inmsg.createReply();
			
			// create an execution engine
			ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();

			// read msg content
			StringTokenizer Tok = new StringTokenizer(inmsg.getContent());

			// read in the service description
			String token_process = Tok.nextElement().toString();
			if (DEBUG) {

				logger.info("[OMS]Doc OWL-S: " + token_process);
			}

			try {
				Service aService = kb.readService(token_process);

				// get the process for the server
				Process aProcess = aService.getProcess();
				// initialize the input values to be empty
				ValueMap values = new ValueMap();

				for (int i = 0; i < aProcess.getInputs().size(); i++) {
					if (aProcess.getInputs().inputAt(i).getLocalName()
							.equalsIgnoreCase("AgentID"))
						values.setValue(aProcess.getInputs().inputAt(i),
								EntityFactory.createDataValue(inmsg.getSender()
										.toString()));
					else
						values.setValue(aProcess.getInputs().inputAt(i),
								EntityFactory.createDataValue(""));
				}
				while (Tok.hasMoreElements()) {
					String token = Tok.nextElement().toString();
					for (int i = 0; i < aProcess.getInputs().size(); i++) {
						String paramName = aProcess.getInputs().inputAt(i)
								.getLocalName().toLowerCase();
						if (paramName.equalsIgnoreCase(token.split("=")[0]
								.toLowerCase())) {
							if (token.split("=").length >= 2)
								values.setValue(
										aProcess.getInputs().inputAt(i),
										EntityFactory.createDataValue(token
												.split("=")[1]));
							else
								values.setValue(
										aProcess.getInputs().inputAt(i),
										EntityFactory.createDataValue(""));
						}

					}
				}// end while

				// execute the service
				if (DEBUG) {

					logger.info("[OMS]Executing... "
							+ values.getValues().toString());
				}
				values = exec.execute(aProcess, values);
				if (DEBUG) {

					logger.info("[OMS]Values obtained... " + values.toString());
				}
				if (DEBUG) {

					logger.info("[OMS]Creating inform message to send...");
				}

				msg.setPerformative(ACLMessage.INFORM);
				if (DEBUG) {

					logger.info("[OMS]Before set message content...");

				}
				msg.setContent(aProcess.getLocalName() + "="
						+ values.toString());

			} catch (Exception e) {
				if (DEBUG) {

					logger.error(e.toString());
				}
				msg.setPerformative(ACLMessage.FAILURE);
			}
			return (msg);
		} // end prepareResultNotification

	}// end class OMSResponder

	/**
	 * Starts the OMS agent and registers all the OMS services (process,
	 * profile, grounding)
	 */
	protected void execute() {

		logger.info("Agent OMS active");
		OMSResponder responder = new OMSResponder(this);

		this.addTask(responder);
		mon.waiting();
	}// end setup

	public void finalize() {
		logger.info("Agent OMS leaves the system.");
		oms = null;
		mon.advise();
	}

} // end OMS Agent
